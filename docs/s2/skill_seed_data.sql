-- ========================================
-- 技能交换场景种子数据（v2 — 飞书文档全量分类）
-- 参考: https://my.feishu.cn/docx/TktDd2jHFo1zu5xwMbtcEjXznxf
-- 运行前需先执行 s2_schema.sql 建表
-- ========================================

-- ========================================
-- 0. 清理旧数据（v1 的 291~295 分组及其子标签）
-- ========================================
-- 0a. 删除 section_group 关联
DELETE FROM `member_scene_section_group` WHERE `group_tag_id` IN (291, 292, 293, 294, 295);

-- 0b. 删除 level-4 叶子（旧 level-3 的子标签）
DELETE FROM `member_tag` WHERE `parent_id` IN (
  SELECT t.id FROM (
    SELECT id FROM `member_tag` WHERE `parent_id` IN (291, 292, 293, 294, 295)
  ) t
);

-- 0c. 删除 level-3 子分类
DELETE FROM `member_tag` WHERE `parent_id` IN (291, 292, 293, 294, 295);

-- 0d. 删除 level-2 分组
DELETE FROM `member_tag` WHERE `id` IN (291, 292, 293, 294, 295);

-- ========================================
-- 1. 根分类标签
-- ========================================
INSERT IGNORE INTO `member_tag` (`id`, `tag_name`, `parent_id`, `category`, `code`, `sort`, `status`) VALUES
(190,  '技能',     0, 'skill',          'skill',           10, 1);

-- ========================================
-- 2. 一级分类 (level-2) → 前端 tab
-- ========================================
INSERT IGNORE INTO `member_tag` (`id`, `tag_name`, `parent_id`, `category`, `code`, `sort`, `status`) VALUES
(296,  '运动健身',  190, '', 'sports_fitness',       1, 1),
(297,  '音乐表演',  190, '', 'music_performance',    2, 1),
(298,  '学术教育',  190, '', 'academic_education',   3, 1);

