-- ========================================
-- S2 标签体系升级 - 种子数据
-- ========================================

-- ========================================
-- 0. member_user_tag 表结构升级（幂等）
--    旧 unique key: (user_id, tag_id)  → 跨场景/跨 section 不可重用同 tag
--    新 unique key: (user_id, tag_id, source)  → 同一 source 内不重复即可
-- ========================================
SET @has_legacy_uk := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'member_user_tag'
      AND INDEX_NAME = 'uk_user_tag'
);
SET @has_new_uk := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'member_user_tag'
      AND INDEX_NAME = 'uk_user_tag_source'
);
SET @sql := IF(@has_legacy_uk > 0 AND @has_new_uk = 0,
    'ALTER TABLE `member_user_tag` DROP INDEX `uk_user_tag`, ADD UNIQUE KEY `uk_user_tag_source` (`user_id`, `tag_id`, `source`)',
    'SELECT 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========================================
-- 1. 根分类标签 (parent_id=0, category有值)
-- ========================================
INSERT INTO `member_tag` (`id`, `tag_name`, `parent_id`, `category`, `code`, `sort`, `status`) VALUES
-- 性格
(100,  '性格',     0, 'personality',    'personality',     1, 1),
-- 职业
(110,  '职业',     0, 'profession',     'profession',      2, 1),
-- 兴趣
(120,  '兴趣',     0, 'interest',       'interest',        3, 1),
-- 搭子活动
(130,  '搭子活动', 0, 'buddy_activity',  'buddy_activity',  4, 1),
-- 交往目的
(140,  '交往目的', 0, 'love_purpose',   'love_purpose',    5, 1),
-- 恋爱观
(150,  '恋爱观',   0, 'love_views',     'love_views',      6, 1),
-- 运动
(160,  '运动',     0, 'sport',          'sport',           7, 1),
-- 生活
(170,  '生活',     0, 'lifestyle',      'lifestyle',       8, 1),
-- 爱好
(180,  '爱好',     0, 'hobby',          'hobby',           9, 1),
-- 技能
(190,  '技能',     0, 'skill',          'skill',           10, 1),
-- 物品类型
(200,  '物品类型', 0, 'item',           'item',            11, 1);

-- ========================================
-- 2. 子分组标签 (level-2, parent_id指向根, category='')
-- ========================================
INSERT INTO `member_tag` (`id`, `tag_name`, `parent_id`, `category`, `code`, `sort`, `status`) VALUES
-- 性格(100) 下
(201,  '情绪特质',  100, '', 'emotional_trait',   1, 1),
(202,  '人格特质',  100, '', 'personality_trait', 2, 1),
-- 兴趣(120) 下
(221,  '生活类',    120, '', 'living',            1, 1),
(222,  '兴趣类',    120, '', 'hobby',             2, 1),
(223,  '另类类',    120, '', 'alternative',       3, 1),
-- 恋爱观(150) 下
(251,  '态度',      150, '', 'attitude',          1, 1),
(252,  '底线',      150, '', 'bottom_line',       2, 1),
-- 技能(190) 下
(291,  '创意技能',  190, '', 'creative',          1, 1),
(292,  '商业技能',  190, '', 'business',          2, 1),
(293,  '技术技能',  190, '', 'tech',              3, 1),
(294,  '生活技能',  190, '', 'lifestyle_skill',   4, 1),
(295,  '教学技能',  190, '', 'teaching',          5, 1);

-- ========================================
-- 3. 更多偏好子分组 (复用性格标签，独立根分类)
-- 因为更多偏好里"性格特质/生活节奏/兴趣爱好"与已有分类不同，独立创建
-- ========================================
INSERT INTO `member_tag` (`id`, `tag_name`, `parent_id`, `category`, `code`, `sort`, `status`) VALUES
(261,  '性格特质',  0, 'buddy_personality', 'buddy_personality', 12, 1),
(262,  '生活节奏',  0, 'lifestyle_rhythm',  'lifestyle_rhythm',  13, 1),
(263,  '兴趣爱好',  0, 'buddy_hobby',       'buddy_hobby',       14, 1);

-- 更多偏好无子分组，叶子标签直接挂在261/262/263下

-- ========================================
-- 4. 叶子标签 (level-3, parent_id指向子分组)
-- ========================================

-- 4a. 情绪特质(201)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('情绪稳定', 201, 'emotional_stable', 1, 1),
('敏感细腻', 201, 'sensitive', 2, 1),
('乐观开朗', 201, 'optimistic', 3, 1),
('内向慢热', 201, 'introverted', 4, 1),
('外向健谈', 201, 'extroverted', 5, 1),
('理性冷静', 201, 'rational', 6, 1),
('幽默风趣', 201, 'humorous', 7, 1),
('温柔体贴', 201, 'gentle', 8, 1),
('独立自强', 201, 'independent', 9, 1),
('社恐',     201, 'social_anxiety', 10, 1),
('社牛',     201, 'social_butterfly', 11, 1),
('慢热型',   201, 'slow_warm', 12, 1);

-- 4b. 人格特质(202)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('责任心强', 202, 'responsible', 1, 1),
('有上进心', 202, 'ambitious', 2, 1),
('佛系随缘', 202, 'casual', 3, 1),
('完美主义', 202, 'perfectionist', 4, 1),
('随性洒脱', 202, 'free_spirited', 5, 1),
('浪漫主义', 202, 'romantic', 6, 1),
('现实主义', 202, 'realistic', 7, 1),
('有主见',   202, 'opinionated', 8, 1),
('爱宅家',   202, 'homebody', 9, 1);

-- 4c. 生活类(221)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('KTV',      221, 'ktv', 1, 1),
('旅行',     221, 'travel', 2, 1),
('电影',     221, 'movie', 3, 1),
('club',     221, 'club', 4, 1),
('戏剧',     221, 'drama', 5, 1),
('livehouse',221, 'livehouse', 6, 1),
('蹦迪',     221, 'clubbing', 7, 1),
('健身',     221, 'fitness', 8, 1),
('宠物',     221, 'pet', 9, 1),
('恋爱',     221, 'dating', 10, 1);

-- 4d. 兴趣类(222)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('机车',     222, 'motorcycle', 1, 1),
('音乐制作', 222, 'music_production', 2, 1),
('说唱',     222, 'rap', 3, 1),
('改装车',   222, 'car_mod', 4, 1),
('电竞',     222, 'esports', 5, 1),
('二次元',   222, 'anime', 6, 1),
('极限运动', 222, 'extreme_sports', 7, 1),
('摄影',     222, 'photography', 8, 1),
('电子烟',   222, 'vape', 9, 1),
('纹身',     222, 'tattoo', 10, 1),
('球鞋爱好者', 222, 'sneakerhead', 11, 1);

-- 4e. 另类类(223)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('灵魂社交', 223, 'soul_social', 1, 1),
('冥想',     223, 'meditation', 2, 1),
('lgbt',     223, 'lgbt', 3, 1),
('字母',     223, 'bdsm', 4, 1),
('怪癖',     223, 'quirks', 5, 1),
('释放',     223, 'release', 6, 1);

-- 4f. 搭子活动(130, 无子分组, 叶子直接挂根)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('正在找羽毛球搭子', 130, 'buddy_badminton', 1, 1),
('周末想去露营',     130, 'buddy_camping', 2, 1),
('今晚想拼单奶茶',   130, 'buddy_milk_tea', 3, 1),
('加班求安慰',       130, 'buddy_overtime', 4, 1),
('想找人一起看电影', 130, 'buddy_movie', 5, 1),
('想找人一起吃饭',   130, 'buddy_eat', 6, 1),
('想找人一起学习',   130, 'buddy_study', 7, 1),
('想找人一起健身',   130, 'buddy_workout', 8, 1),
('想找人一起旅行',   130, 'buddy_travel', 9, 1),
('想找人聊天',       130, 'buddy_chat', 10, 1),
('想找情绪树洞',     130, 'buddy_vent', 11, 1),
('想找游戏搭子',     130, 'buddy_gaming', 12, 1),
('想找饭搭子',       130, 'buddy_foodie', 13, 1),
('想找学习搭子',     130, 'buddy_study_buddy', 14, 1),
('想找健身搭子',     130, 'buddy_gym_buddy', 15, 1);

-- 4g. 交往目的(140, 无子分组)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('恋爱', 140, 'love_goal', 1, 1),
('结婚', 140, 'marriage', 2, 1),
('交朋友', 140, 'friendship', 3, 1);

-- 4h. 恋爱观-态度(251)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('认真奔结婚',   251, 'serious_marriage', 1, 1),
('先相处再确定', 251, 'take_time', 2, 1),
('慢热型恋爱',   251, 'slow_love', 3, 1),
('日久生情型',   251, 'grow_love', 4, 1),
('一见钟情型',   251, 'love_at_first', 5, 1),
('理性恋爱',     251, 'rational_love', 6, 1);

-- 4i. 恋爱观-底线(252)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('拒绝冷暴力', 252, 'no_cold_violence', 1, 1),
('不接受出轨', 252, 'no_cheating', 2, 1),
('不接受PUA',  252, 'no_pua', 3, 1),
('接受AA制',   252, 'aa_ok', 4, 1),
('接受婚前同居', 252, 'cohabitation_ok', 5, 1),
('接受丁克',   252, 'dink_ok', 6, 1);

-- 4j. 运动(160, 无子分组)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('篮球',   160, 'basketball', 1, 1),
('羽毛球', 160, 'badminton', 2, 1),
('游泳',   160, 'swimming', 3, 1),
('跑步',   160, 'running', 4, 1),
('健身撸铁', 160, 'gym', 5, 1),
('瑜伽',   160, 'yoga', 6, 1),
('骑行',   160, 'cycling', 7, 1),
('徒步',   160, 'hiking', 8, 1),
('滑雪',   160, 'skiing', 9, 1),
('飞盘',   160, 'frisbee', 10, 1),
('攀岩',   160, 'climbing', 11, 1),
('冲浪',   160, 'surfing', 12, 1);

-- 4k. 生活(170, 无子分组)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('早睡早起',   170, 'early_bird', 1, 1),
('熬夜党',     170, 'night_owl', 2, 1),
('无辣不欢',   170, 'spicy_lover', 3, 1),
('素食主义',   170, 'vegetarian', 4, 1),
('奶茶爱好者', 170, 'milk_tea_fan', 5, 1),
('咖啡续命',   170, 'coffee_addict', 6, 1),
('会做饭',     170, 'cooking', 7, 1),
('外卖党',     170, 'takeout_fan', 8, 1);

-- 4l. 爱好(180, 无子分组)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('读书',     180, 'reading', 1, 1),
('摄影',     180, 'photography_hobby', 2, 1),
('旅行',     180, 'travel_hobby', 3, 1),
('剧本杀',   180, 'murder_mystery', 4, 1),
('KTV',      180, 'ktv_hobby', 5, 1),
('livehouse',180, 'livehouse_hobby', 6, 1),
('电竞',     180, 'esports_hobby', 7, 1),
('cosplay',  180, 'cosplay', 8, 1),
('潮玩',     180, 'trendy_toy', 9, 1),
('脱口秀',   180, 'standup', 10, 1),
('音乐节',   180, 'music_festival', 11, 1),
('桌游',     180, 'board_game', 12, 1);

-- 4m. 职业(110, 无子分组)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('学生党',     110, 'student', 1, 1),
('产品经理',   110, 'pm', 2, 1),
('设计师',     110, 'designer', 3, 1),
('程序员',     110, 'programmer', 4, 1),
('运营',       110, 'operator', 5, 1),
('市场',       110, 'marketing', 6, 1),
('销售',       110, 'sales', 7, 1),
('教师',       110, 'teacher', 8, 1),
('医生',       110, 'doctor', 9, 1),
('护士',       110, 'nurse', 10, 1),
('公务员',     110, 'civil_servant', 11, 1),
('自由职业者', 110, 'freelancer', 12, 1),
('斜杠青年',   110, 'slash', 13, 1),
('创业者',     110, 'entrepreneur', 14, 1),
('艺术家',     110, 'artist', 15, 1),
('音乐人',     110, 'musician', 16, 1),
('摄影师',     110, 'photographer_pro', 17, 1),
('作家',       110, 'writer', 18, 1),
('律师',       110, 'lawyer', 19, 1),
('会计',       110, 'accountant', 20, 1),
('金融从业者', 110, 'finance', 21, 1),
('其他',       110, 'other_profession', 22, 1);

-- 4n. 更多偏好-性格特质(261, 无子分组)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('情绪稳定', 261, 'bp_emotional_stable', 1, 1),
('乐观开朗', 261, 'bp_optimistic', 2, 1),
('内向慢热', 261, 'bp_introverted', 3, 1),
('外向健谈', 261, 'bp_extroverted', 4, 1),
('幽默风趣', 261, 'bp_humorous', 5, 1),
('温柔体贴', 261, 'bp_gentle', 6, 1),
('独立自强', 261, 'bp_independent', 7, 1),
('社恐',     261, 'bp_social_anxiety', 8, 1),
('社牛',     261, 'bp_social_butterfly', 9, 1),
('佛系青年', 261, 'bp_buddha', 10, 1),
('治愈系',   261, 'bp_healing', 11, 1),
('小太阳',   261, 'bp_sunshine', 12, 1);

-- 4o. 更多偏好-生活节奏(262, 无子分组)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('早睡早起',     262, 'lr_early_bird', 1, 1),
('熬夜修仙',     262, 'lr_night_owl', 2, 1),
('咖啡续命',     262, 'lr_coffee', 3, 1),
('奶茶爱好者',   262, 'lr_milk_tea', 4, 1),
('周末宅家',     262, 'lr_homebody', 5, 1),
('周末必出门',   262, 'lr_go_out', 6, 1),
('citywalk爱好者', 262, 'lr_citywalk', 7, 1),
('探店达人',     262, 'lr_explorer', 8, 1),
('健身达人',     262, 'lr_fitness', 9, 1);

-- 4p. 更多偏好-兴趣爱好(263, 无子分组)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('独立摇滚', 263, 'bh_indie_rock', 1, 1),
('民谣',     263, 'bh_folk', 2, 1),
('电子音乐', 263, 'bh_electronic', 3, 1),
('科幻电影', 263, 'bh_scifi', 4, 1),
('文艺电影', 263, 'bh_arthouse', 5, 1),
('动漫',     263, 'bh_anime', 6, 1),
('瑜伽',     263, 'bh_yoga', 7, 1),
('跑步',     263, 'bh_running', 8, 1),
('羽毛球',   263, 'bh_badminton', 9, 1),
('游泳',     263, 'bh_swimming', 10, 1),
('剧本杀',   263, 'bh_murder_mystery', 11, 1),
('桌游',     263, 'bh_board_game', 12, 1);

-- 4q. 物品类型(200, 无子分组)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('书籍',     200, 'book', 1, 1),
('盲盒',     200, 'blind_box', 2, 1),
('手办',     200, 'figure', 3, 1),
('潮玩',     200, 'trendy_toy_item', 4, 1),
('黑胶唱片', 200, 'vinyl', 5, 1),
('手账',     200, 'journal', 6, 1),
('数码配件', 200, 'digital_acc', 7, 1),
('衣物鞋包', 200, 'clothing', 8, 1),
('其他',     200, 'other_item', 9, 1);

-- ========================================
-- 5. 技能子分组下的叶子标签 (skill场景)
-- ========================================

-- 5a. 创意技能(291) -> 插画与绘画、视觉设计、摄影、视频制作、音乐与音频、写作与文学、手工与传统艺术
-- 先插入7个子分组作为技能下的level-3 (用code区分)
-- 注意：技能特殊，需要4层：技能>创意技能>插画与绘画>数字插画
-- 所以插画与绘画是level-3, 数字插画是level-4
-- 但我们最多只到level-3（叶子标签），所以技能场景用额外结构

-- 其实技能这里，skill页面展示的是：分类tab(创意/商业/技术/生活/教学) -> 子分类 -> 技能列表
-- 已经由SKILL_DATA结构定义，可以在应用层用tag树实现，不一定要4层
-- 简单方案：创意技能(291)下直接挂子分类标签(level-3)，子分类下再挂叶子标签(level-4)
-- 但member_tag只支持3层，超过需要parent_id连续
-- 改为：叶子标签的parent_id=子分类tag即可（递归不限层数，只是查询时最多3级树）

-- 创意技能下的子分类 (level-3)
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('插画与绘画',   291, 'illustration', 1, 1),
('视觉设计',     291, 'visual_design', 2, 1),
('摄影',         291, 'photography_skill', 3, 1),
('视频制作',     291, 'video_production', 4, 1),
('音乐与音频',   291, 'music_audio', 5, 1),
('写作与文学',   291, 'writing', 6, 1),
('手工与传统艺术', 291, 'crafts', 7, 1);

-- 插画与绘画的子标签 (level-4, parent_id=last_insert)
-- 由于上面INSERT会自增，需要获取ID。用一个技巧：先查询ID再插入
-- 或者直接用已知ID (假设上面7条从10000开始)

-- 改用先查询后插入的方式
SET @illustration_id = (SELECT id FROM member_tag WHERE code='illustration' AND parent_id=291 LIMIT 1);
SET @visual_design_id = (SELECT id FROM member_tag WHERE code='visual_design' AND parent_id=291 LIMIT 1);
SET @photo_skill_id = (SELECT id FROM member_tag WHERE code='photography_skill' AND parent_id=291 LIMIT 1);
SET @video_id = (SELECT id FROM member_tag WHERE code='video_production' AND parent_id=291 LIMIT 1);
SET @music_id = (SELECT id FROM member_tag WHERE code='music_audio' AND parent_id=291 LIMIT 1);
SET @writing_id = (SELECT id FROM member_tag WHERE code='writing' AND parent_id=291 LIMIT 1);
SET @crafts_id = (SELECT id FROM member_tag WHERE code='crafts' AND parent_id=291 LIMIT 1);

INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('数字插画', @illustration_id, 'digital_illustration', 1, 1),
('传统绘画', @illustration_id, 'traditional_painting', 2, 1),
('角色设计', @illustration_id, 'character_design', 3, 1),
('场景概念设计', @illustration_id, 'concept_art', 4, 1),
('插画商业应用', @illustration_id, 'commercial_illustration', 5, 1),
('条漫与漫画', @illustration_id, 'comic', 6, 1),
('游戏原画', @illustration_id, 'game_art', 7, 1),
('国风插画', @illustration_id, 'chinese_style', 8, 1),
('平面设计', @visual_design_id, 'graphic_design', 1, 1),
('UI/UX设计', @visual_design_id, 'ui_ux', 2, 1),
('品牌视觉设计', @visual_design_id, 'brand_design', 3, 1),
('包装设计', @visual_design_id, 'packaging', 4, 1),
('3D设计', @visual_design_id, '3d_design', 5, 1),
('室内空间设计', @visual_design_id, 'interior_design', 6, 1),
('人像摄影', @photo_skill_id, 'portrait_photography', 1, 1),
('风光摄影', @photo_skill_id, 'landscape_photography', 2, 1),
('商业摄影', @photo_skill_id, 'commercial_photography', 3, 1),
('手机摄影', @photo_skill_id, 'mobile_photography', 4, 1),
('后期修图', @photo_skill_id, 'photo_editing', 5, 1),
('视频剪辑', @video_id, 'video_editing', 1, 1),
('短视频创作', @video_id, 'short_video', 2, 1),
('Vlog制作', @video_id, 'vlog', 3, 1),
('纪录片拍摄', @video_id, 'documentary', 4, 1),
('视频调色', @video_id, 'color_grading', 5, 1),
('影视特效', @video_id, 'vfx', 6, 1),
('音乐制作', @music_id, 'music_production_skill', 1, 1),
('歌曲创作', @music_id, 'songwriting', 2, 1),
('电子音乐制作', @music_id, 'electronic_music', 3, 1),
('影视配乐', @music_id, 'film_scoring', 4, 1),
('播客制作', @music_id, 'podcast', 5, 1),
('音频后期处理', @music_id, 'audio_post', 6, 1),
('创意写作', @writing_id, 'creative_writing', 1, 1),
('剧本写作', @writing_id, 'script_writing', 2, 1),
('非虚构写作', @writing_id, 'nonfiction', 3, 1),
('自媒体写作', @writing_id, 'self_media_writing', 4, 1),
('商业文案', @writing_id, 'copywriting', 5, 1),
('网文创作', @writing_id, 'web_novel', 6, 1),
('陶艺', @crafts_id, 'pottery', 1, 1),
('刺绣', @crafts_id, 'embroidery', 2, 1),
('编织', @crafts_id, 'knitting', 3, 1),
('珠宝设计', @crafts_id, 'jewelry_design', 4, 1),
('水彩画', @crafts_id, 'watercolor', 5, 1),
('篆刻', @crafts_id, 'seal_carving', 6, 1),
('羊毛毡', @crafts_id, 'wool_felt', 7, 1);

-- 5b. 商业技能(292) -> 子分类
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('创业与商业管理', 292, 'entrepreneurship', 1, 1),
('市场营销', 292, 'marketing_skill', 2, 1),
('职业发展', 292, 'career_dev', 3, 1),
('财务与投资', 292, 'finance_investment', 4, 1),
('电商与零售', 292, 'ecommerce', 5, 1);

SET @entre_id = (SELECT id FROM member_tag WHERE code='entrepreneurship' AND parent_id=292 LIMIT 1);
SET @mkt_skill_id = (SELECT id FROM member_tag WHERE code='marketing_skill' AND parent_id=292 LIMIT 1);
SET @career_id = (SELECT id FROM member_tag WHERE code='career_dev' AND parent_id=292 LIMIT 1);
SET @fin_id = (SELECT id FROM member_tag WHERE code='finance_investment' AND parent_id=292 LIMIT 1);
SET @ecom_id = (SELECT id FROM member_tag WHERE code='ecommerce' AND parent_id=292 LIMIT 1);

INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('创业基础', @entre_id, 'startup_basics', 1, 1),
('商业模式设计', @entre_id, 'business_model', 2, 1),
('团队管理', @entre_id, 'team_management', 3, 1),
('项目管理', @entre_id, 'project_management', 4, 1),
('供应链管理', @entre_id, 'supply_chain', 5, 1),
('数字营销', @mkt_skill_id, 'digital_marketing', 1, 1),
('社交媒体营销', @mkt_skill_id, 'social_media_mkt', 2, 1),
('内容营销', @mkt_skill_id, 'content_marketing', 3, 1),
('SEO/SEM', @mkt_skill_id, 'seo_sem', 4, 1),
('私域流量运营', @mkt_skill_id, 'private_domain', 5, 1),
('简历与求职信写作', @career_id, 'resume_writing', 1, 1),
('面试技巧', @career_id, 'interview_skills', 2, 1),
('职场沟通', @career_id, 'workplace_comm', 3, 1),
('时间管理', @career_id, 'time_management_skill', 4, 1),
('副业变现', @career_id, 'side_hustle', 5, 1),
('个人理财', @fin_id, 'personal_finance', 1, 1),
('投资基础', @fin_id, 'investment_basics', 2, 1),
('创业融资', @fin_id, 'startup_funding', 3, 1),
('税务规划', @fin_id, 'tax_planning', 4, 1),
('加密货币与区块链', @fin_id, 'crypto_blockchain', 5, 1),
('独立站搭建', @ecom_id, 'dropshipping_site', 1, 1),
('亚马逊运营', @ecom_id, 'amazon_ops', 2, 1),
('跨境电商', @ecom_id, 'cross_border', 3, 1),
('电商选品', @ecom_id, 'product_selection', 4, 1),
('直播带货', @ecom_id, 'live_selling', 5, 1);

-- 5c. 技术技能(293) -> 子分类
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('编程与开发', 293, 'programming_dev', 1, 1),
('数据科学与分析', 293, 'data_science', 2, 1),
('AI与生成式工具', 293, 'ai_tools', 3, 1),
('设计工具与技术', 293, 'design_tools', 4, 1),
('云计算与运维', 293, 'cloud_devops', 5, 1);

SET @prog_id = (SELECT id FROM member_tag WHERE code='programming_dev' AND parent_id=293 LIMIT 1);
SET @ds_id = (SELECT id FROM member_tag WHERE code='data_science' AND parent_id=293 LIMIT 1);
SET @ai_id = (SELECT id FROM member_tag WHERE code='ai_tools' AND parent_id=293 LIMIT 1);
SET @design_tool_id = (SELECT id FROM member_tag WHERE code='design_tools' AND parent_id=293 LIMIT 1);
SET @cloud_id = (SELECT id FROM member_tag WHERE code='cloud_devops' AND parent_id=293 LIMIT 1);

INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('前端开发', @prog_id, 'frontend', 1, 1),
('后端开发', @prog_id, 'backend', 2, 1),
('全栈开发', @prog_id, 'fullstack', 3, 1),
('移动应用开发', @prog_id, 'mobile_dev', 4, 1),
('游戏开发', @prog_id, 'game_dev', 5, 1),
('数据库设计', @prog_id, 'database', 6, 1),
('API开发', @prog_id, 'api_dev', 7, 1),
('Web安全', @prog_id, 'web_security', 8, 1),
('Python数据分析', @ds_id, 'python_data', 1, 1),
('SQL数据库', @ds_id, 'sql', 2, 1),
('数据可视化', @ds_id, 'data_viz', 3, 1),
('机器学习基础', @ds_id, 'ml_basics', 4, 1),
('大数据处理', @ds_id, 'big_data', 5, 1),
('A/B测试', @ds_id, 'ab_testing', 6, 1),
('Midjourney/DALL-E提示词工程', @ai_id, 'midjourney', 1, 1),
('ChatGPT应用', @ai_id, 'chatgpt', 2, 1),
('AI绘画', @ai_id, 'ai_art', 3, 1),
('AI视频生成', @ai_id, 'ai_video', 4, 1),
('大模型应用开发', @ai_id, 'llm_app', 5, 1),
('RAG技术', @ai_id, 'rag', 6, 1),
('Photoshop高级技巧', @design_tool_id, 'photoshop', 1, 1),
('Illustrator进阶', @design_tool_id, 'illustrator', 2, 1),
('Figma教程', @design_tool_id, 'figma', 3, 1),
('Blender 3D建模', @design_tool_id, 'blender', 4, 1),
('Premiere Pro高级剪辑', @design_tool_id, 'premiere', 5, 1),
('After Effects特效', @design_tool_id, 'after_effects', 6, 1),
('CAD设计', @design_tool_id, 'cad', 7, 1),
('云计算', @cloud_id, 'cloud_computing', 1, 1),
('Linux系统', @cloud_id, 'linux', 2, 1),
('Docker容器化', @cloud_id, 'docker', 3, 1),
('Kubernetes', @cloud_id, 'k8s', 4, 1),
('网络安全基础', @cloud_id, 'cybersecurity', 5, 1),
('DevOps', @cloud_id, 'devops', 6, 1);

-- 5d. 生活技能(294) -> 子分类
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('健康与健身', 294, 'health_fitness', 1, 1),
('美食与烹饪', 294, 'cooking_skill', 2, 1),
('家居与生活', 294, 'home_living', 3, 1),
('旅行与户外', 294, 'travel_outdoor', 4, 1),
('个人成长', 294, 'self_growth', 5, 1);

SET @health_id = (SELECT id FROM member_tag WHERE code='health_fitness' AND parent_id=294 LIMIT 1);
SET @cook_id = (SELECT id FROM member_tag WHERE code='cooking_skill' AND parent_id=294 LIMIT 1);
SET @home_id = (SELECT id FROM member_tag WHERE code='home_living' AND parent_id=294 LIMIT 1);
SET @travel_out_id = (SELECT id FROM member_tag WHERE code='travel_outdoor' AND parent_id=294 LIMIT 1);
SET @growth_id = (SELECT id FROM member_tag WHERE code='self_growth' AND parent_id=294 LIMIT 1);

INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('瑜伽', @health_id, 'yoga_skill', 1, 1),
('普拉提', @health_id, 'pilates', 2, 1),
('力量训练', @health_id, 'strength_training', 3, 1),
('减脂塑形', @health_id, 'fat_loss', 4, 1),
('冥想与正念', @health_id, 'mindfulness', 5, 1),
('睡眠改善', @health_id, 'sleep_improvement', 6, 1),
('心理健康', @health_id, 'mental_health', 7, 1),
('家常菜', @cook_id, 'home_cooking', 1, 1),
('烘焙', @cook_id, 'baking', 2, 1),
('西餐', @cook_id, 'western_food', 3, 1),
('甜品制作', @cook_id, 'dessert', 4, 1),
('咖啡拉花', @cook_id, 'latte_art', 5, 1),
('调酒', @cook_id, 'cocktail', 6, 1),
('素食烹饪', @cook_id, 'vegan_cooking', 7, 1),
('室内装饰', @home_id, 'interior_decor', 1, 1),
('收纳整理', @home_id, 'organizing', 2, 1),
('园艺', @home_id, 'gardening', 3, 1),
('DIY家居改造', @home_id, 'diy_home', 4, 1),
('花艺', @home_id, 'floral', 5, 1),
('宠物护理', @home_id, 'pet_care', 6, 1),
('旅行规划', @travel_out_id, 'travel_planning', 1, 1),
('户外徒步', @travel_out_id, 'hiking_skill', 2, 1),
('露营', @travel_out_id, 'camping', 3, 1),
('登山', @travel_out_id, 'mountaineering', 4, 1),
('潜水', @travel_out_id, 'diving', 5, 1),
('滑雪', @travel_out_id, 'skiing_skill', 6, 1),
('专注力训练', @growth_id, 'focus_training', 1, 1),
('情绪管理', @growth_id, 'emotion_management', 2, 1),
('沟通技巧', @growth_id, 'communication', 3, 1),
('批判性思维', @growth_id, 'critical_thinking', 4, 1),
('阅读技巧', @growth_id, 'reading_skill', 5, 1),
('目标设定', @growth_id, 'goal_setting', 6, 1);

-- 5e. 教学技能(295) -> 子分类
INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('在线教学技巧', 295, 'online_teaching', 1, 1),
('学科教学', 295, 'subject_teaching', 2, 1),
('成人教育', 295, 'adult_education', 3, 1);

SET @online_teach_id = (SELECT id FROM member_tag WHERE code='online_teaching' AND parent_id=295 LIMIT 1);
SET @subject_id = (SELECT id FROM member_tag WHERE code='subject_teaching' AND parent_id=295 LIMIT 1);
SET @adult_edu_id = (SELECT id FROM member_tag WHERE code='adult_education' AND parent_id=295 LIMIT 1);

INSERT INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('课程设计', @online_teach_id, 'course_design', 1, 1),
('课件制作', @online_teach_id, 'courseware', 2, 1),
('在线直播教学', @online_teach_id, 'live_teaching', 3, 1),
('录播课制作', @online_teach_id, 'recorded_course', 4, 1),
('教学互动技巧', @online_teach_id, 'teaching_interaction', 5, 1),
('学员社群运营', @online_teach_id, 'student_community', 6, 1),
('数学', @subject_id, 'math', 1, 1),
('英语', @subject_id, 'english', 2, 1),
('物理', @subject_id, 'physics', 3, 1),
('化学', @subject_id, 'chemistry', 4, 1),
('语文', @subject_id, 'chinese', 5, 1),
('历史', @subject_id, 'history', 6, 1),
('地理', @subject_id, 'geography', 7, 1),
('艺术教学', @subject_id, 'art_teaching', 8, 1),
('科学启蒙', @subject_id, 'science_edu', 9, 1),
('职业技能培训', @adult_edu_id, 'vocational_training', 1, 1),
('语言学习', @adult_edu_id, 'language_learning', 2, 1),
('兴趣爱好教学', @adult_edu_id, 'hobby_teaching', 3, 1),
('继续教育', @adult_edu_id, 'continuing_edu', 4, 1),
('终身学习', @adult_edu_id, 'lifelong_learning', 5, 1);

-- ========================================
-- 6. 场景区块
-- ========================================
INSERT INTO `member_scene_section` (`scene_code`, `code`, `section_name`, `sort`) VALUES
('buddy', 'buddy_activity',  '想干嘛',   1),
('buddy', 'interest_tags',   '兴趣标签', 2),
('buddy', 'preferences',     '更多偏好', 3),
('love',  'basic_info',      '基本资料', 1),
('love',  'personality',     '个人特质', 2),
('skill', 'i_can_teach',     '我会的技能', 1),
('skill', 'i_want_learn',    '我想学的技能', 2),
('swap',  'i_offer',         '我出什么', 1),
('swap',  'i_want',          '我想换什么', 2);

-- ========================================
-- 7. 区块-子分组关联
-- ========================================

-- 7a. buddy - 想干嘛 -> 搭子活动(130, 无子分组，直接挂根)
SET @buddy_activity_section = (SELECT id FROM member_scene_section WHERE code='buddy_activity' LIMIT 1);
INSERT INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@buddy_activity_section, 130, 1);

