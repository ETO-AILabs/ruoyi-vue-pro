# yudao-module-match（MatchAI UP 后端模块）

MatchAI UP 业务模块，基于 ruoyi-vue-pro（yudao）。契约以 `match-ai_web/docs/api-contract.md` 为准（单契约源）。

> 注意：本仓库是 yudao **v2（JDK 8 / Spring Boot 2.7.18 / javax）**，不是 Spring Boot 3。代码均按 JDK 8 + javax 编写。

## 1. 接口清单与鉴权

统一响应信封 `CommonResult`：`{ code: 0, msg, data }`，`code == 0` 成功。
C 端统一前缀 `/app-api`（由 `yudao-spring-boot-starter-web` 的 `WebProperties` 按 `**.controller.app.**` 自动拼接）。
认证头：`Authorization: Bearer <accessToken>`；登录用户用 `SecurityFrameworkUtils.getLoginUserId()` 获取。

| 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|
| GET  | `/app-api/match/avatar` | 需登录 | 当前用户 AI 分身 `AvatarProfile \| null` |
| POST | `/app-api/match/avatar` | 需登录 | 保存 AI 分身，body = `AvatarProfile` |
| GET  | `/app-api/match/candidates` | 需登录 | 推荐候选人 `PageResult<CandidateCard>` |
| POST | `/app-api/match/like` | 需登录 | 喜欢，body `{candidateId}` |
| POST | `/app-api/match/pass` | 需登录 | 跳过，body `{candidateId}` |
| GET  | `/app-api/match/results` | 需登录 | 匹配结果 `PageResult<MatchResultItem>` |
| GET  | `/app-api/discover/tribes` | 公开（`@PermitAll`） | 部落列表 `PageResult<Tribe>` |
| GET  | `/app-api/discover/schools` | 公开（`@PermitAll`） | 学校列表 `School[]` |

`@PermitAll` 由 `YudaoWebSecurityConfigurerAdapter#getPermitAllUrlsFromAnnotations` 自动收集，免登录，**无需**额外在 `application.yaml` 配置 `yudao.security.permit-all-urls`。

认证（登录/登出/me）走已有的 `yudao-module-member` 的 `/app-api/member/auth/*`，本模块不重复实现。

## 2. 包结构

```
yudao-module-match/
├── pom.xml
└── src/main/
    ├── java/cn/iocoder/yudao/module/match/
    │   ├── controller/app/
    │   │   ├── match/AppMatchController.java        # /match/* 七个接口
    │   │   │   └── vo/                              # AvatarProfileVO、CandidateCardVO、MatchResultItemVO、LikeReqVO、PassReqVO
    │   │   └── discover/AppDiscoverController.java  # /discover/* 两个公开接口
    │   │       └── vo/                              # TribeVO、SchoolVO
    │   ├── service/
    │   │   ├── match/MatchService(+Impl)            # 内存 Map 实现，含匹配打分算法
    │   │   └── discover/DiscoverService(+Impl)      # 内存 Map 实现
    │   ├── dal/
    │   │   ├── dataobject/                          # 6 个 DO（MatchAvatarDO / MatchCandidateDO / MatchLikeRecordDO / MatchResultDO / DiscoverTribeDO / DiscoverSchoolDO）
    │   │   └── mysql/                               # 6 个 Mapper 骨架（extends BaseMapperX）
    │   └── enums/ErrorCodeConstants.java            # 错误码段 1-020-xxx-xxx
    └── resources/sql/match.sql                      # 6 张表 DDL + 种子数据
```

## 3. 表设计（resources/sql/match.sql）

| 表 | 用途 | 关键字段 |
|---|---|---|
| `match_avatar` | AI 分身 | `user_id`(FK→member_user.id, 唯一), `school_code`, `school_name`, `campus`, `grade`, `major_category`, `major`, `tags/JSON`, `interests/JSON`, `mbti`, `values/JSON`, `self_intro`, `recent_status` |
| `match_candidate` | 推荐候选人 | `nickname`, `age`, `school_*`, `major_*`, `tags/interests/values`(JSON), `mbti`, `self_intro`, `liked_me`(是否已喜欢当前用户，触发双向匹配) |
| `match_like_record` | 喜欢/跳过记录 | `user_id`, `candidate_id`, `type`(1=喜欢,2=跳过)，唯一键 `(user_id, candidate_id, type)` |
| `match_result` | 匹配结果 | `user_id`, `candidate_id`, `matched_time`，唯一键 `(user_id, candidate_id)` |
| `discover_tribe` | 部落 | `name`, `cover`, `member_count`, `description`, `tags/JSON` |
| `discover_school` | 学校 | `code`(唯一), `name`, `city` |

