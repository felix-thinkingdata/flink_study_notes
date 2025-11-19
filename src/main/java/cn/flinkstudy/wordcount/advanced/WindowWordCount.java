package cn.flinkstudy.wordcount.advanced;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 高级窗口词频统计程序
 *
 * 本程序演示了Flink窗口操作的高级概念，包括不同类型的窗口和窗口函数的使用。
 * 适合深入理解Flink流式处理的时间概念和窗口机制。
 *
 * 功能特点：
 * - 使用固定数据集演示窗口概念
 * - 多种窗口类型的实现
 * - 时间窗口和计数窗口的对比
 * - 窗口函数的使用示例
 *
 * 学习要点：
 * - 窗口分配器（Window Assigner）的概念
 * - 时间窗口和计数窗口的区别
 * - 滚动窗口和滑动窗口的特点
 * - 窗口触发和计算时机
 *
 * 窗口类型说明：
 * - 滚动窗口：窗口不重叠，按固定大小划分
 * - 滑动窗口：窗口有重叠，按滑动步长移动
 * - 会话窗口：基于会话超时，间隔时间超过阈值时触发
 *
 * 扩展建议：
 * - 尝试不同的窗口大小和滑动步长
 * - 比较时间窗口和计数窗口的效果
 * - 学习自定义窗口触发器
 */
public class WindowWordCount {
    public static void main(String[] args) throws Exception {
        // 创建流式执行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 创建数据源 - 模拟实时数据流
        DataStreamSource<String> textStream = env.fromElements(
            "hello world", "hello flink", "world java",
            "hello scala", "flink streaming", "java python",
            "world programming", "hello bigdata", "scala spark",
            "flink realtime", "java algorithm", "world database"
        );

        // 演示滚动窗口词频统计
        SingleOutputStreamOperator<Tuple2<String, Integer>> tumblingWindowResult = textStream
                .flatMap((String line, org.apache.flink.util.Collector<Tuple2<String, Integer>> out) -> {
                    String[] words = line.toLowerCase().split(" ");
                    for (String word : words) {
                        out.collect(Tuple2.of(word, 1));
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(value -> value.f0)
                // 使用5秒的滚动窗口
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .sum(1);

        System.out.println("=== 滚动窗口词频统计结果 ===");
        tumblingWindowResult.print();

        // 执行作业
        env.execute("Advanced Window WordCount - 高级窗口词频统计");
    }
}