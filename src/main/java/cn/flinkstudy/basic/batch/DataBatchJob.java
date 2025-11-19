package cn.flinkstudy.basic.batch;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 批处理基础示例
 *
 * 本示例展示了Flink批处理的基本概念：
 * - 使用固定数据集
 * - 词频统计基本操作
 * - 数据转换和聚合
 */
public class DataBatchJob {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 创建固定数据集作为输入源
        DataStreamSource<String> streamSource = env.fromElements(
            "world count", "hello world", "hello flink", "flink", "hello",
            "hello world", "hello flink", "flink", "hello", "world"
        );

        // 执行词频统计
        SingleOutputStreamOperator<Tuple2<String, Integer>> streamOperator = streamSource.flatMap(
            new FlatMapFunction<String, Tuple2<String, Integer>>() {
                @Override
                public void flatMap(String value, org.apache.flink.util.Collector<Tuple2<String, Integer>> out) throws Exception {
                    String[] split = value.split(" ");
                    for (String s : split) {
                        out.collect(Tuple2.of(s, 1));
                    }
                }
            });

        // 按单词分组并累加计数
        streamOperator.keyBy(value -> value.f0).sum(1).print();

        // 执行作业
        env.execute("Flink Batch WordCount - 词频统计");
    }
}