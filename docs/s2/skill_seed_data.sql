-- ========================================
-- 技能交换场景种子数据
-- 运行前需先执行 s2_schema.sql 建表
-- ========================================

-- ========================================
-- 1. 根分类标签
-- ========================================
INSERT IGNORE INTO `member_tag` (`id`, `tag_name`, `parent_id`, `category`, `code`, `sort`, `status`) VALUES
(190,  '技能',     0, 'skill',          'skill',           10, 1);

-- ========================================
-- 2. 技能子分组标签 (level-2)
-- ========================================
INSERT IGNORE INTO `member_tag` (`id`, `tag_name`, `parent_id`, `category`, `code`, `sort`, `status`) VALUES
(291,  '创意技能',  190, '', 'creative',          1, 1),
(292,  '商业技能',  190, '', 'business',          2, 1),
(293,  '技术技能',  190, '', 'tech',              3, 1),
(294,  '生活技能',  190, '', 'lifestyle_skill',   4, 1),
(295,  '教学技能',  190, '', 'teaching',          5, 1);

-- ========================================
-- 3. 创意技能(291) → 子分类 + 叶子标签
-- ========================================
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('插画与绘画',   291, 'illustration', 1, 1),
('视觉设计',     291, 'visual_design', 2, 1),
('摄影',         291, 'photography_skill', 3, 1),
('视频制作',     291, 'video_production', 4, 1),
('音乐与音频',   291, 'music_audio', 5, 1),
('写作与文学',   291, 'writing', 6, 1),
('手工与传统艺术', 291, 'crafts', 7, 1);

SET @illustration_id = (SELECT id FROM member_tag WHERE code='illustration' AND parent_id=291 LIMIT 1);
SET @visual_design_id = (SELECT id FROM member_tag WHERE code='visual_design' AND parent_id=291 LIMIT 1);
SET @photo_skill_id = (SELECT id FROM member_tag WHERE code='photography_skill' AND parent_id=291 LIMIT 1);
SET @video_id = (SELECT id FROM member_tag WHERE code='video_production' AND parent_id=291 LIMIT 1);
SET @music_id = (SELECT id FROM member_tag WHERE code='music_audio' AND parent_id=291 LIMIT 1);
SET @writing_id = (SELECT id FROM member_tag WHERE code='writing' AND parent_id=291 LIMIT 1);
SET @crafts_id = (SELECT id FROM member_tag WHERE code='crafts' AND parent_id=291 LIMIT 1);

INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
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

-- ========================================
-- 4. 商业技能(292) → 子分类 + 叶子标签
-- ========================================
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
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

INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
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

-- ========================================
-- 5. 技术技能(293) → 子分类 + 叶子标签
-- ========================================
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
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

INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
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

-- ========================================
-- 6. 生活技能(294) → 子分类 + 叶子标签
-- ========================================
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
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

INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
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

-- ========================================
-- 7. 教学技能(295) → 子分类 + 叶子标签
-- ========================================
INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
('在线教学技巧', 295, 'online_teaching', 1, 1),
('学科教学', 295, 'subject_teaching', 2, 1),
('成人教育', 295, 'adult_education', 3, 1);

SET @online_teach_id = (SELECT id FROM member_tag WHERE code='online_teaching' AND parent_id=295 LIMIT 1);
SET @subject_id = (SELECT id FROM member_tag WHERE code='subject_teaching' AND parent_id=295 LIMIT 1);
SET @adult_edu_id = (SELECT id FROM member_tag WHERE code='adult_education' AND parent_id=295 LIMIT 1);

INSERT IGNORE INTO `member_tag` (`tag_name`, `parent_id`, `code`, `sort`, `status`) VALUES
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
-- 8. 场景区块 (幂等：已存在则跳过)
-- ========================================
INSERT IGNORE INTO `member_scene_section` (`scene_code`, `code`, `section_name`, `sort`) VALUES
('skill', 'i_can_teach',     '我会的技能', 1),
('skill', 'i_want_learn',    '我想学的技能', 2);

-- ========================================
-- 9. 区块-子分组关联
-- ========================================
SET @ican_section = (SELECT id FROM member_scene_section WHERE code='i_can_teach' LIMIT 1);
SET @iwant_learn_section = (SELECT id FROM member_scene_section WHERE code='i_want_learn' LIMIT 1);

INSERT IGNORE INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@ican_section, 291, 1),
(@ican_section, 292, 2),
(@ican_section, 293, 3),
(@ican_section, 294, 4),
(@ican_section, 295, 5);

INSERT IGNORE INTO `member_scene_section_group` (`section_id`, `group_tag_id`, `sort`) VALUES
(@iwant_learn_section, 291, 1),
(@iwant_learn_section, 292, 2),
(@iwant_learn_section, 293, 3),
(@iwant_learn_section, 294, 4),
(@iwant_learn_section, 295, 5);

-- ========================================
-- 10. 如果 skill 场景不存在，还需插入场景定义
-- ========================================
INSERT IGNORE INTO `member_scene` (`scene_code`, `scene_type`, `scene_name`, `scene_status`)
VALUES ('skill', 1, '技能交换', 1);

-- ========================================
-- 11. 用户-场景关联（默认对所有用户可见）
-- ========================================
SET @skill_scene_id = (SELECT id FROM member_scene WHERE scene_code='skill' LIMIT 1);
INSERT IGNORE INTO `member_user_scene` (`scene_id`, `scene_type`, `sort`, `scope_id`, `scope_type`)
VALUES (@skill_scene_id, 1, 3, '0', 0);
