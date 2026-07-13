# task004 - P1 ms-table 拆分与可维护性优化

> **阶段**：P1  
> **预计工期**：3 天  
> **前置依赖**：[task006](task006-P1-接口与表格类型治理.md)  
> **阻塞任务**：[task009](task009-P2-大数据表格与树组件体验优化.md)、[task010](task010-P2-前端关键组件测试补齐.md)  

---

## 1. 任务目标

降低 `frontend/src/components/pure/ms-table/base-table.vue` 单文件复杂度，减少后续表格需求改动风险，同时保持现有业务页面兼容。

---

## 2. 当前问题

`base-table.vue` 当前承担职责过多：

- 列配置。
- 行选择、全选、跨页选择。
- 分页。
- 批量操作。
- 筛选。
- 排序。
- 拖拽排序。
- 行内编辑。
- 标签溢出自适应。
- 列宽调整。
- 大量第三方表格样式覆盖。

其中标签溢出逻辑依赖全局 `document.querySelectorAll`，在多表格、多弹窗、多抽屉场景下存在维护风险。

---

## 3. 任务清单

### 3.1 拆分组合式逻辑

建议新增目录：

```text
frontend/src/components/pure/ms-table/hooks/
```

建议拆分：

| Hook | 职责 |
|------|------|
| `useTableSelection` | 单选、多选、全选、半选、禁用选择 |
| `useColumnSettings` | 列初始化、列宽、列显隐、设置弹窗 |
| `useTableFilter` | 默认筛选、筛选状态、重置筛选 |
| `useTableSort` | 排序字段映射、排序事件 |
| `useTableDrag` | 拖拽排序参数生成 |
| `useInlineEdit` | 行内编辑状态和提交 |
| `useTagOverflow` | 标签溢出计算 |

### 3.2 替换全局 DOM 查询

将：

```ts
document.querySelectorAll('.tag-group-class')
```

改为：

- 组件局部 ref。
- `ResizeObserver`。
- 或每个单元格独立的溢出检测组件。

### 3.3 保持对外 API 兼容

不得破坏：

- props。
- emits。
- slots。
- `defineExpose({ initColumn })`。
- 业务页面现有 `tableKey`、`selectedKeys`、`excludeKeys` 用法。

### 3.4 保持现有交互

必须验证：

- 行选择 / 全选。
- 跨页全选。
- 分页切换。
- 批量操作栏。
- 列设置。
- 列宽拖拽。
- 筛选。
- 排序。
- 树形表格展开。
- 标签列展示。

---

## 4. 验收标准

- [ ] 原有业务页面无需大规模改动。
- [ ] `base-table.vue` 代码量显著下降。
- [ ] 表格选择、全选、分页、筛选、拖拽、列宽调整正常。
- [ ] 不再依赖全局 DOM 扫描处理标签溢出。
- [ ] 至少补充选择、分页、筛选、列设置相关测试或测试说明。

---

## 5. 任务状态

| 字段 | 值 |
|------|-----|
| 状态 | 未开始 |
| 开始日期 | |
| 完成日期 | |
