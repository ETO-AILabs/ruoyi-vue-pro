-- ========================================
-- 前端四场景匹配结果展示 - 演示数据
-- 适用于 member 模块（合拍搭子/合拍伴侣/技能交换/易物交友）
-- 幂等：可重复执行，不会产生重复数据
-- ========================================

-- ========================================
-- 0. 匹配结果表结构升级（ai_reason/extension/is_added 等）
-- ========================================
SET @has_ai_reason := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'member_match_task_result' AND COLUMN_NAME = 'ai_reason');
SET @sql := IF(@has_ai_reason = 0,
    'ALTER TABLE `member_match_task_result`
        ADD COLUMN `ai_reason` varchar(500) DEFAULT "" COMMENT "AI 推荐理由" AFTER `match_result`,
        ADD COLUMN `chat_tip` varchar(300) DEFAULT "" COMMENT "开始聊的建议" AFTER `ai_reason`,
        ADD COLUMN `extension` text COMMENT "场景扩展数据(JSON)" AFTER `chat_tip`,
        ADD COLUMN `is_added` tinyint NOT NULL DEFAULT 0 COMMENT "是否已添加" AFTER `extension`,
        ADD COLUMN `added_time` datetime DEFAULT NULL COMMENT "添加时间" AFTER `is_added`',
    'SELECT 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========================================
-- 1. 写入四场景（场景编码与前端 SCENE_META 对齐）
-- ========================================
INSERT INTO `member_scene` (`scene_code`, `scene_type`, `scene_name`, `scene_status`, `creator`, `updater`, `tenant_id`)
SELECT 'buddy', 1, '合拍搭子', 1, 'system', 'system', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_scene` WHERE `scene_code` = 'buddy' AND tenant_id = 1);

INSERT INTO `member_scene` (`scene_code`, `scene_type`, `scene_name`, `scene_status`, `creator`, `updater`, `tenant_id`)
SELECT 'love', 1, '合拍伴侣', 1, 'system', 'system', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_scene` WHERE `scene_code` = 'love' AND tenant_id = 1);

INSERT INTO `member_scene` (`scene_code`, `scene_type`, `scene_name`, `scene_status`, `creator`, `updater`, `tenant_id`)
SELECT 'skill', 1, '技能交换', 1, 'system', 'system', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_scene` WHERE `scene_code` = 'skill' AND tenant_id = 1);

INSERT INTO `member_scene` (`scene_code`, `scene_type`, `scene_name`, `scene_status`, `creator`, `updater`, `tenant_id`)
SELECT 'swap', 1, '易物交友', 1, 'system', 'system', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_scene` WHERE `scene_code` = 'swap' AND tenant_id = 1);

-- ========================================
-- 2. 演示被匹配用户（4 组 × 2 人 = 8 人），ID 1001-1008
--    通过 mobile 唯一性兜底，避免重复插入
-- ========================================
INSERT INTO `member_user` (`id`, `mobile`, `nickname`, `avatar`, `status`, `sex`, `birthday`, `area_id`,
    `is_has_cloned`, `is_verified`, `longitude`, `latitude`, `location`, `wechat`, `user_desc`,
    `residence`, `mbti`, `profession`, `height`, `hometown`, `income`, `creator`, `updater`, `tenant_id`)
VALUES
(1001, '13800001001', '柠檬味的猫',  'https://i.pravatar.cc/300?img=47', 0, 2, '2001-03-12 00:00:00', NULL,
    1, 1, 120.150000, 30.270000, '西湖区文三路', 'wx_lemon_cat',  '周末喜欢逛展和探店',
    '杭州·西湖区', 'INFP', '产品经理', 165, '浙江·杭州', 3, 'system', 'system', 1),
(1002, '13800001002', '夜归人',     'https://i.pravatar.cc/300?img=12', 0, 1, '1999-08-20 00:00:00', NULL,
    1, 0, 120.210000, 30.260000, '拱墅区运河边', 'wx_nightwalk',  '夜跑爱好者，剧本杀重度玩家',
    '杭州·拱墅区', 'ENFP', '前端工程师', 178, '江苏·南京', 3, 'system', 'system', 1),
