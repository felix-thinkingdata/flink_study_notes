# Flink WordCount 构建指南

## 🚀 快速开始

### 1. 使用便捷脚本（推荐）
```bash
# 运行交互式构建脚本
./scripts/build_and_run.sh
```

### 2. 直接使用Maven命令

#### Socket WordCount（基础版本）
```bash
# 构建
mvn clean package -Psocket-wordcount

# 运行
java -jar target/flink_study_notes-0.1.jar localhost 9999
```

#### Socket Window WordCount（窗口版本）
```bash
# 构建
mvn clean package -Psocket-window-wordcount

# 运行
java -jar target/flink_study_notes-0.1-socket-window.jar localhost 9999
```

#### Custom Source WordCount（自定义数据源）
```bash
# 构建
mvn clean package -Pcustom-source-wordcount

# 运行
java -jar target/flink_study_notes-0.1-custom-source.jar
```

#### Development构建（开发环境）
```bash
# 构建
mvn clean package -Pdevelopment

# 运行
java -jar target/flink_study_notes-0.1.jar localhost 9999

# 或使用Maven直接运行
mvn -Pdevelopment compile exec:java -Dexec.mainClass="cn.flinkstudy.wordcount.sources.socket.SocketWordCount" -Dexec.args="localhost 9999"
```

#### Assembly构建（生产环境）
```bash
# 构建
mvn clean package -Passembly

# 运行
java -jar target/flink_study_notes-0.1.jar localhost 9999
```

## 📋 Maven Profiles说明

| Profile | 描述 | 主类 | 输出文件 |
|---------|------|------|----------|
| `socket-wordcount` | Socket基础版本 | `cn.flinkstudy.wordcount.sources.socket.SocketWordCount` | `flink_study_notes-0.1.jar` |
| `socket-window-wordcount` | Socket窗口版本 | `cn.flinkstudy.wordcount.sources.socket.SocketWindowWordCount` | `flink_study_notes-0.1-socket-window.jar` |
| `custom-source-wordcount` | 自定义数据源 | `cn.flinkstudy.wordcount.sources.custom.CustomSourceWordCount` | `flink_study_notes-0.1-custom-source.jar` |
| `development` | 开发环境 | `cn.flinkstudy.wordcount.sources.socket.SocketWordCount` | `flink_study_notes-0.1.jar` |
| `assembly` | 生产环境 | `cn.flinkstudy.wordcount.sources.socket.SocketWordCount` | `flink_study_notes-0.1.jar` |

## 🛠️ 使用说明

### Socket版本（基础和窗口）
1. **启动Socket服务器**：
   ```bash
   nc -lk 9999
   ```

2. **运行Flink程序**（选择对应版本）：
   ```bash
   # 基础版本
   java -jar target/flink_study_notes-0.1.jar localhost 9999

   # 窗口版本
   java -jar target/flink_study_notes-0.1-socket-window.jar localhost 9999
   ```

3. **在Socket终端输入测试数据**：
   ```
   hello world
   hello flink
   streaming wordcount
   ```

4. **观察输出**：
   - 基础版本：实时输出词频统计
   - 窗口版本：每5秒输出一次统计结果

### Custom Source版本
```bash
# 直接运行，不需要socket
java -jar target/flink_study_notes-0.1-custom-source.jar
```

## 🧹 清理构建缓存
```bash
# 清理所有构建文件
mvn clean

# 或使用脚本
./scripts/build_and_run.sh
# 选择选项6：清理构建缓存
```

## 📁 项目结构

```
src/main/java/cn/flinkstudy/
├── wordcount/
│   ├── sources/
│   │   ├── socket/
│   │   │   ├── SocketWordCount.java          # Socket基础版本
│   │   │   └── SocketWindowWordCount.java    # Socket窗口版本
│   │   └── custom/
│   │       ├── CustomSourceWordCount.java    # 自定义数据源版本
│   │       └── ParallelCustomSourceWordCount.java
│   └── core/
│       └── BasicWordCount.java               # 核心工具类
├── basic/
│   ├── batch/
│   │   └── DataBatchJob.java                 # 批处理示例
│   └── streaming/
│       └── DataStreamJob.java                # 流处理示例
└── utils/
    └── sources/
        └── SourceUtils.java                  # 数据源工具类
```

## 🔧 故障排除

### 常见问题

1. **端口被占用**
   ```bash
   # 检查端口占用
   lsof -i :9999

   # 或使用其他端口
   java -jar target/flink_study_notes-0.1.jar localhost 8888
   ```

2. **构建失败**
   ```bash
   # 清理并重新构建
   mvn clean package -P<profile-name>
   ```

3. **ClassNotFoundException**
   - 确保使用了正确的profile
   - 检查依赖是否完整包含在JAR中

### 日志配置
所有profiles都配置了完整的日志支持，包括：
- SLF4J API 1.7.36
- Log4j2 2.17.1
- 完整的日志实现

## 📚 学习路径

1. **新手入门**：
   - 先运行 `socket-wordcount` 了解基本概念
   - 然后尝试 `socket-window-wordcount` 学习窗口操作

2. **进阶学习**：
   - 研究 `custom-source-wordcount` 了解自定义数据源
   - 查看 `BasicWordCount.java` 学习核心逻辑

3. **生产部署**：
   - 使用 `assembly` profile构建生产包
   - 配置集群环境和参数

## 🎯 最佳实践

1. **开发阶段**：使用 `development` profile
2. **测试特定功能**：使用专门的profiles（如 `socket-window-wordcount`）
3. **生产部署**：使用 `assembly` profile
4. **日常构建**：使用 `./scripts/build_and_run.sh` 脚本