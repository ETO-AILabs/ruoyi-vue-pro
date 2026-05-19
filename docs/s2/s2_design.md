# AI 社交 S2 - 标签体系升级 & 场景表单设计

## 1. 概述

### 1.1 目标
- 标签体系从单表平铺升级为分层分类结构，标签增加编码字段
- 每个场景按区块标题 → 子分组 → 标签列表三层结构展示
- 用户标签从 `member_user.tag_ids` 迁移到独立关联表
- 场景内非标签字段（微信号、自我介绍、图片等）独立存储
- 复用上次匹配配置，可修改，修改后持久化

### 1.2 涉及模块
`yudao-module-member`，不跨模块。

### 1.3 4 个场景
| scene_code | 名称 | 区块 | 说明 |
|-----------|------|------|------|
| buddy | 合拍搭子 | 想干嘛、兴趣标签、更多偏好 | 首页内联表单 |
| love | 合拍伴侣 | 基本资料、个人特质 | 04_伴侣填写.html |
| skill | 技能交换 | 我会的技能、我想学的技能 | 05_技能填写.html |
| swap | 易物交友 | 我出什么、我想换什么 | 首页内联表单 |

---

## 2. 数据库设计

### 2.1 member_tag —— 标签元数据重构

```sql
-- 现有字段保留：id, tag_name, sort, status, tenant_id
-- 新增字段：
ALTER TABLE `member_tag`
    ADD COLUMN `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父ID，0=根节点',
    ADD COLUMN `category` varchar(32) DEFAULT '' COMMENT '分类编码',
    ADD COLUMN `code` varchar(64) DEFAULT '' COMMENT '标签编码（前端用，同一分组内唯一）',
    ADD INDEX `idx_parent_id` (`parent_id`),
    ADD INDEX `idx_category` (`category`),
    ADD INDEX `idx_code` (`code`);
```

**category 枚举值：**
| category | 说明 | 适用场景 |
|----------|------|---------|
| personality | 性格 | love, buddy(更多偏好) |
| profession | 职业 | 创建分身时单选 |
| interest | 兴趣 | love, buddy(兴趣标签), swap |
| skill | 技能 | skill(技能交换) |
| item | 物品类型 | swap(易物交友) |
| buddy_activity | 搭子活动 | buddy(想干嘛) |
| love_purpose | 交往目的 | love |
| lifestyle | 生活节奏 | buddy(更多偏好) |
| sport | 运动 | love |

**标签层级结构：**

```
level-1 (parent_id=0, category/sort 有值) → 根分类
    例：插画与绘画(category=skill, code=skill_illustration, parent_id=0)

level-2 (parent_id=level1.id, category='') → 子分组（对应前端子分组标题）
    例：生活类(code=living, parent_id=兴趣根ID, sort=1)
    例：兴趣类(code=hobby, parent_id=兴趣根ID, sort=2)

level-3 (parent_id=level2.id, category='') → 叶子标签（可选）
    例：KTV(code=ktv, parent_id=生活类ID, sort=1)
    例：机车(code=motorcycle, parent_id=兴趣类ID, sort=1)
