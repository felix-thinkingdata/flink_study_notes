/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.flinkstudy.basic.streaming;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * 流处理基础示例
 *
 * 本示例展示了Flink流处理的基本概念：
 * - Socket数据源连接
 * - 并行度设置
 * - 数据流转换
 * - 实时词频统计
 *
 * 运行方法：
 * 1. 启动socket服务：nc -lk 7777
 * 2. 运行此程序
 * 3. 在socket终端输入文本
 */
public class DataStreamJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置全局并行度为3
        env.setParallelism(3);

        // 连接到Socket数据源
        DataStreamSource<String> socketDS = env.socketTextStream("127.0.0.1", 7777);

        // 执行词频统计
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = socketDS
                .flatMap(
                    (String value, Collector<Tuple2<String, Integer>> out) -> {
                        String[] words = value.split(" ");
                        for (String word : words) {
                            out.collect(Tuple2.of(word, 1));
                        }
                    }
                )
                // 设置flatMap的并行度为2
                .setParallelism(2)
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(value -> value.f0)
                .sum(1);

        // 打印结果
        sum.print();

        // 执行作业
        env.execute("Flink Stream WordCount - 实时词频统计");
    }
}