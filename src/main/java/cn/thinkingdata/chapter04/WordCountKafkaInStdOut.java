package cn.thinkingdata.chapter04;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;


public class WordCountKafkaInStdOut {

    public static void main(String[] args) throws Exception {
        // 设置Flink执行环境
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        //设置kafka source
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("ta1:9092,ta2:9092,ta3:9092")
                .setTopics("Shakespeare")
                .setGroupId("flink-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        DataStream<String> stream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kafka source");
        // 从Kafka中读取数据
        SingleOutputStreamOperator<Tuple2<String, Integer>> streamOperator = stream.flatMap((String value, Collector<Tuple2<String, Integer>> out) -> {
            String[] split = value.split(" ");
            for (String s : split) {
                out.collect(Tuple2.of(s, 1));
            }
        });
        // 计算单词出现的次数
        streamOperator.returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(value -> value.f0)
                .window(
                        org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows.of(Time.seconds(5))
                ).sum(1).print();
        // 执行任务
        env.execute("count the number of times a word appears");

    }
}
