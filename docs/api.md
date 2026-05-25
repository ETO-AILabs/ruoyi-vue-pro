# AI 社交模块 API 文档

基于 S1 + S2 设计的完整接口清单。

## 通用约定

- 路由前缀: App `/member/social/*`、`/member/user/*`；Admin `/member/social/*`
- 鉴权: App 登录态由 JWT 提供，userId 由 `SecurityFrameworkUtils.getLoginUserId()` 注入
- 返回: 统一 `CommonResult<T>` 包装 `{code, data, msg}`
- 时间: `LocalDateTime`，ISO 格式
- 分页: 入参继承 `PageParam(pageNo, pageSize)`，出参 `PageResult<T>{list, total}`

## 错误码 (1-004-013-xxx)

| code | 含义 |
|------|------|
| 1004013000 | 分身已创建，不能重复创建 |
| 1004013001 | 今天昵称生成次数已达上限 |
| 1004013002 | 场景不存在 |
| 1004013003 | 当前场景已有匹配任务进行中 |
| 1004013004 | 同场景匹配冷却中 |
| 1004013005 | 匹配任务不存在 |
| 1004013006 | 已有待审核学校认证申请 |
| 1004013007 | 用户尚未创建分身 |
| 1004013008 | 标签不存在 |
| 1004013009 | 学校认证记录不存在 |

---

# 一、App 端接口

## 1. 分身（AppMemberUserController, `/member/user`）

### 创建分身前必须调用的初始化接口

按以下顺序调用，获取页面所需选项数据：

| 顺序 | 接口 | 用途 |
|------|------|------|
| 1 | `POST /member/user/nickname/generate` | 生成随机昵称（前端只读展示，日限3次） |
| 2 | `GET /member/user/avatar/random` | 随机头像（可选，预览用；创建分身时后端会再随机一次） |
| 3 | `GET /member/social/region/tree` | 省市区三级树（常住地选择器数据源） |
| 4 | `GET /member/social/tag/list?category=profession` | 职业标签列表（chip 单选） |
| 5 | `GET /member/social/school/search?name=xxx` | 学校搜索（用户输入时调用，可选） |

MBTI 固定 16 个值，由前端硬编码：`INTJ/INTP/ENTJ/ENTP/INFJ/INFP/ENFJ/ENFP/ISTJ/ISFJ/ESTJ/ESFJ/ISTP/ISFP/ESTP/ESFP`。

### 1.1 创建分身

`POST /member/user/clone`

Request `AppMemberUserCloneCreateReqVO`:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nickname | String | 是 | 昵称（取自 `/nickname/generate`） |
| sex | Integer | 是 | 性别 1男 2女 |
| birthday | LocalDateTime | 是 | 出生日期 |
| residence | String | 是 | 常驻地，格式 `城市 · 区`，如 `杭州 · 西湖区` |
| mbti | String | 是 | MBTI 16 种值之一 |
| professionTagId | Long | 否 | 职业标签ID（来自 `/tag/list?category=profession`） |
| groupId | Long | 否 | 学校ID（来自 `/school/search`） |
| longitude | BigDecimal | 否 | 经度（兼容字段） |
| latitude | BigDecimal | 否 | 纬度（兼容字段） |
| location | String | 否 | 地理位置名称（兼容字段） |
| tagIds | List\<Long\> | 否 | 兴趣标签ID列表（建议改走 `/user-tag/batch-save`） |
| userDesc | String | 否 | 个人描述 |
| wechat | String | 否 | 微信号 |

Response: `CommonResult<Boolean>`

业务规则：
- `is_has_cloned=0` 才允许；创建后置 1
- 头像从 `member_avatars` 随机选一条写入 `member_user.avatar`
- `groupId` 非空时校验学校存在
- `professionTagId` 非空时查 `member_tag` 把标签名写入 `member_user.profession`

---

### 1.2 获取分身信息

`GET /member/user/clone/info`

