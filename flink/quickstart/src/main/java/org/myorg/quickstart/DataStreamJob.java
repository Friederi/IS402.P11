package org.myorg.quickstart;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DataStreamJob {

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		KafkaSource<Transaction> kafkaSource = KafkaSource.<Transaction>builder()
				.setBootstrapServers("localhost:9092")
				.setTopics("financial_transactions")
				.setGroupId("flink-consumer-group")
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new TransactionDeserializationSchema())
				.build();

		DataStream<Transaction> transactions = env.fromSource(kafkaSource,
				WatermarkStrategy.noWatermarks(),
				"kafkaSource");

		DataStream<Tuple2<Double, Integer>> revenueAndQuantityStream = transactions
				.map(transaction -> new Tuple2<>(transaction.getProductQuantity() * transaction.getProductPrice(),
						transaction.getProductQuantity()))
				.returns(new TypeHint<Tuple2<Double, Integer>>(){})
				.keyBy(value -> {
					return 1;
				})
				.reduce(new ReduceFunction<Tuple2<Double, Integer>>() {
					@Override
					public Tuple2<Double, Integer> reduce(Tuple2<Double, Integer> value1,
														  Tuple2<Double, Integer> value2) {
						return new Tuple2<>(value1.f0 + value2.f0, value1.f1 + value2.f1);
					}
				});

		revenueAndQuantityStream.addSink(JdbcSink.sink(
				"insert into revenueandquantity (revenue, quantity, transaction_time) values (?, ?, default);",
				(statement, event) -> {
					statement.setDouble(1, event.f0);
					statement.setInt(2, event.f1);
				},
				JdbcExecutionOptions.builder()
						.withBatchSize(1000)
						.withBatchIntervalMs(200)
						.withMaxRetries(5)
						.build(),
				new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
						.withUrl("jdbc:postgresql://localhost:5432/store")
						.withDriverName("org.postgresql.Driver")
						.withUsername("root")
						.withPassword("root")
						.build()
		));

		// Tính doanh thu theo từng sản phẩm
		DataStream<Tuple2<String, Double>> revenueByProductStream = transactions
				.map(transaction -> new Tuple2<>(transaction.getProductId(),
						transaction.getProductQuantity() * transaction.getProductPrice()))
				.returns(new TypeHint<Tuple2<String, Double>>() {})
				.keyBy(value -> value.f0)
				.sum(1);

		revenueByProductStream.addSink(JdbcSink.sink(
				"insert into revenueofproduct (product_id, revenue, transaction_time) values (?, ?, default) " +
					"on conflict (product_id) do update set revenue = excluded.revenue, transaction_time = now()",
				(statement, event) -> {
					statement.setString(1, event.f0);
					statement.setDouble(2, event.f1);
				},
				JdbcExecutionOptions.builder()
						.withBatchSize(1000)
						.withBatchIntervalMs(200)
						.withMaxRetries(5)
						.build(),
				new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
						.withUrl("jdbc:postgresql://localhost:5432/store")
						.withDriverName("org.postgresql.Driver")
						.withUsername("root")
						.withPassword("root")
						.build()
		));

		// Tính doanh thu theo từng cửa hàng
		DataStream<Tuple2<String, Double>> revenueByStoreStream = transactions
				.map(transaction -> new Tuple2<>(transaction.getStoreId(),
						transaction.getProductQuantity() * transaction.getProductPrice()))
				.returns(new TypeHint<Tuple2<String, Double>>() {})
				.keyBy(value -> value.f0)
				.sum(1);

		revenueByStoreStream.addSink(JdbcSink.sink(
				"insert into revenueofstore (store_id, revenue, transaction_time) values (?, ?, default) " +
						"on conflict (store_id) do update set revenue = excluded.revenue, transaction_time = now()",
				(statement, event) -> {
					statement.setString(1, event.f0);
					statement.setDouble(2, event.f1);
				},
				JdbcExecutionOptions.builder()
						.withBatchSize(1000)
						.withBatchIntervalMs(200)
						.withMaxRetries(5)
						.build(),
				new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
						.withUrl("jdbc:postgresql://localhost:5432/store")
						.withDriverName("org.postgresql.Driver")
						.withUsername("root")
						.withPassword("root")
						.build()
		));

		//  Execute
		env.execute("Flink Java API");
	}
}
