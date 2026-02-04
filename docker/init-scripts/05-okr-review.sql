-- OKR review fields

ALTER TABLE t_okr_objective
  ADD COLUMN score DECIMAL(4,2) DEFAULT NULL COMMENT '评分' AFTER status,
  ADD COLUMN review_note VARCHAR(1000) DEFAULT NULL COMMENT '复盘说明' AFTER score,
  ADD COLUMN review_user_id BIGINT DEFAULT NULL COMMENT '复盘人' AFTER review_note,
  ADD COLUMN review_time DATETIME DEFAULT NULL COMMENT '复盘时间' AFTER review_user_id;

ALTER TABLE t_okr_key_result
  ADD COLUMN score DECIMAL(4,2) DEFAULT NULL COMMENT '评分' AFTER sort,
  ADD COLUMN review_note VARCHAR(1000) DEFAULT NULL COMMENT '复盘说明' AFTER score,
  ADD COLUMN review_user_id BIGINT DEFAULT NULL COMMENT '复盘人' AFTER review_note,
  ADD COLUMN review_time DATETIME DEFAULT NULL COMMENT '复盘时间' AFTER review_user_id;
