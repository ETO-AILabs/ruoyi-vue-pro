# AI 社交需求 - 详细设计文档

## 1. 架构概览

### 1.1 模块范围
所有功能在 `yudao-module-member` 模块内实现，不跨模块。

### 1.2 代码分层（遵循现有风格）
```
controller/app/social/       # App 端接口（小程序调用）
  vo/                         # 请求/响应 VO
service/social/               # Service 接口 + 实现
convert/social/               # MapStruct 转换器
dal/dataobject/social/        # 数据对象（DO）
dal/mysql/social/             # MyBatis Plus Mapper
enums/                        # 错误码（追加到 ErrorCodeConstants）
```

### 1.3 路由前缀
- App 接口: `/member/social/*`, `/member/user/*`（复用已有 controller）
- Admin 接口: `/member/social/*`（场景管理、学校管理、头像管理）

---

## 2. 数据库设计

### 2.1 现有表扩展

#### member_user 新增字段
```sql
ALTER TABLE `member_user` ADD COLUMN `is_has_cloned` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否创建分身';
ALTER TABLE `member_user` ADD COLUMN `is_verified` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否学生认证';
ALTER TABLE `member_user` ADD COLUMN `longitude` decimal(10,6) COMMENT '经度';
ALTER TABLE `member_user` ADD COLUMN `latitude` decimal(10,6) COMMENT '纬度';
ALTER TABLE `member_user` ADD COLUMN `location` varchar(128) COMMENT '地理位置名称';
ALTER TABLE `member_user` ADD COLUMN `wechat` varchar(64) COMMENT '微信号';
ALTER TABLE `member_user` ADD COLUMN `user_desc` varchar(200) COMMENT '个人描述';
-- 标签复用已有 tag_ids 字段（List<Long>）
-- 头像复用已有 avatar 字段，直接存头像URL
```

### 2.2 新增表

#### member_scene（场景基础信息）
同文档定义，新增字段 `scene_config` varchar(300) for 匹配算法参数配置。

#### member_user_scene（用户场景配置）
同文档定义。

#### member_match_task（匹配任务）
修正：
- `match_status` tinyint NOT NULL COMMENT '0-未开始 1-进行中 2-失败 3-成功'
- `match_remark` varchar(300) COMMENT '匹配备注'
- `match_goal` varchar(200) COMMENT '匹配诉求'
- `scene_id` bigint NOT NULL

#### member_match_task_result（匹配结果）
修正：
- `matched_user_id` bigint NOT NULL（tinyint→bigint）
- `task_id` 注释修正为"匹配任务ID"

#### member_school_verify（学校认证申请）
同文档定义。

#### member_group_ext（学校扩展信息）
同文档定义。

#### member_avatars（预置头像库）
同文档定义。

#### member_nickname（昵称生成记录）
修正：
- `nick_name` varchar(64)（bigint→varchar）
- `gen_date` date（int→date）

#### member_tag（预置标签表）
```sql
CREATE TABLE `member_tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_name` varchar(32) NOT NULL COMMENT '标签名称',
  `sort` tinyint NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 1-可用 0-不可用',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预置标签';
```


---

## 3. 接口设计

### 3.1 App 端接口（小程序）

#### 分身相关（复用 AppMemberUserController）

| Method | Path | Description |
|--------|------|-------------|
| POST | `/member/user/clone` | 创建分身（填写昵称、生日、性别、位置、学校、标签、头像、个人描述） |
| GET | `/member/user/clone/info` | 获取分身信息（用于编辑） |
| PUT | `/member/user/clone` | 编辑分身信息 |
| POST | `/member/user/nickname/generate` | 生成随机昵称（调大模型，日限3次） |
| GET | `/member/user/avatar/random` | 随机获取头像 |

#### 学校相关

| Method | Path | Description |
|--------|------|-------------|
| GET | `/member/social/school/search` | 搜索学校（模糊匹配） |
| POST | `/member/social/school/verify` | 提交学校认证申请 |
| GET | `/member/social/school/verify/status` | 查询认证状态 |

#### 场景相关

