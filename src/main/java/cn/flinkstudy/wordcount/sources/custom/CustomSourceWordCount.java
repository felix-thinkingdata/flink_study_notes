package cn.flinkstudy.wordcount.sources.custom;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

/**
 * 基于自定义数据源的词频统计程序
 *
 * 本程序演示了如何创建和使用自定义数据源，以及基本的流式词频统计。
 * 适合学习Flink的自定义数据源概念。
 *
 * 功能特点：
 * - 使用自定义SourceFunction生成随机文本数据
 * - 模拟真实的数据流场景
 * - 实时词频统计
 * - 自动化数据生成，无需外部输入
 *
 * 学习要点：
 * - 自定义SourceFunction的实现
 * - 数据生成的控制逻辑
 * - 流式处理的基本模式
 */
public class CustomSourceWordCount {
    public static void main(String[] args) throws Exception {
        // 创建流式执行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 创建自定义数据源
        DataStream<String> wordStream = env.addSource(new WordSource());

        // 执行词频统计
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordCounts = wordStream
                // 分词并转换为(word, 1)格式
                .flatMap((String line, org.apache.flink.util.Collector<Tuple2<String, Integer>> out) -> {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        if (!word.trim().isEmpty()) {
                            out.collect(Tuple2.of(word.trim().toLowerCase(), 1));
                        }
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                // 按单词分组
                .keyBy(value -> value.f0)
                // 累加次数
                .sum(1);

        // 打印结果
        wordCounts.print();

        // 执行作业
        env.execute("Custom Source WordCount - 自定义数据源词频统计");
    }

    /**
     * 自定义SourceFunction，不断生成模拟文本数据
     */
    public static class WordSource implements SourceFunction<String> {
        private volatile boolean isRunning = true;
        private final Random random = new Random();

        // 预定义单词库
        private final String[] words = {
            "hello", "world", "flink", "streaming", "processing",
            "data", "pipeline", "apache", "real", "time",
            "big", "data", "analytics", "machine", "learning",
            "distributed", "computing", "scalable", "system",
            "java", "python", "scala", "programming", "language"
        };

        @Override
        public void run(SourceContext<String> ctx) throws Exception {
            while (isRunning) {
                // 随机生成1-5个单词组成的句子
                int wordCount = random.nextInt(5) + 1;
                StringBuilder sentence = new StringBuilder();

                for (int i = 0; i < wordCount; i++) {
                    if (i > 0) {
                        sentence.append(" ");
                    }
                    sentence.append(words[random.nextInt(words.length)]);
                }

                // 发送生成的句子
                ctx.collect(sentence.toString());

                // 每100-500毫秒生成一次数据
                Thread.sleep(random.nextInt(400) + 100);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }
}