```

**code 字段使用：**
- 前端用 `code` 做 v-for 的 :key，不用 id
- 后端更新时用 `tag_id`
- 同一 parent_id 下 code 唯一，全局不要求唯一
- 前端向后端提交时带 `tag_id` 列表

---

### 2.2 member_scene_section —— 场景区块标题

```sql
CREATE TABLE `member_scene_section` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `scene_code` varchar(32) NOT NULL COMMENT '场景编码(buddy/love/skill/swap)',
    `code` varchar(64) NOT NULL COMMENT '区块编码(前端用，如buddy_activity/interest_tags/preferences)',
    `section_name` varchar(64) NOT NULL COMMENT '区块标题("想干嘛"/"兴趣标签"/"更多偏好")',
    `sort` tinyint DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (`id`),
    KEY `idx_scene_code` (`scene_code`),
    UNIQUE KEY `uk_scene_code` (`scene_code`, `code`)
) COMMENT='场景区块标题表';
```

**种子数据：**

| scene_code | code | section_name | sort |
|-----------|------|-------------|------|
| buddy | buddy_activity | 想干嘛 | 1 |
| buddy | interest_tags | 兴趣标签 | 2 |
| buddy | preferences | 更多偏好 | 3 |
| love | basic_info | 基本资料 | 1 |
| love | personality | 个人特质 | 2 |
| skill | i_can_teach | 我会的技能 | 1 |
| skill | i_want_learn | 我想学的技能 | 2 |
| swap | i_offer | 我出什么 | 1 |
| swap | i_want | 我想换什么 | 2 |

---

### 2.3 member_scene_section_group —— 区块-子分组关联

```sql
CREATE TABLE `member_scene_section_group` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `section_id` bigint NOT NULL COMMENT '区块ID',
    `group_tag_id` bigint NOT NULL COMMENT '子分组标签ID(level-2 tag, 如"生活类")',
    `sort` tinyint DEFAULT 0 COMMENT '分组排序',
    PRIMARY KEY (`id`),
    KEY `idx_section_id` (`section_id`),
    KEY `idx_group_tag_id` (`group_tag_id`)
) COMMENT='场景区块子分组关联表';
```

**说明：**
- 区块只关联 level-2 的 tag（子分组）
- level-2 tag 的 children（level-3）就是可选标签列表
- "想干嘛"这种没有子分组的场景，区块直接关联 level-1 分类，查其下所有叶子

---

### 2.4 member_user_tag —— 用户标签

```sql
CREATE TABLE `member_user_tag` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `tag_id` bigint NOT NULL COMMENT '标签ID',
    `source` varchar(32) DEFAULT 'self' COMMENT '来源(self=自选/auto=行为打标)',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_tag` (`user_id`, `tag_id`),
    KEY `idx_user_id` (`user_id`)
) COMMENT='用户标签表';
```

**说明：** 替代 `member_user.tag_ids`，迁移后旧字段保留但不维护。

---

### 2.5 member_user 新增字段

```sql
ALTER TABLE `member_user`
    ADD COLUMN `residence` varchar(64) DEFAULT '' COMMENT '常驻地(格式：杭州·西湖区)',
    ADD COLUMN `mbti` varchar(8) DEFAULT '' COMMENT 'MBTI性格类型',
    ADD COLUMN `profession` varchar(32) DEFAULT '' COMMENT '职业(单选，对应member_tag.category=profession的标签名)';
```

---

### 2.6 match_task.match_config JSON Schema

只存**非标签的额外字段**（微信号、文本、图片URL等），标签选择落地 `member_user_tag` 表。

```json
{"extraFields": {"wx": "mywechat123", "aboutMe": "..."}}
```

各场景 extraFields 示例：

**buddy：** `{"wx": "mywechat123"}`
**love：** `{"purpose": "恋爱", "wx": "...", "birthday": "2000.01.01", "height": "175", "hometown": "杭州", "income": "1-2万", "photos": [...], "aboutMe": "喜欢旅行", "idealTa": "..."}`
**skill：** `{"wx": "...", "teachSkills": [{"skill": "数字插画", "level": "精通", "desc": "..."}], "learnSkill": "UI/UX设计"}`
**swap：** `{"itemImages": [...], "itemDesc": "...", "itemValue": 50, "extraNote": "...", "wx": "..."}`

`member_match_task.match_config` 建议从 VARCHAR 改为 TEXT。

---

## 3. API 详细设计

### 3.1 获取场景表单

`GET /member/social/scene/form?sceneCode=buddy`

**用途：** 进入场景匹配页时加载——区块标题、子分组、标签列表、用户选中状态

**查询逻辑：**
```
1. SELECT section FROM member_scene_section WHERE scene_code=X ORDER BY sort
2. 遍历每个 section：
   a. 查 member_scene_section_group WHERE section_id=section.id → 拿到 group_tag_id 列表
   b. group_tag_id 对应 level-2 tag，查 member_tag WHERE parent_id=group_tag_id → 拿到子级标签列表
   c. 查 member_user_tag WHERE user_id=Y AND tag_id IN (子标签IDs) → 标记 is_selected
   d. 若无 group（"想干嘛"场景），直接查 section对应分类下的叶子标签
3. 组装返回
```

