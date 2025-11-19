# 脚本文件说明

本目录包含了用于测试和运行Flink示例程序的脚本文件。

## 脚本列表

### test_socket_wordcount.sh
**功能**: Socket WordCount程序的测试脚本
**用法**:
```bash
./scripts/test_socket_wordcount.sh
```

**描述**:
- 提供Socket WordCount程序的完整测试流程说明
- 检查端口可用性
- 可选择启动socket服务器
- 提供测试数据示例

### verify_compilation.sh
**功能**: 项目编译验证脚本
**用法**:
```bash
./scripts/verify_compilation.sh
```

**描述**:
- 检查源文件是否存在
- 执行项目编译
- 验证编译结果
- 显示项目结构信息

## 使用方法

1. 确保脚本有执行权限：
   ```bash
   chmod +x scripts/*.sh
   ```

2. 运行对应的脚本：
   ```bash
   ./scripts/脚本名.sh
   ```

## 注意事项

- 脚本需要在项目根目录下运行
- 确保已安装必要的工具（nc, java, mvn等）
- 如果遇到权限问题，请使用`chmod +x`添加执行权限