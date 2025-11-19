# Flink学习笔记文档

本目录包含了Flink学习相关的所有文档和说明。

## 文档结构

### 示例程序说明
- `README_Socket_WordCount.md` - 基于Socket为Source的Flink WordCount样例程序说明

### 项目结构说明

本项目是一个Flink学习工程，采用了模块化的包结构设计，按功能场景分类：

```
flink_study_notes/
├── src/main/java/cn/flinkstudy/     # 重新组织的代码包结构
│   ├── basic/                      # 基础概念和入门示例
│   │   ├── batch/                  # 批处理示例
│   │   │   └── DataBatchJob.java
│   │   └── streaming/              # 流处理基础
│   │       └── DataStreamJob.java
│   ├── wordcount/                  # 词频统计专题
│   │   ├── core/                   # 核心词频统计实现
│   │   │   └── BasicWordCount.java
│   │   ├── sources/                # 不同数据源的词频统计
│   │   │   ├── socket/             # Socket数据源
│   │   │   │   ├── SocketWordCount.java
│   │   │   │   └── SocketWindowWordCount.java
│   │   │   ├── kafka/              # Kafka数据源
│   │   │   │   └── KafkaWordCount.java
│   │   │   └── custom/             # 自定义数据源
│   │   │       ├── CustomSourceWordCount.java
│   │   │       └── ParallelCustomSourceWordCount.java
│   │   └── advanced/               # 高级词频统计功能
│   │       └── WindowWordCount.java
│   ├── connectors/                 # 连接器示例
│   │   ├── kafka/                  # Kafka连接器
│   │   └── socket/                 # Socket连接器
│   ├── windows/                    # 窗口操作专题
│   │   ├── tumbling/               # 滚动窗口
│   │   ├── sliding/                # 滑动窗口
│   │   └── session/                # 会话窗口
│   └── utils/                      # 工具类和通用组件
│       ├── sources/                # 数据源工具
│       └── functions/              # 自定义函数
├── docs/                           # 📚 文档目录
├── scripts/                        # 🛠️ 脚本目录
└── pom.xml                         # Maven配置
```

## 学习路径

### 🚀 新手入门路径
1. **基础概念** (`cn.flinkstudy.basic`)
   - 批处理入门：`DataBatchJob.java`
   - 流处理入门：`DataStreamJob.java`

2. **词频统计专题** (`cn.flinkstudy.wordcount`)
   - 核心实现：`BasicWordCount.java`
   - Socket数据源：`SocketWordCount.java` ⭐ **推荐从这开始**
   - 窗口版本：`SocketWindowWordCount.java`

3. **数据源和连接器** (`cn.flinkstudy.wordcount.sources`)
   - 自定义数据源：`CustomSourceWordCount.java`
   - 并行数据源：`ParallelCustomSourceWordCount.java`
   - Kafka数据源：`KafkaWordCount.java`

4. **高级概念** (`cn.flinkstudy.wordcount.advanced`)
   - 窗口操作：`WindowWordCount.java`

### 🎯 按场景学习
- **Socket源学习**：`wordcount/sources/socket/` 包下所有示例
- **自定义源学习**：`wordcount/sources/custom/` 包下所有示例
- **连接器学习**：`wordcount/sources/kafka/` 包下所有示例
- **窗口操作学习**：`wordcount/advanced/` 和 `windows/` 包下所有示例

### 📚 对应书中章节
- **4.1章节**：Socket WordCount → `wordcount/sources/socket/SocketWordCount.java`
- **窗口操作**：详见 `wordcount/advanced/` 目录

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