-- ========================================
-- 3. 二级分类 (level-3) → 前端子分组标题
-- ========================================
-- 运动健身 (296)
INSERT IGNORE INTO `member_tag` (`id`, `tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
(300, '球类运动',  296, 'ball_sports',      1, 1),
(301, '游泳健身',  296, 'swimming_fitness', 2, 1),
(302, '武术搏击',  296, 'martial_arts',     3, 1),
(303, '极限运动',  296, 'extreme_sports',   4, 1),
(304, '骑行跑步',  296, 'cycling_running',  5, 1);

-- 音乐表演 (297)
INSERT IGNORE INTO `member_tag` (`id`, `tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
(305, '乐器演奏',  297, 'instruments',       1, 1),
(306, '声乐',      297, 'vocal',             2, 1),
(307, '音乐制作',  297, 'music_production',  3, 1),
(308, '乐理知识',  297, 'music_theory',      4, 1),
(309, '舞蹈',      297, 'dance',             5, 1),
(310, '戏曲',      297, 'opera',             6, 1);

-- 学术教育 (298)
INSERT IGNORE INTO `member_tag` (`id`, `tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
(311, '学科辅导',  298, 'subject_tutoring', 1, 1),
(312, '考试辅导',  298, 'exam_prep',        2, 1);

-- ========================================
-- 4. 三级技能项 (level-4) → 前端 chips
-- ========================================

-- --- 球类运动 (300) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('乒乓球', 300, 'table_tennis',  1, 1),
('羽毛球', 300, 'badminton',     2, 1),
('篮球',   300, 'basketball',    3, 1),
('网球',   300, 'tennis',        4, 1),
('足球',   300, 'football',      5, 1),
('台球',   300, 'billiards',     6, 1),
('排球',   300, 'volleyball',    7, 1),
('匹克球', 300, 'pickleball',    8, 1);

-- --- 游泳健身 (301) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('力量训练', 301, 'strength_training', 1, 1),
('增肌减脂', 301, 'bodybuilding',      2, 1),
('瑜伽',     301, 'yoga_skill',        3, 1),
('自由泳',   301, 'freestyle_swim',    4, 1),
('蛙泳',     301, 'breaststroke',      5, 1),
('蝶泳',     301, 'butterfly_stroke',  6, 1);

-- --- 武术搏击 (302) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('拳击',     302, 'boxing',       1, 1),
('跆拳道',   302, 'taekwondo',    2, 1),
('散打',     302, 'sanda',        3, 1),
('太极拳',   302, 'tai_chi',      4, 1),
('泰拳',     302, 'muay_thai',    5, 1);

-- --- 极限运动 (303) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('滑板',     303, 'skateboard',   1, 1),
('陆冲',     303, 'surfskate',    2, 1),
('攀岩',     303, 'rock_climbing', 3, 1),
('冲浪',     303, 'surfing',      4, 1),
('户外徒步', 303, 'hiking_skill', 5, 1);

-- --- 骑行跑步 (304) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('公路骑行',   304, 'road_cycling',   1, 1),
('马拉松训练', 304, 'marathon',       2, 1),
('越野跑',     304, 'trail_running',  3, 1);

-- --- 乐器演奏 (305) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('吉他',     305, 'guitar',     1, 1),
('钢琴',     305, 'piano',      2, 1),
('尤克里里', 305, 'ukulele',    3, 1),
('架子鼓',   305, 'drums',      4, 1),
('小提琴',   305, 'violin',     5, 1),
('古筝',     305, 'guzheng',    6, 1),
('笛子',     305, 'flute',      7, 1);

-- --- 声乐 (306) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('流行唱法',   306, 'pop_vocal',      1, 1),
('美声',       306, 'bel_canto',      2, 1),
('合唱指导',   306, 'choir_coaching', 3, 1);

-- --- 音乐制作 (307) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('编曲',           307, 'arrangement',        1, 1),
('混音',           307, 'mixing',             2, 1),
('电子音乐制作',   307, 'electronic_music',   3, 1),
('Beat制作',       307, 'beat_making',        4, 1);

-- --- 乐理知识 (308) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('基础乐理',   308, 'basic_theory',   1, 1),
('和声学',     308, 'harmony',        2, 1),
('视唱练耳',   308, 'ear_training',   3, 1);

-- --- 舞蹈 (309) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('街舞',     309, 'street_dance',   1, 1),
('爵士舞',   309, 'jazz_dance',     2, 1),
('民族舞',   309, 'folk_dance',     3, 1),
('拉丁舞',   309, 'latin_dance',    4, 1),
('芭蕾基础', 309, 'ballet_basics',  5, 1);

-- --- 戏曲 (310) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('京剧',     310, 'peking_opera',   1, 1),
('昆曲',     310, 'kunqu_opera',    2, 1),
('越剧入门', 310, 'yueju_opera',    3, 1);

-- --- 学科辅导 (311) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('数学',       311, 'math',       1, 1),
('物理',       311, 'physics',    2, 1),
('化学',       311, 'chemistry',  3, 1),
('生物辅导',   311, 'biology',    4, 1);

-- --- 考试辅导 (312) ---
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('考研辅导',   312, 'grad_exam',    1, 1),
('考公辅导',   312, 'civil_exam',   2, 1),
('司法考试',   312, 'bar_exam',     3, 1),
('CPA',       312, 'cpa',          4, 1);

-- ========================================
-- 5. 场景区块 (幂等：已存在则跳过)
-- ========================================
INSERT IGNORE INTO `member_scene_section` (`scene_code`, `code`, `section_name`, `sort`) VALUES
('skill', 'i_can_teach',     '我会的技能', 1),
('skill', 'i_want_learn',    '我想学的技能', 2);

-- ========================================
-- 6. 区块-子分组关联（关联到新 level-2: 296/297/298）
-- ========================================
SET @ican_section = (SELECT id FROM `member_scene_section` WHERE code='i_can_teach' LIMIT 1);
SET @iwant_learn_section = (SELECT id FROM `member_scene_section` WHERE code='i_want_learn' LIMIT 1);

INSERT IGNORE INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@ican_section, 296, 1),
(@ican_section, 297, 2),
(@ican_section, 298, 3);

INSERT IGNORE INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@iwant_learn_section, 296, 1),
(@iwant_learn_section, 297, 2),
(@iwant_learn_section, 298, 3);

-- ========================================
-- 7. 场景定义（幂等）
-- ========================================
INSERT IGNORE INTO `member_scene` (`scene_code`, `scene_type`, `scene_name`, `scene_status`)
VALUES ('skill', 1, '技能交换', 1);

-- ========================================
-- 8. 用户-场景关联（默认对所有用户可见）
-- ========================================
SET @skill_scene_id = (SELECT id FROM `member_scene` WHERE scene_code='skill' LIMIT 1);
INSERT IGNORE INTO `member_user_scene` (`scene_id`, `scene_type`, `sort`, `scope_id`, `scope_type`)
VALUES (@skill_scene_id, 1, 3, '0', 0);
