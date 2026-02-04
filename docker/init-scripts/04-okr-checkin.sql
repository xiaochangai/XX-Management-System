-- OKR checkin

CREATE TABLE IF NOT EXISTS t_okr_checkin (
  checkin_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '更新ID',
  objective_id BIGINT NOT NULL COMMENT '目标ID',
  key_result_id BIGINT DEFAULT NULL COMMENT '关键结果ID',
  current_value DECIMAL(18,4) DEFAULT NULL COMMENT '当前值',
  progress DECIMAL(5,2) DEFAULT 0 COMMENT '进度',
  status TINYINT DEFAULT NULL COMMENT '状态',
  confidence TINYINT DEFAULT NULL COMMENT '置信度',
  note VARCHAR(1000) DEFAULT NULL COMMENT '更新说明',
  create_user_id BIGINT DEFAULT NULL COMMENT '创建人',
  create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  update_time DATETIME DEFAULT NULL COMMENT '更新时间',
  INDEX idx_okr_checkin_objective (objective_id),
  INDEX idx_okr_checkin_kr (key_result_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OKR进展更新';
