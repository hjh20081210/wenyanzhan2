-- ============================================================
-- 文言斩 WenyanZhan V1.3 完整建表 SQL
-- 技术基线：MySQL 8.0 / utf8mb4 / 主键全部雪花ID(禁止自增)
-- 数据库名：wenyan_zhan
-- 依据：《文言斩V1.3产品与技术设计文档》第4章
-- ============================================================

CREATE DATABASE IF NOT EXISTS `wenyan_zhan` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `wenyan_zhan`;

-- ------------------------------------------------------------
-- 1. 用户主表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id`                 BIGINT       NOT NULL COMMENT '雪花主键',
  `nickname`           VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '昵称',
  `avatar`             VARCHAR(255) COMMENT '头像URL',
  `open_id`            VARCHAR(128) COMMENT '微信openId',
  `qq_open_id`         VARCHAR(128) COMMENT 'QQ openId',
  `phone`              VARCHAR(20)  COMMENT '手机号(可选绑定)',
  `member_expire_time` DATETIME     COMMENT '会员到期时间，NULL表示无会员',
  `is_first_init`      TINYINT      NOT NULL DEFAULT 1 COMMENT '是否首次初始化 1是/0否',
  `quota_total`        INT          NOT NULL DEFAULT 150 COMMENT '斩词总额度(缓存冗余)',
  `quota_used`         INT          NOT NULL DEFAULT 0 COMMENT '已消耗额度(缓存冗余)',
  `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_open_id` (`open_id`),
  UNIQUE KEY `uk_qq_open_id` (`qq_open_id`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB COMMENT='用户主表';

-- ------------------------------------------------------------
-- 2. 用户配置表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_setting` (
  `id`             BIGINT      NOT NULL COMMENT '雪花主键',
  `user_id`        BIGINT      NOT NULL COMMENT '用户ID',
  `study_grade`    TINYINT     NOT NULL DEFAULT 1 COMMENT '学段 1初中/2高中',
  `remind_switch`  TINYINT     NOT NULL DEFAULT 0 COMMENT '打卡提醒开关 0关/1开',
  `remind_time`    VARCHAR(8)  COMMENT '提醒时间 HH:mm',
  `new_word_daily` INT         NOT NULL DEFAULT 10 COMMENT '每日新词数量 5~30',
  `font_type`      VARCHAR(32) NOT NULL DEFAULT 'system' COMMENT '阅读字体 unibest/system/yalnhan/maxulun',
  `bg_type`        VARCHAR(32) NOT NULL DEFAULT 'paper' COMMENT '阅读背景 paper护眼纸/night夜间',
  `font_size`      INT         NOT NULL DEFAULT 18 COMMENT '字号px',
  `line_height`    DECIMAL(3,1) NOT NULL DEFAULT 1.8 COMMENT '行间距',
  `create_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user` (`user_id`)
) ENGINE=InnoDB COMMENT='用户配置表';

