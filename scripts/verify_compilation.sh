#!/bin/bash

echo "=== Flink 项目编译验证 ==="
echo ""

# 检查新的包结构文件是否存在
echo "检查源文件..."
if [ -f "src/main/java/cn/flinkstudy/wordcount/sources/socket/SocketWordCount.java" ]; then
    echo "✓ SocketWordCount.java 存在 (新位置)"
else
    echo "✗ SocketWordCount.java 不存在"
    exit 1
fi

if [ -f "src/main/java/cn/flinkstudy/wordcount/sources/socket/SocketWindowWordCount.java" ]; then
    echo "✓ SocketWindowWordCount.java 存在 (新位置)"
else
    echo "✗ SocketWindowWordCount.java 不存在"
    exit 1
fi

# 检查其他关键文件
if [ -f "src/main/java/cn/flinkstudy/basic/batch/DataBatchJob.java" ]; then
    echo "✓ DataBatchJob.java 存在"
else
    echo "⚠ DataBatchJob.java 不存在 (可选)"
fi

if [ -f "src/main/java/cn/flinkstudy/basic/streaming/DataStreamJob.java" ]; then
    echo "✓ DataStreamJob.java 存在"
else
    echo "⚠ DataStreamJob.java 不存在 (可选)"
fi

echo ""
echo "编译项目..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ 编译成功！"
    echo ""
    echo "📁 新的项目结构："
    echo "基础示例:"
    echo "- DataBatchJob.java: 批处理入门"
    echo "- DataStreamJob.java: 流处理入门"
    echo ""
    echo "词频统计专题:"
    echo "- SocketWordCount.java: 基础版本，实时统计词频"
    echo "- SocketWindowWordCount.java: 窗口版本，每5秒统计一次"
    echo "- CustomSourceWordCount.java: 自定义数据源"
    echo "- ParallelCustomSourceWordCount.java: 并行自定义数据源"
    echo "- KafkaWordCount.java: Kafka数据源"
    echo ""
    echo "工具和文档:"
    echo "- scripts/: 脚本文件目录"
    echo "- docs/: 文档目录"
    echo "- BasicWordCount.java: 核心工具类"
    echo ""
    echo "🚀 快速运行 (推荐开始)："
    echo "1. 终端1: nc -lk 9999"
    echo "2. 终端2: mvn compile exec:java -Dexec.mainClass=\"cn.flinkstudy.wordcount.sources.socket.SocketWordCount\" -Dexec.args=\"localhost 9999\""
    echo "3. 在终端1输入文本，观察终端2的输出"
    echo ""
    echo "📖 更多信息请参考:"
    echo "- docs/README.md: 项目总体介绍"
    echo "- docs/README_Socket_WordCount.md: Socket WordCount详细说明"
else
    echo "✗ 编译失败"
    exit 1
fi