**Response `CommonResult<List<AppSocialSceneFormRespVO>>`：**

```java
// VO 定义
@Data
public class AppSocialSceneFormRespVO {
    private String code;                          // "buddy_activity"（前端 :key）
    private String sectionName;                   // "兴趣标签"（展示用）
    private List<SubGroup> subGroups;             // 子分组列表

    @Data
    public static class SubGroup {
        private String groupName;                 // "生活类"
        private String groupCode;                 // "living"（子分组 :key）
        private List<TagItem> tags;               // 标签列表
    }

    @Data
    public static class TagItem {
        private Long tagId;                       // 标签ID（更新时用）
        private String tagCode;                   // 标签编码（前端 :key）
        private String tagName;                   // 标签名称
        private Boolean isSelected;               // 是否选中
        private Integer sort;                     // 排序
    }
}
```

```json
[
  {
    "code": "buddy_activity",
    "sectionName": "想干嘛",
    "subGroups": [
      {
        "groupName": "",
        "groupCode": "",
        "tags": [
          {"tagId": 501, "tagCode": "buddy_badminton", "tagName": "正在找羽毛球搭子", "isSelected": true, "sort": 1},
          {"tagId": 502, "tagCode": "buddy_camping", "tagName": "周末想去露营", "isSelected": false, "sort": 2}
        ]
      }
    ]
  },
  {
    "code": "interest_tags",
    "sectionName": "兴趣标签",
    "subGroups": [
      {
        "groupName": "生活类",
        "groupCode": "living",
        "tags": [
          {"tagId": 101, "tagCode": "ktv", "tagName": "KTV", "isSelected": true, "sort": 1},
          {"tagId": 102, "tagCode": "travel", "tagName": "旅行", "isSelected": false, "sort": 2}
        ]
      },
      {
        "groupName": "兴趣类",
        "groupCode": "hobby",
        "tags": [
          {"tagId": 201, "tagCode": "motorcycle", "tagName": "机车", "isSelected": false, "sort": 1}
        ]
      }
    ]
  },
  {
    "code": "preferences",
    "sectionName": "更多偏好",
    "subGroups": [
      {
        "groupName": "性格特质",
        "groupCode": "personality",
        "tags": [
          {"tagId": 301, "tagCode": "emotional_stable", "tagName": "情绪稳定", "isSelected": true, "sort": 1}
        ]
      },
      {
        "groupName": "生活节奏",
        "groupCode": "lifestyle",
        "tags": [
          {"tagId": 401, "tagCode": "early_bird", "tagName": "早睡早起", "isSelected": false, "sort": 1}
        ]
      }
    ]
  }
]
```

**表结构是否满足：** 是。member_scene_section → member_scene_section_group → member_tag(level-2 → level-3) → member_user_tag

---

### 3.2 提交场景匹配配置

`POST /member/social/match/config/save`

**用途：** 保存当前场景的标签选中 + 额外字段

**Request `AppSocialMatchConfigSaveReqVO`：**
```json
{
  "sceneCode": "buddy",
  "tagIds": [501, 101, 102, 301],
  "extraFields": {
    "wx": "mywechat123"
  }
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sceneCode | String | 是 | 场景编码 |
| tagIds | Long[] | 是 | 当前场景选中的全部标签ID（前端用 tagCode 做 :key，提交时转 tagId） |
| extraFields | Map | 否 | 非标签字段 |

**更新逻辑：**
```
1. 保存标签（覆盖式）：
   DELETE FROM member_user_tag WHERE user_id=X AND source='self'
   INSERT INTO member_user_tag (user_id, tag_id, source) VALUES ...

2. 保存配置到匹配任务最新记录：
   SELECT * FROM member_match_task WHERE user_id=X AND scene_id=Y ORDER BY create_time DESC LIMIT 1
   IF 有记录 → UPDATE match_config = {"extraFields": {...}}
   IF 无记录 → 暂不创建（等 create 时写入）
