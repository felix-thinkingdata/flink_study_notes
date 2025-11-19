# Socket WordCount 调试问题解决报告

## 🔍 问题总结

在重构包结构后，Socket WordCount程序遇到了运行时错误，经过分析和修复，问题已成功解决。

## 🐛 发现的问题

### 主要错误
1. **ClassNotFoundException**: `org.apache.flink.api.common.ExecutionConfig`
2. **NoClassDefFoundError**: `org/apache/logging/log4j/core/impl/ThrowableProxy`

### 错误原因
Maven shade插件在assembly profile中排除了slf4j和log4j依赖，导致本地运行时找不到必要的日志类。

## 🔧 解决方案

### 1. 修复POM配置
在`assembly` profile中：
- ✅ 添加了slf4j依赖：`slf4j-api:1.7.36`
- ✅ 确保log4j依赖包含在JAR中
- ✅ 移除了对slf4j和log4j的排除配置
- ✅ 更新了主类配置指向新的包结构

### 2. 更新依赖配置
```xml
<!-- 添加slf4j和log4j依赖 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.36</version>
    <scope>compile</scope>
</dependency>
```

### 3. 修复shade插件配置
```xml
<!-- Assembly profile中的配置 -->
<artifactSet>
    <excludes>
        <exclude>org.apache.flink:flink-shaded-force-shading</exclude>
        <exclude>com.google.code.findbugs:jsr305</exclude>
        <!-- 不排除slf4j和log4j，因为本地运行需要 -->
    </excludes>
</artifactSet>

<!-- 默认shade插件配置也需要修复 -->
<artifactSet>
    <excludes>
        <exclude>org.apache.flink:flink-shaded-force-shading</exclude>
        <exclude>com.google.code.findbugs:jsr305</exclude>
        <!-- 移除了slf4j和log4j的排除 -->
    </excludes>
</artifactSet>
```

### 4. 更新主类配置
```xml
<!-- 更新默认shade插件的主类配置 -->
<transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
    <mainClass>cn.flinkstudy.wordcount.sources.socket.SocketWordCount</mainClass>
</transformer>
```

## ✅ 验证结果

### 成功步骤
1. **重新构建JAR包**：
   ```bash
   # 方法1：使用assembly profile（推荐）
   mvn clean package -Passembly

   # 方法2：使用development profile（修复后）
   mvn clean package -Pdevelopment
   ```

2. **成功运行程序**：
   ```bash
   # 使用构建好的JAR包
   java -jar target/flink_study_notes-0.1.jar localhost 9999
   ```

3. **程序启动正常**：
   - ✅ Flink集群成功初始化
   - ✅ 作业成功提交
   - ✅ Socket连接成功建立
   - ✅ 数据流处理正常
   - ✅ 日志框架正常工作（无log4j错误）

### 测试验证
- ✅ 程序可以正常启动和连接Socket
- ✅ 数据传输和接收正常
- ✅ Flink集群状态正常

## 📝 运行指南

### 方法1：使用JAR包（推荐）
```bash
# 1. 启动Socket服务器
nc -lk 9999

# 2. 运行程序
java -jar target/flink_study_notes-0.1.jar localhost 9999

# 3. 在Socket终端输入数据
hello flink world
apache flink streaming
```

### 方法2：使用Maven（Development Profile）
```bash
# 1. 启动Socket服务器
nc -lk 9999

# 2. 使用development profile运行
mvn -Pdevelopment compile exec:java -Dexec.mainClass="cn.flinkstudy.wordcount.sources.socket.SocketWordCount" -Dexec.args="localhost 9999"
```

## 🎯 关键学习点

1. **依赖管理**：理解Maven profiles和依赖范围的重要性
2. **Shade插件配置**：正确配置uber-jar构建，避免排除必要的依赖
3. **运行环境**：区分本地开发环境和生产环境的依赖配置
4. **调试技巧**：通过错误日志快速定位问题根源

## 📋 文件更新清单

- ✅ `pom.xml` - 修复assembly profile配置
- ✅ `target/flink_study_notes-0.1.jar` - 重新构建的完整JAR包
- ✅ `docs/SOCKET_DEBUG_REPORT.md` - 本调试报告

## 🚀 项目状态

重构后的项目现在可以正常运行：
- ✅ 新包结构工作正常
- ✅ Socket WordCount程序功能完整
- ✅ 依赖配置正确
- ✅ 编译和运行流程顺畅

**重构成功完成！** 🎉