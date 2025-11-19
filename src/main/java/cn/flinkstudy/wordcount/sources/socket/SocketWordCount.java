package cn.flinkstudy.wordcount.sources.socket;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 基于Socket数据源的词频统计程序
 *
 * 本程序演示了如何使用Socket作为Flink的数据源进行实时词频统计。
 * 这是学习Flink流式处理的入门示例，对应书中4.1章节的练习内容。
 *
 * 功能特点：
 * - 连接到指定Socket端口接收实时文本数据
 * - 智能分词（支持空格和标点符号）
 * - 实时统计词频
 * - 过滤空行和无效数据
 *
 * 使用方法：
 * 1. 启动socket服务：nc -lk 9999
 * 2. 运行程序：mvn compile exec:java -Dexec.mainClass="cn.flinkstudy.wordcount.sources.socket.SocketWordCount" -Dexec.args="localhost 9999"
 * 3. 在socket终端输入文本，观察词频统计结果
 *
 * 测试数据示例：
 * hello world flink streaming
 * hello apache flink
 * real time processing
 * big data analytics
 */
public class SocketWordCount {
    public static void main(String[] args) throws Exception {
        // 创建流式执行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置并行度为1，便于观察结果
        env.setParallelism(1);

        // 参数检查和解析
        if (args.length != 2) {
            System.err.println("Usage: SocketWordCount <hostname> <port>");
            System.err.println("Example: SocketWordCount localhost 9999");
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

        System.out.println("=== Flink Socket WordCount ===");
        System.out.println("Connecting to socket at " + hostname + ":" + port);
        System.out.println("Start typing messages in the socket terminal...");
        System.out.println("Press Ctrl+C to stop the program");

        // 从Socket创建数据源
        DataStream<String> textStream = env.socketTextStream(hostname, port);

        // 执行词频统计转换逻辑
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordCounts = textStream
                // 过滤掉空行和空字符串
                .filter(line -> line != null && !line.trim().isEmpty())
                // 分词并转换为(word, 1)格式
                .flatMap((String line, org.apache.flink.util.Collector<Tuple2<String, Integer>> out) -> {
                    // 按空格和标点符号分词，支持中英文
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
                // 累加次数
                .sum(1);

        // 将结果打印到控制台
        wordCounts.print();

        // 执行作业
        env.execute("Socket WordCount - 实时词频统计");
    }
}