Response `AppMemberUserCloneInfoRespVO`:

| 字段 | 类型 | 说明 |
|------|------|------|
| nickname | String | 昵称 |
| avatar | String | 头像URL |
| sex | Integer | 性别 |
| birthday | LocalDateTime | 出生日期 |
| residence | String | 常驻地 |
| mbti | String | MBTI |
| professionTagId | Long | 职业标签ID（暂未持久化，保留位） |
| professionName | String | 职业名称（取自 `member_user.profession`） |
| schoolName | String | 学校名称（按 groupId 查 member_group 补齐） |
| groupId | Long | 学校ID |
| longitude/latitude/location | - | 兼容字段 |
| tagIds | List\<Long\> | 标签ID列表 |
| userDesc | String | 个人描述 |
| wechat | String | 微信号 |

---

### 1.3 更新分身

`PUT /member/user/clone`

Request `AppMemberUserCloneUpdateReqVO`（字段同 create，全部选填）

Response: `CommonResult<Boolean>`

业务规则：未创建分身时抛 `USER_NOT_CLONED`；其他校验同 create。

---

### 1.4 生成随机昵称

`POST /member/user/nickname/generate`

调用大模型生成；记录到 `member_nickname`；按 `gen_date + user_id` 每日 3 次。

Response `AppMemberUserNicknameGenerateRespVO`:
```json
{"nickname": "小艾", "todayUsed": 1, "dailyLimit": 3}
```

---

### 1.5 随机头像

`GET /member/user/avatar/random`

Response: `CommonResult<String>` (头像URL)

---

### 1.6 省市区树（常住地）

`GET /member/social/region/tree`

Response `List<AppSocialRegionTreeRespVO>`:
```json
[
  {
    "id": 110000, "name": "北京",
    "children": [
      {"id": 110100, "name": "北京市", "children": [
        {"id": 110101, "name": "东城区"},
        {"id": 110102, "name": "西城区"}
      ]}
    ]
  }
]
```

数据源：`AreaUtils`（内存加载 `area.csv`），已过滤港澳台（id ≥ 810000）。前端选择后拼接成 `城市 · 区` 提交。

---

## 2. 学校（AppSocialController, `/member/social`）

### 2.1 搜索学校

`GET /member/social/school/search?name=北京`

Response `List<AppSocialSchoolSearchRespVO>`:
```json
[{"id": 1, "name": "北京大学"}]
```

`name` 空字符串时返回空列表。

---

### 2.2 提交学校认证申请

`POST /member/social/school/verify`

Request `AppSocialSchoolVerifyReqVO`:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| groupId | Long | 是 | 学校ID（member_group.id, group_type=1） |
| schoolUrl | String | 是 | 学生证图片URL |

Response: `CommonResult<Long>` (认证记录ID)

业务: 已有 `verifyStatus=0` 待审核记录 → 抛 `SCHOOL_VERIFY_EXISTS`

---

### 2.3 查询认证状态

`GET /member/social/school/verify/status`

Response `AppSocialSchoolVerifyRespVO`:
```json
{"verifyStatus": 0, "schoolName": "北京大学"}
```

`verifyStatus`: null=未提交, 0=待审核, 1=通过, 2=拒绝

---

## 3. 场景（AppSocialController, `/member/social/scene`）

### 3.1 获取用户可见场景列表

`GET /member/social/scene/list`

Response `List<AppSocialSceneRespVO>`:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| sceneCode | Long | 场景编码 |
| sceneName | String | 场景名称 |
| sceneConfig | String | 场景配置 JSON |
| pagePath | String | 页面路径 |

可见性: 有学校 → 取 `scope_type=1, scope_id=学校ID`；否则取 `scope_type=0, scope_id=0` 兜底。

---

### 3.2 获取场景表单 (S2 新增)

`GET /member/social/scene/form?sceneCode=buddy`

Response `List<AppSocialSceneFormRespVO>` — 3 层结构 section → subGroup → tag