公共列（yudao 约定）：`creator / create_time / updater / update_time / deleted(逻辑删) / tenant_id(多租户)`。数组字段以 JSON 数组字符串存储，接入 MyBatis-Plus 后可配 `JacksonTypeHandler` 自动映射。

## 4. 匹配算法（伪代码）

基于用户分身与候选人的五维打分，`matchScore ∈ [0,100]`，同时输出 `matchReasons`。

```text
function score(me, candidate):
    score = 30                     # 基础分
    reasons = []

    # 1. 同校（10）
    if me.schoolCode == candidate.schoolCode:
        score += 10; reasons.add("同校之缘")

    # 2. 专业（大类 15，同专业再 +15）
    if me.majorCategory == candidate.majorCategory:
        score += 15; reasons.add("专业方向契合")
    if me.major == candidate.major:
        score += 15; reasons.add("同专业")

    # 3. 兴趣（每个交集兴趣 +15，封顶 3 个）
    overlap = intersect(me.interests, candidate.interests)
    if overlap.size > 0:
        score += min(overlap.size, 3) * 15; reasons.add("兴趣契合")

    # 4. 三观（每个交集价值观 +10，封顶 2 个）
    valuesOverlap = intersect(me.values, candidate.values)
    if valuesOverlap.size > 0:
        score += min(valuesOverlap.size, 2) * 10; reasons.add("三观一致")

    # 5. 性格（MBTI 四轴 E-I/N-S/F-T/J-P）
    opposite = countOppositeAxes(me.mbti, candidate.mbti)   # 每对相反轴计 1
    if opposite >= 2:
        score += 15; reasons.add("性格互补")
    elif opposite == 0:
        score += 5;  reasons.add("性格相似")

    return min(100, score), reasons.top3
```

- 推荐列表：过滤已喜欢/已跳过 → 打分 → 按 `matchScore` 降序 → 分页。
- 双向匹配：用户 `like` 候选者时，若 `candidate.liked_me == true`，生成一条 `match_result`（`matchedAt = now`，`liked = true`）。

## 5. 接入 yudao-server（手动执行）

本模块**未**修改根 pom / yudao-server / 其它任何现有模块，以下步骤需人工完成：

1. **根 pom 注册模块**：`pom.xml` 的 `<modules>` 增加
   `<module>yudao-module-match</module>`。
2. **server 加依赖**：`yudao-server/pom.xml` 增加
   ```xml
   <dependency>
       <groupId>cn.iocoder.boot</groupId>
       <artifactId>yudao-module-match</artifactId>
       <version>${revision}</version>
   </dependency>
   ```
3. **MapperScan**：`yudao-server/src/main/java/cn/iocoder/yudao/server/YudaoServerApplication.java`
   的 `@MapperScan` 中追加包 `"cn.iocoder.yudao.module.match.dal.mysql"`。
4. **建表**：执行 `yudao-module-match/src/main/resources/sql/match.sql`（或接入 yudao 的 db 初始化 / 迁移）。
5. **数据源 / MyBatis-Plus**：由 `yudao-spring-boot-starter-mybatis` 自动装配，无需额外配置。
6. **重启 server**，联调 `/app-api/match/*` 与 `/app-api/discover/*`。

> 当前服务为内存 Map 实现（`MatchServiceImpl` / `DiscoverServiceImpl`），不依赖数据库即可联调。
> 切换真实数据库时，把 ServiceImpl 内的 `Map` 换成对应 `*Mapper` 调用即可（DO/Mapper 骨架已建好）。

## 6. 错误码

`enums/ErrorCodeConstants`，错误码段 `1-020-xxx-xxx`：

| 错误码 | 说明 |
|---|---|
| `1_020_001_000` | AI 分身不存在，请先完成个人资料 |
| `1_020_002_000` | 推荐候选人不存在 |
| `1_020_002_001` | 已喜欢过该候选人，请勿重复操作 |
| `1_020_002_002` | 已跳过该候选人，请勿重复操作 |