| Method | Path | Description |
|--------|------|-------------|
| GET | `/member/social/scene/list` | 获取用户可见场景列表 |
| GET | `/member/social/scene/detail` | 场景详情 |

#### 匹配相关

| Method | Path | Description |
|--------|------|-------------|
| POST | `/member/social/match/create` | 创建匹配任务 |
| GET | `/member/social/match/task/page` | 分页查询我的匹配记录 |
| GET | `/member/social/match/result/list` | 获取匹配结果列表（12h内有效） |
| GET | `/member/social/user/detail` | 获取用户社交主页（userId，展示昵称、头像、年龄、性别、标签、学校、个人描述、微信号） |

#### 标签相关

| Method | Path | Description |
|--------|------|-------------|
| GET | `/member/social/tag/list` | 获取预置标签列表 |

### 3.2 Admin 端接口

| Method | Path | Description |
|--------|------|-------------|
| CRUD | `/member/social/scene/*` | 场景管理 |
| CRUD | `/member/social/tag/*` | 标签管理 |
| CRUD | `/member/social/avatar/*` | 头像管理 |
| CRUD | `/member/social/school/*` | 学校管理（member_group 扩展） |
| GET | `/member/social/school/verify/page` | 认证审核列表 |
| PUT | `/member/social/school/verify/audit` | 审核认证（通过/拒绝） |
| CRUD | `/member/social/match-config/*` | 匹配参数配置 |

---

## 4. 业务规则

### 4.1 分身创建
- is_has_cloned=0 时允许创建，创建后置为1
- 昵称、性别、生日、位置（经纬度+地理名称）为必填
- 标签、学校、个人描述、微信号为可选
- 头像：从 member_avatars 随机选一条，URL 写入 member_user.avatar 字段

### 4.2 昵称生成
- 调用大模型 API 生成昵称
- 记录到 member_nickname，gen_date 记录当天日期
- 每天最多3次（按 gen_date + user_id 统计）

### 4.3 场景可见性
- 用户有学校（已选学校）→ 取 scope_type=1, scope_id=学校ID 的场景
- 用户无学校 → 取 scope_type=0, scope_id=0 的兜底场景

### 4.4 匹配冷却（同场景）
- 检查是否有 match_status IN (0,1) 的匹配任务 → 有则拒绝
- 检查最近一次匹配任务的 create_time < 当前时间-24h → 未满则拒绝
- 冷却时间可配置（系统配置字典或 member_config 表）

### 4.5 匹配结果可见性
- 发起方 A 可见匹配对象 B 的信息
- 12h 过期配置：以 create_time 为准 + 12h
- 过期后不再展示

### 4.6 学校认证
- member_group.group_type=1 表示学校
- 认证审核通过后 member_user.is_verified=1
- member_school_verify 存储审核记录

---

## 5. 文件清单

### 5.1 新增文件

