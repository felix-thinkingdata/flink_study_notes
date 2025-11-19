package cn.flinkstudy.wordcount.sources.kafka;

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

/**
 * 基于Kafka数据源的词频统计程序
 *
 * 本程序演示了如何使用Kafka作为Flink的数据源，展示了Flink与消息队列的集成能力。
 * 适合学习Flink连接器和实际生产环境的数据处理模式。
 *
 * 功能特点：
 * - 连接到Kafka集群消费数据
 * - 使用时间窗口进行词频统计
 * - 支持多topic和多分区
 * - 生产环境适用的数据处理模式
 *
 * 使用前准备：
 * 1. 确保Kafka集群正常运行
 * 2. 修改bootstrapServers为实际Kafka地址
 * 3. 确保topic存在或有创建权限
 * 4. 配置合适的消费者组ID
 *
 * 技术要点：
 * - KafkaSource的配置和使用
 * - 水印策略（WatermarkStrategy）
 * - 偏移量初始化策略
 * - 窗口操作与消息流的结合
 *
 * 注意事项：
 * - 需要Kafka连接器依赖（已在pom.xml中配置）
 * - 确保网络连接和权限配置正确
 * - 生产环境需要考虑偏移量提交策略
 */
public class KafkaWordCount {
    public static void main(String[] args) throws Exception {
        // 设置Flink执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 创建Kafka数据源
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                // 设置Kafka集群地址
                .setBootstrapServers("ta1:9092,ta2:9092,ta3:9092")
                // 设置消费的topic
                .setTopics("Shakespeare")
                // 设置消费者组ID
                .setGroupId("flink-group")
                // 设置起始偏移量策略：从最新开始消费
                .setStartingOffsets(OffsetsInitializer.latest())
                // 设置值反序列化器
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // 从Kafka中读取数据，不使用水印
        DataStream<String> stream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kafka source");

        // 执行词频统计
        SingleOutputStreamOperator<Tuple2<String, Integer>> streamOperator = stream.flatMap(
            (String value, Collector<Tuple2<String, Integer>> out) -> {
                String[] split = value.split(" ");
                for (String s : split) {
                    out.collect(Tuple2.of(s, 1));
                }
            });

        // 使用5秒滚动窗口计算单词出现次数
        streamOperator
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(value -> value.f0)
                .window(
                    org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows.of(Time.seconds(5))
                )
                .sum(1)
                .print();

        // 执行作业
        env.execute("Kafka WordCount - Kafka数据源词频统计");
    }
}