(1003, '13800001003', '麦芽糖',     'https://i.pravatar.cc/300?img=23', 0, 2, '2002-11-05 00:00:00', NULL,
    1, 1, 120.130000, 30.280000, '浙大紫金港',   'wx_malt',       '在校大学生·甜品DIY',
    '杭州·西湖区', 'ISFJ', '学生',      162, '浙江·宁波', 1, 'system', 'system', 1),
(1004, '13800001004', '风一样的帅', 'https://i.pravatar.cc/300?img=15', 0, 1, '1997-05-19 00:00:00', NULL,
    1, 0, 120.170000, 30.310000, '钱江新城',     'wx_windcool',   '冲浪滑雪 / 周末爬山',
    '杭州·上城区', 'ESTP', '金融分析师', 182, '浙江·温州', 5, 'system', 'system', 1),
(1005, '13800001005', '海边小屋',   'https://i.pravatar.cc/300?img=32', 0, 2, '2000-02-14 00:00:00', NULL,
    1, 0, 120.090000, 30.300000, '西溪湿地',     'wx_seahouse',   '会尤克里里，想学油画',
    '杭州·余杭区', 'INFP', '插画师',     168, '浙江·杭州', 2, 'system', 'system', 1),
(1006, '13800001006', '铁锤',       'https://i.pravatar.cc/300?img=33', 0, 1, '1998-12-08 00:00:00', NULL,
    1, 0, 120.220000, 30.250000, '武林广场',     'wx_ironhammer', '会咖啡拉花，想学调酒',
    '杭州·下城区', 'ENTJ', '咖啡店主理人', 175, '浙江·杭州', 4, 'system', 'system', 1),
(1007, '13800001007', '旧物控',     'https://i.pravatar.cc/300?img=44', 0, 2, '2001-07-22 00:00:00', NULL,
    1, 1, 120.140000, 30.290000, '浙江工商大学', 'wx_oldstuff',   '易物爱好者 / 闲置清仓',
    '杭州·西湖区', 'INFJ', '学生',      160, '浙江·杭州', 1, 'system', 'system', 1),
(1008, '13800001008', '相机小子',   'https://i.pravatar.cc/300?img=51', 0, 1, '1996-04-11 00:00:00', NULL,
    1, 0, 120.200000, 30.260000, '运河天地',     'wx_cameraguys', '胶片玩家 / 二手入坑',
    '杭州·拱墅区', 'ISTP', '摄影师',    180, '浙江·杭州', 4, 'system', 'system', 1)
ON DUPLICATE KEY UPDATE
    `nickname` = VALUES(`nickname`),
    `avatar` = VALUES(`avatar`),
    `user_desc` = VALUES(`user_desc`),
    `residence` = VALUES(`residence`),
    `mbti` = VALUES(`mbti`),
    `profession` = VALUES(`profession`),
    `wechat` = VALUES(`wechat`),
    `updater` = 'system',
    `update_time` = NOW();

-- ========================================
-- 3. 演示标签：先确保这些标签存在
-- ========================================
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `category`, `code`, `sort`, `status`, `creator`, `updater`, `tenant_id`)
VALUES
('摄影',    0, 'hobby', 'hobby_photo',     1, 1, 'system', 'system', 1),
('旅行',    0, 'hobby', 'hobby_travel',    2, 1, 'system', 'system', 1),
('美食',    0, 'hobby', 'hobby_food',      3, 1, 'system', 'system', 1),
('阅读',    0, 'hobby', 'hobby_read',      4, 1, 'system', 'system', 1),
('运动',    0, 'hobby', 'hobby_sport',     5, 1, 'system', 'system', 1),
('音乐',    0, 'hobby', 'hobby_music',     6, 1, 'system', 'system', 1),
('电影',    0, 'hobby', 'hobby_movie',     7, 1, 'system', 'system', 1),
('游戏',    0, 'hobby', 'hobby_game',      8, 1, 'system', 'system', 1),
('咖啡',    0, 'hobby', 'hobby_coffee',    9, 1, 'system', 'system', 1),
('手作',    0, 'hobby', 'hobby_diy',      10, 1, 'system', 'system', 1),
('桌游',    0, 'hobby', 'hobby_board',    11, 1, 'system', 'system', 1),
('剧本杀',  0, 'hobby', 'hobby_jubensha', 12, 1, 'system', 'system', 1)
ON DUPLICATE KEY UPDATE `status` = 1, `update_time` = NOW();

