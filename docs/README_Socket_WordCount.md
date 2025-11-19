# Flink Socket WordCount 样例程序

本目录包含了基于Socket为Source的Flink WordCount样例程序，这是对应书中4.1章节的练习内容。

## 文件说明

- `SocketWordCount.java` - 基础版本的Socket WordCount程序
- `SocketWindowWordCount.java` - 带窗口功能的Socket WordCount程序
- `test_socket_wordcount.sh` - 测试脚本

## 程序功能

这两个程序都实现了以下功能：
1. 连接到指定的Socket端口
2. 接收实时文本数据
3. 分词并统计词频
4. 输出统计结果

## 运行方法

### 方法1：手动运行

**步骤1：启动Socket服务器**
```bash
# 在第一个终端窗口中运行
nc -lk 9999
```

**步骤2：运行Flink程序**

基础版本（实时统计）：
```bash
mvn compile exec:java -Dexec.mainClass="cn.demo.SocketWordCount" -Dexec.args="localhost 9999"
```

窗口版本（每5秒统计一次）：
```bash
mvn compile exec:java -Dexec.mainClass="cn.demo.SocketWindowWordCount" -Dexec.args="localhost 9999"
```

**步骤3：发送测试数据**

在第一个终端窗口中输入文本，例如：
```
hello world flink streaming
hello apache flink
real time processing
big data analytics
hello world
flink streaming processing
```

### 方法2：使用测试脚本

```bash
./test_socket_wordcount.sh
```

## 程序特点

### SocketWordCount.java
- 使用 `env.socketTextStream()` 连接Socket
- 实时处理输入数据
- 累加统计词频
- 适合学习基础的Socket连接

### SocketWindowWordCount.java
- 在基础版本上增加了时间窗口功能
- 使用 `TumblingProcessingTimeWindows.of(Time.seconds(5))`
- 每5秒输出一次窗口内的统计结果
- 展示了流式处理的核心概念

## 关键API说明

- `env.socketTextStream(hostname, port)` - 创建Socket数据源
- `flatMap()` - 分词并转换为(word, 1)格式
- `keyBy()` - 按单词分组
- `sum()` - 累加统计
- `window()` - 时间窗口操作（窗口版本）

## 测试数据示例

你可以使用以下测试数据来验证程序：

```
hello world flink streaming
hello apache flink
real time processing
big data analytics
hello world
flink streaming processing
java python scala
programming language
data science machine learning
```

## 预期输出

程序会实时输出词频统计结果，格式为：
```
(word, count)
```

例如：
```
(hello,1)
(world,1)
(flink,1)
(streaming,1)
(hello,2)
(world,2)
(flink,2)
```

## 注意事项

1. 确保端口9999没有被其他程序占用
2. 需要先启动Socket服务器，再运行Flink程序
3. 输入文本后按Enter发送
4. 按Ctrl+C停止程序

## 学习要点

1. 理解Flink的Source概念（Socket作为数据源）
2. 掌握流式处理的基本操作
3. 了解窗口函数的使用
4. 学习实时数据处理的基本模式