-- OKR schema

CREATE TABLE IF NOT EXISTS t_okr_period (
  period_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '周期ID',
  period_name VARCHAR(50) NOT NULL COMMENT '周期名称',
  start_date DATE NOT NULL COMMENT '开始日期',
  end_date DATE NOT NULL COMMENT '结束日期',
  status TINYINT NOT NULL COMMENT '状态',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  deleted_flag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标识',
  create_user_id BIGINT DEFAULT NULL COMMENT '创建人',
  update_time DATETIME DEFAULT NULL COMMENT '更新时间',
  create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  INDEX idx_okr_period_status (status),
  INDEX idx_okr_period_date (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OKR周期';

CREATE TABLE IF NOT EXISTS t_okr_objective (
  objective_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '目标ID',
  period_id BIGINT NOT NULL COMMENT '周期ID',
  parent_objective_id BIGINT DEFAULT NULL COMMENT '对齐目标ID',
  owner_employee_id BIGINT NOT NULL COMMENT '负责人',
  title VARCHAR(200) NOT NULL COMMENT '标题',
  description VARCHAR(1000) DEFAULT NULL COMMENT '描述',
  progress DECIMAL(5,2) DEFAULT 0 COMMENT '进度',
  confidence TINYINT DEFAULT NULL COMMENT '置信度',
  status TINYINT NOT NULL COMMENT '状态',
  deleted_flag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标识',
  create_user_id BIGINT DEFAULT NULL COMMENT '创建人',
  update_time DATETIME DEFAULT NULL COMMENT '更新时间',
  create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  INDEX idx_okr_objective_period (period_id),
  INDEX idx_okr_objective_owner (owner_employee_id),
  INDEX idx_okr_objective_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OKR目标';

CREATE TABLE IF NOT EXISTS t_okr_key_result (
  key_result_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关键结果ID',
  objective_id BIGINT NOT NULL COMMENT '目标ID',
  title VARCHAR(200) NOT NULL COMMENT '标题',
  metric_name VARCHAR(100) DEFAULT NULL COMMENT '度量名称',
  start_value DECIMAL(18,4) DEFAULT 0 COMMENT '起始值',
  target_value DECIMAL(18,4) DEFAULT NULL COMMENT '目标值',
  current_value DECIMAL(18,4) DEFAULT 0 COMMENT '当前值',
  unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
  progress DECIMAL(5,2) DEFAULT 0 COMMENT '进度',
  weight INT DEFAULT NULL COMMENT '权重',
  confidence TINYINT DEFAULT NULL COMMENT '置信度',
  status TINYINT NOT NULL COMMENT '状态',
  sort INT DEFAULT 0 COMMENT '排序',
  deleted_flag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标识',
  create_user_id BIGINT DEFAULT NULL COMMENT '创建人',
  update_time DATETIME DEFAULT NULL COMMENT '更新时间',
  create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  INDEX idx_okr_key_result_objective (objective_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OKR关键结果';