-- ========================================
-- 4. 演示被匹配用户的标签
-- ========================================
INSERT INTO `member_user_tag` (`user_id`, `tag_id`, `source`, `create_time`)
SELECT u.user_id, t.id, 'self', NOW()
FROM (
    SELECT 1001 AS user_id, '摄影' AS tag_name UNION ALL SELECT 1001, '旅行' UNION ALL SELECT 1001, '咖啡' UNION ALL SELECT 1001, '阅读' UNION ALL SELECT 1001, '电影'
    UNION ALL
    SELECT 1002, '桌游' UNION ALL SELECT 1002, '剧本杀' UNION ALL SELECT 1002, '运动' UNION ALL SELECT 1002, '美食' UNION ALL SELECT 1002, '游戏'
    UNION ALL
    SELECT 1003, '手作' UNION ALL SELECT 1003, '美食' UNION ALL SELECT 1003, '阅读' UNION ALL SELECT 1003, '电影'
    UNION ALL
    SELECT 1004, '运动' UNION ALL SELECT 1004, '旅行' UNION ALL SELECT 1004, '游戏' UNION ALL SELECT 1004, '摄影'
    UNION ALL
    SELECT 1005, '音乐' UNION ALL SELECT 1005, '手作' UNION ALL SELECT 1005, '阅读' UNION ALL SELECT 1005, '电影'
    UNION ALL
    SELECT 1006, '咖啡' UNION ALL SELECT 1006, '音乐' UNION ALL SELECT 1006, '美食' UNION ALL SELECT 1006, '手作'
    UNION ALL
    SELECT 1007, '手作' UNION ALL SELECT 1007, '阅读' UNION ALL SELECT 1007, '电影' UNION ALL SELECT 1007, '游戏'
    UNION ALL
    SELECT 1008, '摄影' UNION ALL SELECT 1008, '旅行' UNION ALL SELECT 1008, '游戏' UNION ALL SELECT 1008, '音乐'
) u
JOIN `member_tag` t ON t.`tag_name` = u.`tag_name`
ON DUPLICATE KEY UPDATE `update_time` = NOW();

-- ========================================
-- 5. 演示「当前用户」- 取数据库中第一个真实用户作为 viewUserId
--    注：如无真实用户，请先在 member_user 表插入至少 1 个，再执行本脚本
-- ========================================
SET @viewUserId := (SELECT id FROM `member_user` WHERE id NOT BETWEEN 1001 AND 1008 ORDER BY id ASC LIMIT 1);
-- 若无真实用户，则使用 admin（id=1）兜底
SET @viewUserId := IFNULL(@viewUserId, 1);

-- ========================================
-- 6. 为 viewUserId 写入四个匹配任务（每个场景一个，状态=已完成）
--    task_id 由后续 SELECT 取回
-- ========================================
SET @scene_buddy_id := (SELECT id FROM `member_scene` WHERE `scene_code` = 'buddy' AND tenant_id = 1 LIMIT 1);
SET @scene_love_id  := (SELECT id FROM `member_scene` WHERE `scene_code` = 'love'  AND tenant_id = 1 LIMIT 1);
SET @scene_skill_id := (SELECT id FROM `member_scene` WHERE `scene_code` = 'skill' AND tenant_id = 1 LIMIT 1);
SET @scene_swap_id  := (SELECT id FROM `member_scene` WHERE `scene_code` = 'swap'  AND tenant_id = 1 LIMIT 1);

INSERT INTO `member_match_task` (`user_id`, `scene_id`, `match_status`, `match_remark`, `match_goal`, `match_config`, `finish_time`, `creator`, `updater`, `tenant_id`, `create_time`, `update_time`)
SELECT @viewUserId, @scene_buddy_id, 3, '希望找个周末一起逛展拍照的搭子', '逛展拍照', '{}', NOW(), 'system', 'system', 1, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task` WHERE user_id = @viewUserId AND scene_id = @scene_buddy_id AND match_status = 3);

INSERT INTO `member_match_task` (`user_id`, `scene_id`, `match_status`, `match_remark`, `match_goal`, `match_config`, `finish_time`, `creator`, `updater`, `tenant_id`, `create_time`, `update_time`)
SELECT @viewUserId, @scene_love_id, 3, '希望能遇到一个共同成长的另一半', '认真相处', '{}', NOW(), 'system', 'system', 1, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task` WHERE user_id = @viewUserId AND scene_id = @scene_love_id AND match_status = 3);

