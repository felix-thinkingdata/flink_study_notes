package cn.demo;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

public class ParallelStreamingWordCount {
    public static void main(String[] args) throws Exception {
        // 创建流式执行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


        // 创建并行数据源，设置并行度为2
        DataStream<String> wordStream = env.addSource(new WordSource()).setParallelism(2);

        // 执行WordCount转换逻辑
        SingleOutputStreamOperator<Tuple2<String, Integer>> flatMapResult = wordStream
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
                .disableChaining()  // 打破算子链，让flatMap独立运行
                .setParallelism(1); // 设置flatMap并行度为1

        // 继续处理
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordCounts = flatMapResult
                // 按单词分组
                .keyBy(value -> value.f0)
                // 累加次数
                .sum(1)
                .disableChaining()  // 打破算子链，让sum独立运行
                .setParallelism(1); // 设置sum并行度为1

        // 将结果打印到控制台
        wordCounts.print();

        // 执行作业
        env.execute("Parallel Streaming WordCount Job");
    }

    // 实现ParallelSourceFunction以支持并行执行
    public static class WordSource implements ParallelSourceFunction<String> {
        private volatile boolean isRunning = true;
        private transient Random random;
        private transient int subtaskId;

        @Override
        public void run(SourceContext<String> ctx) throws Exception {
            // 每个并行实例初始化自己的Random对象
            random = new Random();

            // 获取当前子任务ID（通过线程名模拟，因为无法直接获取RuntimeContext）
            String threadName = Thread.currentThread().getName();
            subtaskId = Math.abs(threadName.hashCode()) % 100;

            while (isRunning) {
                // 随机生成1-5个单词组成的句子
                int wordCount = random.nextInt(5) + 1;
                StringBuilder sentence = new StringBuilder();

                // 为每个子任务添加标识，便于观察并行效果
                sentence.append("source").append(subtaskId % 2).append("_");

                for (int i = 0; i < wordCount; i++) {
                    if (i > 0) {
                        sentence.append(" ");
                    }
                    sentence.append(words[random.nextInt(words.length)]);
                }

                // 发送生成的句子
                ctx.collect(sentence.toString());

                // 每50-200毫秒生成一次数据（加快速度便于观察）
                Thread.sleep(random.nextInt(150) + 50);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }

        // 预定义一些单词库
        private final String[] words = {
            "hello", "world", "flink", "streaming", "processing",
            "data", "pipeline", "apache", "real", "time",
            "big", "data", "analytics", "machine", "learning",
            "distributed", "computing", "scalable", "system",
            "java", "python", "scala", "programming", "language"
        };
    }
}
