

#!/bin/bash

# Socket WordCount 测试脚本
# 用于演示基于Socket Source的Flink WordCount程序

echo "=== Flink Socket WordCount 测试脚本 ==="
echo ""
echo "这个脚本将帮助你测试Socket WordCount程序。"
echo "请按照以下步骤操作："
echo ""

# 检查端口是否被占用
check_port() {

    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "警告: 端口 $port 已被占用"
        return 1
    else
        return 0
    fi
}

# 显示使用说明
show_instructions() {
    echo "=== 使用步骤 ==="
    echo ""
    echo "1. 在第一个终端窗口中启动socket服务器："
    echo "   nc -lk 9999"
    echo ""
    echo "2. 在第二个终端窗口中运行Flink程序（基础版本）："
    echo "   mvn compile exec:java -Dexec.mainClass=\"cn.flinkstudy.wordcount.sources.socket.SocketWordCount\" -Dexec.args=\"localhost 9999\""
    echo ""
    echo "   或者运行窗口版本（每5秒统计一次）："
    echo "   mvn compile exec:java -Dexec.mainClass=\"cn.flinkstudy.wordcount.sources.socket.SocketWindowWordCount\" -Dexec.args=\"localhost 9999\""
    echo ""
    echo "3. 在第一个终端窗口中输入文本，例如："
    echo "   hello world"
    echo "   hello flink"
    echo "   world hello"
    echo ""
    echo "4. 观察第二个终端窗口中的词频统计结果"
    echo ""
    echo "=== 测试数据示例 ==="
    echo ""
    echo "你可以复制以下测试文本到socket终端："
    echo "hello world flink streaming"
    echo "hello apache flink"
    echo "real time processing"
    echo "big data analytics"
    echo "hello world"
    echo "flink streaming processing"
    echo ""
    echo "=== 停止程序 ==="
    echo ""
    echo "按 Ctrl+C 停止socket服务器或Flink程序"
    echo ""
}

# 检查端口9999是否可用
if check_port 9999; then
    echo "端口9999可用"
else
    echo "端口9999已被占用，请尝试其他端口或停止占用该端口的进程"
fi

show_instructions

echo "=== 一键启动测试（可选）==="
echo ""
read -p "是否现在启动socket服务器？(y/n): " start_server

if [[ $start_server == "y" || $start_server == "Y" ]]; then
    echo "正在启动socket服务器，端口9999..."
    echo "请在另一个终端运行Flink程序"
    echo "按 Ctrl+C 停止服务器"
    echo ""
    nc -lk 9999
fi
