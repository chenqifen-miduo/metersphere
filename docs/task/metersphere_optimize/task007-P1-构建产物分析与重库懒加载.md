# task007 - P1 构建产物分析与重库懒加载

> **阶段**：P1  
> **预计工期**：2 天  
> **前置依赖**：无  
> **阻塞任务**：[task010](task010-P2-前端关键组件测试补齐.md)  

---

## 1. 任务目标

针对重库和重功能模块做构建分析与按需加载，降低首屏包体风险，减少无关页面加载 PDF、脑图、富文本、代码编辑器、图表等大依赖。

> 本任务不包含生产 legacy 插件重新评估，不调整 legacy 插件相关配置。

---

## 2. 涉及文件

| 文件 | 说明 |
|------|------|
| `frontend/config/vite.config.prod.ts` | manualChunks、构建配置 |
| `frontend/config/plugin/visualizer.ts` | bundle 分析 |
| `frontend/src/router/routes/*` | 路由级懒加载 |
| 重功能页面 | PDF、脑图、富文本、代码编辑器、图表 |

---

## 3. 重点依赖

需要分析使用位置：

| 依赖 | 典型用途 |
|------|----------|
| `monaco-editor` | 代码编辑器 |
| `echarts` / `vue-echarts` | 图表 |
| `@tiptap/*` | 富文本 |
| `@halo-dev/richtext-editor` | 富文本 |
| `html2canvas-pro` | 截图 / PDF |
| `jspdf` / `jspdf-autotable` | PDF 导出 |
| `@7polo/kity` | 脑图 |
| `@7polo/kityminder-core` | 脑图 |

---

## 4. 任务清单

### 4.1 生成构建分析报告

命令建议：

```bash
cd frontend
pnpm report
```

输出：

- 最大 chunks。
- 最大依赖。
- 首屏路径依赖。
- 可懒加载模块清单。

### 4.2 重功能模块懒加载

优先处理：

- PDF 导出页面。
- 脑图编辑器。
- 富文本编辑器。
- 代码编辑器。
- 工作台图表。
- 报表详情。

### 4.3 manualChunks 优化

在不调整 legacy 插件的前提下，评估是否细分：

- `chart`
- `codeEditor`
- `richText`
- `pdf`
- `minder`

### 4.4 输出前后对比

记录：

- 构建总大小。
- gzip 后大小。
- brotli 后大小。
- 首屏相关 chunk 数量。
- 最大 chunk 大小。

---

## 5. 验收标准

- [ ] 生成 bundle 分析报告。
- [ ] 重功能模块不进入无关首屏路径。
- [ ] 构建通过。
- [ ] 输出构建前后包体对比。
- [ ] 不调整 legacy 插件相关配置。

---

## 6. 任务状态

| 字段 | 值 |
|------|-----|
| 状态 | 未开始 |
| 开始日期 | |
| 完成日期 | |
