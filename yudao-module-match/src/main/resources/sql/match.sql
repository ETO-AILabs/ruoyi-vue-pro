-- ---------------------------------------------------------------------
-- MatchAI UP - match 模块 DDL
-- 库：ruoyi-vue-pro（MySQL 5.7+）
-- 说明：字段与前端契约 api-contract.md 对齐；tags/interests/values 以 JSON 数组字符串存储。
-- 当前服务为内存 Map 实现，以下 DDL 供后续切换真实数据库时使用。
-- ---------------------------------------------------------------------

-- 1. AI 分身表 ---------------------------------------------------------
DROP TABLE IF EXISTS `match_avatar`;
CREATE TABLE `match_avatar` (
  `id`            bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id`       bigint NOT NULL COMMENT '用户编号（关联 member_user.id）',
  `school_code`   varchar(32)  DEFAULT NULL COMMENT '学校编码',
  `school_name`   varchar(64)  DEFAULT NULL COMMENT '学校名称',
  `campus`        varchar(64)  DEFAULT NULL COMMENT '校区',
  `grade`         varchar(32)  DEFAULT NULL COMMENT '年级',
  `major_category` varchar(32) DEFAULT NULL COMMENT '专业大类',
  `major`         varchar(64)  DEFAULT NULL COMMENT '专业',
  `tags`          varchar(512) DEFAULT NULL COMMENT '标签（JSON 数组字符串，如 ["算法"]）',
  `interests`     varchar(512) DEFAULT NULL COMMENT '兴趣（JSON 数组字符串）',
  `mbti`          varchar(8)   DEFAULT NULL COMMENT 'MBTI',
  `values`        varchar(512) DEFAULT NULL COMMENT '价值观（JSON 数组字符串）',
  `self_intro`    varchar(255) DEFAULT NULL COMMENT '一句话介绍',
  `recent_status` varchar(255) DEFAULT NULL COMMENT '最近在忙的事',
  `creator`       varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`       varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`     bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`, `deleted`),
  KEY `idx_school_code` (`school_code`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = 'MatchAI AI 分身表';

-- 2. 推荐候选人表 -----------------------------------------------------
DROP TABLE IF EXISTS `match_candidate`;
CREATE TABLE `match_candidate` (
  `id`             bigint NOT NULL AUTO_INCREMENT COMMENT '候选人编号',
  `avatar`         varchar(512) DEFAULT NULL COMMENT '头像',
  `nickname`       varchar(64)  DEFAULT NULL COMMENT '昵称',
  `age`            int DEFAULT NULL COMMENT '年龄',
  `school_code`    varchar(32)  DEFAULT NULL COMMENT '学校编码',
  `school_name`    varchar(64)  DEFAULT NULL COMMENT '学校名称',
  `grade`          varchar(32)  DEFAULT NULL COMMENT '年级',
  `major_category` varchar(32)  DEFAULT NULL COMMENT '专业大类',
  `major`          varchar(64)  DEFAULT NULL COMMENT '专业',
  `tags`           varchar(512) DEFAULT NULL COMMENT '标签（JSON 数组字符串）',
  `interests`      varchar(512) DEFAULT NULL COMMENT '兴趣（JSON 数组字符串）',
  `mbti`           varchar(8)   DEFAULT NULL COMMENT 'MBTI',
  `values`         varchar(512) DEFAULT NULL COMMENT '价值观（JSON 数组字符串）',
  `self_intro`     varchar(255) DEFAULT NULL COMMENT '一句话介绍',
  `liked_me`       bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已喜欢当前用户（触发双向匹配）',
  `creator`        varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`        varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`      bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_school_code` (`school_code`),
  KEY `idx_major_category` (`major_category`)
) ENGINE = InnoDB AUTO_INCREMENT = 1001 DEFAULT CHARSET = utf8mb4 COMMENT = 'MatchAI 推荐候选人表';

-- 3. 喜欢/跳过记录表 --------------------------------------------------
DROP TABLE IF EXISTS `match_like_record`;
CREATE TABLE `match_like_record` (
  `id`           bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id`      bigint NOT NULL COMMENT '用户编号（关联 member_user.id）',
  `candidate_id` bigint NOT NULL COMMENT '候选人编号（关联 match_candidate.id）',
  `type`         tinyint NOT NULL COMMENT '记录类型：1=喜欢，2=跳过',
  `creator`      varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`      varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`    bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_candidate_type` (`user_id`, `candidate_id`, `type`, `deleted`),
  KEY `idx_candidate_id` (`candidate_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = 'MatchAI 喜欢/跳过记录表';

-- 4. 匹配结果表 -------------------------------------------------------
DROP TABLE IF EXISTS `match_result`;
CREATE TABLE `match_result` (
  `id`           bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id`      bigint NOT NULL COMMENT '用户编号（关联 member_user.id）',
  `candidate_id` bigint NOT NULL COMMENT '候选人编号（关联 match_candidate.id）',
  `matched_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '匹配时间',
  `creator`      varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`      varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`    bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_candidate` (`user_id`, `candidate_id`, `deleted`),
  KEY `idx_matched_time` (`matched_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = 'MatchAI 匹配结果表';

-- 5. 部落表 -----------------------------------------------------------
DROP TABLE IF EXISTS `discover_tribe`;
CREATE TABLE `discover_tribe` (
  `id`            bigint NOT NULL AUTO_INCREMENT COMMENT '部落编号',
  `name`          varchar(64)  NOT NULL COMMENT '部落名称',
  `cover`         varchar(512) DEFAULT NULL COMMENT '封面图',
  `member_count`  int NOT NULL DEFAULT '0' COMMENT '成员数量',
  `description`   varchar(512) DEFAULT NULL COMMENT '描述',
  `tags`          varchar(512) DEFAULT NULL COMMENT '标签（JSON 数组字符串）',
  `creator`       varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`       varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`     bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = 'MatchAI 部落表';

-- 6. 学校表 -----------------------------------------------------------
DROP TABLE IF EXISTS `discover_school`;
CREATE TABLE `discover_school` (
  `id`          bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `code`        varchar(32)  NOT NULL COMMENT '学校编码',
  `name`        varchar(64)  NOT NULL COMMENT '学校名称',
  `city`        varchar(32)  DEFAULT NULL COMMENT '所在城市',
  `creator`     varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`     varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`   bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`, `deleted`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = 'MatchAI 学校表';

-- ---------------------------------------------------------------------
-- 种子数据
-- ---------------------------------------------------------------------

-- 学校
INSERT INTO `discover_school` (`code`, `name`, `city`, `tenant_id`) VALUES
('zju', '浙江大学', '杭州', 0),
('zjgsu', '浙江工商大学', '杭州', 0),
('zjut', '浙江工业大学', '杭州', 0),
('hzsu', '杭州师范大学', '杭州', 0),
('zjcm', '中国美术学院', '杭州', 0),
('fudan', '复旦大学', '上海', 0),
('sjtu', '上海交通大学', '上海', 0),
('tju', '同济大学', '上海', 0),
('pku', '北京大学', '北京', 0),
('thu', '清华大学', '北京', 0);

-- 部落
INSERT INTO `discover_tribe` (`name`, `cover`, `member_count`, `description`, `tags`, `tenant_id`) VALUES
('夜跑搭子营', '', 128, '每周末晚绕西湖夜跑，配速随意，安全第一', '["运动","跑步"]', 0),
('桌游推理社', '', 86, '剧本杀、狼人杀、德式桌游，周末组局', '["桌游 / 剧本杀","社交"]', 0),
('极客实验室', '', 210, 'AI、开源、硬件黑客，一起造点东西', '["科技 / 极客","编程"]', 0),
('电影与展览', '', 54, '每周一部片或一个展，看完一起聊', '["电影 / 展览","艺术"]', 0),
('羽毛球俱乐部', '', 302, '校队退役选手带练，菜鸟友好', '["运动","羽毛球"]', 0),
('摄影同好会', '', 77, '扫街、人像、风光，器材不是重点', '["摄影","旅行"]', 0);

-- 候选种子（liked_me=1 表示喜欢当前用户，可触发双向匹配）
INSERT INTO `match_candidate` (`id`, `avatar`, `nickname`, `age`, `school_code`, `school_name`, `grade`, `major_category`, `major`, `tags`, `interests`, `mbti`, `values`, `self_intro`, `liked_me`, `tenant_id`) VALUES
(1001, '', '林一', 21, 'zju', '浙江大学', '本科三年级', '理工科', '计算机科学与技术', '["算法","健身"]', '["桌游 / 剧本杀","科技 / 极客"]', 'INTJ', '["真诚","成长"]', 'ACMer，喜欢健身和桌游', b'1', 0),
(1002, '', '陈默', 22, 'zju', '浙江大学', '本科四年级', '理工科', '软件工程', '["摄影","篮球"]', '["科技 / 极客","运动"]', 'ISTP', '["自由","成长"]', '相机不离手的程序员', b'1', 0),
(1003, '', '苏晚', 20, 'zju', '浙江大学', '本科二年级', '人文社科', '新闻学', '["写作","旅行"]', '["旅行","电影 / 展览"]', 'ENFP', '["真诚","热爱生活"]', '想记录每一个有趣的人', b'0', 0),
(1004, '', '周航', 23, 'zju', '浙江大学', '研究生一年级', '理工科', '电子科学与技术', '["羽毛球","音乐"]', '["音乐","运动"]', 'ESTJ', '["进取","自律"]', '实验室卷王，球场主力', b'0', 0),
(1005, '', '许晴', 21, 'zjcm', '中国美术学院', '本科三年级', '艺术', '视觉传达', '["绘画","设计"]', '["电影 / 展览","桌游 / 剧本杀"]', 'INFJ', '["美感","自由"]', '正在做毕业设计的设计师', b'1', 0),
(1006, '', '李想', 22, 'fudan', '复旦大学', '本科三年级', '理工科', '人工智能', '["AI","骑行"]', '["科技 / 极客","运动"]', 'ENTP', '["创新","成长"]', 'AI 炼丹师，业余骑行者', b'0', 0),
(1007, '', '王悦', 20, 'zju', '浙江大学', '本科二年级', '经管', '工商管理', '["舞蹈","烘焙"]', '["美食","音乐"]', 'ESFJ', '["乐观","温暖"]', '社团活动组织者', b'0', 0),
(1008, '', '郑南', 24, 'zju', '浙江大学', '研究生二年级', '理工科', '控制科学与工程', '["游泳","读书"]', '["读书","旅行"]', 'ISFJ', '["内省","踏实"]', '安静的读书人', b'1', 0);
