# task006 - P1 接口与表格类型治理

> **阶段**：P1  
> **预计工期**：2 天  
> **前置依赖**：[task003](task003-P0-通用错误处理与日志治理.md)  
> **阻塞任务**：[task004](task004-P1-ms-table拆分与可维护性优化.md)、[task010](task010-P2-前端关键组件测试补齐.md)  

---

## 1. 任务目标

减少无约束 `any`，优先治理接口层、表格列配置、分页查询、动态字段模型，提升后续重构的类型安全和 IDE 提示质量。

---

## 2. 任务范围

优先模块：

| 模块 | 路径 |
|------|------|
| 请求层 | `frontend/src/api/http/*` |
| API modules | `frontend/src/api/modules/*` |
| 表格类型 | `frontend/src/components/pure/ms-table/type.ts` |
| 用例管理 | `frontend/src/views/case-management/*` |
| 接口管理 | `frontend/src/views/api-test/*` |
| 测试计划 | `frontend/src/views/test-plan/*` |
| 缺陷管理 | `frontend/src/views/bug-management/*` |

---

## 3. 任务清单

### 3.1 建立通用类型

建议补充：

```ts
interface PageRequest {
  current: number;
  pageSize: number;
}

interface PageResponse<T> {
  list: T[];
  total: number;
}

type SortParams = Record<string, 'asc' | 'desc'>;
type FilterParams = Record<string, Array<string | number | boolean>>;
```

### 3.2 治理 API 返回类型

优先给高频接口补充：

- 列表查询参数。
- 分页响应。
- 新建/编辑请求体。
- 删除/启用/禁用响应。
- 动态字段响应。

### 3.3 治理表格列配置

增强：

- `MsTableColumn`
- `MsTableColumnData`
- `MsPaginationI`
- `BatchActionConfig`
- 筛选配置类型。
- 行选择禁用配置类型。

### 3.4 控制新增 any

新增规则：

- 新代码不得新增无说明的 `any`。
- 必须使用 `unknown` 或明确业务类型。
- 无法立刻治理的历史类型需加 TODO 和原因。

---

## 4. 验收标准

- [ ] 高频模块新增/修改代码不再依赖裸 `any`。
- [ ] `vue-tsc --noEmit` 通过或失败项可解释。
- [ ] 表格列配置具备更好的类型提示。
- [ ] 接口请求行为不变。
- [ ] 为 `ms-table` 拆分提供必要类型基础。

---

## 5. 任务状态

| 字段 | 值 |
|------|-----|
| 状态 | 未开始 |
| 开始日期 | |
| 完成日期 | |
