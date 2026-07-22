# 团队 Git 协作规范

> 适用仓库：该项目  
> 分支策略：单分支 `master` + PR 工作流  
> 团队规模：28 人（7 人活跃提交，21 人只读）


## 一、快速导航

| 我想知道... | 跳转 |
|------------|------|
| 总体流程是什么？ | [二、工作流程](#二工作流程) |
| 提交信息怎么写？ | [三、Commit 规范](#三commit-规范) |
| 我是翻译/美术，怎么操作？ | [四、翻译/美术操作指南](#四翻译美术操作指南) |
| 我是开发，怎么操作？ | [五、开发操作指南](#五开发操作指南) |
| PR 怎么写？ | [六、PR 规范](#六pr-规范) |
| 遇到问题怎么办？ | [七、常见问题](#七常见问题) |


## 二、工作流程

### 2.1 整体流程图

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌────────┐ │
│   │ 从 master │ → │ 新建分支  │ → │ 提交改动  │ → │ 发起 PR │ │
│   │ 拉取最新  │    │ 开始开发  │    │ 写规范    │    │ 请求合并│ │
│   └──────────┘    └──────────┘    └──────────┘    └────────┘ │
│                                                         │      │
│                                                         ▼      │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌────────┐ │
│   │ 合并入   │ ← │ 审核通过  │ ← │ 代码审查  │ ← │ 指定   │ │
│   │ master   │    │ 点击合并  │    │ Review   │    │ Reviewer│ │
│   └──────────┘    └──────────┘    └──────────┘    └────────┘ │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 一句话概括

> **从 master 拉分支 → 改东西 → 提 PR → 审核通过 → 合入 master**


## 三、Commit 规范

### 3.1 格式

```bash
<type>(<scope>): <subject>
```

### 3.2 Type 类型（必填）

| 类型 | 说明 | 适用角色 |
|------|------|----------|
| `feat` | 新功能 / 新内容 | 开发、美术 |
| `fix` | 修复 Bug | 开发 |
| `docs` | 文档 / 文案修改 | 翻译 |
| `style` | 样式 / UI 调整（不影响逻辑） | 美术、开发 |
| `refactor` | 代码重构（不改变功能） | 开发 |
| `chore` | 构建 / 工具 / 依赖更新 | 开发 |
| `i18n` | 多语言 / 翻译相关 | 翻译 |

### 3.3 Scope（范围，可选）

标明影响的模块，例如：`ui`、`api`、`home`、`settings`、`README`、`assets`

### 3.4 Subject（标题，必填）

- 使用中文
- 不超过 **50 个字符**
- 使用祈使句：`添加`、`修复`、`更新`

### 3.5 正确示例

```bash
feat(api): 添加用户登录接口
fix(home): 修复首页列表加载失败
i18n(zh): 更新设置页中文翻译
style(ui): 统一按钮圆角大小
feat(assets): 添加空状态占位图
docs(README): 补充部署说明
```

### 3.6 错误示例

```bash
fix bug           ❌ 缺少 type 和 scope
修改              ❌ 不明确，看不出改了啥
update            ❌ 太模糊
feat: 加了个功能  ❌ 缺少 scope，描述不清晰
```


## 四、翻译/美术操作指南

> 本指南使用 **GitHub Desktop** 可视化工具，全程鼠标点击，无需记忆命令。

### 4.1 准备工作

#### 安装 GitHub Desktop

```
下载地址：https://desktop.github.com/
```

#### 登录账号

```
打开 GitHub Desktop → Settings → Accounts → Sign in → 登录 GitHub 账号
```


### 4.2 日常三步走

#### 第 1 步：拉取仓库

打开 GitHub Desktop 点击 `Clone a repository` → `URL` 页签 → 填写仓库地址 → 点击 `Clone`

> 如果已克隆，打开软件即可。

#### 第 2 步：拉取最新代码

```
打开软件后，点击顶部的 Fetch origin
→ 如果有更新，按钮会变成 Pull origin → 点击拉取最新代码
```

#### 第 3 步：提交改动

1. 在本地文件夹中修改文件
2. 切回 GitHub Desktop，左侧会显示改过的文件列表
3. 勾选要提交的文件（可全选）
4. 在左下角填写提交信息，格式：

```
<type>(<scope>): <描述>

示例：
i18n(zh): 更新首页中文文案
style(ui): 替换应用图标
```

5. 点击 **Commit to 当前分支名**
6. 点击 **Push origin**


### 4.3 发起 PR（Pull Request）

```
1. 打开浏览器 → 进入 GitHub 仓库页面
2. 点击 Pull requests 标签
3. 点击 New pull request
4. base: master ← compare: 你的分支名
5. 点击 Create pull request
6. 填写 PR 标题（参考第六节格式）
7. 点击 Create pull request 提交
8. 在群里 @开发同学 说“PR 已提交，麻烦审核”
```


### 4.4 图示示意

```
GitHub Desktop 界面

┌───────────────────────────────────────────────────────┐
│  [Fetch origin]  [Pull origin]  [Push origin]       │
│                                                       │
│  ┌─────────────────────────────────────────────────┐ │
│  │  Changes (3 files)                             │ │
│  │  ☑ src/locales/zh-CN.json                     │ │
│  │  ☑ src/locales/en-US.json                     │ │
│  │  ☑ README.md                                  │ │
│  └─────────────────────────────────────────────────┘ │
│                                                       │
│  Summary (required)                                  │
│  ┌─────────────────────────────────────────────────┐ │
│  │  i18n(zh): 更新首页翻译文案                    │ │
│  └─────────────────────────────────────────────────┘ │
│                                                       │
│  Description (optional)                              │
│  ┌─────────────────────────────────────────────────┐ │
│  │  补充了首页缺失的 3 条翻译                      │ │
│  └─────────────────────────────────────────────────┘ │
│                                                       │
│           [Commit to main]                           │
└───────────────────────────────────────────────────────┘
```


## 五、开发操作指南

### 5.1 日常命令速查

```bash
# 1. 拉取最新 master
git checkout master
git pull origin master

# 2. 新建功能分支
git checkout -b feat/功能描述

# 3. 提交改动
git add .
git commit -m "feat(api): 添加用户登录接口"

# 4. 推送到远程
git push origin feat/功能描述

# 5. 网页发起 PR → 合并 → 删除本地分支
git checkout master
git pull origin master
git branch -d feat/功能描述
```

### 5.2 分支命名规范

| 类型 | 格式 | 示例 |
|------|------|------|
| 新功能 | `feat/功能描述` | `feat/login-api` |
| Bug 修复 | `fix/问题描述` | `fix/home-crash` |
| 重构 | `refactor/模块名` | `refactor/store` |

### 5.3 Review 职责

| 提交类型 | 谁来 Review |
|----------|------------|
| 代码改动 | 另一位开发 |
| 翻译文案 | 开发确认 |
| 美术资源 | 开发确认 |


## 六、PR 规范

### 6.1 PR 标题格式

```bash
[<type>] 简短描述

示例：
[feat] 添加用户登录接口
[i18n] 更新首页中文翻译
[style] 替换应用图标
```

### 6.2 PR 描述模板

复制以下内容填入 PR 描述框：

```markdown
## 变更类型
- [ ] feat（新功能）
- [ ] fix（Bug 修复）
- [ ] i18n（翻译）
- [ ] style（UI/样式）
- [ ] docs（文档）
- [ ] refactor（重构）
- [ ] chore（构建/工具）

## 变更说明
（描述你做了什么）

## 如何验证
（怎么测试）

## 截图（如适用）
（附上截图）
```

### 6.3 合并方式

- 默认使用 **Create a merge commit**（保留完整历史）
- 多个小 commit 可选用 **Squash and merge**（合并成一个 commit）


## 七、常见问题

### 7.1 push 被拒绝，提示 protected branch

```bash
remote: error: GH006: Protected branch update failed for refs/heads/master.
```

**原因**：你直接 push 到 master 了，被保护规则拦截。

**解决**：
```
1. 本地切换到新分支：git checkout -b fix/xxx
2. 推送新分支：git push origin fix/xxx
3. 网页发起 PR
```

### 7.2 忘记新建分支，直接在 master 上改了

```bash
# 把当前改动移到新分支
git checkout -b feat/xxx
git push origin feat/xxx
# 然后网页发起 PR
```

### 7.3 冲突了怎么办

```
1. 在 PR 页面点击 Resolve conflicts
2. 手动解决冲突（删除 <<<<<<< 和 >>>>>>> 标记）
3. 点击 Mark as resolved
4. 提交合并
```

### 7.4 PR 提交后没人审核怎么办

- 在群里 @开发同学 提醒
- 直接在 PR 评论区 @ 对应 Reviewer

### 7.5 翻译/美术不会用命令行怎么办

- 全程使用 GitHub Desktop（可视化点击）
- 遇到问题随时在群里问


## 八、权限说明

| 角色 | 人数 | 权限 |
|------|------|------|
| Owner（开发负责人） | 1 人 | 仓库最高权限，紧急情况可直接 push |
| Write（开发/翻译/美术） | 6 人 | 可 push 分支、发起 PR、合并 PR（需审核） |
| Read（其他同事） | 21 人 | 只读查看，不可修改 |

> Owner 虽然保留了直接 push 的权限，**日常 90% 情况也走 PR 流程**，仅在仓库紧急故障时使用。


## 九、常用命令速查卡

### 翻译/美术（GitHub Desktop）

| 操作 | 怎么做 |
|------|--------|
| 拉取最新 | 点击 `Fetch origin` → 有更新点 `Pull origin` |
| 提交改动 | 勾选文件 → 填信息 → `Commit` → `Push` |
| 发起 PR | 网页操作：Pull requests → New pull request |

### 开发（命令行）

```bash
# 拉取 & 建分支
git checkout master && git pull
git checkout -b feat/xxx

# 提交 & 推送
git add . && git commit -m "feat(api): 添加登录接口"
git push origin feat/xxx

# 合并后清理
git checkout master && git pull
git branch -d feat/xxx
```
严禁长分支，要不然合并成本爆炸！

## 十、版本记录

| 日期 | 版本 | 变更说明 |
|------|------|----------|
| 2026-07-22 | v1.0 | 初始版本，首次发布 |

---

*如有疑问，随时在群里提出 👋*