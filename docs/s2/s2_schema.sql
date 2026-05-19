-- ========================================
-- S2 标签体系升级 - DDL
-- ========================================

-- 1. member_tag 表结构变更
ALTER TABLE `member_tag`
    ADD COLUMN `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父ID，0=根节点' AFTER `tag_name`,
    ADD COLUMN `category` varchar(32) DEFAULT '' COMMENT '分类编码' AFTER `parent_id`,
    ADD COLUMN `code` varchar(64) DEFAULT '' COMMENT '标签编码(前端用，同分组内唯一)' AFTER `category`,
    ADD INDEX `idx_parent_id` (`parent_id`),
    ADD INDEX `idx_category` (`category`),
    ADD INDEX `idx_code` (`code`);

-- 2. member_scene_section 场景区块标题表
CREATE TABLE IF NOT EXISTS `member_scene_section` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `scene_code` varchar(32) NOT NULL COMMENT '场景编码(buddy/love/skill/swap)',
    `code` varchar(64) NOT NULL COMMENT '区块编码(前端用，如buddy_activity/interest_tags/preferences)',
    `section_name` varchar(64) NOT NULL COMMENT '区块标题(想干嘛/兴趣标签/更多偏好)',
    `sort` tinyint DEFAULT 0 COMMENT '排序',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_scene_code` (`scene_code`),
    UNIQUE KEY `uk_scene_code` (`scene_code`, `code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景区块标题表';

-- 3. member_scene_section_group 区块-子分组关联表
CREATE TABLE IF NOT EXISTS `member_scene_section_group` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `section_id` bigint NOT NULL COMMENT '区块ID',
    `group_tag_id` bigint NOT NULL COMMENT '子分组标签ID(level-2 tag，如"生活类")',
    `sort` tinyint DEFAULT 0 COMMENT '分组排序',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_section_id` (`section_id`),
    KEY `idx_group_tag_id` (`group_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景区块子分组关联表';

-- 4. member_user_tag 用户标签表
CREATE TABLE IF NOT EXISTS `member_user_tag` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `tag_id` bigint NOT NULL COMMENT '标签ID',
    `source` varchar(32) DEFAULT 'self' COMMENT '来源(self=自选/auto=行为打标)',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_tag` (`user_id`, `tag_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签表';

-- 5. member_user 新增字段
ALTER TABLE `member_user`
    ADD COLUMN `residence` varchar(64) DEFAULT '' COMMENT '常驻地(格式：杭州·西湖区)' AFTER `location`,
    ADD COLUMN `mbti` varchar(8) DEFAULT '' COMMENT 'MBTI性格类型' AFTER `residence`,
    ADD COLUMN `profession` varchar(32) DEFAULT '' COMMENT '职业' AFTER `mbti`;