-- 7b. buddy - 兴趣标签 -> 生活类(221), 兴趣类(222), 另类类(223)
SET @interest_tags_section = (SELECT id FROM member_scene_section WHERE code='interest_tags' LIMIT 1);
INSERT INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@interest_tags_section, 221, 1),
(@interest_tags_section, 222, 2),
(@interest_tags_section, 223, 3);

-- 7c. buddy - 更多偏好 -> 性格特质(261), 生活节奏(262), 兴趣爱好(263)
SET @preferences_section = (SELECT id FROM member_scene_section WHERE code='preferences' LIMIT 1);
INSERT INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@preferences_section, 261, 1),
(@preferences_section, 262, 2),
(@preferences_section, 263, 3);

-- 7d. love - 基本资料 -> 交往目的(140)
SET @basic_info_section = (SELECT id FROM member_scene_section WHERE code='basic_info' LIMIT 1);
INSERT INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@basic_info_section, 140, 1);

-- 7e. love - 个人特质 -> 情绪特质(201), 人格特质(202), 态度(251), 底线(252), 运动(160), 生活(170), 爱好(180)
SET @personality_section = (SELECT id FROM member_scene_section WHERE code='personality' LIMIT 1);
INSERT INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@personality_section, 201, 1),
(@personality_section, 202, 2),
(@personality_section, 251, 3),
(@personality_section, 252, 4),
(@personality_section, 160, 5),
(@personality_section, 170, 6),
(@personality_section, 180, 7);