```json
[
  {
    "code": "interest_tags",
    "sectionName": "兴趣标签",
    "subGroups": [
      {
        "groupName": "生活类",
        "groupCode": "living",
        "tags": [
          {"tagId": 101, "tagCode": "ktv", "tagName": "KTV", "isSelected": true, "sort": 1}
        ]
      }
    ]
  }
]
```

查询逻辑：`member_scene_section` → `member_scene_section_group` → `member_tag(parent_id=groupTagId)` → join `member_user_tag` 标记 isSelected

---

## 4. 匹配（AppSocialController, `/member/social/match`）

### 4.1 创建匹配任务

`POST /member/social/match/create`

Request `AppSocialMatchCreateReqVO`:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sceneId | Long | 是 | 场景ID |
| tagIds | List\<Long\> | 否 | S2 新增，标签ID列表 |
| matchGoal | String | 否 | 匹配诉求 |
| matchRemark | String | 否 | 匹配备注 |
| extraFields | Map | 否 | S2 新增，非标签字段（wx/aboutMe/photos...） |

Response: `CommonResult<Long>` (任务ID)

业务规则：
1. 检查 `match_status IN (0,1)` 任务 → 拒绝 `MATCH_TASK_EXISTS`
2. 检查最近任务 `create_time + 24h < now` → 拒绝 `MATCH_COOLDOWN`
3. 标签覆盖式保存到 `member_user_tag(source=self)`
4. `extraFields` 序列化为 `{"extraFields":{...}}` 存 `member_match_task.match_config`

---

### 4.2 分页查询匹配记录

`GET /member/social/match/task/page?pageNo=1&pageSize=10`

Request `AppSocialMatchPageReqVO` (PageParam)

Response `PageResult<AppSocialMatchTaskRespVO>`:

```json
{"list":[{"id":1,"sceneId":1,"sceneName":"校园交友","matchStatus":0,
"matchGoal":"...","matchRemark":"...","createTime":"...","finishTime":null}],"total":5}
```

matchStatus: 0未开始, 1进行中, 2失败, 3成功

---

### 4.3 获取指定场景匹配状态 + 有效结果

`GET /member/social/match/scene/status?sceneId=1`

Response `AppSocialMatchSceneRespVO`:

```json
{"matchStatus": 2, "results": [{"id":1,"matchedUserId":10,"matchScore":0.85}]}
```

状态码: 0待匹配, 1匹配中, 2匹配完成, 3冷却中

判断逻辑:
- 无任务 → 0
- status ∈ {0,1} → 1
- status=3 且 `create_time + 12h > now` → 2 + 结果列表
- `create_time + 24h > now` → 3
- 否则 → 0

---

### 4.4 保存场景匹配配置 (S2 新增)

`POST /member/social/match/config/save`

Request `AppSocialMatchConfigSaveReqVO`:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sceneCode | String | 是 | 场景编码 (buddy/love/skill/swap) |
| tagIds | List\<Long\> | 是 | 选中的标签ID |
| extraFields | Map | 否 | 非标签字段 |

Response: `CommonResult<Boolean>`

业务: 覆盖式保存 `member_user_tag(source=self)`；extraFields 当前实现仅保留接口，写入随 `match/create` 落地

---

### 4.5 获取上次额外字段 (S2 新增)

`GET /member/social/match/config/get?sceneCode=buddy`

Response: `CommonResult<Map<String, Object>>`

当前实现返回空 Map，扩展时需 sceneCode↔sceneId 映射后从 `member_match_task.match_config` 读取 `extraFields` 节点

---

## 5. 标签（AppSocialController, `/member/social`）

### 5.1 获取预置标签列表

`GET /member/social/tag/list?category=profession`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| category | String | 否 | 分类编码，如 `profession`、`interest`；不传返回全部 |

Response `List<AppSocialTagRespVO>`:
```json
[{"id":1,"tagName":"运动"}]
```

