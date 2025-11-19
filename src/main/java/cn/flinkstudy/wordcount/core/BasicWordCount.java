package cn.flinkstudy.wordcount.core;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.Collector;

/**
 * 基础词频统计核心类
 *
 * 本类提供了词频统计的核心功能实现，可以被其他示例程序复用。
 * 设计为工具类，演示了Flink编程的最佳实践和代码复用模式。
 *
 * 功能特点：
 * - 提供可复用的词频统计逻辑
 * - 支持Lambda表达式和函数式编程
 * - 清晰的数据处理流程
 * - 易于扩展和定制
 *
 * 核心方法：
 * - buildWordCountPipeline(): 构建词频统计处理流水线
 * - splitAndTokenize(): 分词和标记化方法
 *
 * 使用方式：
 * 可以在其他程序中通过调用静态方法来复用词频统计功能，
 * 也可以作为学习Flink数据处理模式参考。
 *
 * 设计原则：
 * - 单一职责：专注于词频统计功能
 * - 高内聚低耦合：逻辑内聚，接口简洁
 * - 可复用：支持不同数据源和场景
 */
public class BasicWordCount {

    /**
     * 构建词频统计处理流水线
     *
     * @param textStream 输入的文本数据流
     * @return 处理后的词频统计结果流
     */
    public static DataStream<Tuple2<String, Integer>> buildWordCountPipeline(DataStream<String> textStream) {
        return textStream
                // 分词并转换为(word, 1)格式
                .flatMap(BasicWordCount::splitAndTokenize)
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                // 按单词分组
                .keyBy(value -> value.f0)
                // 累加次数
                .sum(1);
    }

    /**
     * 分词和标记化处理
     *
     * @param line 输入文本行
     * @param out 输出收集器
     */
    private static void splitAndTokenize(String line, Collector<Tuple2<String, Integer>> out) {
        // 预处理：转换为小写并去除首尾空格
        String cleanLine = line.toLowerCase().trim();

        if (cleanLine.isEmpty()) {
            return;
        }

        // 分词：按空格和常见标点符号分割
        String[] words = cleanLine.split("[\\s\\p{Punct}]+");

        // 输出每个单词
        for (String word : words) {
            if (!word.trim().isEmpty()) {
                out.collect(Tuple2.of(word.trim(), 1));
            }
        }
    }

    /**
     * 简化版词频统计（单次处理）
     *
     * @param textStream 输入文本流
     * @return 词频统计结果
     */
    public static DataStream<Tuple2<String, Integer>> simpleWordCount(DataStream<String> textStream) {
        return textStream
                .flatMap((String line, Collector<Tuple2<String, Integer>> out) -> {
                    String[] words = line.toLowerCase().split(" ");
                    for (String word : words) {
                        if (!word.trim().isEmpty()) {
                            out.collect(Tuple2.of(word.trim(), 1));
                        }
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(value -> value.f0)
                .sum(1);
    }

    /**
     * 过滤版本词频统计（支持自定义过滤条件）
     *
     * @param textStream 输入文本流
     * @param minWordLength 最小单词长度
     * @return 词频统计结果
     */
    public static DataStream<Tuple2<String, Integer>> filteredWordCount(
            DataStream<String> textStream, int minWordLength) {
        return textStream
                .flatMap((String line, Collector<Tuple2<String, Integer>> out) -> {
                    String[] words = line.toLowerCase().split("[\\s\\p{Punct}]+");
                    for (String word : words) {
                        if (!word.trim().isEmpty() && word.length() >= minWordLength) {
                            out.collect(Tuple2.of(word.trim(), 1));
                        }
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(value -> value.f0)
                .sum(1);
    }
}