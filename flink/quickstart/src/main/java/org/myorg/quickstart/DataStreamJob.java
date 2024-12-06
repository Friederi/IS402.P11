package org.myorg.quickstart;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeHint;
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

		// TÍnh tổng doanh thu và số lượng sản phẩm
		DataStream<Tuple2<Double, Integer>> totalAndQuantityStream = transactions
				.map(transaction -> new Tuple2<>(transaction.getProductQuantity() * transaction.getProductPrice(),
						transaction.getProductQuantity()))
				.returns(new TypeHint<Tuple2<Double, Integer>>(){})
				.keyBy(value -> {
					return 1;
				})
				.reduce((v1, v2) -> {
						return new Tuple2<>(v1.f0 + v2.f0, v1.f1 + v2.f1);
				});

		totalAndQuantityStream.addSink(JdbcSink.sink(
				"insert into total_revenue (revenue, quantity, transaction_time) values (?, ?, default);",
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
		DataStream<ProductSales> productSalesStream = transactions
				.map(transaction -> new ProductSales(transaction.getProductId(),transaction.getProductName(),
						transaction.getProductQuantity(),
						transaction.getProductQuantity() * transaction.getProductPrice()))
				.returns(ProductSales.class)
				.keyBy(ProductSales::getProductId)
				.reduce((p1,p2)->{
					return new ProductSales(p1.getProductId(), p1.getProductName(),
							p1.getProductQuantity() + p2.getProductQuantity(),
							p1.getRevenue() + p2.getRevenue());
				});

		productSalesStream.addSink(JdbcSink.sink(
				"insert into product_sales (product_id, product_name, quantity, revenue, transaction_time) values (?, ?, ?, ?, default) " +
					"on conflict (product_id) do update set quantity = excluded.quantity, revenue = excluded.revenue, transaction_time = now()",
				(statement, event) -> {
					statement.setString(1, event.getProductId());
					statement.setString(2, event.getProductName());
					statement.setInt(3, event.getProductQuantity());
					statement.setDouble(4, event.getRevenue());
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
		DataStream<StoreSales> storeSalesStream = transactions
				.map(transaction -> new StoreSales(transaction.getStoreId(), transaction.getProductQuantity(),
						transaction.getProductQuantity() * transaction.getProductPrice()))
				.returns(StoreSales.class)
				.keyBy(StoreSales::getStoreId)
				.reduce((s1,s2)->{
					return new StoreSales(s1.getStoreId(),s1.getProductQuantity()+s2.getProductQuantity(),
							s1.getRevenue() + s2.getRevenue());
				});

		storeSalesStream.addSink(JdbcSink.sink(
				"insert into store_sales (store_id, quantity, revenue, transaction_time) values (?, ?, ?, default) " +
						"on conflict (store_id) do update set quantity = excluded.quantity, revenue = excluded.revenue, transaction_time = now()",
				(statement, event) -> {
					statement.setString(1, event.getStoreId());
					statement.setInt(2, event.getProductQuantity());
					statement.setDouble(3, event.getRevenue());
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
