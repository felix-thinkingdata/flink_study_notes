package cn.demo;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 基于Socket Source的Flink窗口WordCount程序
 * 使用5秒的滚动窗口来统计词频，更好地展示流式处理的特点
 *
 * 使用方法：
 * 1. 首先启动一个socket服务：nc -lk 9999
 * 2. 运行此程序
 * 3. 在socket终端输入文本，程序会每5秒统计一次词频
 */
public class SocketWindowWordCount {
    public static void main(String[] args) throws Exception {
        // 创建流式执行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置并行度为1，便于观察结果
        env.setParallelism(1);

        // 参数检查
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

        System.out.println("Connecting to socket at " + hostname + ":" + port);
        System.out.println("Start typing messages in the socket terminal...");
        System.out.println("Word count will be calculated every 5 seconds");

        // 从Socket创建数据源
        DataStream<String> textStream = env.socketTextStream(hostname, port);

        // 执行WordCount转换逻辑
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
        env.execute("Socket Window WordCount Job");
    }
}