-- 7f. skill - 我会的技能 -> 创意技能(291), 商业技能(292), 技术技能(293), 生活技能(294), 教学技能(295)
SET @ican_section = (SELECT id FROM member_scene_section WHERE code='i_can_teach' LIMIT 1);
INSERT INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@ican_section, 291, 1),
(@ican_section, 292, 2),
(@ican_section, 293, 3),
(@ican_section, 294, 4),
(@ican_section, 295, 5);

-- 7g. skill - 我想学的技能 -> 同上
SET @iwant_learn_section = (SELECT id FROM member_scene_section WHERE code='i_want_learn' LIMIT 1);
INSERT INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@iwant_learn_section, 291, 1),
(@iwant_learn_section, 292, 2),
(@iwant_learn_section, 293, 3),
(@iwant_learn_section, 294, 4),
(@iwant_learn_section, 295, 5);

-- 7h. swap - 我出什么 -> 物品类型(200)
SET @ioffer_section = (SELECT id FROM member_scene_section WHERE code='i_offer' LIMIT 1);
INSERT INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@ioffer_section, 200, 1);

-- 7i. swap - 我想换什么 -> 物品类型(200，复用)
SET @iwant_section = (SELECT id FROM member_scene_section WHERE code='i_want' LIMIT 1);
INSERT INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@iwant_section, 200, 1);