不传 category 时返回 `status=1` 全部标签，按 sort 升序；传 category 时取该分类下根节点（`parent_id=0`）。

---

### 5.2 批量保存用户标签 (S2 新增)

`POST /member/social/user-tag/batch-save`

Request `AppSocialUserTagBatchSaveReqVO`:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| tagIds | List\<Long\> | 是 | 标签ID列表 |
| source | String | 是 | self=自选 / auto=行为打标 |

Response: `CommonResult<Boolean>`

业务: 先删 (user_id + source) → 批量插入

---

### 5.3 获取用户标签ID列表 (S2 新增)

`GET /member/social/user-tag/list`

Response: `CommonResult<List<Long>>`

返回当前用户全部标签ID（含 self + auto）

---

## 6. 用户社交主页（AppSocialController）

### 6.1 获取用户社交主页

`GET /member/social/user/detail?userId=10`

Response `AppSocialUserDetailRespVO`:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |
| nickname | String | 昵称 |
| avatar | String | 头像URL |
| sex | Integer | 性别 |
| age | Integer | 年龄(根据生日计算) |
| userDesc | String | 个人描述 |
| wechat | String | 微信号 |
| tagNames | List\<String\> | 标签名称(优先取 member_user_tag) |
| schoolName | String | 学校名称 |
| residence | String | 常驻地（S2 新增） |
| mbti | String | MBTI（S2 新增） |
| profession | String | 职业（S2 新增） |

业务: 未创建分身 → 抛 `USER_NOT_CLONED`

---

## 7. 社交账号绑定（AppSocialUserController, `/member/social-user`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/bind` | 社交绑定（使用 code 授权码） |
| DELETE | `/unbind` | 取消社交绑定 |
| GET | `/get?type=10` | 获取已绑定的社交用户 |
| POST | `/wxa-qrcode` | 获取微信小程序码 (base64) |
| GET | `/get-subscribe-template-list` | 获取微信小程订阅模板列表 |

---

# 二、Admin 端接口

## 8. 场景管理（SceneController, `/member/social/scene`）

权限前缀: `member:social:scene:*`

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| POST | `/create` | scene:create | 创建场景 |
| PUT | `/update` | scene:update | 更新场景 |
| DELETE | `/delete?id=1` | scene:delete | 删除场景 |
| GET | `/get?id=1` | scene:query | 获取场景详情 |
| GET | `/page` | scene:query | 分页查询场景 |

VO 字段: id, sceneCode, sceneType (1校外/2校园), sceneName, sceneStatus (1可用/0不可用)

分页过滤: sceneName (like), sceneType, sceneStatus (eq)

---

## 9. 学校认证审核（SchoolVerifyController, `/member/social/school/verify`）

权限前缀: `member:social:school:verify:*`

### 9.1 分页查询

`GET /member/social/school/verify/page`

权限: `member:social:school:verify:query`

Request `SchoolVerifyPageReqVO` (PageParam + verifyStatus)

Response `PageResult<SchoolVerifyRespVO>`:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| userId | Long | 用户ID |
| nickname | String | 用户昵称 |
| groupId | Long | 学校ID |
| schoolName | String | 学校名称 |
| schoolUrl | String | 学生证图片URL |
| verifyStatus | Integer | 0未审核 1通过 2拒绝 |
| createTime | LocalDateTime | 创建时间 |

---

### 9.2 审核

`PUT /member/social/school/verify/audit`

权限: `member:social:school:verify:audit`

Request `SchoolVerifyAuditReqVO`:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 认证记录ID |
| verifyStatus | Integer | 是 | 1通过 2拒绝 |

业务: status=1 时同步设置 `member_user.is_verified=true`

---

# 三、S2 设计但未实现的接口

以下为设计文档列出但当前代码未实现，需后续补齐：

