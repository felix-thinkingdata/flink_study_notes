package cn.flinkstudy.wordcount.sources.socket;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 基于Socket数据源的窗口词频统计程序
 *
 * 本程序在基础Socket词频统计上增加了时间窗口功能，演示了Flink流式处理的核心概念之一：窗口操作。
 *
 * 功能特点：
 * - 使用5秒的滚动窗口进行词频统计
 * - 每个窗口独立统计，窗口之间不累加
 * - 展示了流式处理的时间概念
 * - 适合理解有界数据和无界数据的区别
 *
 * 使用方法：
 * 1. 启动socket服务：nc -lk 9999
 * 2. 运行程序：mvn compile exec:java -Dexec.mainClass="cn.flinkstudy.wordcount.sources.socket.SocketWindowWordCount" -Dexec.args="localhost 9999"
 * 3. 在socket终端快速输入多行文本
 * 4. 观察每5秒输出的窗口统计结果
 *
 * 窗口概念说明：
 * - 滚动窗口：窗口不重叠，每个时间段一个独立窗口
 * - 窗口大小：5秒，每5秒统计一次该时间段内的词频
 * - 处理时间：使用系统时间作为窗口划分依据
 *
 * 对比学习：
 * - SocketWordCount：累加统计，全局计数
 * - SocketWindowWordCount：窗口统计，局部计数
 */
public class SocketWindowWordCount {
    public static void main(String[] args) throws Exception {
        // 创建流式执行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置并行度为1，便于观察结果
        env.setParallelism(1);

        // 参数检查和解析
        if (args.length != 2) {
            System.err.println("Usage: SocketWindowWordCount <hostname> <port>");
            System.err.println("Example: SocketWindowWordCount localhost 9999");
            return;
        }

        String hostname = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error: Port must be a number");
            return;
        }

        System.out.println("=== Flink Socket Window WordCount ===");
        System.out.println("Connecting to socket at " + hostname + ":" + port);
        System.out.println("Window size: 5 seconds (Tumbling Window)");
        System.out.println("Start typing messages in the socket terminal...");
        System.out.println("Word count will be calculated every 5 seconds");

        // 从Socket创建数据源
        DataStream<String> textStream = env.socketTextStream(hostname, port);

        // 执行窗口词频统计
        DataStream<Tuple2<String, Integer>> wordCounts = textStream
                // 过滤掉空行
                .filter(line -> line != null && !line.trim().isEmpty())
                // 分词并转换为(word, 1)格式
                .flatMap((String line, org.apache.flink.util.Collector<Tuple2<String, Integer>> out) -> {
                    // 按空格和标点符号分词
                    String[] words = line.toLowerCase().split("[\\s\\p{Punct}]+");
                    for (String word : words) {
                        if (!word.trim().isEmpty()) {
                            out.collect(Tuple2.of(word.trim(), 1));
                        }
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                // 按单词分组
                .keyBy(value -> value.f0)
                // 使用5秒的滚动窗口
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                // 在窗口内累加次数
                .sum(1);

        // 将结果打印到控制台
        wordCounts.print();

        // 执行作业
        env.execute("Socket Window WordCount - 5秒滚动窗口词频统计");
    }
}