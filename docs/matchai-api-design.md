# MatchAI UP 后端接口规划设计

> 模块: `yudao-module-match` (分支 `feat/match-module`)
> 前端契约源: `match-ai_web/docs/api-contract.md` (单契约源, 本文对齐该契约)
> 实现: 内存 Map 先行, 可平滑替换 MyBatis-Plus Mapper (DO/Mapper 骨架已建)

## 1. 背景

MatchAI UP 是校园 AI 社交匹配平台官网 (C 端)。前端 Vue3 + Naive UI 已完成, 走 mock 先行。
后端在 ruoyi-vue-pro (yudao v2, JDK8/Spring Boot 2.7.18/javax) 上新增业务模块, 提供分身/匹配/校园发现接口。

## 2. 架构与契约约定

- **统一信封** (`CommonResult`): `{ code: 0, msg, data }`, `code == 0` 成功
- **C 端前缀** `/app-api`: 由 `yudao-spring-boot-starter-web` 的 `WebProperties` 按 `**.controller.app.**` 自动拼接
- **认证**: `Authorization: Bearer <accessToken>`; 登录用户 `SecurityFrameworkUtils.getLoginUserId()`
- **分页**: `PageResult { list, total }` (`PageParam`: pageNo/pageSize)
- **登录复用**: `/app-api/member/auth/*` (手机+密码 / 短信登录) 由 `yudao-module-member` 提供, 本模块不重复实现

## 3. 接口清单

| 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|
| GET  | `/app-api/match/avatar` | 需登录 | 当前用户 AI 分身 `AvatarProfile \| null` |
| POST | `/app-api/match/avatar` | 需登录 | 保存 AI 分身, body = `AvatarProfile` |
| GET  | `/app-api/match/candidates` | 需登录 | 推荐候选人 `PageResult<CandidateCard>` |
| POST | `/app-api/match/like` | 需登录 | 喜欢 `{candidateId}` |
| POST | `/app-api/match/pass` | 需登录 | 跳过 `{candidateId}` |
| GET  | `/app-api/match/results` | 需登录 | 匹配结果 `PageResult<MatchResultItem>` |
| GET  | `/app-api/discover/tribes` | 公开 `@PermitAll` | 部落列表 `PageResult<Tribe>` |
| GET  | `/app-api/discover/schools` | 公开 `@PermitAll` | 学校列表 `School[]` |

`@PermitAll` 由 `YudaoWebSecurityConfigurerAdapter#getPermitAllUrlsFromAnnotations` 自动收集, 免登录。

## 4. 请求 / 响应字段

### 4.1 AI 分身 `AvatarProfile`
与前端 onboarding 问卷 `MatchQuestion.id` 对齐:

```json
{
  "schoolCode": "zju", "schoolName": "浙江大学", "campus": "玉泉",
  "grade": "本科三年级", "majorCategory": "理工科", "major": "计算机科学与技术",
  "tags": ["算法"], "interests": ["桌游 / 剧本杀", "科技 / 极客"],
  "mbti": "INTJ", "values": ["真诚", "成长"],
  "selfIntro": "一句话介绍", "recentStatus": "最近在忙的事"
}
```

### 4.2 推荐候选人 `CandidateCard`

```json
{
  "id": 1001, "avatar": "", "nickname": "林一", "age": 21,
  "schoolName": "浙江大学", "grade": "本科三年级", "majorCategory": "理工科",
  "tags": ["算法", "健身"], "mbti": "INTJ", "selfIntro": "一句话介绍",
  "matchScore": 85, "matchReasons": ["兴趣契合", "性格互补"]
}
```

### 4.3 匹配结果 `MatchResultItem` = `CandidateCard` + `{ "matchedAt": 毫秒, "liked": bool }`

### 4.4 部落 `Tribe` / 学校 `School`

```json
Tribe:  { "id": 1, "name": "夜跑搭子营", "cover": "", "memberCount": 128, "description": "…", "tags": ["运动", "跑步"] }
School: { "code": "zju", "name": "浙江大学", "city": "杭州" }
```