```

**Response：** `CommonResult<Boolean>`

---

### 3.3 获取上次额外字段

`GET /member/social/match/config/get?sceneCode=buddy`

**用途：** 进入场景页回填上次填的微信号、aboutMe 等

**查询逻辑：**
```
SELECT match_config FROM member_match_task 
WHERE user_id=X AND scene_id=Y ORDER BY create_time DESC LIMIT 1
解析 JSON → 返回 extraFields 部分
```

**Response `CommonResult<Map>`：**
```json
{"wx": "mywechat123", "aboutMe": "喜欢旅行"}
```
无配置返回：`{}`

---

### 3.4 创建匹配任务（修改）

`POST /member/social/match/create`

**Request 修改后 `AppSocialMatchCreateReqVO`：**
```json
{
  "sceneId": 1,
  "tagIds": [501, 101, 102, 301],
  "matchGoal": "想找人一起打球",
  "matchRemark": "周末有空",
  "extraFields": {
    "wx": "mywechat123"
  }
}
```

**流程（`MatchServiceImpl.createMatchTask`）：**
```
1. 校验同场景是否有进行中任务（status=0/1）→ 抛异常
2. 校验冷却时间 → 抛异常
3. 保存标签：先删后插 member_user_tag（source=self）
4. 构造 matchConfig = {"extraFields": {...}}
5. 创建 MemberMatchTaskDO：写入 matchConfig JSON
6. return taskId
```

---

### 3.5 批量保存用户标签（通用）

`POST /member/social/user-tag/batch-save`

**用途：** 非场景场景下保存标签（创建分身时选性格/职业）

```json
{"tagIds": [1, 5, 12], "source": "self"}
```

逻辑：先删后插，全量覆盖。

---

### 3.6 获取用户标签 ID 列表

`GET /member/social/user-tag/list`

**Response：** `[1, 5, 12]`

---

### 3.7 获取标签树（技能分类）

`GET /member/social/tag/tree?category=skill`

**用途：** 技能交换页三级树展示

**Response：**
```json
[
  {
    "id": 1,
    "tagName": "插画与绘画",
    "code": "skill_illustration",
    "category": "skill",
    "parentId": 0,
    "children": [
      {
        "id": 10,
        "tagName": "创意技能",
        "code": "creative",
        "parentId": 1,
        "children": [
          {"id": 100, "tagName": "数字插画", "code": "digital_illustration", "parentId": 10},
          {"id": 101, "tagName": "传统绘画", "code": "traditional_painting", "parentId": 10}
        ]
      }
    ]
  }
]
```

---

### 3.8 按分类获取标签

`GET /member/social/tag/list-by-category?category=profession`

**用途：** 分身页职业单选

**Response：**
```json
[
  {"id": 101, "tagName": "学生党", "code": "student"},
  {"id": 102, "tagName": "产品经理", "code": "pm"}
]
```

---

### 3.9 克隆接口增强

`POST /member/user/clone` — 新增 residence/mbti/profession
`PUT /member/user/clone` — 同上
`GET /member/user/clone/info` — 新增返回 residence/mbti/profession/tagIds

---

### 3.10 匹配结果查询（已有）

`GET /member/social/match/scene/status?sceneId=1` — 不变

---

### 3.11 Admin 接口

| 方法 | URL | 说明 |
|------|-----|------|
| GET | `/member/social/admin/tag/page` | 标签分页 |
| POST | `/member/social/admin/tag/create` | 新建标签（含 code） |
| PUT | `/member/social/admin/tag/update` | 修改标签 |
| DELETE | `/member/social/admin/tag/delete` | 删除（有子节点禁止） |
| POST | `/member/social/admin/section/save` | 配置区块+子分组 |
| GET | `/member/social/admin/section/list?sceneCode=buddy` | 查场景配置 |

---

## 4. 数据流

```
[进入场景页]
    │
    ├─ GET /scene/form?sceneCode=buddy
    │   ← [{sectionName, subGroups: [{groupName, groupCode, tags: [{tagId,tagCode,tagName,isSelected,sort}]}]}]
    │
    ├─ GET /match/config/get?sceneCode=buddy
    │   ← {"wx": "xxx", "aboutMe": "..."}
    │
    ├─ [用户操作：前端用 tagCode 做 :key，提交时传 tagId]
    │
    └─ POST /match/create
         {sceneId, tagIds, extraFields}
         → member_user_tag（先删后插）
         → member_match_task.match_config（写入 extraFields JSON）
