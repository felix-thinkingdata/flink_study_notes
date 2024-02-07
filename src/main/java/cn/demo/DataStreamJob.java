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

package cn.demo;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class DataStreamJob {
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(3);
		DataStreamSource<String> socketDS = env.socketTextStream("127.0.0.1", 7777);
		SingleOutputStreamOperator<Tuple2<String, Integer>> sum = socketDS
				.flatMap(
						(String value, Collector<Tuple2<String, Integer>> out) -> {
							String[] words = value.split(" ");
							for (String word : words) {
								out.collect(Tuple2.of(word, 1));
							}
						}
				)
				.setParallelism(2)
				.returns(Types.TUPLE(Types.STRING, Types.INT))
				.keyBy(value -> value.f0)
				.sum(1);
		sum.print();
		env.execute();
	}
}
