# AI社交需求
## 需求描述
    基于当前系统扩至支持社交场景，用户可以在系统里创建分身，基于分身属性匹配聊交友
## 功能描述
### 微信登录    
    用户首页点击登录唤起微信登录到系统（当前系统实现："ruoyi-vue-pro\yudao-module-member\src\main\java\cn\iocoder\yudao\module\member\controller\app\auth\AppAuthController.java"）

### 分身创建
    登录后支持用户填写分身昵称、生日、性别、定位自己的位置、搜索自己的学校、选择自己的标签，然后创建分身

### 场景推荐
    基于场景给用户匹配自己的分身好友
   用户可以选择不同的场景，在匹配时填写自己的微信，如果已填，直接显示，还有自己匹配的诉求，新建一个匹配任务，
   同一个场景匹配任务有冷却时间限制：1、如果当前已经有匹配任务不允许再建 2、如果最近一次匹配任务距离当前时间小于24小时不允许匹配（可配置）
   
   推荐结果展示：匹配后12小时（可配置）以内可以看到匹配结果，过12小时之后消失

### 场景配置
   每个用户看到的场景列表是不同的，比如有的基于学校，每个人会有一个默认配置做兜底

### 学校认证
    用户会有是否认证为学生，默认未认证，认证需要填写学校名称，上传学生证

## 表设计
### member_user 用户及其分身信息
    新增字段is_has_cloned 是否已经创建分身1-是 0-否，默认0
    is_verified 是否认证  0否 1是 默认0
    分身的创建 分身昵称、生日、性别 复用member_user
    longitude 经度 latitude 维度  location  地理位置名称
### member_scene 场景基础信息

    CREATE TABLE `member_scene` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
    `scene_code` bigint NOT NULL COMMENT '场景编码',
    `scene_type` tinyint NOT NULL COMMENT '场景类型 1-校外 2-校园',
    `scene_name` varchar(64) NOT NULL COMMENT '场景名称',
    `scene_status` tinyint NOT NULL COMMENT '状态1-可用 0-不可用',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场景基础信息';

### 场景配置

    用户在不同scene_type下不同scope的场景配置
    scope_type=0 scope_id=0 作为兜底
    CREATE TABLE `member_user_scene` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
    `scene_id` bigint NOT NULL COMMENT '场景主键 ID',
    `scene_type` tinyint NOT NULL COMMENT '场景类型 1-校外 2-校园',
    `sort` tinyint NOT NULL COMMENT '排序',
    `scope_id` varchar(64) NOT NULL COMMENT '用户的信息id',
    `scope_type` tinyint NOT NULL COMMENT '用户的信息id类型 0-默认 1-学校id',
    scene_config varchar(300)  NOT NULL COMMENT '场景配置',
    page_path varchar(64) NOT NULL COMMENT '页面路径',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场景配置';


### 匹配任务


    CREATE TABLE `member_match_task` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `scene_id` tinyint NOT NULL COMMENT '场景ID',
    `match_status` tinyint NOT NULL COMMENT '匹配状态0 未开始 1 进行中 2 失败 3 成功',
    `match_remark` varchar(300) NOT NULL COMMENT '匹配备注',
    match_config varchar(300)  NOT NULL COMMENT '匹配配置',
    finish_time  datetime COMMENT '匹配完成时间',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='匹配任务';


### 匹配结果

    CREATE TABLE `member_match_task_result` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
    `task_id` bigint NOT NULL COMMENT '用户ID',
    `matched_user_id` bigint NOT NULL COMMENT '被匹配的用户ID',
    match_score  float NOT NULL COMMENT '匹配得分',
    match_result varchar(300)  NOT NULL COMMENT '匹配结果',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='匹配结果';


### 申请认证

    CREATE TABLE `member_school_verify` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    group_id bigint NOT NULL COMMENT '学校id',
    school_url  varchar(300)  NOT NULL COMMENT '图片地址',
    verify_status  tinyint  NOT NULL DEFAULT 0 COMMENT '0 未审核 1-审核通过 2-拒绝',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='申请认证';


### 学校信息

    为了记录用户的学校信息，扩展了member_group表，新增group_type，等于1时为学校
    member_group_ext则表示了学校信息
    CREATE TABLE `member_group_ext` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
    `group_id` bigint NOT NULL COMMENT '用户ID',
    school_type float NOT NULL COMMENT '1 本科 2 专科 3 高中 等',
    school_tag  varchar(300)  NOT NULL COMMENT '985,211等',
    province varchar(64)    COMMENT '所属省份',
    city varchar(64)    COMMENT '所属市',
    district varchar(64)    COMMENT '所属区',
    school_status tinyint     COMMENT '1 正常 0 停用',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学校信息';


    
### 头像列表

    CREATE TABLE `member_avatars` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
    image_url  varchar(300) COMMENT '头像地址',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='头像地址';


    
### 用户生成昵称记录表

    CREATE TABLE `member_nickname` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
     user_id  bigint COMMENT '用户id',
     nick_name  varchar(64) COMMENT '用户昵称',
     gen_count int  COMMENT '生成次数',
     gen_date Date  COMMENT '生成次数',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='头像地址';