INSERT INTO `member_match_task` (`user_id`, `scene_id`, `match_status`, `match_remark`, `match_goal`, `match_config`, `finish_time`, `creator`, `updater`, `tenant_id`, `create_time`, `update_time`)
SELECT @viewUserId, @scene_skill_id, 3, '想找会摄影的人一起学调酒', '互换技能', '{}', NOW(), 'system', 'system', 1, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task` WHERE user_id = @viewUserId AND scene_id = @scene_skill_id AND match_status = 3);

INSERT INTO `member_match_task` (`user_id`, `scene_id`, `match_status`, `match_remark`, `match_goal`, `match_config`, `finish_time`, `creator`, `updater`, `tenant_id`, `create_time`, `update_time`)
SELECT @viewUserId, @scene_swap_id, 3, '想把闲置 Kindle 换个蓝牙耳机', '易物', '{}', NOW(), 'system', 'system', 1, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task` WHERE user_id = @viewUserId AND scene_id = @scene_swap_id AND match_status = 3);

-- ========================================
-- 7. 演示匹配结果（每个场景 2-3 条）
-- ========================================

-- 7.1 合拍搭子 (buddy) - task 1: 1001, 1002, 1003
SET @task_buddy := (SELECT id FROM `member_match_task` WHERE user_id = @viewUserId AND scene_id = @scene_buddy_id AND match_status = 3 ORDER BY id DESC LIMIT 1);
INSERT INTO `member_match_task_result` (`task_id`, `matched_user_id`, `match_score`, `match_result`, `ai_reason`, `chat_tip`, `extension`, `is_added`, `creator`, `updater`, `tenant_id`)
SELECT @task_buddy, 1001, 95.5, '高度匹配', '你们都喜欢摄影与咖啡，闲暇时间也接近，周末可约。', '问问她最近去过哪些独立咖啡馆。',
       '{"currentStatus":"上班族"}', 0, 'system', 'system', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task_result` WHERE task_id = @task_buddy AND matched_user_id = 1001);

INSERT INTO `member_match_task_result` (`task_id`, `matched_user_id`, `match_score`, `match_result`, `ai_reason`, `chat_tip`, `extension`, `is_added`, `creator`, `updater`, `tenant_id`)
SELECT @task_buddy, 1002, 88.0, '高匹配', '你们都爱桌游剧本杀，作息偏夜猫子。', '组局去玩一局 3 小时剧本杀。',
       '{"currentStatus":"上班族"}', 0, 'system', 'system', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task_result` WHERE task_id = @task_buddy AND matched_user_id = 1002);

INSERT INTO `member_match_task_result` (`task_id`, `matched_user_id`, `match_score`, `match_result`, `ai_reason`, `chat_tip`, `extension`, `is_added`, `creator`, `updater`, `tenant_id`)
SELECT @task_buddy, 1003, 82.5, '良好匹配', '你俩都爱手作和阅读，常去杭州的小展。', '约一场甜品 DIY workshop。',
       '{"currentStatus":"学生"}', 0, 'system', 'system', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task_result` WHERE task_id = @task_buddy AND matched_user_id = 1003);

-- 7.2 合拍伴侣 (love) - task 2: 1004, 1005
SET @task_love := (SELECT id FROM `member_match_task` WHERE user_id = @viewUserId AND scene_id = @scene_love_id AND match_status = 3 ORDER BY id DESC LIMIT 1);
INSERT INTO `member_match_task_result` (`task_id`, `matched_user_id`, `match_score`, `match_result`, `ai_reason`, `chat_tip`, `extension`, `is_added`, `creator`, `updater`, `tenant_id`)
SELECT @task_love, 1004, 91.2, '灵魂契合', '你们 MBTI 互补，运动爱好一致，对未来节奏的看法也接近。', '聊聊他最近一次旅行印象最深的事。',
       '{"currentStatus":"上班族"}', 0, 'system', 'system', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task_result` WHERE task_id = @task_love AND matched_user_id = 1004);

