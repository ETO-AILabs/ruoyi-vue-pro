-- ========================================
-- 匹配结果表扩展 - 增加前端展示所需字段
-- ========================================

ALTER TABLE `member_match_task_result`
    ADD COLUMN `ai_reason` varchar(500) DEFAULT '' COMMENT 'AI 推荐理由',
    ADD COLUMN `chat_tip` varchar(300) DEFAULT '' COMMENT '开始聊的建议',
    ADD COLUMN `extension` text COMMENT '场景扩展数据(JSON: currentStatus/teachSkill/wantSkill/offer/want/photos/conditionValue 等)',
    ADD COLUMN `is_added` tinyint NOT NULL DEFAULT 0 COMMENT '是否已添加 0-否 1-是',
    ADD COLUMN `added_time` datetime DEFAULT NULL COMMENT '添加时间';

-- 增加匹配结果索引（按用户查询）
CREATE INDEX `idx_matched_user` ON `member_match_task_result` (`matched_user_id`);