```
────────┬────────────────────────────────┬────────┐
  │ Method │              Path              │ Status │
  ├────────┼────────────────────────────────┼────────┤
  │ POST   │ /member/user/clone             │ ✅     │
  ├────────┼────────────────────────────────┼────────┤
  │ GET    │ /member/user/clone/info        │ ✅     │
  ├────────┼────────────────────────────────┼────────┤
  │ PUT    │ /member/user/clone             │ ✅     │
  ├────────┼────────────────────────────────┼────────┤
  │ POST   │ /member/user/nickname/generate │ ✅     │
  ├────────┼────────────────────────────────┼────────┤
  │ GET    │ /member/user/avatar/random     │ ✅     │
  └────────┴────────────────────────────────┴────────┘
# Controller - App
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/social/AppSocialController.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/social/vo/AppSocialSceneRespVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/social/vo/AppSocialTagRespVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/social/vo/AppSocialMatchCreateReqVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/social/vo/AppSocialMatchTaskRespVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/social/vo/AppSocialMatchResultRespVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/social/vo/AppSocialUserDetailRespVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/social/vo/AppSocialSchoolSearchRespVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/social/vo/AppSocialSchoolVerifyReqVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/social/vo/AppSocialSchoolVerifyRespVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/social/vo/AppSocialMatchPageReqVO.java

# VO for clone (extend user module)
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/user/vo/AppMemberUserCloneCreateReqVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/user/vo/AppMemberUserCloneInfoRespVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/user/vo/AppMemberUserCloneUpdateReqVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/user/vo/AppMemberUserNicknameGenerateRespVO.java

# Controller - Admin
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/admin/social/SceneController.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/admin/social/vo/SceneBaseVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/admin/social/vo/SceneCreateReqVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/admin/social/vo/SceneUpdateReqVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/admin/social/vo/SceneRespVO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/admin/social/vo/ScenePageReqVO.java

# DOs
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/dataobject/social/MemberSceneDO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/dataobject/social/MemberUserSceneDO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/dataobject/social/MemberMatchTaskDO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/dataobject/social/MemberMatchTaskResultDO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/dataobject/social/MemberSchoolVerifyDO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/dataobject/social/MemberGroupExtDO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/dataobject/social/MemberAvatarsDO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/dataobject/social/MemberNicknameRecordDO.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/dataobject/social/MemberTagDO.java

# Mappers
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/mysql/social/MemberSceneMapper.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/mysql/social/MemberUserSceneMapper.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/mysql/social/MemberMatchTaskMapper.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/mysql/social/MemberMatchTaskResultMapper.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/mysql/social/MemberSchoolVerifyMapper.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/mysql/social/MemberGroupExtMapper.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/mysql/social/MemberAvatarsMapper.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/mysql/social/MemberNicknameRecordMapper.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/mysql/social/MemberTagMapper.java

# Services
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/social/SocialService.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/social/SocialServiceImpl.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/social/MatchService.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/social/MatchServiceImpl.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/social/NicknameService.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/social/NicknameServiceImpl.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/social/AvatarService.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/social/AvatarServiceImpl.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/social/SchoolVerifyService.java
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/social/SchoolVerifyServiceImpl.java

# Convert
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/convert/social/SocialConvert.java

# Error codes appended to
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/enums/ErrorCodeConstants.java
```

### 5.2 修改文件
```
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/dataobject/user/MemberUserDO.java  # +新字段
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/mysql/user/MemberUserMapper.java  # +按需查询方法
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/convert/user/MemberUserConvert.java    # +clone VO转换
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/user/MemberUserService.java    # +clone方法
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/user/MemberUserServiceImpl.java # +clone实现
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/user/AppMemberUserController.java # +clone相关endpoint
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/enums/ErrorCodeConstants.java # +社交相关错误码
```

---

## 6. 任务拆分

### 任务 1：数据库 DDL + 实体类（DO）
- 编写全部 DDL（新建表 + 字段追加）
- 创建所有 MemberXxxDO.java
- 追加 MemberUserDO 新字段
- 预估：2-3h

### 任务 2：Mapper 层
- 创建所有 Mapper 接口
- 追加 MemberUserMapper 查询方法（selectByHasCloned 等）
- 预估：1h

### 任务 3：错误码 + Convert
- 追加社交模块错误码到 ErrorCodeConstants
- 创建 SocialConvert（MapStruct）
- 更新 MemberUserConvert 追加 clone VO 转换
- 预估：1h

### 任务 4：Service 层 - 核心业务
- SocialService：场景查询、学校搜索、标签列表
- NicknameService：昵称生成（大模型调用 + 日限校验）
- AvatarService：随机头像
- SchoolVerifyService：认证提交 + 审核
- MatchService：创建匹配任务、查询匹配结果（冷却校验、过期校验）
- 预估：4-5h

### 任务 5：Controller - App 端
- AppSocialController：场景、匹配、学校、标签接口
- 追加 AppMemberUserController：创建分身、分身信息、生成昵称、随机头像
- 预估：2h

### 任务 6：Controller - Admin 端
- SceneController：场景 CRUD
- 标签管理（可复用已有 MemberTagController 模式）
- 头像管理（简单 CRUD）
- 学校认证审核
- 预估：2h

### 任务 7：SQL 初始化脚本
- 创建 `yudao-module-member/src/main/resources/` 下的 mapper XML（如需要）
- 初始化数据：预置头像、预置标签、兜底场景
- 预估：1h

### 总预估：13-15h
