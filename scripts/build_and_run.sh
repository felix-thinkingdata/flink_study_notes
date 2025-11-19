#!/bin/bash

# Flink WordCount 构建和运行脚本
# 提供不同WordCount实现的便捷构建和运行方式

echo "=== Flink WordCount 构建和运行脚本 ==="
echo ""

# 显示可用的选项
show_options() {
    echo "可用的 WordCount 实现："
    echo "1. Socket WordCount (基础版本 - 实时词频统计)"
    echo "2. Socket Window WordCount (窗口版本 - 每5秒统计一次)"
    echo "3. Custom Source WordCount (自定义数据源)"
    echo "4. Development 构建 (开发环境)"
    echo "5. Assembly 构建 (生产环境)"
    echo "6. 清理构建缓存"
    echo "7. 退出"
    echo ""
}

# 构建 Socket WordCount
build_socket_wordcount() {
    echo "🔨 构建 Socket WordCount..."
    mvn clean package -Psocket-wordcount
    if [ $? -eq 0 ]; then
        echo "✅ 构建成功！"
        echo "📦 JAR文件: target/flink_study_notes-0.1.jar"
        echo ""
        echo "🚀 运行命令："
        echo "   java -jar target/flink_study_notes-0.1.jar localhost 9999"
        echo ""
        echo "💡 使用说明："
        echo "   1. 在第一个终端运行: nc -lk 9999"
        echo "   2. 在第二个终端运行上面的java命令"
        echo "   3. 在第一个终端输入文本进行测试"
    else
        echo "❌ 构建失败！"
    fi
}

# 构建 Socket Window WordCount
build_socket_window_wordcount() {
    echo "🔨 构建 Socket Window WordCount..."
    mvn clean package -Psocket-window-wordcount
    if [ $? -eq 0 ]; then
        echo "✅ 构建成功！"
        echo "📦 JAR文件: target/flink_study_notes-0.1-socket-window.jar"
        echo ""
        echo "🚀 运行命令："
        echo "   java -jar target/flink_study_notes-0.1-socket-window.jar localhost 9999"
        echo ""
        echo "💡 使用说明："
        echo "   1. 在第一个终端运行: nc -lk 9999"
        echo "   2. 在第二个终端运行上面的java命令"
        echo "   3. 在第一个终端输入文本，每5秒会输出一次统计结果"
    else
        echo "❌ 构建失败！"
    fi
}

# 构建 Custom Source WordCount
build_custom_source_wordcount() {
    echo "🔨 构建 Custom Source WordCount..."
    mvn clean package -Pcustom-source-wordcount
    if [ $? -eq 0 ]; then
        echo "✅ 构建成功！"
        echo "📦 JAR文件: target/flink_study_notes-0.1-custom-source.jar"
        echo ""
        echo "🚀 运行命令："
        echo "   java -jar target/flink_study_notes-0.1-custom-source.jar"
        echo ""
        echo "💡 说明："
        echo "   这个程序使用自定义数据源，不需要socket连接"
        echo "   程序会自动生成测试数据并进行词频统计"
    else
        echo "❌ 构建失败！"
    fi
}

# Development 构建
build_development() {
    echo "🔨 构建 Development 版本..."
    mvn clean package -Pdevelopment
    if [ $? -eq 0 ]; then
        echo "✅ 构建成功！"
        echo "📦 JAR文件: target/flink_study_notes-0.1.jar"
        echo ""
        echo "🚀 运行命令："
        echo "   java -jar target/flink_study_notes-0.1.jar localhost 9999"
        echo ""
        echo "💡 Maven 直接运行方式："
        echo "   mvn -Pdevelopment compile exec:java -Dexec.mainClass=\"cn.flinkstudy.wordcount.sources.socket.SocketWordCount\" -Dexec.args=\"localhost 9999\""
    else
        echo "❌ 构建失败！"
    fi
}

# Assembly 构建
build_assembly() {
    echo "🔨 构建 Assembly 版本..."
    mvn clean package -Passembly
    if [ $? -eq 0 ]; then
        echo "✅ 构建成功！"
        echo "📦 JAR文件: target/flink_study_notes-0.1.jar"
        echo ""
        echo "🚀 运行命令："
        echo "   java -jar target/flink_study_notes-0.1.jar localhost 9999"
        echo ""
        echo "💡 说明："
        echo "   这是生产环境的构建配置，包含所有依赖"
    else
        echo "❌ 构建失败！"
    fi
}

# 清理构建缓存
clean_build() {
    echo "🧹 清理构建缓存..."
    mvn clean
    if [ $? -eq 0 ]; then
        echo "✅ 清理完成！"
    else
        echo "❌ 清理失败！"
    fi
}

# 主循环
while true; do
    show_options
    read -p "请选择要构建的选项 (1-7): " choice

    case $choice in
        1)
            build_socket_wordcount
            ;;
        2)
            build_socket_window_wordcount
            ;;
        3)
            build_custom_source_wordcount
            ;;
        4)
            build_development
            ;;
        5)
            build_assembly
            ;;
        6)
            clean_build
            ;;
        7)
            echo "👋 再见！"
            exit 0
            ;;
        *)
            echo "❌ 无效选项，请选择 1-7"
            ;;
    esac

    echo ""
    read -p "按 Enter 键继续..."
    echo ""
done