## 5. 数据库表设计 (`resources/sql/match.sql`)

| 表 | 用途 | 关键字段 |
|---|---|---|
| `match_avatar` | AI 分身 | `user_id`(FK, 唯一), `school_code`, `school_name`, `campus`, `grade`, `major_category`, `major`, `tags/interests/values`(JSON), `mbti`, `self_intro`, `recent_status` |
| `match_candidate` | 推荐候选人 | `nickname`, `age`, `school_*`, `major_*`, `tags/interests/values`(JSON), `mbti`, `self_intro`, `liked_me` |
| `match_like_record` | 喜欢/跳过 | `user_id`, `candidate_id`, `type`(1=喜欢,2=跳过), 唯一键 `(user_id,candidate_id,type)` |
| `match_result` | 匹配结果 | `user_id`, `candidate_id`, `matched_time`, 唯一键 `(user_id,candidate_id)` |
| `discover_tribe` | 部落 | `name`, `cover`, `member_count`, `description`, `tags`(JSON) |
| `discover_school` | 学校 | `code`(唯一), `name`, `city` |

公共列 (yudao 约定): `creator / create_time / updater / update_time / deleted(逻辑删) / tenant_id`。
数组字段以 JSON 字符串存储, 接入 MyBatis-Plus 后配 `JacksonTypeHandler` 自动映射。

## 6. 匹配算法 (五维打分)

`matchScore ∈ [0,100]`, 输出 `matchReasons` (≤3):

| 维度 | 加分 | 理由 |
|---|---|---|
| 基础 | 30 | — |
| 同校 | +10 | 同校之缘 |
| 专业大类相同 | +15 | 专业方向契合 |
| 同专业 | +15 | 同专业 |
| 兴趣交集 | +15 × min(交集数, 3) | 兴趣契合 |
| 三观交集 | +10 × min(交集数, 2) | 三观一致 |
| MBTI 互补 (≥2 轴相反) | +15 | 性格互补 |
| MBTI 完全同 | +5 | 性格相似 |

- 推荐: 过滤已喜欢/已跳过 → 打分 → `matchScore` 降序 → 分页
- 双向匹配: 用户 `like` 候选人且 `candidate.likedMe == true` → 生成 `match_result`

## 7. 接入 yudao-server 步骤 (待执行)

1. 根 `pom.xml` `<modules>` 加 `<module>yudao-module-match</module>`
2. `yudao-server/pom.xml` 加依赖 `cn.iocoder.boot:yudao-module-match:${revision}`
3. MapperScan: 框架 `YudaoMybatisAutoConfiguration` `@MapperScan("${yudao.info.base-package}")` 自动覆盖 `cn.iocoder.yudao.**`, **无需手改**
4. 建表: 执行 `yudao-module-match/src/main/resources/sql/match.sql`
5. 重启 server, 联调 `/app-api/match/*`、`/app-api/discover/*`

> 当前 ServiceImpl 为内存 Map, 不依赖 DB 即可联调; 切 DB 时把 Map 换成 `*Mapper` 调用 (DO/Mapper 骨架已建)。

## 8. 错误码 (段 `1-020-xxx-xxx`)

| 码 | 说明 |
|---|---|
| `1_020_001_000` | AI 分身不存在, 请先完成个人资料 |
| `1_020_002_000` | 推荐候选人不存在 |
| `1_020_002_001` | 已喜欢过该候选人, 请勿重复操作 |
| `1_020_002_002` | 已跳过该候选人, 请勿重复操作 |

## 9. 前端联调切换

1. `match-ai_web/.env.development` 设 `VITE_USE_MOCK=false`
2. vite proxy `/app-api` → `http://localhost:8082` (后端 server.port 当前 8080, 需 `--server.port=8082` 或改前端代理端口)
3. 字段有出入 → 改 `match-ai_web/src/api/types.ts` + 同步 mock, 保持单契约源