INSERT INTO `member_match_task_result` (`task_id`, `matched_user_id`, `match_score`, `match_result`, `ai_reason`, `chat_tip`, `extension`, `is_added`, `creator`, `updater`, `tenant_id`)
SELECT @task_love, 1005, 86.8, '高度匹配', '你俩都偏好慢节奏生活，对艺术与阅读兴趣浓厚。', '问她最近在听什么专辑。',
       '{"currentStatus":"自由职业"}', 0, 'system', 'system', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task_result` WHERE task_id = @task_love AND matched_user_id = 1005);

-- 7.3 技能交换 (skill) - task 3: 1006(咖啡), 1005(尤克里里)
SET @task_skill := (SELECT id FROM `member_match_task` WHERE user_id = @viewUserId AND scene_id = @scene_skill_id AND match_status = 3 ORDER BY id DESC LIMIT 1);
INSERT INTO `member_match_task_result` (`task_id`, `matched_user_id`, `match_score`, `match_result`, `ai_reason`, `chat_tip`, `extension`, `is_added`, `creator`, `updater`, `tenant_id`)
SELECT @task_skill, 1006, 93.7, '完美互补', '他会咖啡拉花，想学调酒；你擅长调酒，他可以教你咖啡拉花。', '约一次工作室互访，先约上午 10 点。',
       '{"teachSkill":"咖啡拉花","wantSkill":"调酒入门"}', 0, 'system', 'system', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task_result` WHERE task_id = @task_skill AND matched_user_id = 1006);

INSERT INTO `member_match_task_result` (`task_id`, `matched_user_id`, `match_score`, `match_result`, `ai_reason`, `chat_tip`, `extension`, `is_added`, `creator`, `updater`, `tenant_id`)
SELECT @task_skill, 1005, 87.3, '良好互补', '她会尤克里里，想学油画；你可以教她摄影基础，她教你尤克里里入门。', '提议周末在湿地公园来一次外拍。',
       '{"teachSkill":"尤克里里","wantSkill":"油画入门"}', 0, 'system', 'system', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task_result` WHERE task_id = @task_skill AND matched_user_id = 1005);

-- 7.4 易物交友 (swap) - task 4: 1007, 1008
SET @task_swap := (SELECT id FROM `member_match_task` WHERE user_id = @viewUserId AND scene_id = @scene_swap_id AND match_status = 3 ORDER BY id DESC LIMIT 1);
INSERT INTO `member_match_task_result` (`task_id`, `matched_user_id`, `match_score`, `match_result`, `ai_reason`, `chat_tip`, `extension`, `is_added`, `creator`, `updater`, `tenant_id`)
SELECT @task_swap, 1007, 89.0, '换起来', '她想出 Kindle 入门 4 代 9 成新，想换无线鼠标。', '问她愿不愿意加点小礼物换。',
       '{"offer":"Kindle Paperwhite 4（9 成新）","want":"蓝牙鼠标","conditionValue":"9 成新","price":299,"photos":["https://picsum.photos/seed/kindle/600/600","https://picsum.photos/seed/kindle2/600/600"]}', 0, 'system', 'system', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task_result` WHERE task_id = @task_swap AND matched_user_id = 1007);

INSERT INTO `member_match_task_result` (`task_id`, `matched_user_id`, `match_score`, `match_result`, `ai_reason`, `chat_tip`, `extension`, `is_added`, `creator`, `updater`, `tenant_id`)
SELECT @task_swap, 1008, 84.5, '物品契合', '他有一只富士 X100V 想出，期望换便携蓝牙音箱。', '聊聊他这台机器的快门数。',
       '{"offer":"Fujifilm X100V（带皮套）","want":"JBL Flip 6 蓝牙音箱","conditionValue":"95 新","price":6800,"photos":["https://picsum.photos/seed/camera1/600/600","https://picsum.photos/seed/camera2/600/600","https://picsum.photos/seed/camera3/600/600"]}', 0, 'system', 'system', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `member_match_task_result` WHERE task_id = @task_swap AND matched_user_id = 1008);

-- ========================================
-- 8. 完成信息
-- ========================================
SELECT '演示数据初始化完成' AS msg, @viewUserId AS viewer_user_id;
