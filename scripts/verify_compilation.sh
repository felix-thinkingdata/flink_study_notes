#!/bin/bash

echo "=== Flink Socket WordCount 编译验证 ==="
echo ""

# 检查Java文件是否存在
echo "检查源文件..."
if [ -f "src/main/java/cn/demo/SocketWordCount.java" ]; then
    echo "✓ SocketWordCount.java 存在"
else
    echo "✗ SocketWordCount.java 不存在"
    exit 1
fi

if [ -f "src/main/java/cn/demo/SocketWindowWordCount.java" ]; then
    echo "✓ SocketWindowWordCount.java 存在"
else
    echo "✗ SocketWindowWordCount.java 不存在"
    exit 1
fi

echo ""
echo "编译项目..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ 编译成功！"
    echo ""
    echo "项目结构："
    echo "- SocketWordCount.java: 基础版本，实时统计词频"
    echo "- SocketWindowWordCount.java: 窗口版本，每5秒统计一次"
    echo "- test_socket_wordcount.sh: 测试脚本"
    echo "- README_Socket_WordCount.md: 使用说明"
    echo ""
    echo "要运行程序，请参考 README_Socket_WordCount.md 中的说明"
    echo ""
    echo "基本运行步骤："
    echo "1. 终端1: nc -lk 9999"
    echo "2. 终端2: mvn compile exec:java -Dexec.mainClass=\"cn.demo.SocketWordCount\" -Dexec.args=\"localhost 9999\""
    echo "3. 在终端1输入文本，观察终端2的输出"
else
    echo "✗ 编译失败"
    exit 1
fi