```

## 5. 文件清单

### 5.1 新建文件

| 路径 | 说明 |
|------|------|
| `dal/dataobject/social/MemberSceneSectionDO.java` | 场景区块 DO |
| `dal/dataobject/social/MemberSceneSectionGroupDO.java` | 区块子分组关联 DO |
| `dal/dataobject/social/MemberUserTagDO.java` | 用户标签 DO |
| `dal/mysql/social/MemberSceneSectionMapper.java` | Mapper |
| `dal/mysql/social/MemberSceneSectionGroupMapper.java` | Mapper |
| `dal/mysql/social/MemberUserTagMapper.java` | Mapper |
| `controller/app/social/vo/AppSocialSceneFormRespVO.java` | 场景表单 VO |
| `controller/app/social/vo/AppSocialTagTreeRespVO.java` | 标签树 VO |
| `controller/app/social/vo/AppSocialTagItemRespVO.java` | 标签项 VO |
| `controller/app/social/vo/AppSocialMatchConfigSaveReqVO.java` | 配置保存请求 |
| `controller/app/social/vo/AppSocialUserTagBatchSaveReqVO.java` | 用户标签保存请求 |
| `controller/admin/social/vo/TagPageReqVO.java` | Admin 标签分页请求 |
| `controller/admin/social/vo/TagCreateReqVO.java` | Admin 标签创建请求 |
| `controller/admin/social/vo/TagUpdateReqVO.java` | Admin 标签更新请求 |
| `controller/admin/social/vo/TagRespVO.java` | Admin 标签响应 |
| `controller/admin/social/vo/SectionSaveReqVO.java` | Admin 区块配置请求 |
| `controller/admin/social/AdminSceneSectionController.java` | Admin 区块管理接口 |
| `controller/admin/social/AdminTagController.java` | Admin 标签管理接口 |

### 5.2 修改文件

| 路径 | 变更 |
|------|------|
| `dal/dataobject/social/MemberTagDO.java` | 新增 parentId, category, code |
| `dal/dataobject/member/MemberUserDO.java` | 新增 residence, mbti, profession |
| `dal/mysql/social/MemberTagMapper.java` | 新增按 parentId/category 查询方法 |
| `controller/app/social/AppSocialController.java` | 新增场景表单/配置/标签接口 |
| `controller/app/member/AppMemberUserController.java` | 克隆接口增强 |
| `convert/social/SocialConvert.java` | 新增转换方法 |
| `convert/member/MemberUserConvert.java` | 新增字段转换 |
| `service/social/SocialService.java` | 新增接口定义 |
| `service/social/SocialServiceImpl.java` | 实现 |
| `service/social/MatchService.java` | 修改 createMatchTask |
| `service/social/MatchServiceImpl.java` | 实现 |
| `service/member/MemberUserService.java` | 修改克隆接口 |
| `service/member/MemberUserServiceImpl.java` | 实现 |
| `enums/ErrorCodeConstants.java` | 追加错误码 |

---

## 6. 实现步骤

| 步骤 | 内容 | 文件数 |
|------|------|--------|
| 1 | member_tag 加 parent_id + category + code | 3 |
| 2 | member_scene_section + member_scene_section_group 表 + DO/Mapper | 6 |
| 3 | member_user_tag 表 + DO/Mapper | 3 |
| 4 | member_user 新增字段 | 2 |
| 5 | GET /scene/form 场景表单接口 | 4 |
| 6 | GET/POST /match/config 配置接口 | 4 |
| 7 | POST /match/create 改造 | 3 |
| 8 | POST/GET /user-tag 接口 | 3 |
| 9 | GET /tag/tree + /tag/list-by-category | 3 |
| 10 | 克隆接口增强 | 4 |
| 11 | Admin 标签 CRUD | 6 |
| 12 | Admin 场景区块配置 | 4 |
| 13 | 种子 SQL + 数据迁移 | 2 |
