package cn.flinkstudy.wordcount.sources.custom;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

import java.util.Random;

/**
 * 基于并行自定义数据源的词频统计程序
 *
 * 本程序演示了如何创建支持并行的自定义数据源，展示了Flink的并行处理概念。
 *
 * 功能特点：
 * - 实现ParallelSourceFunction支持并行执行
 * - 多个并行实例同时生成数据
 * - 每个实例有唯一标识，便于观察并行效果
 * - 更高的数据生成速率
 *
 * 学习要点：
 * - ParallelSourceFunction与SourceFunction的区别
 * - 并行源的实现要点
 * - 多实例数据源的设计
 * - 并行度的理解和配置
 *
 * 运行效果：
 * - 可在输出中看到不同并行实例生成的数据
 * - 观察并行处理对性能的影响
 * - 理解数据分发的原理
 */
public class ParallelCustomSourceWordCount {
    public static void main(String[] args) throws Exception {
        // 创建流式执行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 创建并行数据源
        DataStream<String> wordStream = env.addSource(new ParallelWordSource());

        // 执行词频统计
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
                .returns(Types.TUPLE(Types.STRING, Types.INT));

        // 继续处理
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordCounts = flatMapResult
                // 按单词分组
                .keyBy(value -> value.f0)
                // 累加次数
                .sum(1);

        // 打印结果
        wordCounts.print();

        // 执行作业
        env.execute("Parallel Custom Source WordCount - 并行自定义数据源词频统计");
    }

    /**
     * 支持并行的自定义数据源
     */
    public static class ParallelWordSource implements ParallelSourceFunction<String> {
        private volatile boolean isRunning = true;
        private transient Random random;
        private transient int subtaskId;

        @Override
        public void run(SourceContext<String> ctx) throws Exception {
            // 每个并行实例初始化自己的Random对象
            random = new Random();

            // 获取当前子任务ID（通过线程名模拟）
            String threadName = Thread.currentThread().getName();
            subtaskId = Math.abs(threadName.hashCode()) % 100;

            while (isRunning) {
                // 随机生成1-5个单词组成的句子
                int wordCount = random.nextInt(5) + 1;
                StringBuilder sentence = new StringBuilder();

                // 为每个子任务添加标识，便于观察并行效果
                sentence.append("source").append(subtaskId % 4).append("_");

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

        // 预定义单词库
        private final String[] words = {
            "hello", "world", "flink", "streaming", "processing",
            "data", "pipeline", "apache", "real", "time",
            "big", "data", "analytics", "machine", "learning",
            "distributed", "computing", "scalable", "system",
            "java", "python", "scala", "programming", "language"
        };
    }
}