-- ------------------------------------------------------------
-- 3. 斩词额度表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_word_quota` (
  `id`          BIGINT      NOT NULL COMMENT '雪花主键',
  `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
  `free_quota`  INT         NOT NULL DEFAULT 150 COMMENT '免费额度 初始150',
  `buy_quota`   INT         NOT NULL DEFAULT 0 COMMENT '购买额度',
  `used_quota`  INT         NOT NULL DEFAULT 0 COMMENT '已消耗额度',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user` (`user_id`)
) ENGINE=InnoDB COMMENT='斩词额度表';

-- ------------------------------------------------------------
-- 4. 课文篇目表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `article` (
  `article_id`  BIGINT       NOT NULL COMMENT '篇目ID 雪花主键',
  `title`       VARCHAR(128) NOT NULL COMMENT '标题',
  `author`      VARCHAR(64)  COMMENT '作者',
  `dynasty`     VARCHAR(32)  COMMENT '朝代',
  `content`     MEDIUMTEXT   COMMENT '原文',
  `translate`   MEDIUMTEXT   COMMENT '译文',
  `appreciate`  TEXT         COMMENT '赏析',
  `notes`       TEXT         COMMENT '注释JSON',
  `genre`       VARCHAR(32)  COMMENT '体裁(散文/诗词/传记...)',
  `grade`       TINYINT      NOT NULL DEFAULT 0 COMMENT '学段 0通用/1初中/2高中/3小学',
  `is_required` TINYINT      NOT NULL DEFAULT 0 COMMENT '必背标记 1是/0否',
  `read_count`  INT          NOT NULL DEFAULT 0 COMMENT '阅读量',
  `like_count`  INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
  `cover_url`   VARCHAR(255) COMMENT '水墨封面缩略图',
  `source`      VARCHAR(64)  COMMENT '数据来源仓库',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`article_id`),
  KEY `idx_title` (`title`),
  KEY `idx_dynasty` (`dynasty`),
  KEY `idx_grade` (`grade`)
) ENGINE=InnoDB COMMENT='课文篇目表';

-- ------------------------------------------------------------
-- 5. 文言字词库
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `word_lib` (
  `word_id`    BIGINT      NOT NULL COMMENT '字词ID 雪花主键',
  `word`       VARCHAR(64) NOT NULL COMMENT '字词(如:而/之/欧阳修)',
  `word_type`  TINYINT     NOT NULL DEFAULT 1 COMMENT '类型 1实词/2虚词/3通假字/4古今异义/5词类活用/6特殊句式',
  `pos`        VARCHAR(32) COMMENT '词性',
  `explain`    TEXT        COMMENT '释义',
  `example`    TEXT        COMMENT '例句',
  `tag_list`   JSON        COMMENT '知识点标签JSON数组',
  `difficulty` TINYINT     NOT NULL DEFAULT 2 COMMENT '难度 1简单/2中等/3困难',
  `create_time` DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`word_id`),
  KEY `idx_word` (`word`),
  KEY `idx_type` (`word_type`)
) ENGINE=InnoDB COMMENT='文言字词库';

-- ------------------------------------------------------------
-- 6. SRS字词记忆记录（字词错题来源、打卡判定依据）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_word_record` (
  `id`                BIGINT    NOT NULL COMMENT '雪花主键',
  `user_id`           BIGINT    NOT NULL COMMENT '用户ID',
  `word_id`           BIGINT    NOT NULL COMMENT '字词ID',
  `memory_level`      INT       NOT NULL DEFAULT 0 COMMENT '记忆等级 0不认识/99斩词完成',
  `review_interval`   INT       NOT NULL DEFAULT 1 COMMENT '复习间隔天数',
  `next_review_time`  DATETIME  NOT NULL COMMENT '下次复习时间',
  `is_error`          TINYINT   NOT NULL DEFAULT 0 COMMENT '是否错题 1是/0否',
  `error_count`       INT       NOT NULL DEFAULT 0 COMMENT '错误次数',
  `recite_count`      INT       NOT NULL DEFAULT 0 COMMENT '背诵次数',
  `submit_time`       DATETIME  NOT NULL COMMENT '作答时间',
  `create_time`       DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`       DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_word` (`user_id`,`word_id`),
  KEY `idx_next_review` (`next_review_time`),
  KEY `idx_submit` (`submit_time`)
) ENGINE=InnoDB COMMENT='SRS字词记忆记录';

-- ------------------------------------------------------------
-- 7. 默写练习题
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `write_exercise` (
  `exercise_id`  BIGINT       NOT NULL COMMENT '练习ID 雪花主键',
  `article_id`   BIGINT       NOT NULL COMMENT '篇目ID',
  `content`      MEDIUMTEXT   NOT NULL COMMENT '默写原文',
  `blank_pos`    JSON         COMMENT '挖空位置,如[3,8,15]',
  `answer`       TEXT         COMMENT '挖空答案JSON',
  `type`         TINYINT      NOT NULL DEFAULT 1 COMMENT '题型 1直接默写/2理解性默写',
  `hint`         VARCHAR(255) COMMENT '理解性默写题干提示',
  `grade`        TINYINT      NOT NULL DEFAULT 0 COMMENT '学段',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`exercise_id`),
  KEY `idx_article` (`article_id`)
) ENGINE=InnoDB COMMENT='默写练习题';

