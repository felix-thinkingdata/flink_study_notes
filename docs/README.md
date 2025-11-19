# Flink学习笔记文档

本目录包含了Flink学习相关的所有文档和说明。

## 文档结构

### 示例程序说明
- `README_Socket_WordCount.md` - 基于Socket为Source的Flink WordCount样例程序说明

### 项目结构说明

本项目是一个Flink学习工程，包含了多个示例程序：

```
flink_study_notes/
├── src/main/java/cn/demo/           # 基础示例程序
│   ├── SocketWordCount.java         # Socket基础版本WordCount
│   ├── SocketWindowWordCount.java   # Socket窗口版本WordCount
│   ├── StreamingWordCount.java      # 流式WordCount（自定义源）
│   ├── ParallelStreamingWordCount.java # 并行流式WordCount
│   └── ...                          # 其他示例程序
├── src/main/java/cn/thinkingdata/   # 书中章节示例
│   └── chapter04/                   # 第4章示例
├── scripts/                         # 脚本文件（可选）
├── docs/                            # 本文档目录
└── pom.xml                          # Maven配置
```

## 学习路径

1. **基础概念**
   - 批处理作业（DataBatchJob.java）
   - 流处理作业（DataStreamJob.java）

2. **数据源和连接器**
   - Socket数据源（SocketWordCount.java）
   - 自定义数据源（StreamingWordCount.java）
   - Kafka连接器（WordCountKafkaInStdOut.java）

3. **流处理核心概念**
   - 窗口操作（WindowWordCount.java）
   - 并行处理（ParallelStreamingWordCount.java）

## 使用建议

1. 按照学习路径逐步学习各个示例
2. 运行每个示例并观察输出结果
3. 修改参数和配置，理解其影响
4. 参考对应章节的文档进行深入学习

## 贡献

如需添加新的示例或文档，请：
1. 将相关文档放在docs目录下
2. 保持代码和文档的一致性
3. 更新本README文件