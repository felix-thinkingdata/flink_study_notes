# Flink 构建 Profiles 使用指南

本项目已配置了三个Maven profiles来简化本地开发和集群部署之间的依赖管理。

## 可用 Profiles

### 1. Development Profile (默认)
**用途**: 本地IDE调试和运行
**激活**: 默认激活，无需额外参数
**依赖特点**:
- Flink核心库 (`flink-streaming-java`, `flink-clients`) scope = `compile`
- 日志依赖 scope = `compile`
- 连接器依赖 scope = `compile`
- 生成包含所有依赖的uber-jar

**使用命令**:
```bash
# 编译 (默认使用development profile)
mvn clean compile

# 打包 (生成包含所有依赖的jar)
mvn clean package

# 直接运行
mvn clean package exec:java -Dexec.mainClass="cn.demo.ParallelStreamingWordCount"
```

### 2. Production Profile
**用途**: 集群部署 (Flink集群已提供核心库)
**激活**: `-Pproduction`
**依赖特点**:
- Flink核心库 scope = `provided` (不打包到jar中)
- 日志依赖 scope = `provided`
- 连接器依赖 scope = `compile` (需要打包)
- 生成轻量级jar，仅包含业务代码和连接器

**使用命令**:
```bash
# 使用production profile编译
mvn clean compile -Pproduction

# 使用production profile打包 (生成轻量级jar)
mvn clean package -Pproduction

# 上传到集群后运行
# flink run -c cn.demo.ParallelStreamingWordCount your-job.jar
```

### 3. Assembly Profile
**用途**: 创建包含所有依赖的uber-jar (类似于development)
**激活**: `-Passembly`
**依赖特点**:
- 所有依赖 scope = `compile`
- 生成包含所有依赖的完整jar

**使用命令**:
```bash
# 创建uber-jar
mvn clean package -Passembly
```

## 常见使用场景

### 场景1: 本地开发调试
```bash
# 默认development profile，包含所有依赖
mvn clean package
java -jar target/flink_study_notes-0.1-shaded.jar
```

### 场景2: 提交到Flink集群
```bash
# 使用production profile，生成轻量级jar
mvn clean package -Pproduction
# 提交: flink run -c cn.demo.ParallelStreamingWordCount target/flink_study_notes-0.1.jar
```

### 场景3: 快速切换profiles
```bash
# 查看当前激活的profiles
mvn help:active-profiles

# 查看所有可用profiles
mvn help:all-profiles
```

## 优势

1. **无需手动修改scope**: 自动根据环境调整依赖scope
2. **避免依赖冲突**: 集群环境使用集群提供的Flink库
3. **减小jar大小**: Production profile生成的jar更小，上传更快
4. **IDE友好**: Development profile适合IDE直接运行
5. **灵活配置**: 可根据需要扩展更多profiles

## IDE配置

### IntelliJ IDEA
1. 打开Maven Projects面板
2. 找到Profiles节点
3. 勾选需要激活的profile
4. 执行Maven命令时会自动使用选中的profile

### Eclipse
1. 右键项目 → Maven → Select Profiles
2. 勾选需要的profile
3. 执行Maven命令

## 注意事项

- Development profile是默认的，不需要特别指定
- 集群部署时记得使用`-Pproduction`
- 可以同时激活多个profiles: `-Pproduction,assembly`
- 依赖冲突时，后激活的profile会覆盖先激活的profile配置