| 方法 | 路径 | 说明 | 替代方案 |
|------|------|------|---------|
| GET | `/member/social/tag/tree?category=skill` | 标签树（技能分类三级树） | 临时用 `/tag/list?category=xxx` 拉根节点后前端拼装 |
| GET | `/member/social/admin/tag/page` | Admin 标签分页 | 临时可用 MyBatis Plus 通用查询 |
| POST | `/member/social/admin/tag/create` | Admin 标签创建（带 code） | - |
| PUT | `/member/social/admin/tag/update` | Admin 标签更新 | - |
| DELETE | `/member/social/admin/tag/delete` | Admin 标签删除（有子节点禁止） | - |
| POST | `/member/social/admin/section/save` | Admin 配置区块+子分组 | - |
| GET | `/member/social/admin/section/list?sceneCode=buddy` | Admin 查场景配置 | - |

---

# 四、数据流参考

## 进入场景匹配页

```
GET /scene/form?sceneCode=buddy
  -> [{sectionName, subGroups:[{groupName,groupCode,tags:[{tagId,tagCode,tagName,isSelected,sort}]}]}]

GET /match/config/get?sceneCode=buddy
  -> {"wx":"xxx","aboutMe":"..."}

[用户操作: 前端用 tagCode 做 :key, 提交时传 tagId]

POST /match/create {sceneId, tagIds, matchGoal, matchRemark, extraFields}
  -> member_user_tag (先删 source=self 后批量插入)
  -> member_match_task.match_config = {"extraFields":{...}}
```

## 创建分身

```
1. POST /member/user/nickname/generate           -> 随机昵称（日限3次）
2. GET  /member/user/avatar/random               -> 随机头像URL（可选预览）
3. GET  /member/social/region/tree               -> 省市区树（常住地选择器）
4. GET  /member/social/tag/list?category=profession  -> 职业标签 chip 选项
5. GET  /member/social/school/search?name=北京    -> 学校搜索（用户输入触发）
6. POST /member/user/clone {nickname,sex,birthday,residence,mbti,
                            professionTagId?,groupId?,...}
                                                 -> 写入 member_user, is_has_cloned=1
                                                    随机头像、若有 professionTagId 则解析为名称写入
7. POST /member/social/school/verify             -> 提交学校认证（可选）
8. GET  /member/social/school/verify/status      -> 查询审核状态
```

---

# 五、模块文件清单

## DO
- `MemberUserDO` (扩展 wechat/userDesc/longitude/latitude/location/isHasCloned/isVerified/residence/mbti/profession)
- `MemberSceneDO` `MemberUserSceneDO` `MemberMatchTaskDO` `MemberMatchTaskResultDO`
- `MemberSchoolVerifyDO` `MemberGroupExtDO`
- `MemberAvatarsDO` `MemberNicknameRecordDO`
- `MemberTagDO` (扩展 parentId/category/code)
- S2 新增: `MemberSceneSectionDO` `MemberSceneSectionGroupDO` `MemberUserTagDO`

## Mapper (BaseMapperX)
- 对应每个 DO 各一个 Mapper

## Service
- `SocialService` / `Impl` — 场景、学校搜索、标签、用户主页、S2 场景表单+用户标签
- `MatchService` / `Impl` — 创建匹配任务（含标签覆盖+extraFields）、分页、场景状态
- `SchoolVerifyService` / `Impl` — 认证提交+审核
- `AvatarService` / `Impl` — 随机头像
- `NicknameService` / `Impl` — 大模型昵称生成（日限校验）

## Controller
- App: `AppSocialController` / `AppSocialUserController` / `AppMemberUserController`
- Admin: `SceneController` / `SchoolVerifyController`

## SQL
- `docs/sql/social_module_init.sql` — S1 初始 DDL + 种子
- `docs/s2/s2_schema.sql` — S2 表结构变更
- `docs/s2/s2_seed.sql` — S2 种子数据（11 根分类 + 19 子分组 + 数百叶子标签 + 场景区块配置）