-- ------------------------------------------------------------
-- 8. 题库题目表（细化字段）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `exam_question` (
  `question_id`  BIGINT      NOT NULL COMMENT '题目ID 雪花主键',
  `article_id`   BIGINT      COMMENT '篇目ID 关联课文库',
  `word_id`      BIGINT      COMMENT '关联字词ID',
  `exam_level`   TINYINT     NOT NULL DEFAULT 2 COMMENT '学段 2中考/3高考',
  `type`         VARCHAR(24) NOT NULL COMMENT '题型 SINGLE/MULTI/TRUE_FALSE/UNDERSTAND_DICTATE/DIRECT_DICTATE/SENTENCE_BREAK/WORD_EXPLAIN/TRANSLATE/CONTENT_QUESTION',
  `title`        TEXT        NOT NULL COMMENT '题干',
  `options`      JSON        COMMENT '选项JSON',
  `answer`       TEXT        COMMENT '答案',
  `analysis`     TEXT        COMMENT '解析',
  `difficulty`   TINYINT     NOT NULL DEFAULT 2 COMMENT '难度 1简单/2中等/3困难',
  `tag_list`     JSON        COMMENT '知识点标签JSON数组',
  `is_free`      TINYINT     NOT NULL DEFAULT 1 COMMENT '是否免费 1免费/0会员',
  `is_high_order` TINYINT    NOT NULL DEFAULT 0 COMMENT '是否高阶真题(会员专属) 1是/0否',
  `source`       VARCHAR(64) COMMENT '题库来源',
  `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`question_id`),
  KEY `idx_article` (`article_id`),
  KEY `idx_level` (`exam_level`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB COMMENT='题库题目表';

-- ------------------------------------------------------------
-- 9. 真题做题记录（真题错题来源、打卡判定依据）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_exam_record` (
  `id`            BIGINT    NOT NULL COMMENT '雪花主键',
  `user_id`       BIGINT    NOT NULL COMMENT '用户ID',
  `question_id`   BIGINT    NOT NULL COMMENT '题目ID',
  `user_answer`   TEXT      COMMENT '用户答案',
  `is_correct`    TINYINT   NOT NULL DEFAULT 0 COMMENT '是否正确 1是/0否',
  `is_error`      TINYINT   NOT NULL DEFAULT 1 COMMENT '是否错题 1是/0否',
  `submit_time`   DATETIME  NOT NULL COMMENT '作答时间',
  `create_time`   DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_question` (`user_id`,`question_id`),
  KEY `idx_submit` (`submit_time`)
) ENGINE=InnoDB COMMENT='真题做题记录';

-- ------------------------------------------------------------
-- 10. 用户自定义试卷
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_exam_paper` (
  `paper_id`    BIGINT      NOT NULL COMMENT '试卷ID 雪花主键',
  `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
  `paper_name`  VARCHAR(64) NOT NULL COMMENT '试卷名称',
  `paper_type`  TINYINT     NOT NULL DEFAULT 1 COMMENT '类型 1错题组卷/2自定义组卷',
  `question_count` INT      NOT NULL DEFAULT 0 COMMENT '题目数量',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`paper_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB COMMENT='用户自定义试卷';

-- ------------------------------------------------------------
-- 11. 试卷题目中间表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_exam_paper_item` (
  `id`          BIGINT   NOT NULL COMMENT '雪花主键',
  `paper_id`    BIGINT   NOT NULL COMMENT '试卷ID',
  `question_id` BIGINT   NOT NULL COMMENT '题目ID',
  `sort_no`     INT      NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_paper_question` (`paper_id`,`question_id`),
  KEY `idx_question` (`question_id`)
) ENGINE=InnoDB COMMENT='试卷题目中间表';

-- ------------------------------------------------------------
-- 12. 会员/额度购买订单
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `member_order` (
  `order_id`    BIGINT      NOT NULL COMMENT '订单ID 雪花主键',
  `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
  `order_no`    VARCHAR(64) COMMENT '业务订单号',
  `order_type`  VARCHAR(16) NOT NULL COMMENT '订单类型 member会员/quota额度',
  `amount`      INT         NOT NULL DEFAULT 0 COMMENT '购买额度数量(额度单)',
  `fee`         DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '支付金额',
  `pay_status`  TINYINT     NOT NULL DEFAULT 0 COMMENT '支付状态 0待支付/1已支付/2已取消',
  `pay_time`    DATETIME    COMMENT '支付时间',
  `member_months` INT       COMMENT '会员月数(会员单)',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB COMMENT='会员/额度购买订单';

-- ------------------------------------------------------------
-- 13. 推送记录表（防重复打卡推送）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_push_record` (
  `id`          BIGINT      NOT NULL COMMENT '雪花主键',
  `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
  `push_date`   VARCHAR(16) NOT NULL COMMENT '推送日期 YYYY-MM-DD',
  `push_time`   VARCHAR(8)  COMMENT '推送时刻 HH:mm:ss',
  `push_type`   VARCHAR(16) NOT NULL DEFAULT 'remind' COMMENT '推送类型 remind打卡提醒',
  `content`     TEXT        COMMENT '推送内容',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`,`push_date`),
  KEY `idx_date` (`push_date`)
) ENGINE=InnoDB COMMENT='推送记录表';

-- ------------------------------------------------------------
-- 14. 用户收藏/划线笔记（阅读页扩展）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id`          BIGINT      NOT NULL COMMENT '雪花主键',
  `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
  `article_id`  BIGINT      NOT NULL COMMENT '篇目ID',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`)
) ENGINE=InnoDB COMMENT='用户收藏篇目';

CREATE TABLE IF NOT EXISTS `user_article_note` (
  `id`          BIGINT       NOT NULL COMMENT '雪花主键',
  `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
  `article_id`  BIGINT       NOT NULL COMMENT '篇目ID',
  `note_type`   VARCHAR(16)  COMMENT '类型 bookmark书签/line划线',
  `position`    INT          COMMENT '位置(字符偏移/句序号)',
  `start_pos`   INT          COMMENT '划线起点',
  `end_pos`     INT          COMMENT '划线终点',
  `content`     VARCHAR(200) COMMENT '划线内容/笔记',
  `text`        TEXT         COMMENT '笔记内容',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_article` (`user_id`,`article_id`)
) ENGINE=InnoDB COMMENT='用户书签/划线笔记';

-- ============================================================
-- 数据源导入说明：
--  article     ← hefengbao/jingmo（含译文注释赏析）+ weimeng23/SchoolChinese(课内)
--  word_lib    ← jiaeyan/Jiayan + 人工整理实词虚词
--  exam_question ← Binkic/Reciter + 人工改写
--  所有原始素材面向用户展示前必须人工二次改写，规避版权风险
-- ============================================================
-- ============================================================
-- V1.4 新增：字典查询
-- ============================================================
CREATE TABLE IF NOT EXISTS `dictionary` (
  `dict_id`    BIGINT      NOT NULL COMMENT '词典ID 雪花主键',
  `entry`      VARCHAR(32) NOT NULL COMMENT '查询词条(单字或词)',
  `type`       TINYINT     NOT NULL DEFAULT 1 COMMENT '类型 1单字/2词组',
  `pinyin`     VARCHAR(64) COMMENT '拼音(带声调)',
  `radical`    VARCHAR(16) COMMENT '部首',
  `stroke`     INT COMMENT '笔画数',
  `wubi`       VARCHAR(16) COMMENT '五笔',
  `explain`    TEXT        COMMENT '释义(JSON数组: 每条含释义+例句+出处)',
  `source`     VARCHAR(128) COMMENT '来源(如:古汉语常用字字典/说文解字)',
  `idf`        DOUBLE      COMMENT '词频权重',
  `hit_count`  INT         NOT NULL DEFAULT 0 COMMENT '查询次数',
  `create_time` DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`dict_id`),
  KEY `idx_entry` (`entry`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB COMMENT='文言文字典';
