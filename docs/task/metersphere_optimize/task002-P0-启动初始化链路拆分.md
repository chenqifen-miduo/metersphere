# task002 - P0 启动初始化链路拆分

> **阶段**：P0  
> **预计工期**：1.5 天  
> **前置依赖**：[task001](task001-P0-前端工程基线清理.md)  
> **阻塞任务**：无  

---

## 1. 任务目标

降低首屏挂载前阻塞，让“必须同步完成”的初始化和“可延后执行”的初始化分层，提升刷新、登录、进入工作台时的稳定性和可感知速度。

---

## 2. 涉及文件

| 文件 | 说明 |
|------|------|
| `frontend/src/main.ts` | Vue 应用挂载、i18n、组件注册 |
| `frontend/src/App.vue` | 全局初始化、登录态检查、系统配置 |
| `frontend/src/router/guard` | 路由权限与登录态守卫 |
| `frontend/src/store/modules/app` | 系统配置、页面配置、项目列表 |
| `frontend/src/store/modules/user` | 登录态与用户本地配置 |

---

## 3. 当前链路梳理

需要梳理以下调用是否必须阻塞首屏：

| 调用 | 建议分类 |
|------|----------|
| `setupI18n` | 必须在应用可用前完成 |
| `getDefaultLocale` | 可缓存，首次可异步降级 |
| `getPublicKeyRequest` | 登录相关，按登录页需要加载 |
| `checkIsLogin` | 路由权限相关，进入受保护页面前完成 |
| `initSystemVersion` | 可延后 |
| `initSystemPackage` | 可延后或登录后执行 |
| `initPageConfig` | 可缓存，影响主题时需提供默认值 |
| `getProjectInfos` | 可登录后异步执行 |
| `getAISourceNameList` | 可延后 |

---

## 4. 任务清单

### 4.1 划分初始化阶段

建议拆为：

| 阶段 | 内容 |
|------|------|
| bootstrap | 创建 app、注册必要插件、加载 i18n 默认语言 |
| auth init | 检查 token、扫码回调、登录态 |
| app config init | 页面配置、系统版本、license、文件大小限制 |
| lazy init | 项目列表、AI 配置、本地执行配置 |

### 4.2 提供默认降级状态

- 页面配置未加载时使用默认主题和默认标题。
- 项目列表未加载时菜单不应空白崩溃。
- license 校验失败时保留明确提示。

### 4.3 减少重复 onBeforeMount

将 `App.vue` 中多个 `onBeforeMount` 初始化逻辑合并或拆到 composable：

- `useBootstrapInit`
- `useAuthCallback`
- `useAppLazyInit`

### 4.4 保持现有登录行为

必须覆盖：

- 普通账号密码登录。
- URL token 登录。
- 飞书 / 飞书套件扫码回调。
- 未登录访问白名单页面。
- 未登录访问受保护页面。

---

## 5. 验收标准

- [ ] 登录页可正常打开。
- [ ] 已登录用户刷新页面菜单和权限正常。
- [ ] 首屏挂载前阻塞请求数量减少。
- [ ] 系统配置、AI 配置、项目列表可延后加载且不导致页面空白。
- [ ] 扫码登录、URL token 登录行为不变。
- [ ] 初始化失败有明确用户反馈或可恢复降级。

---

## 6. 任务状态

| 字段 | 值 |
|------|-----|
| 状态 | 未开始 |
| 开始日期 | |
| 完成日期 | |
