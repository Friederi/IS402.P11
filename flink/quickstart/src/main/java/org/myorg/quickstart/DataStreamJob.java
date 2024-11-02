package org.myorg.quickstart;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DataStreamJob {

	public static void main(String[] args) throws Exception {

		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		KafkaSource<Transaction> kafkaSource	= KafkaSource.<Transaction>builder()
				.setBootstrapServers("localhost:9092")
				.setTopics("financial_transactions")
				.setGroupId("flink-consumer-group")
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new TransactionDeserializationSchema())
				.build();

		DataStream<Transaction> transactions = env.fromSource(kafkaSource,
				WatermarkStrategy.noWatermarks(),
				"kafkaSource");

		// Tính tổng doanh thu và số lượng sản phẩm
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

		revenueAndQuantityStream
				.map(value -> "Tổng doanh thu: " + value.f0 + ", Tổng số lượng sản phẩm: " + value.f1)
				.returns(TypeInformation.of(String.class))
				.print();

		// Tính doanh thu theo từng sản phẩm
		DataStream<Tuple2<String, Double>> revenueByProductStream = transactions
				.map(transaction -> new Tuple2<>(transaction.getProductId(),
						transaction.getProductQuantity() * transaction.getProductPrice()))
				.returns(new TypeHint<Tuple2<String, Double>>() {})
				.keyBy(value -> value.f0)
				.sum(1);

		revenueByProductStream
				.map(value -> "Doanh thu cho sản phẩm " + value.f0 + ": " + value.f1)
				.returns(TypeInformation.of(String.class))
				.print();

		// Tính doanh thu theo từng cửa hàng
		DataStream<Tuple2<String, Double>> revenueByStoreStream = transactions
				.map(transaction -> new Tuple2<>(transaction.getStoreId(),
						transaction.getProductQuantity() * transaction.getProductPrice()))
				.returns(new TypeHint<Tuple2<String, Double>>() {})
				.keyBy(value -> value.f0)
				.sum(1);

		revenueByStoreStream
				.map(value -> "Doanh thu cho cửa hàng " + value.f0 + ": " + value.f1)
				.returns(TypeInformation.of(String.class))
				.print();

		//  Execute
		env.execute("Flink Java API");
	}
}
