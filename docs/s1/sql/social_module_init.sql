-- ========== AI 社交模块 - 数据库初始化脚本 ==========

-- 1. member_user 扩展字段
ALTER TABLE `member_user`
    ADD COLUMN `is_has_cloned` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否创建分身',
    ADD COLUMN `is_verified` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否学生认证',
    ADD COLUMN `longitude` decimal(10,6) COMMENT '经度',
    ADD COLUMN `latitude` decimal(10,6) COMMENT '纬度',
    ADD COLUMN `location` varchar(128) COMMENT '地理位置名称',
    ADD COLUMN `wechat` varchar(64) COMMENT '微信号',
    ADD COLUMN `user_desc` varchar(200) COMMENT '个人描述';

-- 2. member_group 扩展字段
ALTER TABLE `member_group`
    ADD COLUMN `group_type` tinyint COMMENT '分组类型：1-学校';

-- 3. 场景基础信息
CREATE TABLE IF NOT EXISTS `member_scene` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
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

-- 4. 用户场景配置
CREATE TABLE IF NOT EXISTS `member_user_scene` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `scene_id` bigint NOT NULL COMMENT '场景主键 ID',
    `scene_type` tinyint NOT NULL COMMENT '场景类型 1-校外 2-校园',
    `sort` tinyint NOT NULL COMMENT '排序',
    `scope_id` varchar(64) NOT NULL COMMENT '用户的信息id',
    `scope_type` tinyint NOT NULL COMMENT '用户的信息id类型 0-默认 1-学校id',
    `scene_config` varchar(300) NOT NULL COMMENT '场景配置',
    `page_path` varchar(64) NOT NULL COMMENT '页面路径',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场景配置';

-- 5. 匹配任务
CREATE TABLE IF NOT EXISTS `member_match_task` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `scene_id` bigint NOT NULL COMMENT '场景ID',
    `match_status` tinyint NOT NULL COMMENT '匹配状态 0-未开始 1-进行中 2-失败 3-成功',
    `match_remark` varchar(300) COMMENT '匹配备注',
    `match_goal` varchar(200) COMMENT '匹配诉求',
    `match_config` varchar(300) COMMENT '匹配配置',
    `finish_time` datetime COMMENT '匹配完成时间',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_user_scene` (`user_id`, `scene_id`),
    KEY `idx_match_status` (`match_status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='匹配任务';

-- 6. 匹配结果
CREATE TABLE IF NOT EXISTS `member_match_task_result` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `task_id` bigint NOT NULL COMMENT '匹配任务ID',
    `matched_user_id` bigint NOT NULL COMMENT '被匹配的用户ID',
    `match_score` float NOT NULL COMMENT '匹配得分',
    `match_result` varchar(300) COMMENT '匹配结果',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='匹配结果';

-- 7. 学校认证申请
CREATE TABLE IF NOT EXISTS `member_school_verify` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `group_id` bigint NOT NULL COMMENT '学校id',
    `school_url` varchar(300) COMMENT '图片地址',
    `verify_status` tinyint NOT NULL DEFAULT 0 COMMENT '0-未审核 1-审核通过 2-拒绝',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学校认证申请';

-- 8. 学校信息扩展
CREATE TABLE IF NOT EXISTS `member_group_ext` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `group_id` bigint NOT NULL COMMENT '学校id',
    `school_type` tinyint COMMENT '1-本科 2-专科 3-高中',
    `school_tag` varchar(300) COMMENT '985,211等标签',
    `province` varchar(64) COMMENT '所属省份',
    `city` varchar(64) COMMENT '所属市',
    `district` varchar(64) COMMENT '所属区',
    `school_status` tinyint COMMENT '1-正常 0-停用',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学校信息';

-- 9. 预置头像库
CREATE TABLE IF NOT EXISTS `member_avatars` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `image_url` varchar(300) COMMENT '头像地址',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预置头像库';

-- 10. 昵称生成记录
CREATE TABLE IF NOT EXISTS `member_nickname` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint COMMENT '用户id',
    `nick_name` varchar(64) COMMENT '用户昵称',
    `gen_count` int COMMENT '生成次数',
    `gen_date` date COMMENT '生成日期',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_user_date` (`user_id`, `gen_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='昵称生成记录';

-- 11. 预置标签表
CREATE TABLE IF NOT EXISTS `member_tag` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tag_name` varchar(32) NOT NULL COMMENT '标签名称',
    `sort` tinyint NOT NULL DEFAULT '0' COMMENT '排序',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 1-可用 0-不可用',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预置标签';

-- ========== 初始化数据 ==========

-- 预置标签
INSERT INTO `member_tag` (`tag_name`, `sort`, `status`) VALUES
('运动', 1, 1),
('音乐', 2, 1),
('阅读', 3, 1),
('游戏', 4, 1),
('摄影', 5, 1),
('旅行', 6, 1),
('美食', 7, 1),
('电影', 8, 1),
('绘画', 9, 1),
('编程', 10, 1),
('舞蹈', 11, 1),
('宠物', 12, 1);

-- 预置头像
INSERT INTO `member_avatars` (`image_url`) VALUES
('https://example.com/avatar/1.png'),
('https://example.com/avatar/2.png'),
('https://example.com/avatar/3.png'),
('https://example.com/avatar/4.png'),
('https://example.com/avatar/5.png'),
('https://example.com/avatar/6.png'),
('https://example.com/avatar/7.png'),
('https://example.com/avatar/8.png'),
('https://example.com/avatar/9.png'),
('https://example.com/avatar/10.png');

-- 兜底场景（scope_type=0, scope_id=0）
INSERT INTO `member_scene` (`scene_code`, `scene_type`, `scene_name`, `scene_status`) VALUES
(1001, 1, '兴趣交友', 1),
(1002, 1, '附近的人', 1),
(1003, 2, '校园交友', 1);

INSERT INTO `member_user_scene` (`scene_id`, `scene_type`, `sort`, `scope_id`, `scope_type`, `scene_config`, `page_path`) VALUES
(1, 1, 1, '0', 0, '{}', '/pages/match/interest'),
(2, 1, 2, '0', 0, '{}', '/pages/match/nearby'),
(3, 2, 1, '0', 0, '{}', '/pages/match/campus');
