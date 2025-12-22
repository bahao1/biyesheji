# AI Kei助手集成计划

## 1. 需求分析

* 集成AI Kei助手到现有校园墙系统

* 保留现有AI功能（通义千问）

* 支持AI助手切换

* 确保前后端兼容

## 2. 技术方案

### 2.1 后端修改

1. **AIService.java**：扩展现有服务支持多AI助手

   * 添加AI助手类型枚举

   * 实现AI Kei API调用方法

   * 支持根据配置或请求参数选择AI助手

2. **AIController.java**：更新API支持AI助手参数

   * 修改ChatRequest添加aiType字段

   * 更新聊天接口支持选择AI助手

3. **配置文件**：添加AI Kei配置

   * 添加ai.kei.api.key配置项

   * 添加ai.kei.api.url配置项

### 2.2 前端修改

1. **ChatAssistantView\.vue**：添加AI助手切换功能

   * 添加AI助手选择下拉框

   * 保存用户选择的AI助手

   * 更新聊天请求添加aiType参数

2. **ai.ts**：更新API调用支持AI类型

   * 修改chatAssistant函数添加aiType参数

   * 更新请求体结构

3. **样式优化**：适配AI Kei的UI要求

   * 调整聊天界面样式

   * 优化响应式设计

## 3. 实现步骤

### 3.1 后端实现

1. 修改AIService.java，添加AI Kei支持
2. 更新AIController.java，支持AI类型参数
3. 添加AI Kei配置到application.properties
4. 测试后端API功能

### 3.2 前端实现

1. 修改ai.ts，更新API调用
2. 更新ChatAssistantView\.vue，添加AI助手切换
3. 测试前端聊天功能
4. 优化UI样式

## 4. 测试验证

1. 测试AI Kei助手功能
2. 测试现有AI功能兼容性
3. 测试AI助手切换功能
4. 测试异常处理

## 5. 风险评估

* **API兼容性**：确保现有功能不受影响

* **配置管理**：妥善管理多个AI密钥

* **性能影响**：监控AI调用性能

* **错误处理**：完善异常处理机制

## 6. 后续优化

* 支持更多AI助手

* 添加AI助手评分功能

* 优化AI回复质量

* 添加AI使用统计

## 7. 文件修改清单

### 后端文件

* `src/main/java/com/example/biyesheji/service/AIService.java`

* `src/main/java/com/example/biyesheji/controller/AIController.java`

* `src/main/resources/application.properties`

### 前端文件

* `campus-wall-frontend/src/api/ai.ts`

* `campus-wall-frontend/src/views/ChatAssistantView.vue`

