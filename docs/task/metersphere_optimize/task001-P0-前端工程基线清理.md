# task001 - P0 前端工程基线清理

> **阶段**：P0  
> **预计工期**：1 天  
> **前置依赖**：无  
> **阻塞任务**：task002、task003、task005  

---

## 1. 任务目标

清理影响维护效率的低风险前端工程问题，建立后续优化基线，为启动链路拆分、UI 治理、类型治理提供可靠起点。

---

## 2. 任务清单

### 2.1 修复核心文件中文注释乱码

重点文件：

| 文件 | 说明 |
|------|------|
| `frontend/config/vite.config.base.ts` | Vite 基础配置注释 |
| `frontend/src/main.ts` | 应用启动入口注释 |
| `frontend/src/App.vue` | 全局初始化注释 |
| `frontend/src/assets/style/global.less` | 全局样式注释 |

要求：

- 统一保存为 UTF-8。
- 仅修复注释和明显文案乱码，不改变运行逻辑。
- 对无法确认原意的注释，改写为准确的简短说明。

### 2.2 校验 dev proxy rewrite 规则

重点文件：

`frontend/config/vite.config.dev.ts`

重点检查路径：

| proxy key | 检查点 |
|-----------|--------|
| `/ws` | rewrite 是否错误匹配 `/front/ws` |
| `/file` | rewrite 是否错误匹配 `/front/file` |
| `/project` | rewrite 是否错误匹配 `/front/project` |
| `/attachment` | 与实际本地访问路径是否一致 |
| `/test-plan/report` | 与真实接口路径是否一致 |

### 2.3 执行基线检查

命令建议：

```bash
cd frontend
pnpm type:check
pnpm lint
```

如本地环境不满足执行条件，需要记录原因。

### 2.4 输出治理基线数据

统计并记录：

- `any` 数量。
- `console.*` 数量。
- `:deep()` 数量。
- `!important` 数量。
- 前 20 个超大 `.vue/.ts/.tsx` 文件。

---

## 3. 验收标准

- [ ] 核心前端文件不再出现中文注释乱码。
- [ ] dev proxy rewrite 与实际 proxy key 一致。
- [ ] 输出一份前端治理基线数据。
- [ ] `type:check`、`lint` 已执行或明确记录阻塞原因。
- [ ] 不改变业务功能行为。

---

## 4. 任务状态

| 字段 | 值 |
|------|-----|
| 状态 | 未开始 |
| 开始日期 | |
| 完成日期 | |
