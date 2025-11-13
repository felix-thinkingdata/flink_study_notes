package cn.demo;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

public class StreamingWordCount {
    public static void main(String[] args) throws Exception {
        // 创建流式执行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 创建自定义数据源，不断生成单词
        DataStream<String> wordStream = env.addSource(new WordSource());

        // 执行WordCount转换逻辑
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

        // 将结果打印到控制台
        wordCounts.print();

        // 执行作业
        env.execute("Streaming WordCount Job");
    }

    // 自定义SourceFunction，不断模拟生成单词
    public static class WordSource implements SourceFunction<String> {
        private volatile boolean isRunning = true;
        private final Random random = new Random();

        // 预定义一些单词库
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