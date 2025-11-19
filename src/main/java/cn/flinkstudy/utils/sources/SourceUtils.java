package cn.flinkstudy.utils.sources;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.Random;

/**
 * 数据源工具类
 *
 * 本类提供了各种常用的数据源实现和工具方法，方便在不同示例中复用。
 * 包含随机数据生成、测试数据创建等功能。
 */
public class SourceUtils {

    /**
     * 创建简单的随机文本数据源
     */
    public static class SimpleTextSource implements SourceFunction<String> {
        private volatile boolean isRunning = true;
        private final Random random = new Random();

        private final String[] words = {
            "hello", "world", "flink", "java", "stream", "data", "process"
        };

        @Override
        public void run(org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext<String> ctx) throws Exception {
            while (isRunning) {
                // 生成随机文本
                StringBuilder sb = new StringBuilder();
                int wordCount = random.nextInt(3) + 1;
                for (int i = 0; i < wordCount; i++) {
                    if (i > 0) sb.append(" ");
                    sb.append(words[random.nextInt(words.length)]);
                }
                ctx.collect(sb.toString());
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

    /**
     * 创建可配置的随机文本数据源
     */
    public static class ConfigurableTextSource extends RichSourceFunction<String> {
        private volatile boolean isRunning = true;
        private transient Random random;
        private final String[] wordPool;
        private final long minDelayMs;
        private final long maxDelayMs;

        public ConfigurableTextSource(String[] wordPool, long minDelayMs, long maxDelayMs) {
            this.wordPool = wordPool;
            this.minDelayMs = minDelayMs;
            this.maxDelayMs = maxDelayMs;
        }

        @Override
        public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
            super.open(parameters);
            random = new Random();
        }

        @Override
        public void run(org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext<String> ctx) throws Exception {
            while (isRunning) {
                String word = wordPool[random.nextInt(wordPool.length)];
                ctx.collect(word);

                long delay = minDelayMs + random.nextInt((int) (maxDelayMs - minDelayMs));
                Thread.sleep(delay);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }
}