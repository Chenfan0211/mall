-- ============================
-- 模块：系统权限与基础配置
-- 操作类型：建表
-- 涉及表：sys_account, sys_account_subject, sys_role, sys_account_role, sys_menu, sys_role_menu, sys_role_button, sys_data_scope, sys_account_data_scope, sys_dict_type, sys_dict_item, sys_region, sys_city, sys_config, sys_task, sys_operation_log, sys_export_log, sys_login_log, sys_file_asset
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：建立后台、WMS、供应商、自提点、团长账号、菜单按钮权限、数据范围、字典、审计、导出与文件基础表
-- ============================

CREATE TABLE IF NOT EXISTS sys_account (
  id BIGINT NOT NULL COMMENT '主键',
  account_no VARCHAR(64) NOT NULL COMMENT '账号编号',
  username VARCHAR(64) NOT NULL COMMENT '登录账号',
  mobile VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  password_hash VARCHAR(255) DEFAULT NULL COMMENT '密码哈希',
  account_type BIGINT NOT NULL COMMENT '账号类型 1员工 2供应商 3仓库 4角色工作台',
  login_portal_code VARCHAR(32) NOT NULL COMMENT '登录端 admin/wms-admin/warehouse-h5/role-workbench-h5',
  subject_type BIGINT NOT NULL COMMENT '默认主体类型 1员工 2供应商 3仓库人员 4自提点 5团长',
  subject_id BIGINT DEFAULT NULL COMMENT '主体ID',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2禁用 3锁定',
  last_login_time DATETIME DEFAULT NULL COMMENT '最近登录时间',
  last_login_ip VARCHAR(64) DEFAULT NULL COMMENT '最近登录IP',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_account_username (username),
  UNIQUE KEY uk_sys_account_no (account_no),
  KEY idx_sys_account_subject (subject_type, subject_id),
  KEY idx_sys_account_portal (login_portal_code, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统一登录账号表';

CREATE TABLE IF NOT EXISTS sys_account_subject (
  id BIGINT NOT NULL COMMENT '主键',
  account_id BIGINT NOT NULL COMMENT '账号ID',
  subject_type BIGINT NOT NULL COMMENT '主体类型 1员工 2供应商 3仓库人员 4自提点 5团长',
  subject_id BIGINT NOT NULL COMMENT '主体ID',
  subject_name_snapshot VARCHAR(128) DEFAULT NULL COMMENT '主体名称快照',
  default_flag BIGINT NOT NULL DEFAULT 0 COMMENT '是否默认主体 0否 1是',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_account_subject (account_id, subject_type, subject_id),
  KEY idx_sys_account_subject_subject (subject_type, subject_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号主体绑定表';

CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT NOT NULL COMMENT '主键',
  role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
  role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
  portal_code VARCHAR(32) NOT NULL COMMENT '所属端 admin/wms-admin/warehouse-h5/role-workbench-h5',
  role_type BIGINT NOT NULL COMMENT '角色类型',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2禁用',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_role_code (role_code),
  KEY idx_sys_role_portal (portal_code, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

CREATE TABLE IF NOT EXISTS sys_account_role (
  id BIGINT NOT NULL COMMENT '主键',
  account_id BIGINT NOT NULL COMMENT '账号ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_account_role (account_id, role_id),
  KEY idx_sys_account_role_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号角色关系表';

CREATE TABLE IF NOT EXISTS sys_menu (
  id BIGINT NOT NULL COMMENT '主键',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  portal_code VARCHAR(32) NOT NULL COMMENT '所属端',
  menu_code VARCHAR(64) NOT NULL COMMENT '菜单编码',
  menu_name VARCHAR(64) NOT NULL COMMENT '菜单名称',
  route_path VARCHAR(255) DEFAULT NULL COMMENT '路由路径',
  menu_type BIGINT NOT NULL COMMENT '菜单类型 1目录 2页面',
  sort_no BIGINT NOT NULL DEFAULT 0 COMMENT '排序',
  visible_status BIGINT NOT NULL DEFAULT 1 COMMENT '可见状态 1显示 2隐藏',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_menu_code (portal_code, menu_code),
  KEY idx_sys_menu_parent (parent_id, sort_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

CREATE TABLE IF NOT EXISTS sys_role_menu (
  id BIGINT NOT NULL COMMENT '主键',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  menu_id BIGINT NOT NULL COMMENT '菜单ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_role_menu (role_id, menu_id),
  KEY idx_sys_role_menu_menu (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单权限表';

CREATE TABLE IF NOT EXISTS sys_role_button (
  id BIGINT NOT NULL COMMENT '主键',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  portal_code VARCHAR(32) NOT NULL COMMENT '所属端',
  module_code VARCHAR(64) NOT NULL COMMENT '模块编码',
  button_code VARCHAR(64) NOT NULL COMMENT '按钮编码',
  button_name VARCHAR(64) NOT NULL COMMENT '按钮名称',
  risk_level BIGINT NOT NULL DEFAULT 1 COMMENT '风险等级 1普通 2高风险',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_role_button (role_id, portal_code, module_code, button_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色按钮权限表';

CREATE TABLE IF NOT EXISTS sys_data_scope (
  id BIGINT NOT NULL COMMENT '主键',
  scope_code VARCHAR(64) NOT NULL COMMENT '范围编码',
  scope_name VARCHAR(128) NOT NULL COMMENT '范围名称',
  scope_type BIGINT NOT NULL COMMENT '范围类型 1全平台 2城市 3供应商 4自提点 5仓库 6司机 7团长',
  scope_object_id BIGINT DEFAULT NULL COMMENT '范围对象ID',
  include_child_flag BIGINT NOT NULL DEFAULT 0 COMMENT '是否包含下级 0否 1是',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_data_scope_code (scope_code),
  KEY idx_sys_data_scope_object (scope_type, scope_object_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据范围定义表';

CREATE TABLE IF NOT EXISTS sys_account_data_scope (
  id BIGINT NOT NULL COMMENT '主键',
  account_id BIGINT NOT NULL COMMENT '账号ID',
  scope_id BIGINT NOT NULL COMMENT '数据范围ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_account_scope (account_id, scope_id),
  KEY idx_sys_account_scope_scope (scope_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号数据范围关系表';

CREATE TABLE IF NOT EXISTS sys_dict_type (
  id BIGINT NOT NULL COMMENT '主键',
  dict_code VARCHAR(64) NOT NULL COMMENT '字典编码',
  dict_name VARCHAR(128) NOT NULL COMMENT '字典名称',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_dict_type_code (dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

CREATE TABLE IF NOT EXISTS sys_dict_item (
  id BIGINT NOT NULL COMMENT '主键',
  dict_code VARCHAR(64) NOT NULL COMMENT '字典编码',
  item_code VARCHAR(64) NOT NULL COMMENT '字典项编码',
  item_name VARCHAR(128) NOT NULL COMMENT '字典项名称',
  item_value BIGINT NOT NULL COMMENT '字典项值',
  sort_no BIGINT NOT NULL DEFAULT 0 COMMENT '排序',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_dict_item (dict_code, item_code),
  KEY idx_sys_dict_item_value (dict_code, item_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典项表';

CREATE TABLE IF NOT EXISTS sys_region (
  id BIGINT NOT NULL COMMENT '主键',
  region_code VARCHAR(64) NOT NULL COMMENT '大区编码',
  region_name VARCHAR(128) NOT NULL COMMENT '大区名称',
  cutoff_time TIME NOT NULL COMMENT '截团时分秒',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_region_code (region_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大区配置表';

CREATE TABLE IF NOT EXISTS sys_city (
  id BIGINT NOT NULL COMMENT '主键',
  region_id BIGINT NOT NULL COMMENT '大区ID',
  city_code VARCHAR(64) NOT NULL COMMENT '城市编码',
  city_name VARCHAR(128) NOT NULL COMMENT '城市名称',
  front_visible_status BIGINT NOT NULL DEFAULT 1 COMMENT '前台展示状态 1展示 2隐藏',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_city_code (city_code),
  KEY idx_sys_city_region (region_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城市配置表';

CREATE TABLE IF NOT EXISTS sys_config (
  id BIGINT NOT NULL COMMENT '主键',
  config_code VARCHAR(64) NOT NULL COMMENT '配置编码',
  config_name VARCHAR(128) NOT NULL COMMENT '配置名称',
  config_value TEXT NOT NULL COMMENT '配置值',
  config_scope_type BIGINT NOT NULL DEFAULT 1 COMMENT '配置范围 1平台 2城市 3仓库',
  config_scope_id BIGINT DEFAULT NULL COMMENT '配置范围对象ID',
  version_no BIGINT NOT NULL DEFAULT 1 COMMENT '版本号',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_config_scope (config_code, config_scope_type, config_scope_id, version_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

CREATE TABLE IF NOT EXISTS sys_task (
  id BIGINT NOT NULL COMMENT '主键',
  task_no VARCHAR(64) NOT NULL COMMENT '任务编号',
  task_code VARCHAR(64) NOT NULL COMMENT '任务编码',
  task_name VARCHAR(128) NOT NULL COMMENT '任务名称',
  biz_type VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  biz_no VARCHAR(64) DEFAULT NULL COMMENT '业务编号',
  idempotent_key VARCHAR(128) NOT NULL COMMENT '幂等键',
  retry_count BIGINT NOT NULL DEFAULT 0 COMMENT '重试次数',
  next_retry_time DATETIME DEFAULT NULL COMMENT '下次重试时间',
  status BIGINT NOT NULL COMMENT '状态 10待执行 20执行中 30成功 40失败 50关闭',
  fail_reason TEXT DEFAULT NULL COMMENT '失败原因',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_task_no (task_no),
  UNIQUE KEY uk_sys_task_idem (idempotent_key),
  KEY idx_sys_task_status (status, next_retry_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统任务表';

CREATE TABLE IF NOT EXISTS sys_operation_log (
  id BIGINT NOT NULL COMMENT '主键',
  log_no VARCHAR(64) NOT NULL COMMENT '日志编号',
  account_id BIGINT NOT NULL COMMENT '操作账号ID',
  role_id BIGINT DEFAULT NULL COMMENT '操作角色ID',
  portal_code VARCHAR(32) NOT NULL COMMENT '操作端',
  module_code VARCHAR(64) NOT NULL COMMENT '模块编码',
  action_code VARCHAR(64) NOT NULL COMMENT '动作编码',
  action_name VARCHAR(128) NOT NULL COMMENT '动作名称',
  biz_type VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  biz_no VARCHAR(64) DEFAULT NULL COMMENT '业务编号',
  before_json JSON DEFAULT NULL COMMENT '变更前内容',
  after_json JSON DEFAULT NULL COMMENT '变更后内容',
  reason VARCHAR(500) DEFAULT NULL COMMENT '原因',
  result_status BIGINT NOT NULL COMMENT '结果 1成功 2失败',
  fail_reason TEXT DEFAULT NULL COMMENT '失败原因',
  ip VARCHAR(64) DEFAULT NULL COMMENT 'IP',
  device_info VARCHAR(255) DEFAULT NULL COMMENT '设备信息',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_operation_log_no (log_no),
  KEY idx_sys_operation_log_biz (biz_type, biz_no),
  KEY idx_sys_operation_log_account (account_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

CREATE TABLE IF NOT EXISTS sys_export_log (
  id BIGINT NOT NULL COMMENT '主键',
  export_no VARCHAR(64) NOT NULL COMMENT '导出编号',
  account_id BIGINT NOT NULL COMMENT '导出账号ID',
  module_code VARCHAR(64) NOT NULL COMMENT '模块编码',
  export_name VARCHAR(128) NOT NULL COMMENT '导出名称',
  filter_json JSON DEFAULT NULL COMMENT '筛选条件',
  scope_json JSON DEFAULT NULL COMMENT '数据范围',
  export_reason VARCHAR(500) NOT NULL COMMENT '导出原因',
  file_asset_id BIGINT DEFAULT NULL COMMENT '文件ID',
  status BIGINT NOT NULL COMMENT '状态 10生成中 20成功 30失败',
  fail_reason TEXT DEFAULT NULL COMMENT '失败原因',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_export_log_no (export_no),
  KEY idx_sys_export_log_account (account_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导出日志表';

CREATE TABLE IF NOT EXISTS sys_login_log (
  id BIGINT NOT NULL COMMENT '主键',
  account_id BIGINT DEFAULT NULL COMMENT '账号ID',
  username VARCHAR(64) NOT NULL COMMENT '登录账号',
  portal_code VARCHAR(32) NOT NULL COMMENT '登录端',
  login_status BIGINT NOT NULL COMMENT '登录结果 1成功 2失败',
  fail_reason VARCHAR(255) DEFAULT NULL COMMENT '失败原因',
  ip VARCHAR(64) DEFAULT NULL COMMENT 'IP',
  device_info VARCHAR(255) DEFAULT NULL COMMENT '设备信息',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_sys_login_log_account (account_id, create_time),
  KEY idx_sys_login_log_username (username, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

CREATE TABLE IF NOT EXISTS sys_file_asset (
  id BIGINT NOT NULL COMMENT '主键',
  file_no VARCHAR(64) NOT NULL COMMENT '文件编号',
  biz_type VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  biz_no VARCHAR(64) DEFAULT NULL COMMENT '业务编号',
  file_name VARCHAR(255) NOT NULL COMMENT '文件名',
  file_url VARCHAR(500) NOT NULL COMMENT '文件地址',
  file_size BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小',
  file_type VARCHAR(64) DEFAULT NULL COMMENT '文件类型',
  uploader_account_id BIGINT DEFAULT NULL COMMENT '上传账号ID',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1有效 2失效',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_file_asset_no (file_no),
  KEY idx_sys_file_asset_biz (biz_type, biz_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件资产表';

-- ============================
-- 模块：用户、自提点与评论
-- 操作类型：建表
-- 涉及表：usr_user, usr_pickup_person, usr_station, usr_leader, usr_station_leader, usr_station_leave, usr_user_favorite, usr_browse_history, usr_comment
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：建立C端用户、自提点、团长、休假、收藏、浏览历史和评价基础表
-- ============================

CREATE TABLE IF NOT EXISTS usr_user (
  id BIGINT NOT NULL COMMENT '主键',
  user_no VARCHAR(64) NOT NULL COMMENT '用户编号',
  open_id VARCHAR(128) DEFAULT NULL COMMENT '微信OpenID',
  union_id VARCHAR(128) DEFAULT NULL COMMENT '微信UnionID',
  nickname VARCHAR(128) DEFAULT NULL COMMENT '昵称',
  avatar_url VARCHAR(500) DEFAULT NULL COMMENT '头像',
  mobile VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  default_city_id BIGINT DEFAULT NULL COMMENT '默认城市ID',
  default_station_id BIGINT DEFAULT NULL COMMENT '默认自提点ID',
  risk_level BIGINT NOT NULL DEFAULT 0 COMMENT '风险等级',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1正常 2禁用 3注销',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_usr_user_no (user_no),
  KEY idx_usr_user_mobile (mobile),
  KEY idx_usr_user_station (default_city_id, default_station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='C端用户表';

CREATE TABLE IF NOT EXISTS usr_pickup_person (
  id BIGINT NOT NULL COMMENT '主键',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  pickup_name VARCHAR(64) NOT NULL COMMENT '提货人姓名',
  pickup_mobile VARCHAR(32) NOT NULL COMMENT '提货人手机号',
  default_flag BIGINT NOT NULL DEFAULT 0 COMMENT '是否默认 0否 1是',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1有效 2无效',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_usr_pickup_user (user_id, default_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户提货人表';

CREATE TABLE IF NOT EXISTS usr_station (
  id BIGINT NOT NULL COMMENT '主键',
  station_no VARCHAR(64) NOT NULL COMMENT '自提点编号',
  station_name VARCHAR(128) NOT NULL COMMENT '自提点名称',
  city_id BIGINT NOT NULL COMMENT '城市ID',
  address VARCHAR(255) NOT NULL COMMENT '详细地址',
  contact_name VARCHAR(64) NOT NULL COMMENT '联系人',
  contact_mobile VARCHAR(32) NOT NULL COMMENT '联系电话',
  business_hours VARCHAR(128) DEFAULT NULL COMMENT '营业时间',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1营业 2休假 3停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_usr_station_no (station_no),
  KEY idx_usr_station_city (city_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自提点表';

CREATE TABLE IF NOT EXISTS usr_leader (
  id BIGINT NOT NULL COMMENT '主键',
  leader_no VARCHAR(64) NOT NULL COMMENT '团长编号',
  user_id BIGINT DEFAULT NULL COMMENT '关联C端用户ID',
  leader_name VARCHAR(64) NOT NULL COMMENT '团长姓名',
  leader_mobile VARCHAR(32) NOT NULL COMMENT '团长手机号',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1正常 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_usr_leader_no (leader_no),
  KEY idx_usr_leader_mobile (leader_mobile)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团长表';

CREATE TABLE IF NOT EXISTS usr_station_leader (
  id BIGINT NOT NULL COMMENT '主键',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  leader_id BIGINT NOT NULL COMMENT '团长ID',
  bind_status BIGINT NOT NULL DEFAULT 1 COMMENT '绑定状态 1有效 2解除',
  bind_time DATETIME NOT NULL COMMENT '绑定时间',
  unbind_time DATETIME DEFAULT NULL COMMENT '解绑时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_usr_station_leader_station (station_id, bind_status),
  KEY idx_usr_station_leader_leader (leader_id, bind_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自提点团长绑定表';

CREATE TABLE IF NOT EXISTS usr_station_leave (
  id BIGINT NOT NULL COMMENT '主键',
  leave_no VARCHAR(64) NOT NULL COMMENT '休假单号',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  leader_id BIGINT DEFAULT NULL COMMENT '团长ID',
  start_time DATETIME NOT NULL COMMENT '开始时间',
  end_time DATETIME NOT NULL COMMENT '结束时间',
  reason VARCHAR(500) NOT NULL COMMENT '休假原因',
  audit_status BIGINT NOT NULL COMMENT '审核状态 10待审核 20通过 30驳回 40撤回',
  audit_account_id BIGINT DEFAULT NULL COMMENT '审核账号ID',
  audit_time DATETIME DEFAULT NULL COMMENT '审核时间',
  audit_remark VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  city_id BIGINT NOT NULL COMMENT '城市ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_usr_station_leave_no (leave_no),
  KEY idx_usr_station_leave_station (station_id, audit_status),
  KEY idx_usr_station_leave_city (city_id, audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自提点休假表';

CREATE TABLE IF NOT EXISTS usr_user_favorite (
  id BIGINT NOT NULL COMMENT '主键',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  sku_id BIGINT DEFAULT NULL COMMENT 'SKU ID',
  station_id BIGINT DEFAULT NULL COMMENT '自提点ID',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1收藏 2取消',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_usr_user_favorite (user_id, product_id, sku_id),
  KEY idx_usr_user_favorite_user (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

CREATE TABLE IF NOT EXISTS usr_browse_history (
  id BIGINT NOT NULL COMMENT '主键',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  sku_id BIGINT DEFAULT NULL COMMENT 'SKU ID',
  station_id BIGINT DEFAULT NULL COMMENT '自提点ID',
  browse_time DATETIME NOT NULL COMMENT '浏览时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_usr_browse_user (user_id, browse_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览历史表';

CREATE TABLE IF NOT EXISTS usr_comment (
  id BIGINT NOT NULL COMMENT '主键',
  comment_no VARCHAR(64) NOT NULL COMMENT '评价编号',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  order_item_id BIGINT NOT NULL COMMENT '订单商品ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  score BIGINT NOT NULL COMMENT '评分',
  content VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  image_json JSON DEFAULT NULL COMMENT '图片JSON',
  responsibility_type BIGINT DEFAULT NULL COMMENT '责任方类型',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1展示 2隐藏 3删除',
  city_id BIGINT NOT NULL COMMENT '城市ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_usr_comment_no (comment_no),
  KEY idx_usr_comment_product (product_id, score, status),
  KEY idx_usr_comment_city (city_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- ============================
-- 模块：商品与团期
-- 操作类型：建表
-- 涉及表：prd_category, prd_product, prd_sku, prd_stock_spec, prd_sku_stock_spec, prd_rank_tag, sale_publish_period, sale_publish_sku, sale_publish_scope, sale_period_snapshot
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：建立商品基础资料、SKU、库存规格、榜单标签、团期发布和快照表
-- ============================

CREATE TABLE IF NOT EXISTS prd_category (
  id BIGINT NOT NULL COMMENT '主键',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父类目ID',
  category_code VARCHAR(64) NOT NULL COMMENT '类目编码',
  category_name VARCHAR(128) NOT NULL COMMENT '类目名称',
  category_type BIGINT NOT NULL COMMENT '类目类型 1前台展示类目 2统计类目',
  sort_no BIGINT NOT NULL DEFAULT 0 COMMENT '排序',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_prd_category_code (category_code),
  KEY idx_prd_category_parent (parent_id, sort_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品类目表';

CREATE TABLE IF NOT EXISTS prd_product (
  id BIGINT NOT NULL COMMENT '主键',
  product_no VARCHAR(64) NOT NULL COMMENT '商品编号',
  product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
  front_category_id BIGINT NOT NULL COMMENT '前台展示类目ID',
  stat_category_id BIGINT NOT NULL COMMENT '统计类目ID',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  main_image_url VARCHAR(500) DEFAULT NULL COMMENT '主图',
  image_json JSON DEFAULT NULL COMMENT '图片JSON',
  audit_status BIGINT NOT NULL DEFAULT 10 COMMENT '审核状态 10待审核 20通过 30驳回',
  sale_status BIGINT NOT NULL DEFAULT 1 COMMENT '商品状态 1启用 2停用',
  city_id BIGINT DEFAULT NULL COMMENT '默认销售城市ID',
  create_account_id BIGINT DEFAULT NULL COMMENT '创建账号ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_prd_product_no (product_no),
  KEY idx_prd_product_supplier (supplier_id, sale_status),
  KEY idx_prd_product_category (front_category_id, stat_category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SPU表';

CREATE TABLE IF NOT EXISTS prd_sku (
  id BIGINT NOT NULL COMMENT '主键',
  sku_no VARCHAR(64) NOT NULL COMMENT 'SKU编号',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  sku_name VARCHAR(128) NOT NULL COMMENT 'SKU名称',
  barcode VARCHAR(64) DEFAULT NULL COMMENT '商品条码',
  sale_spec_text VARCHAR(255) DEFAULT NULL COMMENT '销售规格',
  base_unit VARCHAR(32) DEFAULT NULL COMMENT '基础单位',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  weight_gram BIGINT DEFAULT NULL COMMENT '重量克',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_prd_sku_no (sku_no),
  KEY idx_prd_sku_product (product_id, status),
  KEY idx_prd_sku_supplier (supplier_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

CREATE TABLE IF NOT EXISTS prd_stock_spec (
  id BIGINT NOT NULL COMMENT '主键',
  spec_code VARCHAR(64) NOT NULL COMMENT '库存规格编码',
  spec_name VARCHAR(128) NOT NULL COMMENT '库存规格名称',
  unit_name VARCHAR(32) NOT NULL COMMENT '库存单位',
  conversion_rate BIGINT NOT NULL DEFAULT 1 COMMENT '换算系数',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_prd_stock_spec_code (spec_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存规格表';

CREATE TABLE IF NOT EXISTS prd_sku_stock_spec (
  id BIGINT NOT NULL COMMENT '主键',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  stock_spec_id BIGINT NOT NULL COMMENT '库存规格ID',
  conversion_rate BIGINT NOT NULL DEFAULT 1 COMMENT '换算系数',
  default_flag BIGINT NOT NULL DEFAULT 1 COMMENT '是否默认 0否 1是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_prd_sku_stock_spec (sku_id, stock_spec_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU库存规格关系表';

CREATE TABLE IF NOT EXISTS prd_rank_tag (
  id BIGINT NOT NULL COMMENT '主键',
  tag_code VARCHAR(64) NOT NULL COMMENT '标签编码',
  tag_name VARCHAR(64) NOT NULL COMMENT '标签名称',
  category_id BIGINT DEFAULT NULL COMMENT '适用类目ID',
  sort_no BIGINT NOT NULL DEFAULT 0 COMMENT '排序',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_prd_rank_tag_code (tag_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='榜单标签表';

CREATE TABLE IF NOT EXISTS sale_publish_period (
  id BIGINT NOT NULL COMMENT '主键',
  period_no VARCHAR(64) NOT NULL COMMENT '团期编号',
  period_name VARCHAR(128) NOT NULL COMMENT '团期名称',
  region_id BIGINT NOT NULL COMMENT '大区ID',
  city_id BIGINT NOT NULL COMMENT '城市ID',
  warehouse_id BIGINT NOT NULL COMMENT '采购仓ID',
  sale_start_time DATETIME NOT NULL COMMENT '销售开始时间',
  sale_end_time DATETIME NOT NULL COMMENT '销售结束时间',
  actual_cutoff_time DATETIME NOT NULL COMMENT '实际截团时间',
  delivery_date DATE NOT NULL COMMENT '配送日期',
  stock_mode BIGINT NOT NULL COMMENT '库存模式 1仓库库存 2虚拟库存',
  status BIGINT NOT NULL COMMENT '状态 10待发布 20已发布 30已下架 40已过期',
  publish_account_id BIGINT DEFAULT NULL COMMENT '发布账号ID',
  publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sale_period_no (period_no),
  KEY idx_sale_period_city_status (city_id, status),
  KEY idx_sale_period_time (sale_start_time, actual_cutoff_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团期发布表';

CREATE TABLE IF NOT EXISTS sale_publish_sku (
  id BIGINT NOT NULL COMMENT '主键',
  period_id BIGINT NOT NULL COMMENT '团期ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  sale_price DECIMAL(16,4) NOT NULL COMMENT '销售价',
  purchase_price DECIMAL(16,4) DEFAULT NULL COMMENT '采购价',
  limit_qty BIGINT NOT NULL DEFAULT 0 COMMENT '限购数量 0不限',
  min_buy_qty BIGINT NOT NULL DEFAULT 1 COMMENT '起售数量',
  planned_stock_qty BIGINT NOT NULL DEFAULT 0 COMMENT '计划销售库存数量',
  sold_qty BIGINT NOT NULL DEFAULT 0 COMMENT '已售数量',
  locked_qty BIGINT NOT NULL DEFAULT 0 COMMENT '锁定数量',
  production_date DATE DEFAULT NULL COMMENT '生产日期',
  shelf_life_days BIGINT DEFAULT NULL COMMENT '保质期天数',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sale_publish_sku (period_id, sku_id),
  KEY idx_sale_publish_sku_supplier (supplier_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团期SKU表';

CREATE TABLE IF NOT EXISTS sale_publish_scope (
  id BIGINT NOT NULL COMMENT '主键',
  period_id BIGINT NOT NULL COMMENT '团期ID',
  scope_type BIGINT NOT NULL COMMENT '范围类型 1城市 2大区',
  scope_object_id BIGINT NOT NULL COMMENT '范围对象ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sale_publish_scope (period_id, scope_type, scope_object_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团期发布范围表';

CREATE TABLE IF NOT EXISTS sale_period_snapshot (
  id BIGINT NOT NULL COMMENT '主键',
  period_id BIGINT NOT NULL COMMENT '团期ID',
  period_no VARCHAR(64) NOT NULL COMMENT '团期编号',
  snapshot_node BIGINT NOT NULL COMMENT '快照节点 1发布 2下单 3采购 4出库',
  snapshot_json JSON NOT NULL COMMENT '快照JSON',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_sale_period_snapshot_period (period_id, snapshot_node)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团期快照表';

-- ============================
-- 模块：订单、支付与售后
-- 操作类型：建表
-- 涉及表：ord_cart, ord_order, ord_order_item, ord_order_status_log, ord_fulfillment_track, ord_stockout_record, ord_replenish_order, pay_trade, pay_refund_trade, pay_fund_flow, afs_after_sale, afs_after_sale_item, afs_audit_log, afs_return_record
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：建立购物车、订单、订单商品履约、支付退款流水、售后审核与退货记录表
-- ============================

CREATE TABLE IF NOT EXISTS ord_cart (
  id BIGINT NOT NULL COMMENT '主键',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  city_id BIGINT NOT NULL COMMENT '城市ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  period_id BIGINT NOT NULL COMMENT '团期ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  qty BIGINT NOT NULL COMMENT '购物车数量',
  selected_flag BIGINT NOT NULL DEFAULT 1 COMMENT '是否选中 0否 1是',
  valid_status BIGINT NOT NULL DEFAULT 1 COMMENT '有效状态 1有效 2失效',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_ord_cart_item (user_id, station_id, period_id, sku_id),
  KEY idx_ord_cart_user (user_id, update_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

CREATE TABLE IF NOT EXISTS ord_order (
  id BIGINT NOT NULL COMMENT '主键',
  order_no VARCHAR(64) NOT NULL COMMENT '订单编号',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  region_id BIGINT NOT NULL COMMENT '大区ID',
  city_id BIGINT NOT NULL COMMENT '城市ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  leader_id BIGINT DEFAULT NULL COMMENT '团长ID',
  pickup_name VARCHAR(64) NOT NULL COMMENT '提货人姓名快照',
  pickup_mobile VARCHAR(32) NOT NULL COMMENT '提货人手机号快照',
  total_amount DECIMAL(16,4) NOT NULL COMMENT '商品总金额',
  pay_amount DECIMAL(16,4) NOT NULL COMMENT '实付金额',
  trade_status BIGINT NOT NULL COMMENT '交易状态',
  pay_status BIGINT NOT NULL COMMENT '支付状态',
  fulfill_status BIGINT NOT NULL COMMENT '履约状态',
  expire_time DATETIME NOT NULL COMMENT '支付过期时间',
  pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
  complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
  cancel_reason VARCHAR(500) DEFAULT NULL COMMENT '取消原因',
  idempotent_key VARCHAR(128) NOT NULL COMMENT '下单幂等键',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_ord_order_no (order_no),
  UNIQUE KEY uk_ord_order_idem (idempotent_key),
  KEY idx_ord_order_user (user_id, create_time),
  KEY idx_ord_order_city (city_id, trade_status, create_time),
  KEY idx_ord_order_station (station_id, fulfill_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

CREATE TABLE IF NOT EXISTS ord_order_item (
  id BIGINT NOT NULL COMMENT '主键',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  order_no VARCHAR(64) NOT NULL COMMENT '订单编号',
  order_item_no VARCHAR(64) NOT NULL COMMENT '订单商品编号',
  period_id BIGINT NOT NULL COMMENT '团期ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  warehouse_id BIGINT NOT NULL COMMENT '采购仓/履约仓ID',
  city_id BIGINT NOT NULL COMMENT '城市ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  product_name VARCHAR(128) NOT NULL COMMENT '商品名称快照',
  sku_name VARCHAR(128) NOT NULL COMMENT 'SKU名称快照',
  sale_price DECIMAL(16,4) NOT NULL COMMENT '销售单价',
  qty BIGINT NOT NULL COMMENT '购买数量',
  picked_qty BIGINT NOT NULL DEFAULT 0 COMMENT '已提货数量',
  refunded_qty BIGINT NOT NULL DEFAULT 0 COMMENT '已退款数量',
  item_amount DECIMAL(16,4) NOT NULL COMMENT '商品行金额',
  fulfill_status BIGINT NOT NULL COMMENT '履约状态',
  after_sale_status BIGINT NOT NULL DEFAULT 0 COMMENT '售后状态 0无售后',
  expected_pickup_date DATE DEFAULT NULL COMMENT '预计提货日',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_ord_order_item_no (order_item_no),
  KEY idx_ord_order_item_order (order_id),
  KEY idx_ord_order_item_supplier (supplier_id, fulfill_status),
  KEY idx_ord_order_item_station (station_id, fulfill_status),
  KEY idx_ord_order_item_warehouse (warehouse_id, fulfill_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

CREATE TABLE IF NOT EXISTS ord_order_status_log (
  id BIGINT NOT NULL COMMENT '主键',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  order_item_id BIGINT DEFAULT NULL COMMENT '订单商品ID',
  status_line BIGINT NOT NULL COMMENT '状态线 1交易 2支付 3履约',
  before_status BIGINT DEFAULT NULL COMMENT '变更前状态',
  after_status BIGINT NOT NULL COMMENT '变更后状态',
  change_reason VARCHAR(500) DEFAULT NULL COMMENT '变更原因',
  operator_type BIGINT NOT NULL COMMENT '操作方 1用户 2后台 3系统 4仓库',
  operator_id BIGINT DEFAULT NULL COMMENT '操作方ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_ord_status_log_order (order_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态日志表';

CREATE TABLE IF NOT EXISTS ord_fulfillment_track (
  id BIGINT NOT NULL COMMENT '主键',
  track_no VARCHAR(64) NOT NULL COMMENT '履约轨迹编号',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  order_item_id BIGINT NOT NULL COMMENT '订单商品ID',
  node_type BIGINT NOT NULL COMMENT '节点类型',
  node_name VARCHAR(128) NOT NULL COMMENT '节点名称',
  biz_no VARCHAR(64) DEFAULT NULL COMMENT '关联业务编号',
  status BIGINT NOT NULL COMMENT '状态',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  city_id BIGINT NOT NULL COMMENT '城市ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  warehouse_id BIGINT DEFAULT NULL COMMENT '仓库ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_ord_fulfillment_track_no (track_no),
  KEY idx_ord_fulfillment_order (order_id, order_item_id),
  KEY idx_ord_fulfillment_city (city_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单履约轨迹表';

CREATE TABLE IF NOT EXISTS ord_stockout_record (
  id BIGINT NOT NULL COMMENT '主键',
  stockout_no VARCHAR(64) NOT NULL COMMENT '缺货编号',
  period_id BIGINT DEFAULT NULL COMMENT '团期ID',
  order_id BIGINT DEFAULT NULL COMMENT '订单ID',
  order_item_id BIGINT DEFAULT NULL COMMENT '订单商品ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  station_id BIGINT DEFAULT NULL COMMENT '自提点ID',
  shortage_qty BIGINT NOT NULL COMMENT '缺货数量',
  handled_qty BIGINT NOT NULL DEFAULT 0 COMMENT '已处理数量',
  reason VARCHAR(500) NOT NULL COMMENT '缺货原因',
  status BIGINT NOT NULL COMMENT '状态 10待处理 20处理中 30已处理 40关闭',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_ord_stockout_no (stockout_no),
  KEY idx_ord_stockout_sku (warehouse_id, sku_id, status),
  KEY idx_ord_stockout_order (order_id, order_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='履约缺货记录表';

CREATE TABLE IF NOT EXISTS ord_replenish_order (
  id BIGINT NOT NULL COMMENT '主键',
  replenish_no VARCHAR(64) NOT NULL COMMENT '补发单号',
  source_order_id BIGINT NOT NULL COMMENT '原订单ID',
  source_order_item_id BIGINT NOT NULL COMMENT '原订单商品ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  replenish_qty BIGINT NOT NULL COMMENT '补发数量',
  status BIGINT NOT NULL COMMENT '状态',
  reason VARCHAR(500) NOT NULL COMMENT '补发原因',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_ord_replenish_no (replenish_no),
  KEY idx_ord_replenish_order (source_order_id, source_order_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后补发单表';

CREATE TABLE IF NOT EXISTS pay_trade (
  id BIGINT NOT NULL COMMENT '主键',
  pay_no VARCHAR(64) NOT NULL COMMENT '支付单号',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  order_no VARCHAR(64) NOT NULL COMMENT '订单编号',
  channel_type BIGINT NOT NULL COMMENT '支付渠道 1微信',
  pay_amount DECIMAL(16,4) NOT NULL COMMENT '支付金额',
  trade_status BIGINT NOT NULL COMMENT '支付单状态 10待支付 20处理中 30成功 40失败 50关闭',
  channel_trade_no VARCHAR(128) DEFAULT NULL COMMENT '渠道流水号',
  pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
  idempotent_key VARCHAR(128) NOT NULL COMMENT '幂等键',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pay_trade_no (pay_no),
  UNIQUE KEY uk_pay_trade_idem (idempotent_key),
  KEY idx_pay_trade_order (order_id, trade_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付单表';

CREATE TABLE IF NOT EXISTS pay_refund_trade (
  id BIGINT NOT NULL COMMENT '主键',
  refund_no VARCHAR(64) NOT NULL COMMENT '退款单号',
  pay_no VARCHAR(64) NOT NULL COMMENT '支付单号',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  after_sale_id BIGINT DEFAULT NULL COMMENT '售后单ID',
  refund_amount DECIMAL(16,4) NOT NULL COMMENT '退款金额',
  refund_status BIGINT NOT NULL COMMENT '退款状态 10待退款 20处理中 30成功 40失败',
  channel_refund_no VARCHAR(128) DEFAULT NULL COMMENT '渠道退款流水号',
  refund_time DATETIME DEFAULT NULL COMMENT '退款时间',
  fail_reason VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
  idempotent_key VARCHAR(128) NOT NULL COMMENT '幂等键',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pay_refund_no (refund_no),
  UNIQUE KEY uk_pay_refund_idem (idempotent_key),
  KEY idx_pay_refund_order (order_id, refund_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款单表';

CREATE TABLE IF NOT EXISTS pay_fund_flow (
  id BIGINT NOT NULL COMMENT '主键',
  flow_no VARCHAR(64) NOT NULL COMMENT '资金流水号',
  flow_type BIGINT NOT NULL COMMENT '流水类型 1支付 2退款 3提现 4分账',
  biz_no VARCHAR(64) NOT NULL COMMENT '业务编号',
  amount DECIMAL(16,4) NOT NULL COMMENT '金额',
  direction BIGINT NOT NULL COMMENT '方向 1收入 2支出',
  subject_type BIGINT NOT NULL COMMENT '主体类型',
  subject_id BIGINT DEFAULT NULL COMMENT '主体ID',
  status BIGINT NOT NULL COMMENT '状态',
  channel_flow_no VARCHAR(128) DEFAULT NULL COMMENT '渠道流水号',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pay_fund_flow_no (flow_no),
  KEY idx_pay_fund_flow_biz (biz_no),
  KEY idx_pay_fund_flow_subject (subject_type, subject_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金流水表';

CREATE TABLE IF NOT EXISTS afs_after_sale (
  id BIGINT NOT NULL COMMENT '主键',
  after_sale_no VARCHAR(64) NOT NULL COMMENT '售后单号',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  order_no VARCHAR(64) NOT NULL COMMENT '订单编号',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  city_id BIGINT NOT NULL COMMENT '城市ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  after_sale_type BIGINT NOT NULL COMMENT '售后类型 1仅退款 2退货退款',
  status BIGINT NOT NULL COMMENT '售后状态',
  apply_reason VARCHAR(500) NOT NULL COMMENT '申请原因',
  refund_amount DECIMAL(16,4) NOT NULL COMMENT '申请退款金额',
  responsibility_type BIGINT DEFAULT NULL COMMENT '责任方类型',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_afs_after_sale_no (after_sale_no),
  KEY idx_afs_after_sale_order (order_id, status),
  KEY idx_afs_after_sale_city (city_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后单表';

CREATE TABLE IF NOT EXISTS afs_after_sale_item (
  id BIGINT NOT NULL COMMENT '主键',
  after_sale_id BIGINT NOT NULL COMMENT '售后单ID',
  order_item_id BIGINT NOT NULL COMMENT '订单商品ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  apply_qty BIGINT NOT NULL COMMENT '申请售后数量',
  approved_qty BIGINT NOT NULL DEFAULT 0 COMMENT '审核通过数量',
  refund_amount DECIMAL(16,4) NOT NULL COMMENT '退款金额',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_afs_after_sale_item (after_sale_id),
  KEY idx_afs_after_sale_item_order (order_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后商品表';

CREATE TABLE IF NOT EXISTS afs_audit_log (
  id BIGINT NOT NULL COMMENT '主键',
  after_sale_id BIGINT NOT NULL COMMENT '售后单ID',
  audit_action BIGINT NOT NULL COMMENT '审核动作',
  before_status BIGINT DEFAULT NULL COMMENT '变更前状态',
  after_status BIGINT NOT NULL COMMENT '变更后状态',
  audit_account_id BIGINT DEFAULT NULL COMMENT '审核账号ID',
  audit_reason VARCHAR(500) DEFAULT NULL COMMENT '审核原因',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_afs_audit_log_sale (after_sale_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后审核日志表';

CREATE TABLE IF NOT EXISTS afs_return_record (
  id BIGINT NOT NULL COMMENT '主键',
  return_no VARCHAR(64) NOT NULL COMMENT '退货记录编号',
  after_sale_id BIGINT NOT NULL COMMENT '售后单ID',
  order_item_id BIGINT NOT NULL COMMENT '订单商品ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  warehouse_id BIGINT DEFAULT NULL COMMENT '回仓仓库ID',
  return_qty BIGINT NOT NULL COMMENT '退货数量',
  return_status BIGINT NOT NULL COMMENT '退货状态',
  proof_file_json JSON DEFAULT NULL COMMENT '凭证JSON',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_afs_return_no (return_no),
  KEY idx_afs_return_sale (after_sale_id, return_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后退货记录表';

-- ============================
-- 模块：供应商与采购
-- 操作类型：建表
-- 涉及表：sup_supplier, sup_supplier_contact, sup_qualification, sup_contract, pur_purchase_order, pur_purchase_item, pur_purchase_audit_log, pur_delivery_order, pur_delivery_item, pur_purchase_source_log
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：建立供应商主体、资质合同、采购单、采购审核和送货到仓表
-- ============================

CREATE TABLE IF NOT EXISTS sup_supplier (
  id BIGINT NOT NULL COMMENT '主键',
  supplier_no VARCHAR(64) NOT NULL COMMENT '供应商编号',
  supplier_name VARCHAR(128) NOT NULL COMMENT '供应商名称',
  city_id BIGINT DEFAULT NULL COMMENT '所属城市ID',
  status BIGINT NOT NULL COMMENT '状态 1正常 2停用 3拉黑',
  audit_status BIGINT NOT NULL DEFAULT 10 COMMENT '审核状态 10待审核 20通过 30驳回',
  deposit_required_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '要求押金',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sup_supplier_no (supplier_no),
  KEY idx_sup_supplier_city (city_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商主体表';

CREATE TABLE IF NOT EXISTS sup_supplier_contact (
  id BIGINT NOT NULL COMMENT '主键',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  contact_name VARCHAR(64) NOT NULL COMMENT '联系人',
  contact_mobile VARCHAR(32) NOT NULL COMMENT '联系电话',
  contact_type BIGINT NOT NULL DEFAULT 1 COMMENT '联系人类型 1业务 2财务 3送货',
  default_flag BIGINT NOT NULL DEFAULT 0 COMMENT '是否默认 0否 1是',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1有效 2无效',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_sup_contact_supplier (supplier_id, contact_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商联系人表';

CREATE TABLE IF NOT EXISTS sup_qualification (
  id BIGINT NOT NULL COMMENT '主键',
  qualification_no VARCHAR(64) NOT NULL COMMENT '资质编号',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  qualification_type BIGINT NOT NULL COMMENT '资质类型',
  certificate_no VARCHAR(128) DEFAULT NULL COMMENT '证照编号',
  valid_start_date DATE DEFAULT NULL COMMENT '有效开始日期',
  valid_end_date DATE DEFAULT NULL COMMENT '有效结束日期',
  file_asset_id BIGINT DEFAULT NULL COMMENT '文件ID',
  audit_status BIGINT NOT NULL DEFAULT 10 COMMENT '审核状态 10待审核 20通过 30驳回',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sup_qualification_no (qualification_no),
  KEY idx_sup_qualification_supplier (supplier_id, audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商资质表';

CREATE TABLE IF NOT EXISTS sup_contract (
  id BIGINT NOT NULL COMMENT '主键',
  contract_no VARCHAR(64) NOT NULL COMMENT '合同编号',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  contract_name VARCHAR(128) NOT NULL COMMENT '合同名称',
  valid_start_date DATE NOT NULL COMMENT '有效开始日期',
  valid_end_date DATE NOT NULL COMMENT '有效结束日期',
  deposit_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '合同押金',
  file_asset_id BIGINT DEFAULT NULL COMMENT '合同文件ID',
  status BIGINT NOT NULL COMMENT '状态 10待生效 20生效中 30已到期 40已解除',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sup_contract_no (contract_no),
  KEY idx_sup_contract_supplier (supplier_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商合同表';

CREATE TABLE IF NOT EXISTS pur_purchase_order (
  id BIGINT NOT NULL COMMENT '主键',
  purchase_no VARCHAR(64) NOT NULL COMMENT '采购单号',
  source_type BIGINT NOT NULL COMMENT '来源 1系统截团 2运营创建 3采购员创建 4供应商创建 5缺货补货',
  period_id BIGINT DEFAULT NULL COMMENT '团期ID',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  warehouse_id BIGINT NOT NULL COMMENT '到仓仓库ID',
  region_id BIGINT DEFAULT NULL COMMENT '大区ID',
  city_id BIGINT NOT NULL COMMENT '城市ID',
  delivery_date DATE NOT NULL COMMENT '配送日期',
  purchase_status BIGINT NOT NULL COMMENT '采购状态',
  audit_status BIGINT NOT NULL DEFAULT 10 COMMENT '审核状态 10待审核 20通过 30驳回',
  create_account_id BIGINT DEFAULT NULL COMMENT '创建账号ID',
  create_reason VARCHAR(500) DEFAULT NULL COMMENT '创建原因',
  expected_arrival_time DATETIME DEFAULT NULL COMMENT '预计到仓时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pur_purchase_no (purchase_no),
  KEY idx_pur_purchase_supplier (supplier_id, purchase_status),
  KEY idx_pur_purchase_warehouse (warehouse_id, purchase_status),
  KEY idx_pur_purchase_city (city_id, delivery_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单表';

CREATE TABLE IF NOT EXISTS pur_purchase_item (
  id BIGINT NOT NULL COMMENT '主键',
  purchase_id BIGINT NOT NULL COMMENT '采购单ID',
  purchase_no VARCHAR(64) NOT NULL COMMENT '采购单号',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  apply_qty BIGINT NOT NULL COMMENT '申请数量',
  purchase_qty BIGINT NOT NULL COMMENT '采购数量',
  received_qty BIGINT NOT NULL DEFAULT 0 COMMENT '实收数量',
  rejected_qty BIGINT NOT NULL DEFAULT 0 COMMENT '拒收数量',
  purchase_price DECIMAL(16,4) NOT NULL COMMENT '采购价',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1有效 2关闭',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_pur_purchase_item_order (purchase_id),
  KEY idx_pur_purchase_item_sku (warehouse_id, sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单明细表';

CREATE TABLE IF NOT EXISTS pur_purchase_audit_log (
  id BIGINT NOT NULL COMMENT '主键',
  purchase_id BIGINT NOT NULL COMMENT '采购单ID',
  audit_action BIGINT NOT NULL COMMENT '审核动作 1通过 2驳回 3调整数量',
  before_status BIGINT DEFAULT NULL COMMENT '变更前状态',
  after_status BIGINT NOT NULL COMMENT '变更后状态',
  audit_account_id BIGINT NOT NULL COMMENT '审核账号ID',
  audit_remark VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_pur_audit_purchase (purchase_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购审核日志表';

CREATE TABLE IF NOT EXISTS pur_delivery_order (
  id BIGINT NOT NULL COMMENT '主键',
  delivery_no VARCHAR(64) NOT NULL COMMENT '送货单号',
  purchase_id BIGINT NOT NULL COMMENT '采购单ID',
  purchase_no VARCHAR(64) NOT NULL COMMENT '采购单号',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  expected_arrival_time DATETIME NOT NULL COMMENT '预计到仓时间',
  arrival_proof_json JSON DEFAULT NULL COMMENT '到仓凭证JSON',
  contact_name VARCHAR(64) DEFAULT NULL COMMENT '送货联系人',
  contact_mobile VARCHAR(32) DEFAULT NULL COMMENT '送货联系电话',
  vehicle_no VARCHAR(64) DEFAULT NULL COMMENT '车辆号',
  status BIGINT NOT NULL COMMENT '状态 10待送货 20送货中 30已到仓 40已收货 50关闭',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pur_delivery_no (delivery_no),
  KEY idx_pur_delivery_purchase (purchase_id, status),
  KEY idx_pur_delivery_supplier (supplier_id, status),
  KEY idx_pur_delivery_warehouse (warehouse_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商送货单表';

CREATE TABLE IF NOT EXISTS pur_delivery_item (
  id BIGINT NOT NULL COMMENT '主键',
  delivery_id BIGINT NOT NULL COMMENT '送货单ID',
  purchase_item_id BIGINT NOT NULL COMMENT '采购明细ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  delivery_qty BIGINT NOT NULL COMMENT '送货数量',
  received_qty BIGINT NOT NULL DEFAULT 0 COMMENT '实收数量',
  diff_qty BIGINT NOT NULL DEFAULT 0 COMMENT '差异数量',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_pur_delivery_item_order (delivery_id),
  KEY idx_pur_delivery_item_sku (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商送货明细表';

CREATE TABLE IF NOT EXISTS pur_purchase_source_log (
  id BIGINT NOT NULL COMMENT '主键',
  purchase_id BIGINT NOT NULL COMMENT '采购单ID',
  source_type BIGINT NOT NULL COMMENT '来源类型',
  source_biz_no VARCHAR(64) DEFAULT NULL COMMENT '来源业务编号',
  source_reason VARCHAR(500) DEFAULT NULL COMMENT '来源原因',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_pur_source_purchase (purchase_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购来源记录表';

-- ============================
-- 模块：WMS 仓储履约
-- 操作类型：建表
-- 涉及表：wms_warehouse, wms_zone, wms_location, wms_line, wms_line_station, wms_driver, wms_inbound_order, wms_inbound_item, wms_receive_record, wms_quality_file, wms_putaway_task, wms_inventory, wms_inventory_lock, wms_inventory_log, wms_wave_batch, wms_pick_task, wms_pick_item, wms_loading_order, wms_loading_item, wms_outbound_order, wms_delivery_order, wms_delivery_station, wms_driver_sign_record, wms_return_handover, wms_stocktake_order, wms_operation_log
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：建立第一版WMS主链路，覆盖收货、上架、库存、波次、拣货、装车出库、司机配送、回仓确认和日志
-- ============================

CREATE TABLE IF NOT EXISTS wms_warehouse (
  id BIGINT NOT NULL COMMENT '主键',
  warehouse_code VARCHAR(64) NOT NULL COMMENT '仓库编码',
  warehouse_name VARCHAR(128) NOT NULL COMMENT '仓库名称',
  city_id BIGINT NOT NULL COMMENT '城市ID',
  warehouse_type BIGINT NOT NULL COMMENT '仓库类型 1采购仓 2配送仓',
  manager_account_id BIGINT DEFAULT NULL COMMENT '负责人账号ID',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_warehouse_code (warehouse_code),
  KEY idx_wms_warehouse_city (city_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

CREATE TABLE IF NOT EXISTS wms_zone (
  id BIGINT NOT NULL COMMENT '主键',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  zone_code VARCHAR(64) NOT NULL COMMENT '库区编码',
  zone_name VARCHAR(128) NOT NULL COMMENT '库区名称',
  zone_type BIGINT NOT NULL COMMENT '库区类型 1收货暂存 2待上架 3可售存储 4拣货暂存 5装车暂存 6回仓暂存',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_zone_code (warehouse_id, zone_code),
  KEY idx_wms_zone_type (warehouse_id, zone_type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库区表';

CREATE TABLE IF NOT EXISTS wms_location (
  id BIGINT NOT NULL COMMENT '主键',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  zone_id BIGINT NOT NULL COMMENT '库区ID',
  location_code VARCHAR(64) NOT NULL COMMENT '库位编码',
  location_name VARCHAR(128) DEFAULT NULL COMMENT '库位名称',
  capacity_qty BIGINT NOT NULL DEFAULT 0 COMMENT '容量数量',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_location_code (warehouse_id, location_code),
  KEY idx_wms_location_zone (zone_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库位表';

CREATE TABLE IF NOT EXISTS wms_line (
  id BIGINT NOT NULL COMMENT '主键',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  line_code VARCHAR(64) NOT NULL COMMENT '线路编码',
  line_name VARCHAR(128) NOT NULL COMMENT '线路名称',
  default_driver_account_id BIGINT DEFAULT NULL COMMENT '默认司机账号ID',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_line_code (warehouse_id, line_code),
  KEY idx_wms_line_driver (default_driver_account_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送线路表';

CREATE TABLE IF NOT EXISTS wms_line_station (
  id BIGINT NOT NULL COMMENT '主键',
  line_id BIGINT NOT NULL COMMENT '线路ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  delivery_sort BIGINT NOT NULL COMMENT '配送顺序',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_line_station (line_id, station_id),
  KEY idx_wms_line_station_sort (line_id, delivery_sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线路自提点绑定表';

CREATE TABLE IF NOT EXISTS wms_driver (
  id BIGINT NOT NULL COMMENT '主键',
  driver_no VARCHAR(64) NOT NULL COMMENT '司机编号',
  account_id BIGINT DEFAULT NULL COMMENT '账号ID',
  driver_name VARCHAR(64) NOT NULL COMMENT '司机姓名',
  driver_mobile VARCHAR(32) NOT NULL COMMENT '司机手机号',
  vehicle_no VARCHAR(64) DEFAULT NULL COMMENT '车牌号',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_driver_no (driver_no),
  KEY idx_wms_driver_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='司机档案表';

CREATE TABLE IF NOT EXISTS wms_inbound_order (
  id BIGINT NOT NULL COMMENT '主键',
  inbound_no VARCHAR(64) NOT NULL COMMENT '入库单号',
  source_type BIGINT NOT NULL COMMENT '来源 1采购送货 2退货回仓 3盘点调整',
  source_no VARCHAR(64) NOT NULL COMMENT '来源单号',
  purchase_id BIGINT DEFAULT NULL COMMENT '采购单ID',
  delivery_id BIGINT DEFAULT NULL COMMENT '送货单ID',
  supplier_id BIGINT DEFAULT NULL COMMENT '供应商ID',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  status BIGINT NOT NULL COMMENT '状态',
  receive_account_id BIGINT DEFAULT NULL COMMENT '收货员账号ID',
  receive_time DATETIME DEFAULT NULL COMMENT '收货时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_inbound_no (inbound_no),
  KEY idx_wms_inbound_warehouse (warehouse_id, status),
  KEY idx_wms_inbound_source (source_type, source_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS入库单表';

CREATE TABLE IF NOT EXISTS wms_inbound_item (
  id BIGINT NOT NULL COMMENT '主键',
  inbound_id BIGINT NOT NULL COMMENT '入库单ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  supplier_id BIGINT DEFAULT NULL COMMENT '供应商ID',
  expected_qty BIGINT NOT NULL COMMENT '应收数量',
  received_qty BIGINT NOT NULL DEFAULT 0 COMMENT '实收数量',
  rejected_qty BIGINT NOT NULL DEFAULT 0 COMMENT '拒收数量',
  damaged_qty BIGINT NOT NULL DEFAULT 0 COMMENT '破损数量',
  batch_no VARCHAR(64) DEFAULT NULL COMMENT '批次号',
  production_date DATE DEFAULT NULL COMMENT '生产日期',
  shelf_life_days BIGINT DEFAULT NULL COMMENT '保质期天数',
  diff_reason VARCHAR(500) DEFAULT NULL COMMENT '差异原因',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_wms_inbound_item_order (inbound_id),
  KEY idx_wms_inbound_item_sku (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS入库明细表';

CREATE TABLE IF NOT EXISTS wms_receive_record (
  id BIGINT NOT NULL COMMENT '主键',
  receive_no VARCHAR(64) NOT NULL COMMENT '收货记录编号',
  inbound_id BIGINT NOT NULL COMMENT '入库单ID',
  inbound_item_id BIGINT NOT NULL COMMENT '入库明细ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  receive_qty BIGINT NOT NULL COMMENT '收货数量',
  reject_qty BIGINT NOT NULL DEFAULT 0 COMMENT '拒收数量',
  diff_qty BIGINT NOT NULL DEFAULT 0 COMMENT '差异数量',
  receive_account_id BIGINT NOT NULL COMMENT '收货账号ID',
  quality_status BIGINT NOT NULL COMMENT '质检状态 1合格 2部分合格 3拒收 4待确认',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_receive_no (receive_no),
  KEY idx_wms_receive_inbound (inbound_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS收货记录表';

CREATE TABLE IF NOT EXISTS wms_quality_file (
  id BIGINT NOT NULL COMMENT '主键',
  quality_no VARCHAR(64) NOT NULL COMMENT '质检编号',
  inbound_id BIGINT NOT NULL COMMENT '入库单ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  quality_result BIGINT NOT NULL COMMENT '质检结果',
  file_asset_id BIGINT DEFAULT NULL COMMENT '文件ID',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_quality_no (quality_no),
  KEY idx_wms_quality_inbound (inbound_id, sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS质检文件表';

CREATE TABLE IF NOT EXISTS wms_putaway_task (
  id BIGINT NOT NULL COMMENT '主键',
  putaway_no VARCHAR(64) NOT NULL COMMENT '上架任务编号',
  inbound_id BIGINT NOT NULL COMMENT '入库单ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  from_zone_id BIGINT DEFAULT NULL COMMENT '来源库区ID',
  target_zone_id BIGINT NOT NULL COMMENT '目标库区ID',
  target_location_id BIGINT NOT NULL COMMENT '目标库位ID',
  putaway_qty BIGINT NOT NULL COMMENT '上架数量',
  actual_qty BIGINT NOT NULL DEFAULT 0 COMMENT '实际上架数量',
  status BIGINT NOT NULL COMMENT '状态 10待上架 20上架中 30已上架 40关闭',
  assign_account_id BIGINT DEFAULT NULL COMMENT '指派账号ID',
  complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_putaway_no (putaway_no),
  KEY idx_wms_putaway_warehouse (warehouse_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS上架任务表';

CREATE TABLE IF NOT EXISTS wms_inventory (
  id BIGINT NOT NULL COMMENT '主键',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  zone_id BIGINT NOT NULL COMMENT '库区ID',
  location_id BIGINT NOT NULL COMMENT '库位ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  supplier_id BIGINT DEFAULT NULL COMMENT '供应商ID',
  batch_no VARCHAR(64) NOT NULL COMMENT '批次号',
  production_date DATE DEFAULT NULL COMMENT '生产日期',
  shelf_life_days BIGINT DEFAULT NULL COMMENT '保质期天数',
  in_stock_qty BIGINT NOT NULL DEFAULT 0 COMMENT '在库数量',
  locked_qty BIGINT NOT NULL DEFAULT 0 COMMENT '锁定数量',
  available_qty BIGINT NOT NULL DEFAULT 0 COMMENT '可用数量',
  frozen_qty BIGINT NOT NULL DEFAULT 0 COMMENT '冻结数量',
  inventory_status BIGINT NOT NULL COMMENT '库存状态 10待上架 20可售 30拣货暂存 40回仓暂存 50待确认',
  version BIGINT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_inventory_batch (warehouse_id, location_id, sku_id, batch_no),
  KEY idx_wms_inventory_sku (warehouse_id, sku_id, inventory_status),
  KEY idx_wms_inventory_supplier (supplier_id, sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS实时库存表';

CREATE TABLE IF NOT EXISTS wms_inventory_lock (
  id BIGINT NOT NULL COMMENT '主键',
  lock_no VARCHAR(64) NOT NULL COMMENT '库存锁定编号',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  inventory_id BIGINT NOT NULL COMMENT '库存ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  biz_type BIGINT NOT NULL COMMENT '业务类型 1订单 2波次',
  biz_no VARCHAR(64) NOT NULL COMMENT '业务编号',
  lock_qty BIGINT NOT NULL COMMENT '锁定数量',
  release_qty BIGINT NOT NULL DEFAULT 0 COMMENT '释放数量',
  lock_status BIGINT NOT NULL COMMENT '锁定状态 10锁定中 20已释放 30已扣减',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_inventory_lock_no (lock_no),
  KEY idx_wms_inventory_lock_biz (biz_type, biz_no),
  KEY idx_wms_inventory_lock_sku (warehouse_id, sku_id, lock_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS库存锁定表';

CREATE TABLE IF NOT EXISTS wms_inventory_log (
  id BIGINT NOT NULL COMMENT '主键',
  log_no VARCHAR(64) NOT NULL COMMENT '库存流水编号',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  zone_id BIGINT DEFAULT NULL COMMENT '库区ID',
  location_id BIGINT DEFAULT NULL COMMENT '库位ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  batch_no VARCHAR(64) DEFAULT NULL COMMENT '批次号',
  change_type BIGINT NOT NULL COMMENT '变动类型 1入库 2上架 3锁定 4释放 5出库扣减 6回仓',
  before_qty BIGINT NOT NULL COMMENT '变更前数量',
  change_qty BIGINT NOT NULL COMMENT '变更数量',
  after_qty BIGINT NOT NULL COMMENT '变更后数量',
  biz_type VARCHAR(64) NOT NULL COMMENT '业务类型',
  biz_no VARCHAR(64) NOT NULL COMMENT '业务编号',
  operator_account_id BIGINT DEFAULT NULL COMMENT '操作账号ID',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_inventory_log_no (log_no),
  KEY idx_wms_inventory_log_sku (warehouse_id, sku_id, create_time),
  KEY idx_wms_inventory_log_biz (biz_type, biz_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS库存流水表';

CREATE TABLE IF NOT EXISTS wms_wave_batch (
  id BIGINT NOT NULL COMMENT '主键',
  wave_no VARCHAR(64) NOT NULL COMMENT '波次编号',
  period_id BIGINT DEFAULT NULL COMMENT '团期ID',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  line_id BIGINT NOT NULL COMMENT '线路ID',
  delivery_date DATE NOT NULL COMMENT '配送日期',
  required_qty BIGINT NOT NULL DEFAULT 0 COMMENT '需求数量',
  locked_qty BIGINT NOT NULL DEFAULT 0 COMMENT '锁定数量',
  status BIGINT NOT NULL COMMENT '状态 10待锁定 20已锁定待拣货 30拣货中 40拣货完成 50拣货差异 60取消',
  create_account_id BIGINT DEFAULT NULL COMMENT '创建账号ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_wave_no (wave_no),
  KEY idx_wms_wave_warehouse (warehouse_id, delivery_date, status),
  KEY idx_wms_wave_line (line_id, delivery_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS波次批次表';

CREATE TABLE IF NOT EXISTS wms_pick_task (
  id BIGINT NOT NULL COMMENT '主键',
  pick_no VARCHAR(64) NOT NULL COMMENT '拣货任务编号',
  wave_id BIGINT NOT NULL COMMENT '波次ID',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  line_id BIGINT NOT NULL COMMENT '线路ID',
  assign_account_id BIGINT DEFAULT NULL COMMENT '拣货员账号ID',
  required_qty BIGINT NOT NULL DEFAULT 0 COMMENT '应拣数量',
  actual_qty BIGINT NOT NULL DEFAULT 0 COMMENT '实拣数量',
  shortage_qty BIGINT NOT NULL DEFAULT 0 COMMENT '缺货数量',
  status BIGINT NOT NULL COMMENT '状态 10待拣货 20拣货中 30已拣货 40差异',
  complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_pick_no (pick_no),
  KEY idx_wms_pick_warehouse (warehouse_id, status),
  KEY idx_wms_pick_assign (assign_account_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS拣货任务表';

CREATE TABLE IF NOT EXISTS wms_pick_item (
  id BIGINT NOT NULL COMMENT '主键',
  pick_id BIGINT NOT NULL COMMENT '拣货任务ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  inventory_id BIGINT DEFAULT NULL COMMENT '库存ID',
  location_id BIGINT DEFAULT NULL COMMENT '库位ID',
  required_qty BIGINT NOT NULL COMMENT '应拣数量',
  actual_qty BIGINT NOT NULL DEFAULT 0 COMMENT '实拣数量',
  shortage_qty BIGINT NOT NULL DEFAULT 0 COMMENT '缺货数量',
  diff_reason VARCHAR(500) DEFAULT NULL COMMENT '差异原因',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_wms_pick_item_pick (pick_id),
  KEY idx_wms_pick_item_sku (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS拣货明细表';

CREATE TABLE IF NOT EXISTS wms_loading_order (
  id BIGINT NOT NULL COMMENT '主键',
  loading_no VARCHAR(64) NOT NULL COMMENT '装车单号',
  wave_id BIGINT NOT NULL COMMENT '波次ID',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  line_id BIGINT NOT NULL COMMENT '线路ID',
  driver_account_id BIGINT DEFAULT NULL COMMENT '司机账号ID',
  delivery_date DATE NOT NULL COMMENT '配送日期',
  required_qty BIGINT NOT NULL DEFAULT 0 COMMENT '应装数量',
  actual_qty BIGINT NOT NULL DEFAULT 0 COMMENT '实装数量',
  status BIGINT NOT NULL COMMENT '状态 10待装车 20已装车待出库 30已出库',
  load_account_id BIGINT DEFAULT NULL COMMENT '装车员账号ID',
  load_time DATETIME DEFAULT NULL COMMENT '装车时间',
  outbound_account_id BIGINT DEFAULT NULL COMMENT '确认出库账号ID',
  outbound_time DATETIME DEFAULT NULL COMMENT '确认出库时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_loading_no (loading_no),
  KEY idx_wms_loading_warehouse (warehouse_id, status),
  KEY idx_wms_loading_driver (driver_account_id, delivery_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS装车单表';

CREATE TABLE IF NOT EXISTS wms_loading_item (
  id BIGINT NOT NULL COMMENT '主键',
  loading_id BIGINT NOT NULL COMMENT '装车单ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  required_qty BIGINT NOT NULL COMMENT '应装数量',
  actual_qty BIGINT NOT NULL DEFAULT 0 COMMENT '实装数量',
  outbound_qty BIGINT NOT NULL DEFAULT 0 COMMENT '出库数量',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  KEY idx_wms_loading_item_order (loading_id),
  KEY idx_wms_loading_item_station (station_id, sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS装车明细表';

CREATE TABLE IF NOT EXISTS wms_outbound_order (
  id BIGINT NOT NULL COMMENT '主键',
  outbound_no VARCHAR(64) NOT NULL COMMENT '出库单号',
  loading_id BIGINT NOT NULL COMMENT '装车单ID',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  line_id BIGINT NOT NULL COMMENT '线路ID',
  outbound_qty BIGINT NOT NULL DEFAULT 0 COMMENT '出库数量',
  status BIGINT NOT NULL COMMENT '状态 10待出库 20已出库 30取消',
  outbound_account_id BIGINT NOT NULL COMMENT '确认出库账号ID',
  outbound_time DATETIME NOT NULL COMMENT '确认出库时间',
  idempotent_key VARCHAR(128) NOT NULL COMMENT '幂等键',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_outbound_no (outbound_no),
  UNIQUE KEY uk_wms_outbound_idem (idempotent_key),
  KEY idx_wms_outbound_warehouse (warehouse_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS出库单表';

CREATE TABLE IF NOT EXISTS wms_delivery_order (
  id BIGINT NOT NULL COMMENT '主键',
  delivery_no VARCHAR(64) NOT NULL COMMENT '配送单号',
  outbound_id BIGINT NOT NULL COMMENT '出库单ID',
  loading_id BIGINT NOT NULL COMMENT '装车单ID',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  line_id BIGINT NOT NULL COMMENT '线路ID',
  driver_account_id BIGINT NOT NULL COMMENT '司机账号ID',
  delivery_date DATE NOT NULL COMMENT '配送日期',
  status BIGINT NOT NULL COMMENT '状态 10待配送 20配送中 30已完成 40异常',
  start_time DATETIME DEFAULT NULL COMMENT '发车时间',
  complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_delivery_no (delivery_no),
  KEY idx_wms_delivery_driver (driver_account_id, delivery_date, status),
  KEY idx_wms_delivery_warehouse (warehouse_id, delivery_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS司机配送单表';

CREATE TABLE IF NOT EXISTS wms_delivery_station (
  id BIGINT NOT NULL COMMENT '主键',
  delivery_id BIGINT NOT NULL COMMENT '配送单ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  delivery_sort BIGINT NOT NULL COMMENT '配送顺序',
  status BIGINT NOT NULL COMMENT '状态 10待配送 20已到达 30已完成 40无法送达',
  arrive_time DATETIME DEFAULT NULL COMMENT '到达时间',
  complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_delivery_station (delivery_id, station_id),
  KEY idx_wms_delivery_station_status (station_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS配送自提点表';

CREATE TABLE IF NOT EXISTS wms_driver_sign_record (
  id BIGINT NOT NULL COMMENT '主键',
  sign_no VARCHAR(64) NOT NULL COMMENT '签到编号',
  delivery_id BIGINT NOT NULL COMMENT '配送单ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  driver_account_id BIGINT NOT NULL COMMENT '司机账号ID',
  sign_type BIGINT NOT NULL COMMENT '签到类型 1到达 2完成 3无法送达',
  longitude DECIMAL(16,6) DEFAULT NULL COMMENT '经度',
  latitude DECIMAL(16,6) DEFAULT NULL COMMENT '纬度',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_driver_sign_no (sign_no),
  KEY idx_wms_driver_sign_delivery (delivery_id, station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='司机签到记录表';

CREATE TABLE IF NOT EXISTS wms_return_handover (
  id BIGINT NOT NULL COMMENT '主键',
  return_no VARCHAR(64) NOT NULL COMMENT '回仓交接单号',
  delivery_id BIGINT NOT NULL COMMENT '配送单ID',
  station_id BIGINT NOT NULL COMMENT '自提点ID',
  sku_id BIGINT NOT NULL COMMENT 'SKU ID',
  warehouse_id BIGINT NOT NULL COMMENT '回仓仓库ID',
  return_qty BIGINT NOT NULL COMMENT '退货带回数量',
  reason_type BIGINT NOT NULL COMMENT '原因类型 1退货 2拒收 3带回',
  driver_account_id BIGINT NOT NULL COMMENT '司机账号ID',
  supervisor_account_id BIGINT DEFAULT NULL COMMENT '主管确认账号ID',
  status BIGINT NOT NULL COMMENT '状态 10待回仓确认 20已回仓确认 30待主管处理 40关闭',
  confirm_time DATETIME DEFAULT NULL COMMENT '确认时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_return_no (return_no),
  KEY idx_wms_return_delivery (delivery_id, station_id),
  KEY idx_wms_return_warehouse (warehouse_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS回仓交接表';

CREATE TABLE IF NOT EXISTS wms_stocktake_order (
  id BIGINT NOT NULL COMMENT '主键',
  stocktake_no VARCHAR(64) NOT NULL COMMENT '盘点单号',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  sku_id BIGINT DEFAULT NULL COMMENT 'SKU ID',
  book_qty BIGINT NOT NULL DEFAULT 0 COMMENT '账面数量',
  actual_qty BIGINT NOT NULL DEFAULT 0 COMMENT '实盘数量',
  diff_qty BIGINT NOT NULL DEFAULT 0 COMMENT '差异数量',
  status BIGINT NOT NULL COMMENT '状态 10草稿 20待审核 30已调整 40关闭',
  create_account_id BIGINT NOT NULL COMMENT '创建账号ID',
  audit_account_id BIGINT DEFAULT NULL COMMENT '审核账号ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_stocktake_no (stocktake_no),
  KEY idx_wms_stocktake_warehouse (warehouse_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS盘点单表';

CREATE TABLE IF NOT EXISTS wms_operation_log (
  id BIGINT NOT NULL COMMENT '主键',
  log_no VARCHAR(64) NOT NULL COMMENT '日志编号',
  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
  account_id BIGINT NOT NULL COMMENT '操作账号ID',
  role_code VARCHAR(64) DEFAULT NULL COMMENT '角色编码',
  module_code VARCHAR(64) NOT NULL COMMENT '模块编码',
  action_code VARCHAR(64) NOT NULL COMMENT '动作编码',
  biz_type VARCHAR(64) NOT NULL COMMENT '业务类型',
  biz_no VARCHAR(64) NOT NULL COMMENT '业务编号',
  before_status BIGINT DEFAULT NULL COMMENT '变更前状态',
  after_status BIGINT DEFAULT NULL COMMENT '变更后状态',
  reason VARCHAR(500) DEFAULT NULL COMMENT '原因',
  result_status BIGINT NOT NULL COMMENT '结果 1成功 2失败',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_wms_operation_log_no (log_no),
  KEY idx_wms_operation_log_biz (biz_type, biz_no),
  KEY idx_wms_operation_log_account (account_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WMS操作日志表';

-- ============================
-- 模块：财务、运营、消息、报表与搜索
-- 操作类型：建表
-- 涉及表：fin_account, fin_account_flow, fin_commission_detail, fin_split_flow, fin_withdraw_apply, fin_deposit_record, fin_supplier_settlement, fin_reconciliation_batch, op_todo, op_exception_record, msg_template, msg_record, rpt_snapshot, rpt_export_task, search_suggest_word
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：建立佣金账户、资金流水、分账提现、对账、待办异常、消息通知、固定报表和搜索联想词表
-- ============================

CREATE TABLE IF NOT EXISTS fin_account (
  id BIGINT NOT NULL COMMENT '主键',
  account_no VARCHAR(64) NOT NULL COMMENT '账户编号',
  subject_type BIGINT NOT NULL COMMENT '主体类型 1供应商 2自提点 3团长 4平台',
  subject_id BIGINT DEFAULT NULL COMMENT '主体ID',
  available_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '可用余额',
  frozen_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '冻结余额',
  deposit_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '押金金额',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1正常 2冻结',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_fin_account_no (account_no),
  UNIQUE KEY uk_fin_account_subject (subject_type, subject_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务账户表';

CREATE TABLE IF NOT EXISTS fin_account_flow (
  id BIGINT NOT NULL COMMENT '主键',
  flow_no VARCHAR(64) NOT NULL COMMENT '账户流水号',
  account_id BIGINT NOT NULL COMMENT '账户ID',
  subject_type BIGINT NOT NULL COMMENT '主体类型',
  subject_id BIGINT DEFAULT NULL COMMENT '主体ID',
  flow_type BIGINT NOT NULL COMMENT '流水类型 1佣金入账 2退款扣回 3提现冻结 4提现成功 5提现驳回 6押金释放 7提现撤回',
  direction BIGINT NOT NULL COMMENT '方向 1收入 2支出',
  amount DECIMAL(16,4) NOT NULL COMMENT '金额',
  before_amount DECIMAL(16,4) NOT NULL COMMENT '变更前余额',
  after_amount DECIMAL(16,4) NOT NULL COMMENT '变更后余额',
  biz_no VARCHAR(64) NOT NULL COMMENT '业务编号',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_fin_account_flow_no (flow_no),
  KEY idx_fin_account_flow_account (account_id, create_time),
  KEY idx_fin_account_flow_biz (biz_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户流水表';

CREATE TABLE IF NOT EXISTS fin_commission_detail (
  id BIGINT NOT NULL COMMENT '主键',
  commission_no VARCHAR(64) NOT NULL COMMENT '佣金明细编号',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  order_item_id BIGINT NOT NULL COMMENT '订单商品ID',
  subject_type BIGINT NOT NULL COMMENT '分佣主体类型 1自提点 2团长 3供应商 4平台',
  subject_id BIGINT DEFAULT NULL COMMENT '分佣主体ID',
  supplier_id BIGINT DEFAULT NULL COMMENT '供应商ID',
  station_id BIGINT DEFAULT NULL COMMENT '自提点ID',
  commission_amount DECIMAL(16,4) NOT NULL COMMENT '佣金金额',
  status BIGINT NOT NULL COMMENT '状态 10待计算 20待分账 30已分账 40已扣回',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_fin_commission_no (commission_no),
  KEY idx_fin_commission_order (order_id, order_item_id),
  KEY idx_fin_commission_subject (subject_type, subject_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金明细表';

CREATE TABLE IF NOT EXISTS fin_split_flow (
  id BIGINT NOT NULL COMMENT '主键',
  split_no VARCHAR(64) NOT NULL COMMENT '分账流水号',
  commission_id BIGINT NOT NULL COMMENT '佣金明细ID',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  subject_type BIGINT NOT NULL COMMENT '分账主体类型',
  subject_id BIGINT DEFAULT NULL COMMENT '分账主体ID',
  split_amount DECIMAL(16,4) NOT NULL COMMENT '分账金额',
  split_status BIGINT NOT NULL COMMENT '分账状态 10待分账 20处理中 30成功 40失败',
  fail_reason VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
  idempotent_key VARCHAR(128) NOT NULL COMMENT '幂等键',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_fin_split_no (split_no),
  UNIQUE KEY uk_fin_split_idem (idempotent_key),
  KEY idx_fin_split_order (order_id, split_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分账流水表';

CREATE TABLE IF NOT EXISTS fin_withdraw_apply (
  id BIGINT NOT NULL COMMENT '主键',
  withdraw_no VARCHAR(64) NOT NULL COMMENT '提现单号',
  account_id BIGINT NOT NULL COMMENT '账户ID',
  subject_type BIGINT NOT NULL COMMENT '主体类型',
  subject_id BIGINT DEFAULT NULL COMMENT '主体ID',
  withdraw_amount DECIMAL(16,4) NOT NULL COMMENT '提现金额',
  receive_account_name VARCHAR(128) NOT NULL COMMENT '收款账户名',
  receive_account_no VARCHAR(128) NOT NULL COMMENT '收款账号',
  audit_status BIGINT NOT NULL COMMENT '审核状态 10待审核 20通过 30驳回 40撤回',
  pay_status BIGINT NOT NULL DEFAULT 10 COMMENT '打款状态 10待打款 20处理中 30成功 40失败',
  audit_account_id BIGINT DEFAULT NULL COMMENT '审核账号ID',
  audit_time DATETIME DEFAULT NULL COMMENT '审核时间',
  audit_remark VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_fin_withdraw_no (withdraw_no),
  KEY idx_fin_withdraw_subject (subject_type, subject_id, audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现申请表';

CREATE TABLE IF NOT EXISTS fin_deposit_record (
  id BIGINT NOT NULL COMMENT '主键',
  deposit_no VARCHAR(64) NOT NULL COMMENT '押金记录编号',
  account_id BIGINT NOT NULL COMMENT '账户ID',
  subject_type BIGINT NOT NULL COMMENT '主体类型',
  subject_id BIGINT NOT NULL COMMENT '主体ID',
  amount DECIMAL(16,4) NOT NULL COMMENT '押金金额',
  frozen_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '冻结金额',
  release_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '释放金额',
  status BIGINT NOT NULL COMMENT '状态 10冻结中 20部分释放 30已释放',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_fin_deposit_no (deposit_no),
  KEY idx_fin_deposit_subject (subject_type, subject_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='押金记录表';

CREATE TABLE IF NOT EXISTS fin_supplier_settlement (
  id BIGINT NOT NULL COMMENT '主键',
  settlement_no VARCHAR(64) NOT NULL COMMENT '结算单号',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  period_start_date DATE NOT NULL COMMENT '结算开始日期',
  period_end_date DATE NOT NULL COMMENT '结算结束日期',
  purchase_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '采购结算金额',
  diff_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '差异金额',
  payable_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '应付金额',
  status BIGINT NOT NULL COMMENT '状态 10待确认 20已确认 30已入账 40异常',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_fin_supplier_settlement_no (settlement_no),
  KEY idx_fin_supplier_settlement_supplier (supplier_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商结算单表';

CREATE TABLE IF NOT EXISTS fin_reconciliation_batch (
  id BIGINT NOT NULL COMMENT '主键',
  reconcile_no VARCHAR(64) NOT NULL COMMENT '对账批次号',
  channel_type BIGINT NOT NULL COMMENT '渠道类型',
  reconcile_date DATE NOT NULL COMMENT '对账日期',
  total_count BIGINT NOT NULL DEFAULT 0 COMMENT '总笔数',
  success_count BIGINT NOT NULL DEFAULT 0 COMMENT '成功笔数',
  diff_count BIGINT NOT NULL DEFAULT 0 COMMENT '差异笔数',
  total_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '总金额',
  diff_amount DECIMAL(16,4) NOT NULL DEFAULT 0.0000 COMMENT '差异金额',
  status BIGINT NOT NULL COMMENT '状态 10待对账 20对账中 30完成 40异常',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_fin_reconciliation_no (reconcile_no),
  KEY idx_fin_reconciliation_date (reconcile_date, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对账批次表';

CREATE TABLE IF NOT EXISTS op_todo (
  id BIGINT NOT NULL COMMENT '主键',
  todo_no VARCHAR(64) NOT NULL COMMENT '待办编号',
  todo_type BIGINT NOT NULL COMMENT '待办类型',
  source_biz_type VARCHAR(64) NOT NULL COMMENT '来源业务类型',
  source_biz_no VARCHAR(64) NOT NULL COMMENT '来源业务编号',
  title VARCHAR(128) NOT NULL COMMENT '标题',
  priority BIGINT NOT NULL DEFAULT 2 COMMENT '优先级 1高 2中 3低',
  process_role_code VARCHAR(64) DEFAULT NULL COMMENT '处理角色编码',
  process_account_id BIGINT DEFAULT NULL COMMENT '处理账号ID',
  city_id BIGINT DEFAULT NULL COMMENT '城市ID',
  supplier_id BIGINT DEFAULT NULL COMMENT '供应商ID',
  warehouse_id BIGINT DEFAULT NULL COMMENT '仓库ID',
  status BIGINT NOT NULL COMMENT '状态 10待处理 20处理中 30已处理 40已驳回 50已取消 60已超时 70已关闭',
  due_time DATETIME DEFAULT NULL COMMENT '截止时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_op_todo_no (todo_no),
  KEY idx_op_todo_processor (process_account_id, status),
  KEY idx_op_todo_city (city_id, status),
  KEY idx_op_todo_source (source_biz_type, source_biz_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营待办表';

CREATE TABLE IF NOT EXISTS op_exception_record (
  id BIGINT NOT NULL COMMENT '主键',
  exception_no VARCHAR(64) NOT NULL COMMENT '异常编号',
  exception_type BIGINT NOT NULL COMMENT '异常类型',
  priority BIGINT NOT NULL DEFAULT 2 COMMENT '优先级 1P0 2P1 3P2',
  source_module VARCHAR(64) NOT NULL COMMENT '来源模块',
  source_biz_type VARCHAR(64) NOT NULL COMMENT '来源业务类型',
  source_biz_no VARCHAR(64) NOT NULL COMMENT '来源业务编号',
  city_id BIGINT DEFAULT NULL COMMENT '城市ID',
  supplier_id BIGINT DEFAULT NULL COMMENT '供应商ID',
  station_id BIGINT DEFAULT NULL COMMENT '自提点ID',
  warehouse_id BIGINT DEFAULT NULL COMMENT '仓库ID',
  owner_account_id BIGINT DEFAULT NULL COMMENT '处理账号ID',
  status BIGINT NOT NULL COMMENT '状态 10待处理 20处理中 30已处理 40关闭',
  title VARCHAR(128) NOT NULL COMMENT '异常标题',
  description VARCHAR(1000) DEFAULT NULL COMMENT '异常说明',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_op_exception_no (exception_no),
  KEY idx_op_exception_owner (owner_account_id, status),
  KEY idx_op_exception_city (city_id, status),
  KEY idx_op_exception_source (source_biz_type, source_biz_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常中心记录表';

CREATE TABLE IF NOT EXISTS msg_template (
  id BIGINT NOT NULL COMMENT '主键',
  template_code VARCHAR(64) NOT NULL COMMENT '模板编码',
  template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
  channel_type BIGINT NOT NULL COMMENT '渠道 1站内信 2公众号',
  receiver_type BIGINT NOT NULL COMMENT '接收方类型',
  title_template VARCHAR(255) NOT NULL COMMENT '标题模板',
  content_template TEXT NOT NULL COMMENT '内容模板',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_msg_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息模板表';

CREATE TABLE IF NOT EXISTS msg_record (
  id BIGINT NOT NULL COMMENT '主键',
  msg_no VARCHAR(64) NOT NULL COMMENT '消息编号',
  template_code VARCHAR(64) DEFAULT NULL COMMENT '模板编码',
  receiver_type BIGINT NOT NULL COMMENT '接收方类型',
  receiver_id BIGINT NOT NULL COMMENT '接收方ID',
  channel_type BIGINT NOT NULL COMMENT '渠道',
  title VARCHAR(255) NOT NULL COMMENT '标题',
  content TEXT NOT NULL COMMENT '内容',
  biz_type VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  biz_no VARCHAR(64) DEFAULT NULL COMMENT '业务编号',
  read_status BIGINT NOT NULL DEFAULT 0 COMMENT '已读状态 0未读 1已读',
  send_status BIGINT NOT NULL COMMENT '发送状态 10待发 20成功 30失败',
  fail_reason VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_msg_record_no (msg_no),
  KEY idx_msg_record_receiver (receiver_type, receiver_id, read_status),
  KEY idx_msg_record_biz (biz_type, biz_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息记录表';

CREATE TABLE IF NOT EXISTS rpt_snapshot (
  id BIGINT NOT NULL COMMENT '主键',
  snapshot_no VARCHAR(64) NOT NULL COMMENT '快照编号',
  report_type BIGINT NOT NULL COMMENT '报表类型',
  stat_date DATE NOT NULL COMMENT '统计日期',
  city_id BIGINT DEFAULT NULL COMMENT '城市ID',
  supplier_id BIGINT DEFAULT NULL COMMENT '供应商ID',
  station_id BIGINT DEFAULT NULL COMMENT '自提点ID',
  warehouse_id BIGINT DEFAULT NULL COMMENT '仓库ID',
  metric_json JSON NOT NULL COMMENT '指标JSON',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1有效 2失效',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_rpt_snapshot_no (snapshot_no),
  KEY idx_rpt_snapshot_scope (report_type, stat_date, city_id, supplier_id, station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='固定报表快照表';

CREATE TABLE IF NOT EXISTS rpt_export_task (
  id BIGINT NOT NULL COMMENT '主键',
  export_task_no VARCHAR(64) NOT NULL COMMENT '导出任务编号',
  report_type BIGINT NOT NULL COMMENT '报表类型',
  account_id BIGINT NOT NULL COMMENT '导出账号ID',
  filter_json JSON DEFAULT NULL COMMENT '筛选条件',
  file_asset_id BIGINT DEFAULT NULL COMMENT '文件ID',
  status BIGINT NOT NULL COMMENT '状态 10生成中 20成功 30失败',
  fail_reason VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_rpt_export_task_no (export_task_no),
  KEY idx_rpt_export_task_account (account_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表导出任务表';

CREATE TABLE IF NOT EXISTS search_suggest_word (
  id BIGINT NOT NULL COMMENT '主键',
  word VARCHAR(128) NOT NULL COMMENT '联想词',
  source_type BIGINT NOT NULL COMMENT '来源 1商品 2运营维护',
  product_id BIGINT DEFAULT NULL COMMENT '商品ID',
  city_id BIGINT DEFAULT NULL COMMENT '城市ID',
  hit_count BIGINT NOT NULL DEFAULT 0 COMMENT '命中次数',
  status BIGINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 2停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted BIGINT NOT NULL DEFAULT 0 COMMENT '软删除 0正常 1删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_search_suggest_word (word, city_id),
  KEY idx_search_suggest_city (city_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索联想词表';

-- ============================
-- 模块：系统权限与基础字典
-- 操作类型：初始化
-- 涉及表：sys_dict_type, sys_dict_item, sys_role, sys_menu, sys_role_menu, sys_region, sys_city, wms_warehouse, wms_zone, sys_data_scope
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：初始化第一版角色、菜单、状态字典、大区城市、采购仓、库区和数据范围
-- ============================

INSERT INTO sys_dict_type
(id, dict_code, dict_name, status, create_time, update_time, is_deleted)
VALUES
(10001, 'order_trade_status', '订单交易状态', 1, NOW(), NOW(), 0),
(10002, 'order_pay_status', '订单支付状态', 1, NOW(), NOW(), 0),
(10003, 'order_fulfill_status', '订单履约状态', 1, NOW(), NOW(), 0),
(10004, 'after_sale_status', '售后状态', 1, NOW(), NOW(), 0),
(10005, 'wms_inbound_status', 'WMS入库上架状态', 1, NOW(), NOW(), 0),
(10006, 'wms_pick_status', 'WMS波次拣货状态', 1, NOW(), NOW(), 0),
(10007, 'wms_loading_status', 'WMS装车出库状态', 1, NOW(), NOW(), 0),
(10008, 'wms_delivery_station_status', 'WMS司机配送自提点状态', 1, NOW(), NOW(), 0),
(10009, 'data_scope_type', '数据范围类型', 1, NOW(), NOW(), 0),
(10010, 'portal_code', '登录端类型', 1, NOW(), NOW(), 0);

INSERT INTO sys_dict_item
(id, dict_code, item_code, item_name, item_value, sort_no, status, create_time, update_time, is_deleted)
VALUES
(11001, 'order_trade_status', 'WAIT_PAY', '待支付', 10, 10, 1, NOW(), NOW(), 0),
(11002, 'order_trade_status', 'PAID', '已支付', 20, 20, 1, NOW(), NOW(), 0),
(11003, 'order_trade_status', 'CLOSED', '已关闭', 30, 30, 1, NOW(), NOW(), 0),
(11004, 'order_trade_status', 'CANCELLED', '已取消', 40, 40, 1, NOW(), NOW(), 0),
(11005, 'order_trade_status', 'COMPLETED', '已完成', 50, 50, 1, NOW(), NOW(), 0),
(12001, 'order_pay_status', 'UNPAID', '未支付', 10, 10, 1, NOW(), NOW(), 0),
(12002, 'order_pay_status', 'PROCESSING', '支付处理中', 20, 20, 1, NOW(), NOW(), 0),
(12003, 'order_pay_status', 'PAID', '已支付', 30, 30, 1, NOW(), NOW(), 0),
(12004, 'order_pay_status', 'REFUNDING', '退款中', 40, 40, 1, NOW(), NOW(), 0),
(12005, 'order_pay_status', 'PART_REFUNDED', '部分退款', 50, 50, 1, NOW(), NOW(), 0),
(12006, 'order_pay_status', 'REFUNDED', '已退款', 60, 60, 1, NOW(), NOW(), 0),
(13001, 'order_fulfill_status', 'WAIT_CUTOFF', '待截团', 10, 10, 1, NOW(), NOW(), 0),
(13002, 'order_fulfill_status', 'WAIT_PURCHASE', '待采购', 20, 20, 1, NOW(), NOW(), 0),
(13003, 'order_fulfill_status', 'WAIT_INBOUND', '待入库', 30, 30, 1, NOW(), NOW(), 0),
(13004, 'order_fulfill_status', 'WAIT_PICK', '待分拣', 40, 40, 1, NOW(), NOW(), 0),
(13005, 'order_fulfill_status', 'WAIT_DELIVERY', '待配送', 50, 50, 1, NOW(), NOW(), 0),
(13006, 'order_fulfill_status', 'WAIT_PICKUP', '待自提', 60, 60, 1, NOW(), NOW(), 0),
(13007, 'order_fulfill_status', 'PICKED', '已自提', 70, 70, 1, NOW(), NOW(), 0),
(14001, 'after_sale_status', 'WAIT_AUDIT', '待审核', 10, 10, 1, NOW(), NOW(), 0),
(14002, 'after_sale_status', 'WAIT_RETURN', '已审核待退货', 20, 20, 1, NOW(), NOW(), 0),
(14003, 'after_sale_status', 'WAIT_REFUND', '已审核待退款', 30, 30, 1, NOW(), NOW(), 0),
(14004, 'after_sale_status', 'REFUND_SUCCESS', '退款成功', 40, 40, 1, NOW(), NOW(), 0),
(14005, 'after_sale_status', 'REFUND_FAILED', '退款失败', 50, 50, 1, NOW(), NOW(), 0),
(14006, 'after_sale_status', 'REJECTED', '已驳回', 60, 60, 1, NOW(), NOW(), 0),
(14007, 'after_sale_status', 'CANCELLED', '已撤销', 70, 70, 1, NOW(), NOW(), 0),
(15001, 'wms_inbound_status', 'WAIT_ARRIVAL', '待到仓', 10, 10, 1, NOW(), NOW(), 0),
(15002, 'wms_inbound_status', 'WAIT_RECEIVE', '待收货', 20, 20, 1, NOW(), NOW(), 0),
(15003, 'wms_inbound_status', 'RECEIVING', '收货中', 30, 30, 1, NOW(), NOW(), 0),
(15004, 'wms_inbound_status', 'WAIT_PUTAWAY', '已收货待上架', 40, 40, 1, NOW(), NOW(), 0),
(15005, 'wms_inbound_status', 'PUTAWAYING', '上架中', 50, 50, 1, NOW(), NOW(), 0),
(15006, 'wms_inbound_status', 'ON_SHELF', '已上架可售', 60, 60, 1, NOW(), NOW(), 0),
(16001, 'wms_pick_status', 'WAIT_LOCK', '待锁定', 10, 10, 1, NOW(), NOW(), 0),
(16002, 'wms_pick_status', 'LOCKED_WAIT_PICK', '已锁定待拣货', 20, 20, 1, NOW(), NOW(), 0),
(16003, 'wms_pick_status', 'PICKING', '拣货中', 30, 30, 1, NOW(), NOW(), 0),
(16004, 'wms_pick_status', 'PICKED', '拣货完成', 40, 40, 1, NOW(), NOW(), 0),
(16005, 'wms_pick_status', 'PICK_DIFF', '拣货差异', 50, 50, 1, NOW(), NOW(), 0),
(17001, 'wms_loading_status', 'WAIT_LOAD', '待装车', 10, 10, 1, NOW(), NOW(), 0),
(17002, 'wms_loading_status', 'LOADED_WAIT_OUT', '已装车待出库', 20, 20, 1, NOW(), NOW(), 0),
(17003, 'wms_loading_status', 'OUTBOUND_DONE', '已出库', 30, 30, 1, NOW(), NOW(), 0),
(18001, 'wms_delivery_station_status', 'WAIT_DELIVERY', '待配送', 10, 10, 1, NOW(), NOW(), 0),
(18002, 'wms_delivery_station_status', 'ARRIVED', '已到达', 20, 20, 1, NOW(), NOW(), 0),
(18003, 'wms_delivery_station_status', 'COMPLETED', '已完成', 30, 30, 1, NOW(), NOW(), 0),
(18004, 'wms_delivery_station_status', 'UNABLE_DELIVERY', '无法送达', 40, 40, 1, NOW(), NOW(), 0),
(19001, 'data_scope_type', 'ALL', '全平台', 1, 10, 1, NOW(), NOW(), 0),
(19002, 'data_scope_type', 'CITY', '城市范围', 2, 20, 1, NOW(), NOW(), 0),
(19003, 'data_scope_type', 'SUPPLIER', '供应商范围', 3, 30, 1, NOW(), NOW(), 0),
(19004, 'data_scope_type', 'STATION', '自提点范围', 4, 40, 1, NOW(), NOW(), 0),
(19005, 'data_scope_type', 'WAREHOUSE', '仓库范围', 5, 50, 1, NOW(), NOW(), 0),
(19006, 'data_scope_type', 'DRIVER', '司机任务范围', 6, 60, 1, NOW(), NOW(), 0),
(19007, 'data_scope_type', 'LEADER', '团长范围', 7, 70, 1, NOW(), NOW(), 0),
(19101, 'portal_code', 'ADMIN', '运营后台', 1, 10, 1, NOW(), NOW(), 0),
(19102, 'portal_code', 'WMS_ADMIN', '仓库后台', 2, 20, 1, NOW(), NOW(), 0),
(19103, 'portal_code', 'WAREHOUSE_H5', '仓配H5', 3, 30, 1, NOW(), NOW(), 0),
(19104, 'portal_code', 'ROLE_WORKBENCH_H5', '角色工作台小程序', 4, 40, 1, NOW(), NOW(), 0);

INSERT INTO sys_role
(id, role_code, role_name, portal_code, role_type, status, remark, create_time, update_time, is_deleted)
VALUES
(20001, 'platform_admin', '平台管理员', 'admin', 1, 1, '运营后台全平台权限', NOW(), NOW(), 0),
(20002, 'platform_operator', '平台运营', 'admin', 2, 1, '运营后台平台运营', NOW(), NOW(), 0),
(20003, 'city_operator', '城市运营', 'admin', 3, 1, '按授权城市及下级数据隔离', NOW(), NOW(), 0),
(20004, 'product_operator', '商品运营', 'admin', 4, 1, '商品、类目、团期权限', NOW(), NOW(), 0),
(20005, 'buyer', '采购人员', 'admin', 5, 1, '供应商、采购与送货权限', NOW(), NOW(), 0),
(20006, 'finance', '财务人员', 'admin', 6, 1, '资金、分账、提现、对账权限', NOW(), NOW(), 0),
(20007, 'after_sale_operator', '售后运营', 'admin', 7, 1, '售后审核与退款申请权限', NOW(), NOW(), 0),
(20008, 'supplier_account', '供应商账号', 'admin', 8, 1, '只能看自身供应商数据', NOW(), NOW(), 0),
(21001, 'wms_supervisor', '仓库主管', 'wms-admin', 11, 1, '上架、任务关闭、回仓确认、基础数据维护', NOW(), NOW(), 0),
(21002, 'wms_buyer', '仓配采购员', 'warehouse-h5', 12, 1, '采购协同、到仓跟踪、异常确认', NOW(), NOW(), 0),
(21003, 'wms_receiver', '收货员', 'warehouse-h5', 13, 1, '收货任务处理', NOW(), NOW(), 0),
(21004, 'wms_picker', '拣货员', 'warehouse-h5', 14, 1, '拣货任务处理', NOW(), NOW(), 0),
(21005, 'wms_loader', '装车员', 'warehouse-h5', 15, 1, '装车和确认出库', NOW(), NOW(), 0),
(21006, 'wms_driver', '司机', 'warehouse-h5', 16, 1, '配送状态与退货带回录入', NOW(), NOW(), 0),
(22001, 'station_operator', '自提点账号', 'role-workbench-h5', 21, 1, '角色工作台自提点账号，只看当前自提点数据', NOW(), NOW(), 0),
(22002, 'leader_operator', '团长账号', 'role-workbench-h5', 22, 1, '角色工作台团长账号，只看绑定团长及自提点数据', NOW(), NOW(), 0),
(22003, 'role_supplier_account', '供应商工作台账号', 'role-workbench-h5', 23, 1, '角色工作台供应商账号，只看自身供应商数据', NOW(), NOW(), 0);

INSERT INTO sys_menu
(id, parent_id, portal_code, menu_code, menu_name, route_path, menu_type, sort_no, visible_status, status, create_time, update_time, is_deleted)
VALUES
(30001, 0, 'admin', 'admin_dashboard', '运营驾驶舱', '/dashboard', 1, 10, 1, 1, NOW(), NOW(), 0),
(30002, 0, 'admin', 'admin_product_sale', '商品与团期', '/product-sale', 1, 20, 1, 1, NOW(), NOW(), 0),
(30003, 0, 'admin', 'admin_fulfillment', '交易履约', '/fulfillment', 1, 30, 1, 1, NOW(), NOW(), 0),
(30004, 0, 'admin', 'admin_station_leader', '自提点/团长', '/station-leader', 1, 40, 1, 1, NOW(), NOW(), 0),
(30005, 0, 'admin', 'admin_supplier_purchase', '供应商/采购', '/supplier-purchase', 1, 50, 1, 1, NOW(), NOW(), 0),
(30006, 0, 'admin', 'admin_finance_risk', '财务风控', '/finance-risk', 1, 60, 1, 1, NOW(), NOW(), 0),
(30007, 0, 'admin', 'admin_user_marketing', '用户与营销', '/user-marketing', 1, 70, 1, 1, NOW(), NOW(), 0),
(30008, 0, 'admin', 'admin_report_system', '报表与系统', '/report-system', 1, 80, 1, 1, NOW(), NOW(), 0),
(31001, 0, 'wms-admin', 'wms_base', '基础数据', '/wms/base', 1, 10, 1, 1, NOW(), NOW(), 0),
(31002, 0, 'wms-admin', 'wms_purchase', '采购到仓', '/wms/purchase', 1, 20, 1, 1, NOW(), NOW(), 0),
(31003, 0, 'wms-admin', 'wms_inbound', '收货入库', '/wms/inbound', 1, 30, 1, 1, NOW(), NOW(), 0),
(31004, 0, 'wms-admin', 'wms_putaway', '上架管理', '/wms/putaway', 1, 40, 1, 1, NOW(), NOW(), 0),
(31005, 0, 'wms-admin', 'wms_inventory', '库存管理', '/wms/inventory', 1, 50, 1, 1, NOW(), NOW(), 0),
(31006, 0, 'wms-admin', 'wms_wave', '波次管理', '/wms/wave', 1, 60, 1, 1, NOW(), NOW(), 0),
(31007, 0, 'wms-admin', 'wms_picking', '拣货管理', '/wms/picking', 1, 70, 1, 1, NOW(), NOW(), 0),
(31008, 0, 'wms-admin', 'wms_loading', '装车出库', '/wms/loading', 1, 80, 1, 1, NOW(), NOW(), 0),
(31009, 0, 'wms-admin', 'wms_delivery', '线路配送', '/wms/delivery', 1, 90, 1, 1, NOW(), NOW(), 0),
(31010, 0, 'wms-admin', 'wms_return', '回仓确认', '/wms/return', 1, 100, 1, 1, NOW(), NOW(), 0),
(31011, 0, 'wms-admin', 'wms_stock_flow', '库存流水', '/wms/stock-flow', 1, 110, 1, 1, NOW(), NOW(), 0),
(31012, 0, 'wms-admin', 'wms_operation_log', '操作日志', '/wms/operation-log', 1, 120, 1, 1, NOW(), NOW(), 0),
(31013, 0, 'wms-admin', 'wms_supervisor', '主管工作台', '/wms/supervisor', 1, 130, 1, 1, NOW(), NOW(), 0),
(32001, 0, 'role-workbench-h5', 'role_home', '工作台', '/role/home', 1, 10, 1, 1, NOW(), NOW(), 0),
(32002, 0, 'role-workbench-h5', 'role_station_order', '自提订单', '/role/station/orders', 1, 20, 1, 1, NOW(), NOW(), 0),
(32003, 0, 'role-workbench-h5', 'role_station_pickup', '提货核销', '/role/station/pickup', 1, 30, 1, 1, NOW(), NOW(), 0),
(32004, 0, 'role-workbench-h5', 'role_station_arrival', '到货接收', '/role/station/arrival', 1, 40, 1, 1, NOW(), NOW(), 0),
(32005, 0, 'role-workbench-h5', 'role_station_leave', '休假管理', '/role/station/leave', 1, 50, 1, 1, NOW(), NOW(), 0),
(32006, 0, 'role-workbench-h5', 'role_station_commission', '佣金提现', '/role/station/commission', 1, 60, 1, 1, NOW(), NOW(), 0),
(32007, 0, 'role-workbench-h5', 'role_leader_dashboard', '团长工作台', '/role/leader/dashboard', 1, 70, 1, 1, NOW(), NOW(), 0),
(32008, 0, 'role-workbench-h5', 'role_leader_station', '站点管理', '/role/leader/stations', 1, 80, 1, 1, NOW(), NOW(), 0),
(32009, 0, 'role-workbench-h5', 'role_supplier_home', '供应商工作台', '/role/supplier/home', 1, 90, 1, 1, NOW(), NOW(), 0),
(32010, 0, 'role-workbench-h5', 'role_supplier_delivery', '送货任务', '/role/supplier/delivery', 1, 100, 1, 1, NOW(), NOW(), 0),
(32011, 0, 'role-workbench-h5', 'role_supplier_sales', '销售数据', '/role/supplier/sales', 1, 110, 1, 1, NOW(), NOW(), 0),
(32012, 0, 'role-workbench-h5', 'role_supplier_finance', '结算提现', '/role/supplier/finance', 1, 120, 1, 1, NOW(), NOW(), 0),
(32013, 0, 'role-workbench-h5', 'role_message', '消息通知', '/role/messages', 1, 130, 1, 1, NOW(), NOW(), 0);

INSERT INTO sys_role_menu
(id, role_id, menu_id, create_time, update_time, is_deleted)
VALUES
(33001, 22001, 32001, NOW(), NOW(), 0),
(33002, 22001, 32002, NOW(), NOW(), 0),
(33003, 22001, 32003, NOW(), NOW(), 0),
(33004, 22001, 32004, NOW(), NOW(), 0),
(33005, 22001, 32005, NOW(), NOW(), 0),
(33006, 22001, 32006, NOW(), NOW(), 0),
(33007, 22001, 32013, NOW(), NOW(), 0),
(33008, 22002, 32001, NOW(), NOW(), 0),
(33009, 22002, 32002, NOW(), NOW(), 0),
(33010, 22002, 32006, NOW(), NOW(), 0),
(33011, 22002, 32007, NOW(), NOW(), 0),
(33012, 22002, 32008, NOW(), NOW(), 0),
(33013, 22002, 32013, NOW(), NOW(), 0),
(33014, 22003, 32009, NOW(), NOW(), 0),
(33015, 22003, 32010, NOW(), NOW(), 0),
(33016, 22003, 32011, NOW(), NOW(), 0),
(33017, 22003, 32012, NOW(), NOW(), 0),
(33018, 22003, 32013, NOW(), NOW(), 0);

INSERT INTO sys_region
(id, region_code, region_name, cutoff_time, status, create_time, update_time, is_deleted)
VALUES
(440000, 'SOUTH_CHINA', '华南大区', '22:00:00', 1, NOW(), NOW(), 0);

INSERT INTO sys_city
(id, region_id, city_code, city_name, front_visible_status, status, create_time, update_time, is_deleted)
VALUES
(440100, 440000, '440100', '广州市', 1, 1, NOW(), NOW(), 0);

INSERT INTO wms_warehouse
(id, warehouse_code, warehouse_name, city_id, warehouse_type, manager_account_id, status, create_time, update_time, is_deleted)
VALUES
(50001, 'WH_GZ_CENTER', '广州中心仓', 440100, 1, NULL, 1, NOW(), NOW(), 0);

INSERT INTO wms_zone
(id, warehouse_id, zone_code, zone_name, zone_type, status, create_time, update_time, is_deleted)
VALUES
(51001, 50001, 'RECEIVE_TEMP', '收货暂存区', 1, 1, NOW(), NOW(), 0),
(51002, 50001, 'WAIT_PUTAWAY', '待上架区', 2, 1, NOW(), NOW(), 0),
(51003, 50001, 'SALE_STORAGE', '可售存储区', 3, 1, NOW(), NOW(), 0),
(51004, 50001, 'PICK_TEMP', '拣货暂存区', 4, 1, NOW(), NOW(), 0),
(51005, 50001, 'LOAD_TEMP', '装车暂存区', 5, 1, NOW(), NOW(), 0),
(51006, 50001, 'RETURN_TEMP', '回仓暂存区', 6, 1, NOW(), NOW(), 0);

INSERT INTO sys_data_scope
(id, scope_code, scope_name, scope_type, scope_object_id, include_child_flag, status, create_time, update_time, is_deleted)
VALUES
(60001, 'ALL_PLATFORM', '全平台数据', 1, NULL, 1, 1, NOW(), NOW(), 0),
(60002, 'CITY_GZ', '广州市数据', 2, 440100, 1, 1, NOW(), NOW(), 0),
(60003, 'WAREHOUSE_GZ_CENTER', '广州中心仓数据', 5, 50001, 0, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 初始化：全模块页面状态测试数据
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：补充运营后台、仓库后台、仓配H5、角色工作台小程序可见测试数据，覆盖各模块列表页关键状态
-- ----------------------------

INSERT INTO sys_city
(id, region_id, city_code, city_name, front_visible_status, status, create_time, update_time, is_deleted)
VALUES
(440300, 440000, '440300', '深圳市', 1, 1, NOW(), NOW(), 0),
(440600, 440000, '440600', '佛山市', 2, 1, NOW(), NOW(), 0),
(441900, 440000, '441900', '东莞市', 1, 2, NOW(), NOW(), 0);

INSERT INTO sup_supplier
(id, supplier_no, supplier_name, city_id, status, audit_status, deposit_required_amount, remark, create_time, update_time, is_deleted)
VALUES
(710001, 'SUP_TEST_FRESH', '测试鲜果供应商', 440100, 1, 20, 5000.0000, '正常合作供应商', NOW(), NOW(), 0),
(710002, 'SUP_TEST_VEGETABLE', '测试蔬菜供应商', 440100, 2, 10, 3000.0000, '待复核供应商', NOW(), NOW(), 0),
(710003, 'SUP_TEST_FROZEN', '测试冻品供应商', 440300, 3, 30, 8000.0000, '已驳回供应商', NOW(), NOW(), 0);

INSERT INTO sup_supplier_contact
(id, supplier_id, contact_name, contact_mobile, contact_type, default_flag, status, create_time, update_time, is_deleted)
VALUES
(710101, 710001, '鲜果业务联系人', '13800001001', 1, 1, 1, NOW(), NOW(), 0),
(710102, 710001, '鲜果财务联系人', '13800001002', 2, 0, 1, NOW(), NOW(), 0),
(710103, 710001, '鲜果送货联系人', '13800001003', 3, 0, 1, NOW(), NOW(), 0),
(710104, 710002, '蔬菜无效联系人', '13800001004', 1, 0, 2, NOW(), NOW(), 0);

INSERT INTO sys_file_asset
(id, file_no, biz_type, biz_no, file_name, file_url, file_size, file_type, uploader_account_id, status, create_time, update_time, is_deleted)
VALUES
(710201, 'FILE_TEST_SUP_LICENSE', 'supplier_qualification', 'QUAL_TEST_WAIT', '营业执照样例.pdf', '/mock/files/supplier-license.pdf', 204800, 'pdf', NULL, 1, NOW(), NOW(), 0),
(710202, 'FILE_TEST_CONTRACT', 'supplier_contract', 'CON_TEST_ACTIVE', '供应商合同样例.pdf', '/mock/files/supplier-contract.pdf', 409600, 'pdf', NULL, 1, NOW(), NOW(), 0),
(710203, 'FILE_TEST_EXPORT_DONE', 'export', 'EXP_TEST_DONE', '导出文件样例.xlsx', '/mock/files/export-done.xlsx', 102400, 'xlsx', NULL, 1, NOW(), NOW(), 0),
(710204, 'FILE_TEST_INVALID', 'common', 'FILE_INVALID', '失效文件样例.jpg', '/mock/files/invalid.jpg', 51200, 'jpg', NULL, 2, NOW(), NOW(), 0);

INSERT INTO sup_qualification
(id, qualification_no, supplier_id, qualification_type, certificate_no, valid_start_date, valid_end_date, file_asset_id, audit_status, create_time, update_time, is_deleted)
VALUES
(710301, 'QUAL_TEST_WAIT', 710002, 1, 'CERT-WAIT-001', '2026-01-01', '2027-01-01', 710201, 10, NOW(), NOW(), 0),
(710302, 'QUAL_TEST_PASS', 710001, 1, 'CERT-PASS-001', '2026-01-01', '2027-01-01', 710201, 20, NOW(), NOW(), 0),
(710303, 'QUAL_TEST_REJECT', 710003, 1, 'CERT-REJECT-001', '2026-01-01', '2027-01-01', 710201, 30, NOW(), NOW(), 0);

INSERT INTO sup_contract
(id, contract_no, supplier_id, contract_name, valid_start_date, valid_end_date, deposit_amount, file_asset_id, status, create_time, update_time, is_deleted)
VALUES
(710401, 'CON_TEST_WAIT', 710002, '蔬菜供应合同待生效', '2026-07-01', '2027-06-30', 3000.0000, 710202, 10, NOW(), NOW(), 0),
(710402, 'CON_TEST_ACTIVE', 710001, '鲜果供应合同生效中', '2026-01-01', '2026-12-31', 5000.0000, 710202, 20, NOW(), NOW(), 0),
(710403, 'CON_TEST_EXPIRED', 710001, '鲜果供应合同已到期', '2025-01-01', '2025-12-31', 5000.0000, 710202, 30, NOW(), NOW(), 0),
(710404, 'CON_TEST_CLOSED', 710003, '冻品供应合同已解除', '2026-01-01', '2026-12-31', 8000.0000, 710202, 40, NOW(), NOW(), 0);

INSERT INTO usr_station
(id, station_no, station_name, city_id, address, contact_name, contact_mobile, business_hours, status, create_time, update_time, is_deleted)
VALUES
(720001, 'ST_TEST_OPEN', '海珠测试自提点', 440100, '广州市海珠区测试路1号', '自提点负责人A', '13800002001', '09:00-20:00', 1, NOW(), NOW(), 0),
(720002, 'ST_TEST_LEAVE', '天河测试休假点', 440100, '广州市天河区测试路2号', '自提点负责人B', '13800002002', '10:00-19:00', 2, NOW(), NOW(), 0),
(720003, 'ST_TEST_STOP', '深圳测试停用点', 440300, '深圳市南山区测试路3号', '自提点负责人C', '13800002003', '09:30-18:30', 3, NOW(), NOW(), 0);

INSERT INTO usr_user
(id, user_no, open_id, union_id, nickname, avatar_url, mobile, default_city_id, default_station_id, risk_level, status, create_time, update_time, is_deleted)
VALUES
(740001, 'USER_TEST_NORMAL', 'openid_test_001', 'union_test_001', '测试用户正常', '/mock/avatar/u1.png', '13800004001', 440100, 720001, 0, 1, NOW(), NOW(), 0),
(740002, 'USER_TEST_DISABLED', 'openid_test_002', 'union_test_002', '测试用户禁用', '/mock/avatar/u2.png', '13800004002', 440100, 720001, 1, 2, NOW(), NOW(), 0),
(740003, 'USER_TEST_CANCELLED', 'openid_test_003', 'union_test_003', '测试用户注销', '/mock/avatar/u3.png', '13800004003', 440300, 720003, 2, 3, NOW(), NOW(), 0);

INSERT INTO usr_pickup_person
(id, user_id, pickup_name, pickup_mobile, default_flag, status, create_time, update_time, is_deleted)
VALUES
(740101, 740001, '默认提货人', '13800004101', 1, 1, NOW(), NOW(), 0),
(740102, 740001, '备用提货人', '13800004102', 0, 1, NOW(), NOW(), 0),
(740103, 740002, '无效提货人', '13800004103', 0, 2, NOW(), NOW(), 0);

INSERT INTO usr_leader
(id, leader_no, user_id, leader_name, leader_mobile, status, create_time, update_time, is_deleted)
VALUES
(730001, 'LEADER_TEST_NORMAL', 740001, '测试团长A', '13800003001', 1, NOW(), NOW(), 0),
(730002, 'LEADER_TEST_STOP', 740002, '测试团长B', '13800003002', 2, NOW(), NOW(), 0);

INSERT INTO usr_station_leader
(id, station_id, leader_id, bind_status, bind_time, unbind_time, create_time, update_time, is_deleted)
VALUES
(730101, 720001, 730001, 1, DATE_SUB(NOW(), INTERVAL 30 DAY), NULL, NOW(), NOW(), 0),
(730102, 720002, 730002, 2, DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), NOW(), 0);

INSERT INTO usr_station_leave
(id, leave_no, station_id, leader_id, start_time, end_time, reason, audit_status, audit_account_id, audit_time, audit_remark, city_id, create_time, update_time, is_deleted)
VALUES
(730201, 'LEAVE_TEST_WAIT', 720001, 730001, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), '临时休息待审核', 10, NULL, NULL, NULL, 440100, NOW(), NOW(), 0),
(730202, 'LEAVE_TEST_PASS', 720002, 730002, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), '已通过休假', 20, 700002, DATE_SUB(NOW(), INTERVAL 3 DAY), '同意休假', 440100, NOW(), NOW(), 0),
(730203, 'LEAVE_TEST_REJECT', 720001, 730001, DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 4 DAY), '休假时间冲突', 30, 700002, NOW(), '当前站点有待提订单', 440100, NOW(), NOW(), 0),
(730204, 'LEAVE_TEST_CANCEL', 720001, 730001, DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 6 DAY), '申请人撤回', 40, NULL, NULL, '用户撤回', 440100, NOW(), NOW(), 0);

INSERT INTO sys_account
(id, account_no, username, mobile, password_hash, account_type, login_portal_code, subject_type, subject_id, status, last_login_time, last_login_ip, remark, create_time, update_time, is_deleted)
VALUES
(700001, 'ACC_TEST_PLATFORM_ADMIN', 'test_platform_admin', '13800000001', 'TEST_HASH_CHANGE_ME', 1, 'admin', 1, NULL, 1, NOW(), '127.0.0.1', '平台管理员测试账号', NOW(), NOW(), 0),
(700002, 'ACC_TEST_CITY_OPERATOR', 'test_city_operator', '13800000002', 'TEST_HASH_CHANGE_ME', 1, 'admin', 1, 440100, 1, NOW(), '127.0.0.1', '城市运营测试账号', NOW(), NOW(), 0),
(700003, 'ACC_TEST_BUYER', 'test_buyer', '13800000003', 'TEST_HASH_CHANGE_ME', 1, 'admin', 1, 440100, 1, NOW(), '127.0.0.1', '采购员测试账号', NOW(), NOW(), 0),
(700004, 'ACC_TEST_FINANCE', 'test_finance', '13800000004', 'TEST_HASH_CHANGE_ME', 1, 'admin', 1, NULL, 1, NOW(), '127.0.0.1', '财务测试账号', NOW(), NOW(), 0),
(700005, 'ACC_TEST_AFTERSALE', 'test_after_sale', '13800000005', 'TEST_HASH_CHANGE_ME', 1, 'admin', 1, NULL, 1, NOW(), '127.0.0.1', '售后运营测试账号', NOW(), NOW(), 0),
(700006, 'ACC_TEST_SUPPLIER_ADMIN', 'test_supplier_admin', '13800000006', 'TEST_HASH_CHANGE_ME', 2, 'admin', 2, 710001, 1, NOW(), '127.0.0.1', '供应商后台测试账号', NOW(), NOW(), 0),
(700007, 'ACC_TEST_WMS_SUPERVISOR', 'test_wms_supervisor', '13800000007', 'TEST_HASH_CHANGE_ME', 3, 'wms-admin', 3, 50001, 1, NOW(), '127.0.0.1', '仓库主管测试账号', NOW(), NOW(), 0),
(700008, 'ACC_TEST_WMS_RECEIVER', 'test_wms_receiver', '13800000008', 'TEST_HASH_CHANGE_ME', 3, 'warehouse-h5', 3, 50001, 1, NOW(), '127.0.0.1', '收货员测试账号', NOW(), NOW(), 0),
(700009, 'ACC_TEST_WMS_PICKER', 'test_wms_picker', '13800000009', 'TEST_HASH_CHANGE_ME', 3, 'warehouse-h5', 3, 50001, 1, NOW(), '127.0.0.1', '拣货员测试账号', NOW(), NOW(), 0),
(700010, 'ACC_TEST_WMS_LOADER', 'test_wms_loader', '13800000010', 'TEST_HASH_CHANGE_ME', 3, 'warehouse-h5', 3, 50001, 1, NOW(), '127.0.0.1', '装车员测试账号', NOW(), NOW(), 0),
(700011, 'ACC_TEST_WMS_DRIVER', 'test_wms_driver', '13800000011', 'TEST_HASH_CHANGE_ME', 3, 'warehouse-h5', 3, 50001, 1, NOW(), '127.0.0.1', '司机测试账号', NOW(), NOW(), 0),
(700012, 'ACC_TEST_STATION', 'test_station', '13800000012', 'TEST_HASH_CHANGE_ME', 4, 'role-workbench-h5', 4, 720001, 1, NOW(), '127.0.0.1', '自提点角色工作台测试账号', NOW(), NOW(), 0),
(700013, 'ACC_TEST_LEADER', 'test_leader', '13800000013', 'TEST_HASH_CHANGE_ME', 4, 'role-workbench-h5', 5, 730001, 1, NOW(), '127.0.0.1', '团长角色工作台测试账号', NOW(), NOW(), 0),
(700014, 'ACC_TEST_SUPPLIER_H5', 'test_supplier_h5', '13800000014', 'TEST_HASH_CHANGE_ME', 4, 'role-workbench-h5', 2, 710001, 1, NOW(), '127.0.0.1', '供应商工作台测试账号', NOW(), NOW(), 0),
(700015, 'ACC_TEST_DISABLED', 'test_disabled', '13800000015', 'TEST_HASH_CHANGE_ME', 4, 'role-workbench-h5', 4, 720003, 2, NULL, NULL, '禁用账号样例', NOW(), NOW(), 0),
(700016, 'ACC_TEST_LOCKED', 'test_locked', '13800000016', 'TEST_HASH_CHANGE_ME', 2, 'admin', 2, 710003, 3, NULL, NULL, '锁定账号样例', NOW(), NOW(), 0);

INSERT INTO sys_account_subject
(id, account_id, subject_type, subject_id, subject_name_snapshot, default_flag, status, create_time, update_time, is_deleted)
VALUES
(700101, 700006, 2, 710001, '测试鲜果供应商', 1, 1, NOW(), NOW(), 0),
(700102, 700012, 4, 720001, '海珠测试自提点', 1, 1, NOW(), NOW(), 0),
(700103, 700013, 5, 730001, '测试团长A', 1, 1, NOW(), NOW(), 0),
(700104, 700013, 4, 720001, '海珠测试自提点', 0, 1, NOW(), NOW(), 0),
(700105, 700014, 2, 710001, '测试鲜果供应商', 1, 1, NOW(), NOW(), 0),
(700106, 700015, 4, 720003, '深圳测试停用点', 1, 2, NOW(), NOW(), 0);

INSERT INTO sys_account_role
(id, account_id, role_id, create_time, update_time, is_deleted)
VALUES
(700201, 700001, 20001, NOW(), NOW(), 0),
(700202, 700002, 20003, NOW(), NOW(), 0),
(700203, 700003, 20005, NOW(), NOW(), 0),
(700204, 700004, 20006, NOW(), NOW(), 0),
(700205, 700005, 20007, NOW(), NOW(), 0),
(700206, 700006, 20008, NOW(), NOW(), 0),
(700207, 700007, 21001, NOW(), NOW(), 0),
(700208, 700008, 21003, NOW(), NOW(), 0),
(700209, 700009, 21004, NOW(), NOW(), 0),
(700210, 700010, 21005, NOW(), NOW(), 0),
(700211, 700011, 21006, NOW(), NOW(), 0),
(700212, 700012, 22001, NOW(), NOW(), 0),
(700213, 700013, 22002, NOW(), NOW(), 0),
(700214, 700014, 22003, NOW(), NOW(), 0),
(700215, 700016, 20008, NOW(), NOW(), 0);

INSERT INTO sys_data_scope
(id, scope_code, scope_name, scope_type, scope_object_id, include_child_flag, status, create_time, update_time, is_deleted)
VALUES
(700301, 'SUPPLIER_TEST_FRESH', '测试鲜果供应商数据', 3, 710001, 0, 1, NOW(), NOW(), 0),
(700302, 'STATION_TEST_OPEN', '海珠测试自提点数据', 4, 720001, 0, 1, NOW(), NOW(), 0),
(700303, 'LEADER_TEST_NORMAL', '测试团长A数据', 7, 730001, 1, 1, NOW(), NOW(), 0),
(700304, 'DRIVER_TEST_MAIN', '测试司机任务数据', 6, 700011, 0, 1, NOW(), NOW(), 0),
(700305, 'CITY_SZ', '深圳市数据', 2, 440300, 1, 2, NOW(), NOW(), 0);

INSERT INTO sys_account_data_scope
(id, account_id, scope_id, create_time, update_time, is_deleted)
VALUES
(700401, 700001, 60001, NOW(), NOW(), 0),
(700402, 700002, 60002, NOW(), NOW(), 0),
(700403, 700006, 700301, NOW(), NOW(), 0),
(700404, 700007, 60003, NOW(), NOW(), 0),
(700405, 700008, 60003, NOW(), NOW(), 0),
(700406, 700009, 60003, NOW(), NOW(), 0),
(700407, 700010, 60003, NOW(), NOW(), 0),
(700408, 700011, 700304, NOW(), NOW(), 0),
(700409, 700012, 700302, NOW(), NOW(), 0),
(700410, 700013, 700303, NOW(), NOW(), 0),
(700411, 700014, 700301, NOW(), NOW(), 0);

INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(700501, 20001, 'admin', 'admin_dashboard', 'view', '查看', 1, 1, NOW(), NOW(), 0),
(700502, 20003, 'admin', 'admin_station_leader', 'audit_leave', '审核休假', 2, 1, NOW(), NOW(), 0),
(700503, 20005, 'admin', 'admin_supplier_purchase', 'audit_purchase', '审核采购', 2, 1, NOW(), NOW(), 0),
(700504, 21001, 'wms-admin', 'wms_return', 'confirm_return', '回仓确认', 2, 1, NOW(), NOW(), 0),
(700505, 21003, 'warehouse-h5', 'wms_inbound', 'receive', '收货', 1, 1, NOW(), NOW(), 0),
(700506, 22001, 'role-workbench-h5', 'role_station_pickup', 'confirm_pickup', '提货核销', 1, 1, NOW(), NOW(), 0),
(700507, 22003, 'role-workbench-h5', 'role_supplier_delivery', 'confirm_delivery', '确认送货', 1, 2, NOW(), NOW(), 0);

INSERT INTO sys_config
(id, config_code, config_name, config_value, config_scope_type, config_scope_id, version_no, status, create_time, update_time, is_deleted)
VALUES
(700601, 'ORDER_PAY_EXPIRE_MINUTES', '订单支付过期分钟数', '15', 1, NULL, 1, 1, NOW(), NOW(), 0),
(700602, 'CITY_CUTOFF_NOTICE', '城市截团提醒配置', '提前30分钟提醒', 2, 440100, 1, 1, NOW(), NOW(), 0),
(700603, 'WAREHOUSE_TASK_LIMIT', '仓库任务领取上限', '20', 3, 50001, 1, 1, NOW(), NOW(), 0),
(700604, 'DISABLED_SAMPLE_CONFIG', '停用配置样例', 'off', 1, NULL, 1, 2, NOW(), NOW(), 0);

INSERT INTO sys_task
(id, task_no, task_code, task_name, biz_type, biz_no, idempotent_key, retry_count, next_retry_time, status, fail_reason, create_time, update_time, is_deleted)
VALUES
(700701, 'TASK_TEST_WAIT', 'ORDER_CLOSE_DELAY', '未支付订单关闭任务', 'order', 'ORD_TEST_WAIT_PAY', 'idem_task_wait', 0, DATE_ADD(NOW(), INTERVAL 10 MINUTE), 10, NULL, NOW(), NOW(), 0),
(700702, 'TASK_TEST_RUNNING', 'SYNC_SEARCH_WORD', '搜索联想词同步任务', 'product', 'PRD_TEST_APPLE', 'idem_task_running', 1, DATE_ADD(NOW(), INTERVAL 5 MINUTE), 20, NULL, NOW(), NOW(), 0),
(700703, 'TASK_TEST_SUCCESS', 'PAY_NOTIFY_RETRY', '支付通知重试任务', 'pay', 'PAY_TEST_SUCCESS', 'idem_task_success', 0, NULL, 30, NULL, NOW(), NOW(), 0),
(700704, 'TASK_TEST_FAILED', 'REFUND_RETRY', '退款重试任务', 'refund', 'RF_TEST_FAILED', 'idem_task_failed', 3, DATE_ADD(NOW(), INTERVAL 30 MINUTE), 40, '渠道返回处理中', NOW(), NOW(), 0),
(700705, 'TASK_TEST_CLOSED', 'EXPORT_CLEAN', '导出任务关闭样例', 'export', 'EXP_TEST_CLOSED', 'idem_task_closed', 0, NULL, 50, '人工关闭', NOW(), NOW(), 0);

INSERT INTO sys_operation_log
(id, log_no, account_id, role_id, portal_code, module_code, action_code, action_name, biz_type, biz_no, before_json, after_json, reason, result_status, fail_reason, ip, device_info, create_time, update_time, is_deleted)
VALUES
(700801, 'OP_TEST_SUCCESS', 700002, 20003, 'admin', 'admin_station_leader', 'audit_leave', '审核休假', 'station_leave', 'LEAVE_TEST_PASS', JSON_OBJECT('status', 10), JSON_OBJECT('status', 20), '测试通过', 1, NULL, '127.0.0.1', 'Chrome', NOW(), NOW(), 0),
(700802, 'OP_TEST_FAILED', 700003, 20005, 'admin', 'admin_supplier_purchase', 'audit_purchase', '审核采购', 'purchase', 'PUR_TEST_REJECT', JSON_OBJECT('status', 10), JSON_OBJECT('status', 30), '测试失败记录', 2, '权限不足样例', '127.0.0.1', 'Chrome', NOW(), NOW(), 0);

INSERT INTO sys_export_log
(id, export_no, account_id, module_code, export_name, filter_json, scope_json, export_reason, file_asset_id, status, fail_reason, create_time, update_time, is_deleted)
VALUES
(700901, 'EXP_TEST_CREATING', 700002, 'admin_order', '订单导出生成中', JSON_OBJECT('status', 20), JSON_OBJECT('city_id', 440100), '页面测试', NULL, 10, NULL, NOW(), NOW(), 0),
(700902, 'EXP_TEST_DONE', 700004, 'finance', '财务导出成功', JSON_OBJECT('date', '2026-06-24'), JSON_OBJECT('city_id', 440100), '页面测试', 710203, 20, NULL, NOW(), NOW(), 0),
(700903, 'EXP_TEST_FAILED', 700001, 'report', '报表导出失败', JSON_OBJECT('report_type', 1), JSON_OBJECT('city_id', 440100), '页面测试', NULL, 30, '文件生成失败', NOW(), NOW(), 0);

INSERT INTO sys_login_log
(id, account_id, username, portal_code, login_status, fail_reason, ip, device_info, create_time, update_time, is_deleted)
VALUES
(701001, 700001, 'test_platform_admin', 'admin', 1, NULL, '127.0.0.1', 'Chrome', NOW(), NOW(), 0),
(701002, 700012, 'test_station', 'role-workbench-h5', 1, NULL, '127.0.0.1', 'WeChat', NOW(), NOW(), 0),
(701003, NULL, 'unknown_user', 'admin', 2, '账号不存在', '127.0.0.1', 'Chrome', NOW(), NOW(), 0);

INSERT INTO prd_category
(id, parent_id, category_code, category_name, category_type, sort_no, status, create_time, update_time, is_deleted)
VALUES
(750001, 0, 'FRONT_FRUIT', '水果鲜食', 1, 10, 1, NOW(), NOW(), 0),
(750002, 0, 'FRONT_VEGETABLE', '蔬菜豆制', 1, 20, 1, NOW(), NOW(), 0),
(750003, 0, 'STAT_FRESH', '生鲜统计', 2, 10, 1, NOW(), NOW(), 0),
(750004, 0, 'STAT_FROZEN', '冻品统计', 2, 20, 2, NOW(), NOW(), 0);

INSERT INTO prd_product
(id, product_no, product_name, front_category_id, stat_category_id, supplier_id, main_image_url, image_json, audit_status, sale_status, city_id, create_account_id, create_time, update_time, is_deleted)
VALUES
(751001, 'PRD_TEST_APPLE', '测试红富士苹果', 750001, 750003, 710001, '/mock/product/apple.jpg', JSON_ARRAY('/mock/product/apple-1.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(751002, 'PRD_TEST_BANANA_WAIT', '测试香蕉待审核', 750001, 750003, 710001, '/mock/product/banana.jpg', JSON_ARRAY('/mock/product/banana-1.jpg'), 10, 1, 440100, 700003, NOW(), NOW(), 0),
(751003, 'PRD_TEST_CABBAGE_REJECT', '测试白菜已驳回', 750002, 750003, 710002, '/mock/product/cabbage.jpg', JSON_ARRAY('/mock/product/cabbage-1.jpg'), 30, 1, 440100, 700003, NOW(), NOW(), 0),
(751004, 'PRD_TEST_FISH_STOP', '测试冻鱼已停用', 750001, 750004, 710003, '/mock/product/fish.jpg', JSON_ARRAY('/mock/product/fish-1.jpg'), 20, 2, 440300, 700003, NOW(), NOW(), 0);

INSERT INTO prd_sku
(id, sku_no, product_id, sku_name, barcode, sale_spec_text, base_unit, supplier_id, weight_gram, status, create_time, update_time, is_deleted)
VALUES
(752001, 'SKU_TEST_APPLE_1KG', 751001, '苹果1kg装', '690000000001', '1kg/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(752002, 'SKU_TEST_BANANA_2KG', 751002, '香蕉2kg装', '690000000002', '2kg/份', '份', 710001, 2000, 1, NOW(), NOW(), 0),
(752003, 'SKU_TEST_CABBAGE', 751003, '白菜500g', '690000000003', '500g/份', '份', 710002, 500, 2, NOW(), NOW(), 0),
(752004, 'SKU_TEST_FISH', 751004, '冻鱼1条', '690000000004', '1条/份', '条', 710003, 800, 1, NOW(), NOW(), 0);

INSERT INTO prd_stock_spec
(id, spec_code, spec_name, unit_name, conversion_rate, status, create_time, update_time, is_deleted)
VALUES
(753001, 'SPEC_PIECE', '按份', '份', 1, 1, NOW(), NOW(), 0),
(753002, 'SPEC_BOX', '按箱', '箱', 12, 1, NOW(), NOW(), 0),
(753003, 'SPEC_DISABLED', '停用规格', '袋', 6, 2, NOW(), NOW(), 0);

INSERT INTO prd_sku_stock_spec
(id, sku_id, stock_spec_id, conversion_rate, default_flag, create_time, update_time, is_deleted)
VALUES
(753101, 752001, 753001, 1, 1, NOW(), NOW(), 0),
(753102, 752001, 753002, 12, 0, NOW(), NOW(), 0),
(753103, 752002, 753001, 1, 1, NOW(), NOW(), 0),
(753104, 752003, 753003, 6, 1, NOW(), NOW(), 0);

INSERT INTO prd_rank_tag
(id, tag_code, tag_name, category_id, sort_no, status, create_time, update_time, is_deleted)
VALUES
(754001, 'HOT_SALE', '热销榜', 750001, 10, 1, NOW(), NOW(), 0),
(754002, 'NEW_ARRIVAL', '新品榜', 750002, 20, 1, NOW(), NOW(), 0),
(754003, 'DISABLED_TAG', '停用榜单', NULL, 30, 2, NOW(), NOW(), 0);

INSERT INTO sale_publish_period
(id, period_no, period_name, region_id, city_id, warehouse_id, sale_start_time, sale_end_time, actual_cutoff_time, delivery_date, stock_mode, status, publish_account_id, publish_time, create_time, update_time, is_deleted)
VALUES
(755001, 'PERIOD_TEST_DRAFT', '测试待发布团期', 440000, 440100, 50001, DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 1, 10, NULL, NULL, NOW(), NOW(), 0),
(755002, 'PERIOD_TEST_ONLINE', '测试已发布团期', 440000, 440100, 50001, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 8 HOUR), DATE_ADD(NOW(), INTERVAL 8 HOUR), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 20, 700002, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW(), 0),
(755003, 'PERIOD_TEST_OFFLINE', '测试已下架团期', 440000, 440100, 50001, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), CURDATE(), 2, 30, 700002, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), NOW(), 0),
(755004, 'PERIOD_TEST_EXPIRED', '测试已过期团期', 440000, 440300, 50001, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1, 40, 700002, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), NOW(), 0);

INSERT INTO sale_publish_sku
(id, period_id, product_id, sku_id, supplier_id, sale_price, purchase_price, limit_qty, min_buy_qty, planned_stock_qty, sold_qty, locked_qty, production_date, shelf_life_days, status, create_time, update_time, is_deleted)
VALUES
(756001, 755002, 751001, 752001, 710001, 19.9000, 12.0000, 5, 1, 500, 120, 20, CURDATE(), 7, 1, NOW(), NOW(), 0),
(756002, 755002, 751002, 752002, 710001, 12.9000, 8.0000, 0, 1, 300, 80, 10, CURDATE(), 5, 1, NOW(), NOW(), 0),
(756003, 755001, 751003, 752003, 710002, 6.9000, 4.0000, 3, 1, 200, 0, 0, CURDATE(), 3, 2, NOW(), NOW(), 0),
(756004, 755003, 751004, 752004, 710003, 29.9000, 20.0000, 2, 1, 100, 30, 0, CURDATE(), 180, 1, NOW(), NOW(), 0);

INSERT INTO sale_publish_scope
(id, period_id, scope_type, scope_object_id, create_time, update_time, is_deleted)
VALUES
(756101, 755002, 1, 440100, NOW(), NOW(), 0),
(756102, 755002, 2, 440000, NOW(), NOW(), 0),
(756103, 755004, 1, 440300, NOW(), NOW(), 0);

INSERT INTO sale_period_snapshot
(id, period_id, period_no, snapshot_node, snapshot_json, create_time, update_time, is_deleted)
VALUES
(756201, 755002, 'PERIOD_TEST_ONLINE', 1, JSON_OBJECT('node', 'publish', 'sku_count', 2), NOW(), NOW(), 0),
(756202, 755002, 'PERIOD_TEST_ONLINE', 2, JSON_OBJECT('node', 'order', 'order_count', 5), NOW(), NOW(), 0),
(756203, 755002, 'PERIOD_TEST_ONLINE', 3, JSON_OBJECT('node', 'purchase', 'purchase_count', 3), NOW(), NOW(), 0),
(756204, 755002, 'PERIOD_TEST_ONLINE', 4, JSON_OBJECT('node', 'outbound', 'outbound_count', 2), NOW(), NOW(), 0);

INSERT INTO ord_cart
(id, user_id, city_id, station_id, period_id, product_id, sku_id, qty, selected_flag, valid_status, create_time, update_time, is_deleted)
VALUES
(760001, 740001, 440100, 720001, 755002, 751001, 752001, 2, 1, 1, NOW(), NOW(), 0),
(760002, 740001, 440100, 720001, 755002, 751002, 752002, 1, 0, 1, NOW(), NOW(), 0),
(760003, 740002, 440100, 720001, 755003, 751004, 752004, 1, 1, 2, NOW(), NOW(), 0);

INSERT INTO ord_order
(id, order_no, user_id, region_id, city_id, station_id, leader_id, pickup_name, pickup_mobile, total_amount, pay_amount, trade_status, pay_status, fulfill_status, expire_time, pay_time, complete_time, cancel_reason, idempotent_key, create_time, update_time, is_deleted)
VALUES
(761001, 'ORD_TEST_WAIT_PAY', 740001, 440000, 440100, 720001, 730001, '默认提货人', '13800004101', 39.8000, 39.8000, 10, 10, 10, DATE_ADD(NOW(), INTERVAL 10 MINUTE), NULL, NULL, NULL, 'idem_order_wait_pay', NOW(), NOW(), 0),
(761002, 'ORD_TEST_PAID', 740001, 440000, 440100, 720001, 730001, '默认提货人', '13800004101', 19.9000, 19.9000, 20, 30, 20, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 50 MINUTE), NULL, NULL, 'idem_order_paid', NOW(), NOW(), 0),
(761003, 'ORD_TEST_CLOSED', 740002, 440000, 440100, 720001, 730001, '无效提货人', '13800004103', 12.9000, 12.9000, 30, 50, 10, DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, NULL, '超时未支付自动关闭', 'idem_order_closed', NOW(), NOW(), 0),
(761004, 'ORD_TEST_CANCELLED', 740001, 440000, 440100, 720001, 730001, '默认提货人', '13800004101', 19.9000, 19.9000, 40, 60, 20, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 23 HOUR), NULL, '用户取消退款', 'idem_order_cancelled', NOW(), NOW(), 0),
(761005, 'ORD_TEST_COMPLETED', 740001, 440000, 440100, 720001, 730001, '默认提货人', '13800004101', 59.7000, 59.7000, 50, 30, 70, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 'idem_order_completed', NOW(), NOW(), 0);

INSERT INTO ord_order_item
(id, order_id, order_no, order_item_no, period_id, product_id, sku_id, supplier_id, warehouse_id, city_id, station_id, product_name, sku_name, sale_price, qty, picked_qty, refunded_qty, item_amount, fulfill_status, after_sale_status, expected_pickup_date, create_time, update_time, is_deleted)
VALUES
(762001, 761001, 'ORD_TEST_WAIT_PAY', 'ITEM_TEST_WAIT_PAY', 755002, 751001, 752001, 710001, 50001, 440100, 720001, '测试红富士苹果', '苹果1kg装', 19.9000, 2, 0, 0, 39.8000, 10, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(762002, 761002, 'ORD_TEST_PAID', 'ITEM_TEST_WAIT_PURCHASE', 755002, 751001, 752001, 710001, 50001, 440100, 720001, '测试红富士苹果', '苹果1kg装', 19.9000, 1, 0, 0, 19.9000, 20, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(762003, 761003, 'ORD_TEST_CLOSED', 'ITEM_TEST_CLOSED', 755002, 751002, 752002, 710001, 50001, 440100, 720001, '测试香蕉待审核', '香蕉2kg装', 12.9000, 1, 0, 0, 12.9000, 10, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(762004, 761004, 'ORD_TEST_CANCELLED', 'ITEM_TEST_REFUNDED', 755002, 751001, 752001, 710001, 50001, 440100, 720001, '测试红富士苹果', '苹果1kg装', 19.9000, 1, 0, 1, 19.9000, 20, 40, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(762005, 761005, 'ORD_TEST_COMPLETED', 'ITEM_TEST_WAIT_PICKUP', 755002, 751001, 752001, 710001, 50001, 440100, 720001, '测试红富士苹果', '苹果1kg装', 19.9000, 1, 0, 0, 19.9000, 60, 0, CURDATE(), NOW(), NOW(), 0),
(762006, 761005, 'ORD_TEST_COMPLETED', 'ITEM_TEST_PICKED', 755002, 751002, 752002, 710001, 50001, 440100, 720001, '测试香蕉待审核', '香蕉2kg装', 12.9000, 1, 1, 0, 12.9000, 70, 0, CURDATE(), NOW(), NOW(), 0);

INSERT INTO ord_order_status_log
(id, order_id, order_item_id, status_line, before_status, after_status, change_reason, operator_type, operator_id, create_time, update_time, is_deleted)
VALUES
(763001, 761001, NULL, 1, NULL, 10, '创建待支付订单', 1, 740001, NOW(), NOW(), 0),
(763002, 761002, NULL, 2, 10, 30, '支付成功', 3, NULL, NOW(), NOW(), 0),
(763003, 761003, NULL, 1, 10, 30, '超时关闭', 3, NULL, NOW(), NOW(), 0),
(763004, 761004, NULL, 1, 20, 40, '用户取消', 1, 740001, NOW(), NOW(), 0),
(763005, 761005, 762006, 3, 60, 70, '自提核销', 2, 700012, NOW(), NOW(), 0);

INSERT INTO ord_fulfillment_track
(id, track_no, order_id, order_item_id, node_type, node_name, biz_no, status, remark, city_id, station_id, warehouse_id, create_time, update_time, is_deleted)
VALUES
(764001, 'TRACK_TEST_PAY_WAIT', 761001, 762001, 1, '待支付', 'ORD_TEST_WAIT_PAY', 10, '等待用户支付', 440100, 720001, NULL, NOW(), NOW(), 0),
(764002, 'TRACK_TEST_PURCHASE', 761002, 762002, 2, '待采购', 'PUR_TEST_WAIT_AUDIT', 20, '已进入采购', 440100, 720001, 50001, NOW(), NOW(), 0),
(764003, 'TRACK_TEST_PICKUP', 761005, 762005, 6, '待自提', 'WMS_DELIVERY_ST_TEST_DONE', 60, '已到自提点', 440100, 720001, 50001, NOW(), NOW(), 0),
(764004, 'TRACK_TEST_DONE', 761005, 762006, 7, '已自提', 'PICKUP_DONE', 70, '已完成', 440100, 720001, 50001, NOW(), NOW(), 0);

INSERT INTO pay_trade
(id, pay_no, order_id, order_no, channel_type, pay_amount, trade_status, channel_trade_no, pay_time, idempotent_key, create_time, update_time, is_deleted)
VALUES
(765001, 'PAY_TEST_WAIT', 761001, 'ORD_TEST_WAIT_PAY', 1, 39.8000, 10, NULL, NULL, 'idem_pay_wait', NOW(), NOW(), 0),
(765002, 'PAY_TEST_PROCESSING', 761002, 'ORD_TEST_PAID', 1, 19.9000, 20, 'WX_PROCESSING_001', NULL, 'idem_pay_processing', NOW(), NOW(), 0),
(765003, 'PAY_TEST_SUCCESS', 761005, 'ORD_TEST_COMPLETED', 1, 59.7000, 30, 'WX_SUCCESS_001', DATE_SUB(NOW(), INTERVAL 3 DAY), 'idem_pay_success', NOW(), NOW(), 0),
(765004, 'PAY_TEST_FAILED', 761002, 'ORD_TEST_PAID', 1, 19.9000, 40, 'WX_FAILED_001', NULL, 'idem_pay_failed', NOW(), NOW(), 0),
(765005, 'PAY_TEST_CLOSED', 761003, 'ORD_TEST_CLOSED', 1, 12.9000, 50, NULL, NULL, 'idem_pay_closed', NOW(), NOW(), 0);

INSERT INTO pay_refund_trade
(id, refund_no, pay_no, order_id, after_sale_id, refund_amount, refund_status, channel_refund_no, refund_time, fail_reason, idempotent_key, create_time, update_time, is_deleted)
VALUES
(766001, 'RF_TEST_WAIT', 'PAY_TEST_SUCCESS', 761005, 767001, 19.9000, 10, NULL, NULL, NULL, 'idem_refund_wait', NOW(), NOW(), 0),
(766002, 'RF_TEST_PROCESSING', 'PAY_TEST_SUCCESS', 761005, 767002, 12.9000, 20, 'WX_RF_PROCESSING', NULL, NULL, 'idem_refund_processing', NOW(), NOW(), 0),
(766003, 'RF_TEST_SUCCESS', 'PAY_TEST_SUCCESS', 761004, 767004, 19.9000, 30, 'WX_RF_SUCCESS', NOW(), NULL, 'idem_refund_success', NOW(), NOW(), 0),
(766004, 'RF_TEST_FAILED', 'PAY_TEST_SUCCESS', 761005, 767005, 6.9000, 40, 'WX_RF_FAILED', NULL, '渠道退款失败', 'idem_refund_failed', NOW(), NOW(), 0);

INSERT INTO pay_fund_flow
(id, flow_no, flow_type, biz_no, amount, direction, subject_type, subject_id, status, channel_flow_no, create_time, update_time, is_deleted)
VALUES
(766101, 'FUND_TEST_PAY', 1, 'PAY_TEST_SUCCESS', 59.7000, 1, 4, NULL, 30, 'WX_SUCCESS_001', NOW(), NOW(), 0),
(766102, 'FUND_TEST_REFUND', 2, 'RF_TEST_SUCCESS', 19.9000, 2, 4, NULL, 30, 'WX_RF_SUCCESS', NOW(), NOW(), 0),
(766103, 'FUND_TEST_WITHDRAW', 3, 'WD_TEST_SUCCESS', 100.0000, 2, 2, 720001, 30, 'BANK_WD_SUCCESS', NOW(), NOW(), 0),
(766104, 'FUND_TEST_SPLIT', 4, 'SPLIT_TEST_SUCCESS', 3.0000, 2, 4, NULL, 30, 'SPLIT_SUCCESS', NOW(), NOW(), 0);

INSERT INTO afs_after_sale
(id, after_sale_no, order_id, order_no, user_id, city_id, station_id, after_sale_type, status, apply_reason, refund_amount, responsibility_type, create_time, update_time, is_deleted)
VALUES
(767001, 'AFS_TEST_WAIT_AUDIT', 761005, 'ORD_TEST_COMPLETED', 740001, 440100, 720001, 1, 10, '未提货申请退款', 19.9000, 1, NOW(), NOW(), 0),
(767002, 'AFS_TEST_WAIT_RETURN', 761005, 'ORD_TEST_COMPLETED', 740001, 440100, 720001, 2, 20, '商品破损需退货', 12.9000, 3, NOW(), NOW(), 0),
(767003, 'AFS_TEST_WAIT_REFUND', 761005, 'ORD_TEST_COMPLETED', 740001, 440100, 720001, 1, 30, '审核通过待退款', 6.9000, 2, NOW(), NOW(), 0),
(767004, 'AFS_TEST_REFUND_SUCCESS', 761004, 'ORD_TEST_CANCELLED', 740001, 440100, 720001, 1, 40, '取消退款成功', 19.9000, 4, NOW(), NOW(), 0),
(767005, 'AFS_TEST_REFUND_FAILED', 761005, 'ORD_TEST_COMPLETED', 740001, 440100, 720001, 1, 50, '退款失败待重试', 6.9000, 2, NOW(), NOW(), 0),
(767006, 'AFS_TEST_REJECTED', 761005, 'ORD_TEST_COMPLETED', 740001, 440100, 720001, 2, 60, '超过售后时效', 12.9000, 1, NOW(), NOW(), 0),
(767007, 'AFS_TEST_CANCELLED', 761005, 'ORD_TEST_COMPLETED', 740001, 440100, 720001, 1, 70, '用户撤销售后', 19.9000, 1, NOW(), NOW(), 0);

INSERT INTO afs_after_sale_item
(id, after_sale_id, order_item_id, sku_id, supplier_id, apply_qty, approved_qty, refund_amount, create_time, update_time, is_deleted)
VALUES
(767101, 767001, 762005, 752001, 710001, 1, 0, 19.9000, NOW(), NOW(), 0),
(767102, 767002, 762006, 752002, 710001, 1, 1, 12.9000, NOW(), NOW(), 0),
(767103, 767004, 762004, 752001, 710001, 1, 1, 19.9000, NOW(), NOW(), 0);

INSERT INTO afs_audit_log
(id, after_sale_id, audit_action, before_status, after_status, audit_account_id, audit_reason, create_time, update_time, is_deleted)
VALUES
(767201, 767002, 1, 10, 20, 700005, '同意退货退款', NOW(), NOW(), 0),
(767202, 767006, 2, 10, 60, 700005, '超过售后时效', NOW(), NOW(), 0),
(767203, 767003, 1, 10, 30, 700005, '同意仅退款', NOW(), NOW(), 0);

INSERT INTO afs_return_record
(id, return_no, after_sale_id, order_item_id, sku_id, station_id, warehouse_id, return_qty, return_status, proof_file_json, create_time, update_time, is_deleted)
VALUES
(767301, 'AFS_RET_TEST_WAIT', 767002, 762006, 752002, 720001, 50001, 1, 10, JSON_ARRAY('/mock/return/wait.jpg'), NOW(), NOW(), 0),
(767302, 'AFS_RET_TEST_DONE', 767004, 762004, 752001, 720001, 50001, 1, 20, JSON_ARRAY('/mock/return/done.jpg'), NOW(), NOW(), 0),
(767303, 'AFS_RET_TEST_CANCEL', 767006, 762006, 752002, 720001, 50001, 1, 30, JSON_ARRAY('/mock/return/cancel.jpg'), NOW(), NOW(), 0);

INSERT INTO usr_user_favorite
(id, user_id, product_id, sku_id, station_id, status, create_time, update_time, is_deleted)
VALUES
(768001, 740001, 751001, 752001, 720001, 1, NOW(), NOW(), 0),
(768002, 740001, 751002, 752002, 720001, 2, NOW(), NOW(), 0);

INSERT INTO usr_browse_history
(id, user_id, product_id, sku_id, station_id, browse_time, create_time, update_time, is_deleted)
VALUES
(768101, 740001, 751001, 752001, 720001, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW(), 0),
(768102, 740002, 751002, 752002, 720001, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0);

INSERT INTO usr_comment
(id, comment_no, user_id, order_id, order_item_id, product_id, sku_id, station_id, score, content, image_json, responsibility_type, status, city_id, create_time, update_time, is_deleted)
VALUES
(768201, 'CMT_TEST_SHOW', 740001, 761005, 762006, 751002, 752002, 720001, 5, '商品新鲜，提货顺利', JSON_ARRAY('/mock/comment/show.jpg'), 1, 1, 440100, NOW(), NOW(), 0),
(768202, 'CMT_TEST_HIDE', 740001, 761005, 762005, 751001, 752001, 720001, 2, '包装破损待核查', JSON_ARRAY('/mock/comment/hide.jpg'), 3, 2, 440100, NOW(), NOW(), 0),
(768203, 'CMT_TEST_DELETE', 740002, 761003, 762003, 751002, 752002, 720001, 1, '删除样例', NULL, 2, 3, 440100, NOW(), NOW(), 0);

INSERT INTO pur_purchase_order
(id, purchase_no, source_type, period_id, supplier_id, warehouse_id, region_id, city_id, delivery_date, purchase_status, audit_status, create_account_id, create_reason, expected_arrival_time, create_time, update_time, is_deleted)
VALUES
(770001, 'PUR_TEST_WAIT_AUDIT', 1, 755002, 710001, 50001, 440000, 440100, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 10, 10, 700003, '截团后系统生成', DATE_ADD(NOW(), INTERVAL 8 HOUR), NOW(), NOW(), 0),
(770002, 'PUR_TEST_AUDIT_PASS', 2, 755002, 710001, 50001, 440000, 440100, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 20, 20, 700003, '运营补采', DATE_ADD(NOW(), INTERVAL 6 HOUR), NOW(), NOW(), 0),
(770003, 'PUR_TEST_REJECT', 3, 755001, 710002, 50001, 440000, 440100, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 30, 30, 700003, '数量异常', DATE_ADD(NOW(), INTERVAL 12 HOUR), NOW(), NOW(), 0),
(770004, 'PUR_TEST_DELIVERING', 4, 755002, 710001, 50001, 440000, 440100, CURDATE(), 40, 20, 700006, '供应商创建送货', DATE_ADD(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0),
(770005, 'PUR_TEST_RECEIVED', 5, 755002, 710001, 50001, 440000, 440100, CURDATE(), 50, 20, 700003, '缺货补货', DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0);

INSERT INTO pur_purchase_item
(id, purchase_id, purchase_no, product_id, sku_id, supplier_id, warehouse_id, apply_qty, purchase_qty, received_qty, rejected_qty, purchase_price, status, create_time, update_time, is_deleted)
VALUES
(770101, 770001, 'PUR_TEST_WAIT_AUDIT', 751001, 752001, 710001, 50001, 100, 100, 0, 0, 12.0000, 1, NOW(), NOW(), 0),
(770102, 770002, 'PUR_TEST_AUDIT_PASS', 751002, 752002, 710001, 50001, 80, 80, 0, 0, 8.0000, 1, NOW(), NOW(), 0),
(770103, 770003, 'PUR_TEST_REJECT', 751003, 752003, 710002, 50001, 60, 0, 0, 0, 4.0000, 2, NOW(), NOW(), 0),
(770104, 770004, 'PUR_TEST_DELIVERING', 751001, 752001, 710001, 50001, 120, 120, 0, 0, 12.0000, 1, NOW(), NOW(), 0),
(770105, 770005, 'PUR_TEST_RECEIVED', 751001, 752001, 710001, 50001, 150, 150, 145, 5, 12.0000, 1, NOW(), NOW(), 0);

INSERT INTO pur_purchase_audit_log
(id, purchase_id, audit_action, before_status, after_status, audit_account_id, audit_remark, create_time, update_time, is_deleted)
VALUES
(770201, 770002, 1, 10, 20, 700003, '采购审核通过', NOW(), NOW(), 0),
(770202, 770003, 2, 10, 30, 700003, '采购数量异常驳回', NOW(), NOW(), 0),
(770203, 770002, 3, 20, 20, 700003, '调整采购数量', NOW(), NOW(), 0);

INSERT INTO pur_delivery_order
(id, delivery_no, purchase_id, purchase_no, supplier_id, warehouse_id, expected_arrival_time, arrival_proof_json, contact_name, contact_mobile, vehicle_no, status, create_time, update_time, is_deleted)
VALUES
(771001, 'PDEL_TEST_WAIT', 770002, 'PUR_TEST_AUDIT_PASS', 710001, 50001, DATE_ADD(NOW(), INTERVAL 4 HOUR), NULL, '鲜果送货联系人', '13800001003', '粤A-T001', 10, NOW(), NOW(), 0),
(771002, 'PDEL_TEST_ONROAD', 770004, 'PUR_TEST_DELIVERING', 710001, 50001, DATE_ADD(NOW(), INTERVAL 2 HOUR), JSON_OBJECT('photo', '/mock/pur/onroad.jpg'), '鲜果送货联系人', '13800001003', '粤A-T002', 20, NOW(), NOW(), 0),
(771003, 'PDEL_TEST_ARRIVED', 770004, 'PUR_TEST_DELIVERING', 710001, 50001, DATE_SUB(NOW(), INTERVAL 30 MINUTE), JSON_OBJECT('photo', '/mock/pur/arrived.jpg'), '鲜果送货联系人', '13800001003', '粤A-T003', 30, NOW(), NOW(), 0),
(771004, 'PDEL_TEST_RECEIVED', 770005, 'PUR_TEST_RECEIVED', 710001, 50001, DATE_SUB(NOW(), INTERVAL 3 HOUR), JSON_OBJECT('photo', '/mock/pur/received.jpg'), '鲜果送货联系人', '13800001003', '粤A-T004', 40, NOW(), NOW(), 0),
(771005, 'PDEL_TEST_CLOSED', 770003, 'PUR_TEST_REJECT', 710002, 50001, DATE_ADD(NOW(), INTERVAL 1 DAY), NULL, '蔬菜无效联系人', '13800001004', '粤A-T005', 50, NOW(), NOW(), 0);

INSERT INTO pur_delivery_item
(id, delivery_id, purchase_item_id, sku_id, delivery_qty, received_qty, diff_qty, create_time, update_time, is_deleted)
VALUES
(771101, 771001, 770102, 752002, 80, 0, 0, NOW(), NOW(), 0),
(771102, 771002, 770104, 752001, 120, 0, 0, NOW(), NOW(), 0),
(771103, 771003, 770104, 752001, 120, 0, 0, NOW(), NOW(), 0),
(771104, 771004, 770105, 752001, 150, 145, 5, NOW(), NOW(), 0);

INSERT INTO pur_purchase_source_log
(id, purchase_id, source_type, source_biz_no, source_reason, create_time, update_time, is_deleted)
VALUES
(771201, 770001, 1, 'PERIOD_TEST_ONLINE', '截团生成采购', NOW(), NOW(), 0),
(771202, 770004, 4, 'SUP_TEST_FRESH', '供应商主动送货', NOW(), NOW(), 0),
(771203, 770005, 5, 'STOCKOUT_TEST_WAIT', '缺货补货', NOW(), NOW(), 0);

INSERT INTO wms_location
(id, warehouse_id, zone_id, location_code, location_name, capacity_qty, status, create_time, update_time, is_deleted)
VALUES
(780001, 50001, 51001, 'RCV-A-01', '收货暂存A01', 1000, 1, NOW(), NOW(), 0),
(780002, 50001, 51002, 'PUT-A-01', '待上架A01', 1000, 1, NOW(), NOW(), 0),
(780003, 50001, 51003, 'SALE-A-01', '可售A01', 2000, 1, NOW(), NOW(), 0),
(780004, 50001, 51004, 'PICK-A-01', '拣货暂存A01', 1000, 1, NOW(), NOW(), 0),
(780005, 50001, 51005, 'LOAD-A-01', '装车暂存A01', 1000, 1, NOW(), NOW(), 0),
(780006, 50001, 51006, 'RET-A-01', '回仓暂存A01', 500, 2, NOW(), NOW(), 0);

INSERT INTO wms_driver
(id, driver_no, account_id, driver_name, driver_mobile, vehicle_no, status, create_time, update_time, is_deleted)
VALUES
(780101, 'DRV_TEST_MAIN', 700011, '测试司机A', '13800008001', '粤A-D001', 1, NOW(), NOW(), 0),
(780102, 'DRV_TEST_STOP', NULL, '测试司机停用', '13800008002', '粤A-D002', 2, NOW(), NOW(), 0);

INSERT INTO wms_line
(id, warehouse_id, line_code, line_name, default_driver_account_id, status, create_time, update_time, is_deleted)
VALUES
(780201, 50001, 'LINE_TEST_GZ01', '广州测试线路一', 700011, 1, NOW(), NOW(), 0),
(780202, 50001, 'LINE_TEST_STOP', '停用测试线路', NULL, 2, NOW(), NOW(), 0);

INSERT INTO wms_line_station
(id, line_id, station_id, delivery_sort, status, create_time, update_time, is_deleted)
VALUES
(780301, 780201, 720001, 1, 1, NOW(), NOW(), 0),
(780302, 780201, 720002, 2, 1, NOW(), NOW(), 0),
(780303, 780202, 720003, 1, 2, NOW(), NOW(), 0);

INSERT INTO wms_inbound_order
(id, inbound_no, source_type, source_no, purchase_id, delivery_id, supplier_id, warehouse_id, status, receive_account_id, receive_time, create_time, update_time, is_deleted)
VALUES
(781001, 'INB_TEST_WAIT_ARRIVAL', 1, 'PDEL_TEST_WAIT', 770002, 771001, 710001, 50001, 10, NULL, NULL, NOW(), NOW(), 0),
(781002, 'INB_TEST_WAIT_RECEIVE', 1, 'PDEL_TEST_ARRIVED', 770004, 771003, 710001, 50001, 20, NULL, NULL, NOW(), NOW(), 0),
(781003, 'INB_TEST_RECEIVING', 1, 'PDEL_TEST_ARRIVED', 770004, 771003, 710001, 50001, 30, 700008, NULL, NOW(), NOW(), 0),
(781004, 'INB_TEST_WAIT_PUTAWAY', 1, 'PDEL_TEST_RECEIVED', 770005, 771004, 710001, 50001, 40, 700008, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0),
(781005, 'INB_TEST_PUTAWAYING', 1, 'PDEL_TEST_RECEIVED', 770005, 771004, 710001, 50001, 50, 700008, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW(), NOW(), 0),
(781006, 'INB_TEST_ON_SHELF', 1, 'PDEL_TEST_RECEIVED', 770005, 771004, 710001, 50001, 60, 700008, DATE_SUB(NOW(), INTERVAL 4 HOUR), NOW(), NOW(), 0);

INSERT INTO wms_inbound_item
(id, inbound_id, sku_id, supplier_id, expected_qty, received_qty, rejected_qty, damaged_qty, batch_no, production_date, shelf_life_days, diff_reason, create_time, update_time, is_deleted)
VALUES
(781101, 781001, 752002, 710001, 80, 0, 0, 0, 'BATCH_WAIT_001', CURDATE(), 5, NULL, NOW(), NOW(), 0),
(781102, 781002, 752001, 710001, 120, 0, 0, 0, 'BATCH_RECEIVE_001', CURDATE(), 7, NULL, NOW(), NOW(), 0),
(781103, 781003, 752001, 710001, 120, 60, 0, 0, 'BATCH_RECEIVING_001', CURDATE(), 7, NULL, NOW(), NOW(), 0),
(781104, 781004, 752001, 710001, 150, 145, 5, 0, 'BATCH_PUTAWAY_001', CURDATE(), 7, '少收5份', NOW(), NOW(), 0),
(781105, 781006, 752001, 710001, 150, 145, 5, 0, 'BATCH_SHELF_001', CURDATE(), 7, '少收5份', NOW(), NOW(), 0);

INSERT INTO wms_receive_record
(id, receive_no, inbound_id, inbound_item_id, sku_id, receive_qty, reject_qty, diff_qty, receive_account_id, quality_status, remark, create_time, update_time, is_deleted)
VALUES
(781201, 'RCV_TEST_PASS', 781004, 781104, 752001, 145, 0, 5, 700008, 1, '收货合格', NOW(), NOW(), 0),
(781202, 'RCV_TEST_PART', 781003, 781103, 752001, 60, 0, 60, 700008, 2, '部分收货', NOW(), NOW(), 0),
(781203, 'RCV_TEST_REJECT', 781004, 781104, 752001, 0, 5, 5, 700008, 3, '拒收破损', NOW(), NOW(), 0),
(781204, 'RCV_TEST_CONFIRM', 781002, 781102, 752001, 0, 0, 0, 700008, 4, '待确认', NOW(), NOW(), 0);

INSERT INTO wms_quality_file
(id, quality_no, inbound_id, sku_id, quality_result, file_asset_id, remark, create_time, update_time, is_deleted)
VALUES
(781301, 'QUAL_FILE_PASS', 781004, 752001, 1, 710204, '合格图片样例', NOW(), NOW(), 0),
(781302, 'QUAL_FILE_PART', 781003, 752001, 2, 710204, '部分合格图片样例', NOW(), NOW(), 0),
(781303, 'QUAL_FILE_REJECT', 781004, 752001, 3, 710204, '拒收图片样例', NOW(), NOW(), 0);

INSERT INTO wms_putaway_task
(id, putaway_no, inbound_id, sku_id, warehouse_id, from_zone_id, target_zone_id, target_location_id, putaway_qty, actual_qty, status, assign_account_id, complete_time, create_time, update_time, is_deleted)
VALUES
(782001, 'PUT_TEST_WAIT', 781004, 752001, 50001, 51001, 51003, 780003, 145, 0, 10, 700007, NULL, NOW(), NOW(), 0),
(782002, 'PUT_TEST_DOING', 781005, 752001, 50001, 51002, 51003, 780003, 145, 60, 20, 700007, NULL, NOW(), NOW(), 0),
(782003, 'PUT_TEST_DONE', 781006, 752001, 50001, 51002, 51003, 780003, 145, 145, 30, 700007, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW(), 0),
(782004, 'PUT_TEST_CLOSED', 781004, 752002, 50001, 51001, 51003, 780003, 80, 0, 40, 700007, NULL, NOW(), NOW(), 0);

INSERT INTO wms_inventory
(id, warehouse_id, zone_id, location_id, sku_id, supplier_id, batch_no, production_date, shelf_life_days, in_stock_qty, locked_qty, available_qty, frozen_qty, inventory_status, version, create_time, update_time, is_deleted)
VALUES
(783001, 50001, 51002, 780002, 752001, 710001, 'BATCH_INV_WAIT', CURDATE(), 7, 80, 0, 0, 0, 10, 1, NOW(), NOW(), 0),
(783002, 50001, 51003, 780003, 752001, 710001, 'BATCH_INV_SALE', CURDATE(), 7, 300, 40, 260, 0, 20, 2, NOW(), NOW(), 0),
(783003, 50001, 51004, 780004, 752001, 710001, 'BATCH_INV_PICK', CURDATE(), 7, 50, 0, 50, 0, 30, 1, NOW(), NOW(), 0),
(783004, 50001, 51006, 780006, 752002, 710001, 'BATCH_INV_RETURN', CURDATE(), 5, 10, 0, 0, 10, 40, 1, NOW(), NOW(), 0),
(783005, 50001, 51001, 780001, 752003, 710002, 'BATCH_INV_CONFIRM', CURDATE(), 3, 20, 0, 0, 20, 50, 1, NOW(), NOW(), 0);

INSERT INTO wms_inventory_lock
(id, lock_no, warehouse_id, inventory_id, sku_id, biz_type, biz_no, lock_qty, release_qty, lock_status, create_time, update_time, is_deleted)
VALUES
(783101, 'LOCK_TEST_ACTIVE', 50001, 783002, 752001, 2, 'WAVE_TEST_LOCKED', 40, 0, 10, NOW(), NOW(), 0),
(783102, 'LOCK_TEST_RELEASED', 50001, 783002, 752001, 1, 'ORD_TEST_CANCELLED', 10, 10, 20, NOW(), NOW(), 0),
(783103, 'LOCK_TEST_DEDUCTED', 50001, 783002, 752001, 2, 'WAVE_TEST_DONE', 30, 0, 30, NOW(), NOW(), 0);

INSERT INTO wms_inventory_log
(id, log_no, warehouse_id, zone_id, location_id, sku_id, batch_no, change_type, before_qty, change_qty, after_qty, biz_type, biz_no, operator_account_id, remark, create_time, update_time, is_deleted)
VALUES
(783201, 'INV_LOG_IN', 50001, 51001, 780001, 752001, 'BATCH_LOG_IN', 1, 0, 145, 145, 'inbound', 'INB_TEST_ON_SHELF', 700008, '入库流水', NOW(), NOW(), 0),
(783202, 'INV_LOG_PUT', 50001, 51003, 780003, 752001, 'BATCH_LOG_PUT', 2, 0, 145, 145, 'putaway', 'PUT_TEST_DONE', 700007, '上架流水', NOW(), NOW(), 0),
(783203, 'INV_LOG_LOCK', 50001, 51003, 780003, 752001, 'BATCH_LOG_LOCK', 3, 300, -40, 260, 'wave', 'WAVE_TEST_LOCKED', 700007, '锁定流水', NOW(), NOW(), 0),
(783204, 'INV_LOG_RELEASE', 50001, 51003, 780003, 752001, 'BATCH_LOG_RELEASE', 4, 260, 10, 270, 'order', 'ORD_TEST_CANCELLED', 700007, '释放流水', NOW(), NOW(), 0),
(783205, 'INV_LOG_OUT', 50001, 51003, 780003, 752001, 'BATCH_LOG_OUT', 5, 270, -30, 240, 'outbound', 'OUT_TEST_DONE', 700010, '出库流水', NOW(), NOW(), 0),
(783206, 'INV_LOG_RETURN', 50001, 51006, 780006, 752002, 'BATCH_LOG_RETURN', 6, 0, 3, 3, 'return', 'RET_TEST_WAIT', 700011, '回仓流水', NOW(), NOW(), 0);

INSERT INTO wms_wave_batch
(id, wave_no, period_id, warehouse_id, line_id, delivery_date, required_qty, locked_qty, status, create_account_id, create_time, update_time, is_deleted)
VALUES
(784001, 'WAVE_TEST_WAIT_LOCK', 755002, 50001, 780201, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 100, 0, 10, 700007, NOW(), NOW(), 0),
(784002, 'WAVE_TEST_LOCKED', 755002, 50001, 780201, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 100, 100, 20, 700007, NOW(), NOW(), 0),
(784003, 'WAVE_TEST_PICKING', 755002, 50001, 780201, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 100, 100, 30, 700007, NOW(), NOW(), 0),
(784004, 'WAVE_TEST_DONE', 755002, 50001, 780201, CURDATE(), 80, 80, 40, 700007, NOW(), NOW(), 0),
(784005, 'WAVE_TEST_DIFF', 755002, 50001, 780201, CURDATE(), 80, 80, 50, 700007, NOW(), NOW(), 0),
(784006, 'WAVE_TEST_CANCEL', 755002, 50001, 780201, CURDATE(), 30, 0, 60, 700007, NOW(), NOW(), 0);

INSERT INTO wms_pick_task
(id, pick_no, wave_id, warehouse_id, line_id, assign_account_id, required_qty, actual_qty, shortage_qty, status, complete_time, create_time, update_time, is_deleted)
VALUES
(784101, 'PICK_TEST_WAIT', 784002, 50001, 780201, 700009, 100, 0, 0, 10, NULL, NOW(), NOW(), 0),
(784102, 'PICK_TEST_DOING', 784003, 50001, 780201, 700009, 100, 50, 0, 20, NULL, NOW(), NOW(), 0),
(784103, 'PICK_TEST_DONE', 784004, 50001, 780201, 700009, 80, 80, 0, 30, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0),
(784104, 'PICK_TEST_DIFF', 784005, 50001, 780201, 700009, 80, 75, 5, 40, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW(), 0);

INSERT INTO wms_pick_item
(id, pick_id, sku_id, inventory_id, location_id, required_qty, actual_qty, shortage_qty, diff_reason, create_time, update_time, is_deleted)
VALUES
(784201, 784101, 752001, 783002, 780003, 100, 0, 0, NULL, NOW(), NOW(), 0),
(784202, 784102, 752001, 783002, 780003, 100, 50, 0, NULL, NOW(), NOW(), 0),
(784203, 784103, 752001, 783002, 780003, 80, 80, 0, NULL, NOW(), NOW(), 0),
(784204, 784104, 752001, 783002, 780003, 80, 75, 5, '库位少货', NOW(), NOW(), 0);

INSERT INTO ord_stockout_record
(id, stockout_no, period_id, order_id, order_item_id, sku_id, warehouse_id, station_id, shortage_qty, handled_qty, reason, status, create_time, update_time, is_deleted)
VALUES
(784301, 'STOCKOUT_TEST_WAIT', 755002, 761005, 762005, 752001, 50001, 720001, 5, 0, '拣货少货', 10, NOW(), NOW(), 0),
(784302, 'STOCKOUT_TEST_DOING', 755002, 761005, 762005, 752001, 50001, 720001, 5, 2, '补采处理中', 20, NOW(), NOW(), 0),
(784303, 'STOCKOUT_TEST_DONE', 755002, 761005, 762005, 752001, 50001, 720001, 5, 5, '已补齐', 30, NOW(), NOW(), 0),
(784304, 'STOCKOUT_TEST_CLOSED', 755002, 761005, 762005, 752001, 50001, 720001, 3, 0, '人工关闭', 40, NOW(), NOW(), 0);

INSERT INTO ord_replenish_order
(id, replenish_no, source_order_id, source_order_item_id, sku_id, station_id, replenish_qty, status, reason, create_time, update_time, is_deleted)
VALUES
(784401, 'REPL_TEST_WAIT', 761005, 762005, 752001, 720001, 1, 10, '缺货补发待处理', NOW(), NOW(), 0),
(784402, 'REPL_TEST_DOING', 761005, 762005, 752001, 720001, 1, 20, '补发处理中', NOW(), NOW(), 0),
(784403, 'REPL_TEST_DONE', 761005, 762005, 752001, 720001, 1, 30, '补发完成', NOW(), NOW(), 0),
(784404, 'REPL_TEST_CLOSED', 761005, 762005, 752001, 720001, 1, 40, '补发关闭', NOW(), NOW(), 0);

INSERT INTO wms_loading_order
(id, loading_no, wave_id, warehouse_id, line_id, driver_account_id, delivery_date, required_qty, actual_qty, status, load_account_id, load_time, outbound_account_id, outbound_time, create_time, update_time, is_deleted)
VALUES
(785001, 'LOAD_TEST_WAIT', 784004, 50001, 780201, 700011, CURDATE(), 80, 0, 10, NULL, NULL, NULL, NULL, NOW(), NOW(), 0),
(785002, 'LOAD_TEST_LOADED', 784004, 50001, 780201, 700011, CURDATE(), 80, 80, 20, 700010, DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL, NULL, NOW(), NOW(), 0),
(785003, 'LOAD_TEST_OUTBOUND', 784004, 50001, 780201, 700011, CURDATE(), 80, 80, 30, 700010, DATE_SUB(NOW(), INTERVAL 2 HOUR), 700007, DATE_SUB(NOW(), INTERVAL 90 MINUTE), NOW(), NOW(), 0);

INSERT INTO wms_loading_item
(id, loading_id, station_id, sku_id, required_qty, actual_qty, outbound_qty, create_time, update_time, is_deleted)
VALUES
(785101, 785001, 720001, 752001, 40, 0, 0, NOW(), NOW(), 0),
(785102, 785002, 720001, 752001, 40, 40, 0, NOW(), NOW(), 0),
(785103, 785003, 720001, 752001, 40, 40, 40, NOW(), NOW(), 0),
(785104, 785003, 720002, 752002, 20, 20, 20, NOW(), NOW(), 0);

INSERT INTO wms_outbound_order
(id, outbound_no, loading_id, warehouse_id, line_id, outbound_qty, status, outbound_account_id, outbound_time, idempotent_key, create_time, update_time, is_deleted)
VALUES
(785201, 'OUT_TEST_WAIT', 785002, 50001, 780201, 0, 10, 700007, NOW(), 'idem_out_wait', NOW(), NOW(), 0),
(785202, 'OUT_TEST_DONE', 785003, 50001, 780201, 80, 20, 700007, DATE_SUB(NOW(), INTERVAL 90 MINUTE), 'idem_out_done', NOW(), NOW(), 0),
(785203, 'OUT_TEST_CANCEL', 785001, 50001, 780201, 0, 30, 700007, NOW(), 'idem_out_cancel', NOW(), NOW(), 0);

INSERT INTO wms_delivery_order
(id, delivery_no, outbound_id, loading_id, warehouse_id, line_id, driver_account_id, delivery_date, status, start_time, complete_time, create_time, update_time, is_deleted)
VALUES
(786001, 'WMS_DELIVERY_TEST_WAIT', 785201, 785002, 50001, 780201, 700011, CURDATE(), 10, NULL, NULL, NOW(), NOW(), 0),
(786002, 'WMS_DELIVERY_TEST_RUNNING', 785202, 785003, 50001, 780201, 700011, CURDATE(), 20, DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL, NOW(), NOW(), 0),
(786003, 'WMS_DELIVERY_TEST_DONE', 785202, 785003, 50001, 780201, 700011, CURDATE(), 30, DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW(), 0),
(786004, 'WMS_DELIVERY_TEST_EXCEPTION', 785202, 785003, 50001, 780201, 700011, CURDATE(), 40, DATE_SUB(NOW(), INTERVAL 3 HOUR), NULL, NOW(), NOW(), 0);

INSERT INTO wms_delivery_station
(id, delivery_id, station_id, delivery_sort, status, arrive_time, complete_time, remark, create_time, update_time, is_deleted)
VALUES
(786101, 786001, 720001, 1, 10, NULL, NULL, '待配送到自提点', NOW(), NOW(), 0),
(786102, 786002, 720001, 1, 20, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NULL, '已到达待交接', NOW(), NOW(), 0),
(786103, 786003, 720001, 1, 30, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), '已完成交接', NOW(), NOW(), 0),
(786104, 786004, 720002, 2, 40, NULL, NULL, '自提点休假无法送达', NOW(), NOW(), 0);

INSERT INTO wms_driver_sign_record
(id, sign_no, delivery_id, station_id, driver_account_id, sign_type, longitude, latitude, remark, create_time, update_time, is_deleted)
VALUES
(786201, 'SIGN_TEST_ARRIVE', 786002, 720001, 700011, 1, 113.320000, 23.120000, '司机到达', NOW(), NOW(), 0),
(786202, 'SIGN_TEST_DONE', 786003, 720001, 700011, 2, 113.320100, 23.120100, '交接完成', NOW(), NOW(), 0),
(786203, 'SIGN_TEST_FAIL', 786004, 720002, 700011, 3, 113.330000, 23.130000, '无法送达', NOW(), NOW(), 0);

INSERT INTO wms_return_handover
(id, return_no, delivery_id, station_id, sku_id, warehouse_id, return_qty, reason_type, driver_account_id, supervisor_account_id, status, confirm_time, remark, create_time, update_time, is_deleted)
VALUES
(786301, 'RET_TEST_WAIT', 786004, 720002, 752001, 50001, 3, 1, 700011, NULL, 10, NULL, '待回仓确认', NOW(), NOW(), 0),
(786302, 'RET_TEST_CONFIRMED', 786003, 720001, 752002, 50001, 2, 2, 700011, 700007, 20, NOW(), '已回仓确认', NOW(), NOW(), 0),
(786303, 'RET_TEST_SUPERVISOR', 786004, 720002, 752001, 50001, 1, 3, 700011, NULL, 30, NULL, '待主管处理', NOW(), NOW(), 0),
(786304, 'RET_TEST_CLOSED', 786003, 720001, 752002, 50001, 1, 1, 700011, 700007, 40, DATE_SUB(NOW(), INTERVAL 1 HOUR), '已关闭', NOW(), NOW(), 0);

INSERT INTO wms_stocktake_order
(id, stocktake_no, warehouse_id, sku_id, book_qty, actual_qty, diff_qty, status, create_account_id, audit_account_id, create_time, update_time, is_deleted)
VALUES
(786401, 'STK_TEST_DRAFT', 50001, 752001, 100, 0, 0, 10, 700007, NULL, NOW(), NOW(), 0),
(786402, 'STK_TEST_WAIT_AUDIT', 50001, 752001, 100, 98, -2, 20, 700007, NULL, NOW(), NOW(), 0),
(786403, 'STK_TEST_ADJUSTED', 50001, 752001, 100, 98, -2, 30, 700007, 700007, NOW(), NOW(), 0),
(786404, 'STK_TEST_CLOSED', 50001, 752002, 50, 50, 0, 40, 700007, 700007, NOW(), NOW(), 0);

INSERT INTO wms_operation_log
(id, log_no, warehouse_id, account_id, role_code, module_code, action_code, biz_type, biz_no, before_status, after_status, reason, result_status, create_time, update_time, is_deleted)
VALUES
(786501, 'WMS_LOG_RECEIVE_SUCCESS', 50001, 700008, 'wms_receiver', 'wms_inbound', 'receive', 'inbound', 'INB_TEST_WAIT_PUTAWAY', 20, 40, '收货成功', 1, NOW(), NOW(), 0),
(786502, 'WMS_LOG_PICK_DIFF', 50001, 700009, 'wms_picker', 'wms_picking', 'pick_diff', 'pick', 'PICK_TEST_DIFF', 20, 40, '拣货差异', 1, NOW(), NOW(), 0),
(786503, 'WMS_LOG_OUT_FAIL', 50001, 700010, 'wms_loader', 'wms_loading', 'outbound', 'loading', 'LOAD_TEST_LOADED', 20, 20, '出库失败样例', 2, NOW(), NOW(), 0);

INSERT INTO fin_account
(id, account_no, subject_type, subject_id, available_amount, frozen_amount, deposit_amount, status, create_time, update_time, is_deleted)
VALUES
(790001, 'FIN_ACC_SUPPLIER', 1, 710001, 12000.0000, 500.0000, 5000.0000, 1, NOW(), NOW(), 0),
(790002, 'FIN_ACC_STATION', 2, 720001, 800.0000, 100.0000, 0.0000, 1, NOW(), NOW(), 0),
(790003, 'FIN_ACC_LEADER', 3, 730001, 600.0000, 0.0000, 0.0000, 1, NOW(), NOW(), 0),
(790004, 'FIN_ACC_PLATFORM', 4, NULL, 50000.0000, 0.0000, 0.0000, 1, NOW(), NOW(), 0),
(790005, 'FIN_ACC_FROZEN', 1, 710003, 0.0000, 8000.0000, 8000.0000, 2, NOW(), NOW(), 0);

INSERT INTO fin_account_flow
(id, flow_no, account_id, subject_type, subject_id, flow_type, direction, amount, before_amount, after_amount, biz_no, remark, create_time, update_time, is_deleted)
VALUES
(790101, 'ACC_FLOW_COMMISSION', 790002, 2, 720001, 1, 1, 20.0000, 780.0000, 800.0000, 'COM_TEST_SPLIT', '佣金入账', NOW(), NOW(), 0),
(790102, 'ACC_FLOW_REFUND', 790002, 2, 720001, 2, 2, 5.0000, 805.0000, 800.0000, 'RF_TEST_SUCCESS', '退款扣回', NOW(), NOW(), 0),
(790103, 'ACC_FLOW_FREEZE', 790002, 2, 720001, 3, 2, 100.0000, 900.0000, 800.0000, 'WD_TEST_WAIT', '提现冻结', NOW(), NOW(), 0),
(790104, 'ACC_FLOW_WITHDRAW_SUCCESS', 790002, 2, 720001, 4, 2, 100.0000, 800.0000, 700.0000, 'WD_TEST_SUCCESS', '提现成功', NOW(), NOW(), 0),
(790105, 'ACC_FLOW_WITHDRAW_REJECT', 790002, 2, 720001, 5, 1, 100.0000, 700.0000, 800.0000, 'WD_TEST_REJECT', '提现驳回解冻', NOW(), NOW(), 0),
(790106, 'ACC_FLOW_DEPOSIT_RELEASE', 790001, 1, 710001, 6, 1, 500.0000, 11500.0000, 12000.0000, 'DEP_TEST_RELEASED', '押金释放', NOW(), NOW(), 0);

INSERT INTO fin_commission_detail
(id, commission_no, order_id, order_item_id, subject_type, subject_id, supplier_id, station_id, commission_amount, status, create_time, update_time, is_deleted)
VALUES
(790201, 'COM_TEST_WAIT_CALC', 761005, 762005, 1, 720001, 710001, 720001, 2.0000, 10, NOW(), NOW(), 0),
(790202, 'COM_TEST_WAIT_SPLIT', 761005, 762006, 2, 730001, 710001, 720001, 3.0000, 20, NOW(), NOW(), 0),
(790203, 'COM_TEST_SPLIT', 761005, 762006, 3, 710001, 710001, 720001, 10.0000, 30, NOW(), NOW(), 0),
(790204, 'COM_TEST_DEDUCT', 761004, 762004, 1, 720001, 710001, 720001, 2.0000, 40, NOW(), NOW(), 0);

INSERT INTO fin_split_flow
(id, split_no, commission_id, order_id, subject_type, subject_id, split_amount, split_status, fail_reason, idempotent_key, create_time, update_time, is_deleted)
VALUES
(790301, 'SPLIT_TEST_WAIT', 790202, 761005, 2, 730001, 3.0000, 10, NULL, 'idem_split_wait', NOW(), NOW(), 0),
(790302, 'SPLIT_TEST_PROCESSING', 790202, 761005, 2, 730001, 3.0000, 20, NULL, 'idem_split_processing', NOW(), NOW(), 0),
(790303, 'SPLIT_TEST_SUCCESS', 790203, 761005, 3, 710001, 10.0000, 30, NULL, 'idem_split_success', NOW(), NOW(), 0),
(790304, 'SPLIT_TEST_FAILED', 790204, 761004, 1, 720001, 2.0000, 40, '分账账户异常', 'idem_split_failed', NOW(), NOW(), 0);

INSERT INTO fin_withdraw_apply
(id, withdraw_no, account_id, subject_type, subject_id, withdraw_amount, receive_account_name, receive_account_no, audit_status, pay_status, audit_account_id, audit_time, audit_remark, create_time, update_time, is_deleted)
VALUES
(790401, 'WD_TEST_WAIT', 790002, 2, 720001, 100.0000, '自提点负责人A', '622200000001', 10, 10, NULL, NULL, NULL, NOW(), NOW(), 0),
(790402, 'WD_TEST_PASS', 790002, 2, 720001, 100.0000, '自提点负责人A', '622200000002', 20, 20, 700004, NOW(), '审核通过打款中', NOW(), NOW(), 0),
(790403, 'WD_TEST_REJECT', 790002, 2, 720001, 100.0000, '自提点负责人A', '622200000003', 30, 10, 700004, NOW(), '账户信息不一致', NOW(), NOW(), 0),
(790404, 'WD_TEST_CANCEL', 790003, 3, 730001, 50.0000, '测试团长A', '622200000004', 40, 10, NULL, NULL, '申请人撤回', NOW(), NOW(), 0),
(790405, 'WD_TEST_SUCCESS', 790002, 2, 720001, 100.0000, '自提点负责人A', '622200000005', 20, 30, 700004, DATE_SUB(NOW(), INTERVAL 1 DAY), '已打款', NOW(), NOW(), 0),
(790406, 'WD_TEST_PAY_FAILED', 790001, 1, 710001, 200.0000, '测试鲜果供应商', '622200000006', 20, 40, 700004, DATE_SUB(NOW(), INTERVAL 1 DAY), '银行退回', NOW(), NOW(), 0);

INSERT INTO fin_deposit_record
(id, deposit_no, account_id, subject_type, subject_id, amount, frozen_amount, release_amount, status, create_time, update_time, is_deleted)
VALUES
(790501, 'DEP_TEST_FROZEN', 790001, 1, 710001, 5000.0000, 5000.0000, 0.0000, 10, NOW(), NOW(), 0),
(790502, 'DEP_TEST_PART_RELEASE', 790001, 1, 710001, 5000.0000, 3000.0000, 2000.0000, 20, NOW(), NOW(), 0),
(790503, 'DEP_TEST_RELEASED', 790001, 1, 710001, 5000.0000, 0.0000, 5000.0000, 30, NOW(), NOW(), 0);

INSERT INTO fin_supplier_settlement
(id, settlement_no, supplier_id, period_start_date, period_end_date, purchase_amount, diff_amount, payable_amount, status, create_time, update_time, is_deleted)
VALUES
(790601, 'SETTLE_TEST_WAIT', 710001, '2026-06-01', '2026-06-15', 10000.0000, 0.0000, 10000.0000, 10, NOW(), NOW(), 0),
(790602, 'SETTLE_TEST_CONFIRM', 710001, '2026-06-01', '2026-06-15', 10000.0000, -100.0000, 9900.0000, 20, NOW(), NOW(), 0),
(790603, 'SETTLE_TEST_ACCOUNTED', 710001, '2026-06-01', '2026-06-15', 10000.0000, 0.0000, 10000.0000, 30, NOW(), NOW(), 0),
(790604, 'SETTLE_TEST_EXCEPTION', 710002, '2026-06-01', '2026-06-15', 3000.0000, 300.0000, 3300.0000, 40, NOW(), NOW(), 0);

INSERT INTO fin_reconciliation_batch
(id, reconcile_no, channel_type, reconcile_date, total_count, success_count, diff_count, total_amount, diff_amount, status, create_time, update_time, is_deleted)
VALUES
(790701, 'RECON_TEST_WAIT', 1, CURDATE(), 0, 0, 0, 0.0000, 0.0000, 10, NOW(), NOW(), 0),
(790702, 'RECON_TEST_DOING', 1, CURDATE(), 100, 90, 10, 10000.0000, 100.0000, 20, NOW(), NOW(), 0),
(790703, 'RECON_TEST_DONE', 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 100, 100, 0, 10000.0000, 0.0000, 30, NOW(), NOW(), 0),
(790704, 'RECON_TEST_EXCEPTION', 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 100, 95, 5, 10000.0000, 50.0000, 40, NOW(), NOW(), 0);

INSERT INTO op_todo
(id, todo_no, todo_type, source_biz_type, source_biz_no, title, priority, process_role_code, process_account_id, city_id, supplier_id, warehouse_id, status, due_time, create_time, update_time, is_deleted)
VALUES
(791001, 'TODO_TEST_WAIT', 1, 'purchase', 'PUR_TEST_WAIT_AUDIT', '采购单待审核', 1, 'buyer', 700003, 440100, 710001, 50001, 10, DATE_ADD(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0),
(791002, 'TODO_TEST_DOING', 2, 'after_sale', 'AFS_TEST_WAIT_AUDIT', '售后单处理中', 2, 'after_sale_operator', 700005, 440100, 710001, NULL, 20, DATE_ADD(NOW(), INTERVAL 4 HOUR), NOW(), NOW(), 0),
(791003, 'TODO_TEST_DONE', 3, 'station_leave', 'LEAVE_TEST_PASS', '休假审核已处理', 2, 'city_operator', 700002, 440100, NULL, NULL, 30, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW(), 0),
(791004, 'TODO_TEST_REJECT', 4, 'supplier', 'QUAL_TEST_REJECT', '资质审核驳回', 3, 'platform_operator', 700001, 440100, 710003, NULL, 40, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW(), 0),
(791005, 'TODO_TEST_CANCEL', 5, 'export', 'EXP_TEST_FAILED', '导出任务取消', 3, 'platform_operator', 700001, 440100, NULL, NULL, 50, NULL, NOW(), NOW(), 0),
(791006, 'TODO_TEST_TIMEOUT', 6, 'order', 'ORD_TEST_WAIT_PAY', '订单待处理超时', 1, 'city_operator', 700002, 440100, NULL, NULL, 60, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW(), 0),
(791007, 'TODO_TEST_CLOSED', 7, 'exception', 'EXC_TEST_CLOSED', '待办关闭样例', 3, 'platform_operator', 700001, 440100, NULL, NULL, 70, NULL, NOW(), NOW(), 0);

INSERT INTO op_exception_record
(id, exception_no, exception_type, priority, source_module, source_biz_type, source_biz_no, city_id, supplier_id, station_id, warehouse_id, owner_account_id, status, title, description, create_time, update_time, is_deleted)
VALUES
(791101, 'EXC_TEST_WAIT', 1, 1, 'wms', 'pick', 'PICK_TEST_DIFF', 440100, 710001, 720001, 50001, 700007, 10, '拣货差异待处理', '拣货数量不足', NOW(), NOW(), 0),
(791102, 'EXC_TEST_DOING', 2, 2, 'pay', 'refund', 'RF_TEST_FAILED', 440100, 710001, 720001, NULL, 700004, 20, '退款失败处理中', '渠道退款失败', NOW(), NOW(), 0),
(791103, 'EXC_TEST_DONE', 3, 3, 'supplier', 'settlement', 'SETTLE_TEST_EXCEPTION', 440100, 710002, NULL, NULL, 700003, 30, '结算差异已处理', '差异已确认', NOW(), NOW(), 0),
(791104, 'EXC_TEST_CLOSED', 4, 2, 'delivery', 'station', 'WMS_DELIVERY_TEST_EXCEPTION', 440100, NULL, 720002, 50001, 700007, 40, '配送异常关闭', '自提点休假无法送达', NOW(), NOW(), 0);

INSERT INTO msg_template
(id, template_code, template_name, channel_type, receiver_type, title_template, content_template, status, create_time, update_time, is_deleted)
VALUES
(792001, 'TPL_ORDER_PAY_REMIND', '订单支付提醒', 2, 1, '订单待支付', '您的订单即将关闭，请及时支付', 1, NOW(), NOW(), 0),
(792002, 'TPL_STATION_ARRIVAL', '自提点到货通知', 1, 4, '商品已到达', '请及时完成到货接收', 1, NOW(), NOW(), 0),
(792003, 'TPL_SUPPLIER_DELIVERY', '供应商送货提醒', 1, 2, '送货任务提醒', '请按时送货到仓', 1, NOW(), NOW(), 0),
(792004, 'TPL_DISABLED', '停用模板', 1, 1, '停用模板', '停用内容', 2, NOW(), NOW(), 0);

INSERT INTO msg_record
(id, msg_no, template_code, receiver_type, receiver_id, channel_type, title, content, biz_type, biz_no, read_status, send_status, fail_reason, create_time, update_time, is_deleted)
VALUES
(792101, 'MSG_TEST_WAIT', 'TPL_ORDER_PAY_REMIND', 1, 740001, 2, '订单待支付', 'ORD_TEST_WAIT_PAY 即将关闭', 'order', 'ORD_TEST_WAIT_PAY', 0, 10, NULL, NOW(), NOW(), 0),
(792102, 'MSG_TEST_SUCCESS_UNREAD', 'TPL_STATION_ARRIVAL', 4, 720001, 1, '商品已到达', '请接收今日到货商品', 'delivery_station', 'WMS_DELIVERY_TEST_DONE', 0, 20, NULL, NOW(), NOW(), 0),
(792103, 'MSG_TEST_SUCCESS_READ', 'TPL_SUPPLIER_DELIVERY', 2, 710001, 1, '送货任务提醒', 'PDEL_TEST_WAIT 待送货', 'purchase_delivery', 'PDEL_TEST_WAIT', 1, 20, NULL, NOW(), NOW(), 0),
(792104, 'MSG_TEST_FAILED', 'TPL_ORDER_PAY_REMIND', 1, 740002, 2, '发送失败样例', '公众号发送失败', 'order', 'ORD_TEST_CLOSED', 0, 30, '用户未关注', NOW(), NOW(), 0);

INSERT INTO rpt_snapshot
(id, snapshot_no, report_type, stat_date, city_id, supplier_id, station_id, warehouse_id, metric_json, status, create_time, update_time, is_deleted)
VALUES
(793001, 'RPT_TEST_CITY_ACTIVE', 1, CURDATE(), 440100, NULL, NULL, NULL, JSON_OBJECT('order_count', 5, 'pay_amount', 152.2000), 1, NOW(), NOW(), 0),
(793002, 'RPT_TEST_SUPPLIER_ACTIVE', 2, CURDATE(), 440100, 710001, NULL, NULL, JSON_OBJECT('sale_qty', 200, 'settlement_amount', 10000.0000), 1, NOW(), NOW(), 0),
(793003, 'RPT_TEST_STATION_ACTIVE', 3, CURDATE(), 440100, NULL, 720001, NULL, JSON_OBJECT('pickup_count', 12, 'commission', 20.0000), 1, NOW(), NOW(), 0),
(793004, 'RPT_TEST_WAREHOUSE_INVALID', 4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 440100, NULL, NULL, 50001, JSON_OBJECT('pick_count', 4, 'diff_count', 1), 2, NOW(), NOW(), 0);

INSERT INTO rpt_export_task
(id, export_task_no, report_type, account_id, filter_json, file_asset_id, status, fail_reason, create_time, update_time, is_deleted)
VALUES
(793101, 'RPT_EXP_TEST_CREATING', 1, 700002, JSON_OBJECT('city_id', 440100), NULL, 10, NULL, NOW(), NOW(), 0),
(793102, 'RPT_EXP_TEST_DONE', 2, 700004, JSON_OBJECT('supplier_id', 710001), 710203, 20, NULL, NOW(), NOW(), 0),
(793103, 'RPT_EXP_TEST_FAILED', 4, 700007, JSON_OBJECT('warehouse_id', 50001), NULL, 30, '导出超时', NOW(), NOW(), 0);

INSERT INTO search_suggest_word
(id, word, source_type, product_id, city_id, hit_count, status, create_time, update_time, is_deleted)
VALUES
(794001, '苹果', 1, 751001, 440100, 128, 1, NOW(), NOW(), 0),
(794002, '香蕉', 1, 751002, 440100, 88, 1, NOW(), NOW(), 0),
(794003, '今日生鲜', 2, NULL, 440100, 56, 1, NOW(), NOW(), 0),
(794004, '停用联想词', 2, NULL, 440300, 1, 2, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_account
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：回填测试账号统一密码 Test@123456 的 BCrypt 哈希，支持 mall-auth 密码登录联调
-- ----------------------------
UPDATE sys_account
SET password_hash = '$2a$10$oxgynGL/z8Bad5ExH/or0eNrsHypVvBIttfG7BaymYuYhBint5Rhy',
    update_time = NOW()
WHERE account_no LIKE 'ACC_TEST_%'
  AND is_deleted = 0;

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：补充运营待办与异常中心查看权限，支持 mall-operation 查询接口联调
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(700508, 20001, 'admin', 'operation:todo', 'view', '查看待办', 1, 1, NOW(), NOW(), 0),
(700509, 20001, 'admin', 'operation:exception', 'view', '查看异常', 1, 1, NOW(), NOW(), 0),
(700510, 20002, 'admin', 'operation:todo', 'view', '查看待办', 1, 1, NOW(), NOW(), 0),
(700511, 20002, 'admin', 'operation:exception', 'view', '查看异常', 1, 1, NOW(), NOW(), 0),
(700512, 20001, 'admin', 'operation:dashboard', 'view', '查看运营驾驶舱摘要', 1, 1, NOW(), NOW(), 0),
(700513, 20002, 'admin', 'operation:dashboard', 'view', '查看运营驾驶舱摘要', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：补充商品中心、SKU、类目和团期查询权限，支持 mall-product、mall-sale 首批查询接口联调
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(700514, 20001, 'admin', 'product:product', 'view', '查看商品', 1, 1, NOW(), NOW(), 0),
(700515, 20001, 'admin', 'product:sku', 'view', '查看SKU', 1, 1, NOW(), NOW(), 0),
(700516, 20001, 'admin', 'product:category', 'view', '查看类目', 1, 1, NOW(), NOW(), 0),
(700517, 20001, 'admin', 'sale:period', 'view', '查看团期', 1, 1, NOW(), NOW(), 0),
(700518, 20002, 'admin', 'product:product', 'view', '查看商品', 1, 1, NOW(), NOW(), 0),
(700519, 20002, 'admin', 'product:sku', 'view', '查看SKU', 1, 1, NOW(), NOW(), 0),
(700520, 20002, 'admin', 'product:category', 'view', '查看类目', 1, 1, NOW(), NOW(), 0),
(700521, 20002, 'admin', 'sale:period', 'view', '查看团期', 1, 1, NOW(), NOW(), 0),
(700522, 20004, 'admin', 'product:product', 'view', '查看商品', 1, 1, NOW(), NOW(), 0),
(700523, 20004, 'admin', 'product:sku', 'view', '查看SKU', 1, 1, NOW(), NOW(), 0),
(700524, 20004, 'admin', 'product:category', 'view', '查看类目', 1, 1, NOW(), NOW(), 0),
(700525, 20004, 'admin', 'sale:period', 'view', '查看团期', 1, 1, NOW(), NOW(), 0),
(700526, 20006, 'supplier-h5', 'product:product', 'view', '查看自身商品', 1, 1, NOW(), NOW(), 0),
(700527, 20006, 'supplier-h5', 'product:sku', 'view', '查看自身SKU', 1, 1, NOW(), NOW(), 0),
(700528, 20006, 'supplier-h5', 'product:category', 'view', '查看基础类目', 1, 1, NOW(), NOW(), 0),
(700529, 20006, 'supplier-h5', 'sale:period', 'view', '查看自身团期', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：修正商品与团期查询权限的供应商角色归属，移除误授予财务角色的商品权限，补齐供应商后台和供应商工作台权限
-- ----------------------------
UPDATE sys_role_button
SET status = 2,
    update_time = NOW()
WHERE id IN (700526, 700527, 700528, 700529)
  AND is_deleted = 0;

INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(700530, 20008, 'admin', 'product:product', 'view', '查看自身商品', 1, 1, NOW(), NOW(), 0),
(700531, 20008, 'admin', 'product:sku', 'view', '查看自身SKU', 1, 1, NOW(), NOW(), 0),
(700532, 20008, 'admin', 'product:category', 'view', '查看基础类目', 1, 1, NOW(), NOW(), 0),
(700533, 20008, 'admin', 'sale:period', 'view', '查看自身团期', 1, 1, NOW(), NOW(), 0),
(700534, 22003, 'role-workbench-h5', 'product:product', 'view', '查看自身商品', 1, 1, NOW(), NOW(), 0),
(700535, 22003, 'role-workbench-h5', 'product:sku', 'view', '查看自身SKU', 1, 1, NOW(), NOW(), 0),
(700536, 22003, 'role-workbench-h5', 'product:category', 'view', '查看基础类目', 1, 1, NOW(), NOW(), 0),
(700537, 22003, 'role-workbench-h5', 'sale:period', 'view', '查看自身团期', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_account_data_scope, sys_role_button
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：补齐采购员测试账号的城市数据范围和商品团期查看权限，确保商品与团期页面可联调
-- ----------------------------
INSERT INTO sys_account_data_scope
(id, account_id, scope_id, create_time, update_time, is_deleted)
VALUES
(700412, 700003, 60002, NOW(), NOW(), 0);

INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(700538, 20005, 'admin', 'product:product', 'view', '查看商品', 1, 1, NOW(), NOW(), 0),
(700539, 20005, 'admin', 'product:sku', 'view', '查看SKU', 1, 1, NOW(), NOW(), 0),
(700540, 20005, 'admin', 'product:category', 'view', '查看类目', 1, 1, NOW(), NOW(), 0),
(700541, 20005, 'admin', 'sale:period', 'view', '查看团期', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：补充订单中心、订单商品、履约跟踪和缺货记录只读权限，支持 mall-trade 查询接口联调
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(700542, 20001, 'admin', 'trade:order', 'view', '查看订单', 1, 1, NOW(), NOW(), 0),
(700543, 20001, 'admin', 'trade:order:item', 'view', '查看订单商品', 1, 1, NOW(), NOW(), 0),
(700544, 20001, 'admin', 'trade:fulfillment', 'view', '查看履约跟踪', 1, 1, NOW(), NOW(), 0),
(700545, 20001, 'admin', 'trade:stockout', 'view', '查看缺货记录', 1, 1, NOW(), NOW(), 0),
(700546, 20002, 'admin', 'trade:order', 'view', '查看订单', 1, 1, NOW(), NOW(), 0),
(700547, 20002, 'admin', 'trade:order:item', 'view', '查看订单商品', 1, 1, NOW(), NOW(), 0),
(700548, 20002, 'admin', 'trade:fulfillment', 'view', '查看履约跟踪', 1, 1, NOW(), NOW(), 0),
(700549, 20002, 'admin', 'trade:stockout', 'view', '查看缺货记录', 1, 1, NOW(), NOW(), 0),
(700550, 20003, 'admin', 'trade:order', 'view', '查看订单', 1, 1, NOW(), NOW(), 0),
(700551, 20003, 'admin', 'trade:order:item', 'view', '查看订单商品', 1, 1, NOW(), NOW(), 0),
(700552, 20003, 'admin', 'trade:fulfillment', 'view', '查看履约跟踪', 1, 1, NOW(), NOW(), 0),
(700553, 20003, 'admin', 'trade:stockout', 'view', '查看缺货记录', 1, 1, NOW(), NOW(), 0),
(700554, 20007, 'admin', 'trade:order', 'view', '查看订单', 1, 1, NOW(), NOW(), 0),
(700555, 20007, 'admin', 'trade:order:item', 'view', '查看订单商品', 1, 1, NOW(), NOW(), 0),
(700556, 20007, 'admin', 'trade:fulfillment', 'view', '查看履约跟踪', 1, 1, NOW(), NOW(), 0),
(700557, 20007, 'admin', 'trade:stockout', 'view', '查看缺货记录', 1, 1, NOW(), NOW(), 0),
(700558, 20008, 'admin', 'trade:order:item', 'view', '查看自身订单商品', 1, 1, NOW(), NOW(), 0),
(700559, 20008, 'admin', 'trade:fulfillment', 'view', '查看自身履约跟踪', 1, 1, NOW(), NOW(), 0),
(700560, 20008, 'admin', 'trade:stockout', 'view', '查看自身缺货记录', 1, 1, NOW(), NOW(), 0),
(700561, 22001, 'role-workbench-h5', 'trade:order', 'view', '查看自提点订单', 1, 1, NOW(), NOW(), 0),
(700562, 22001, 'role-workbench-h5', 'trade:order:item', 'view', '查看自提点订单商品', 1, 1, NOW(), NOW(), 0),
(700563, 22001, 'role-workbench-h5', 'trade:fulfillment', 'view', '查看自提点履约', 1, 1, NOW(), NOW(), 0),
(700564, 22001, 'role-workbench-h5', 'trade:stockout', 'view', '查看自提点缺货', 1, 1, NOW(), NOW(), 0),
(700565, 22002, 'role-workbench-h5', 'trade:order', 'view', '查看团长订单', 1, 1, NOW(), NOW(), 0),
(700566, 22002, 'role-workbench-h5', 'trade:order:item', 'view', '查看团长订单商品', 1, 1, NOW(), NOW(), 0),
(700567, 22002, 'role-workbench-h5', 'trade:fulfillment', 'view', '查看团长履约', 1, 1, NOW(), NOW(), 0),
(700568, 22003, 'role-workbench-h5', 'trade:order:item', 'view', '查看自身订单商品', 1, 1, NOW(), NOW(), 0),
(700569, 22003, 'role-workbench-h5', 'trade:fulfillment', 'view', '查看自身履约', 1, 1, NOW(), NOW(), 0),
(700570, 22003, 'role-workbench-h5', 'trade:stockout', 'view', '查看自身缺货', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：补充支付单、退款单和资金流水只读权限，支持 mall-payment 查询接口联调
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(700571, 20001, 'admin', 'payment:trade', 'view', '查看支付单', 1, 1, NOW(), NOW(), 0),
(700572, 20001, 'admin', 'payment:refund', 'view', '查看退款单', 1, 1, NOW(), NOW(), 0),
(700573, 20001, 'admin', 'payment:fund-flow', 'view', '查看资金流水', 1, 1, NOW(), NOW(), 0),
(700574, 20002, 'admin', 'payment:trade', 'view', '查看支付单', 1, 1, NOW(), NOW(), 0),
(700575, 20002, 'admin', 'payment:refund', 'view', '查看退款单', 1, 1, NOW(), NOW(), 0),
(700576, 20002, 'admin', 'payment:fund-flow', 'view', '查看资金流水', 1, 1, NOW(), NOW(), 0),
(700577, 20003, 'admin', 'payment:trade', 'view', '查看城市支付单', 1, 1, NOW(), NOW(), 0),
(700578, 20003, 'admin', 'payment:refund', 'view', '查看城市退款单', 1, 1, NOW(), NOW(), 0),
(700579, 20003, 'admin', 'payment:fund-flow', 'view', '查看城市资金流水', 1, 1, NOW(), NOW(), 0),
(700580, 20006, 'admin', 'payment:trade', 'view', '查看支付单', 1, 1, NOW(), NOW(), 0),
(700581, 20006, 'admin', 'payment:refund', 'view', '查看退款单', 1, 1, NOW(), NOW(), 0),
(700582, 20006, 'admin', 'payment:fund-flow', 'view', '查看资金流水', 1, 1, NOW(), NOW(), 0),
(700583, 20007, 'admin', 'payment:refund', 'view', '查看售后退款单', 1, 1, NOW(), NOW(), 0),
(700584, 20008, 'admin', 'payment:trade', 'view', '查看自身支付单', 1, 1, NOW(), NOW(), 0),
(700585, 20008, 'admin', 'payment:refund', 'view', '查看自身退款单', 1, 1, NOW(), NOW(), 0),
(700586, 20008, 'admin', 'payment:fund-flow', 'view', '查看自身资金流水', 1, 1, NOW(), NOW(), 0),
(700587, 22001, 'role-workbench-h5', 'payment:fund-flow', 'view', '查看自提点资金流水', 1, 1, NOW(), NOW(), 0),
(700588, 22002, 'role-workbench-h5', 'payment:fund-flow', 'view', '查看团长资金流水', 1, 1, NOW(), NOW(), 0),
(700589, 22003, 'role-workbench-h5', 'payment:refund', 'view', '查看自身退款单', 1, 1, NOW(), NOW(), 0),
(700590, 22003, 'role-workbench-h5', 'payment:fund-flow', 'view', '查看自身资金流水', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_account_data_scope
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：补齐财务和售后测试账号的数据范围，确保支付退款读侧接口联调可见数据
-- ----------------------------
INSERT INTO sys_account_data_scope
(id, account_id, scope_id, create_time, update_time, is_deleted)
VALUES
(700413, 700004, 60001, NOW(), NOW(), 0),
(700414, 700005, 60002, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：补充售后单、售后商品、售后审核日志和退货记录只读权限，支持 mall-aftersale 查询接口联调
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(700621, 20001, 'admin', 'aftersale:after-sale', 'view', '查看售后单', 1, 1, NOW(), NOW(), 0),
(700622, 20001, 'admin', 'aftersale:item', 'view', '查看售后商品', 1, 1, NOW(), NOW(), 0),
(700623, 20001, 'admin', 'aftersale:audit-log', 'view', '查看售后审核日志', 1, 1, NOW(), NOW(), 0),
(700624, 20001, 'admin', 'aftersale:return-record', 'view', '查看退货记录', 1, 1, NOW(), NOW(), 0),
(700625, 20002, 'admin', 'aftersale:after-sale', 'view', '查看售后单', 1, 1, NOW(), NOW(), 0),
(700626, 20002, 'admin', 'aftersale:item', 'view', '查看售后商品', 1, 1, NOW(), NOW(), 0),
(700627, 20002, 'admin', 'aftersale:audit-log', 'view', '查看售后审核日志', 1, 1, NOW(), NOW(), 0),
(700628, 20002, 'admin', 'aftersale:return-record', 'view', '查看退货记录', 1, 1, NOW(), NOW(), 0),
(700629, 20003, 'admin', 'aftersale:after-sale', 'view', '查看城市售后单', 1, 1, NOW(), NOW(), 0),
(700630, 20003, 'admin', 'aftersale:item', 'view', '查看城市售后商品', 1, 1, NOW(), NOW(), 0),
(700631, 20003, 'admin', 'aftersale:audit-log', 'view', '查看城市售后审核日志', 1, 1, NOW(), NOW(), 0),
(700632, 20003, 'admin', 'aftersale:return-record', 'view', '查看城市退货记录', 1, 1, NOW(), NOW(), 0),
(700633, 20007, 'admin', 'aftersale:after-sale', 'view', '查看售后单', 1, 1, NOW(), NOW(), 0),
(700634, 20007, 'admin', 'aftersale:item', 'view', '查看售后商品', 1, 1, NOW(), NOW(), 0),
(700635, 20007, 'admin', 'aftersale:audit-log', 'view', '查看售后审核日志', 1, 1, NOW(), NOW(), 0),
(700636, 20007, 'admin', 'aftersale:return-record', 'view', '查看退货记录', 1, 1, NOW(), NOW(), 0),
(700637, 20008, 'admin', 'aftersale:after-sale', 'view', '查看自身售后单', 1, 1, NOW(), NOW(), 0),
(700638, 20008, 'admin', 'aftersale:item', 'view', '查看自身售后商品', 1, 1, NOW(), NOW(), 0),
(700639, 20008, 'admin', 'aftersale:audit-log', 'view', '查看自身售后审核日志', 1, 1, NOW(), NOW(), 0),
(700640, 20008, 'admin', 'aftersale:return-record', 'view', '查看自身退货记录', 1, 1, NOW(), NOW(), 0),
(700641, 22001, 'role-workbench-h5', 'aftersale:after-sale', 'view', '查看自提点售后单', 1, 1, NOW(), NOW(), 0),
(700642, 22001, 'role-workbench-h5', 'aftersale:item', 'view', '查看自提点售后商品', 1, 1, NOW(), NOW(), 0),
(700643, 22001, 'role-workbench-h5', 'aftersale:return-record', 'view', '查看自提点退货记录', 1, 1, NOW(), NOW(), 0),
(700644, 22002, 'role-workbench-h5', 'aftersale:after-sale', 'view', '查看团长售后单', 1, 1, NOW(), NOW(), 0),
(700645, 22002, 'role-workbench-h5', 'aftersale:item', 'view', '查看团长售后商品', 1, 1, NOW(), NOW(), 0),
(700646, 22002, 'role-workbench-h5', 'aftersale:return-record', 'view', '查看团长退货记录', 1, 1, NOW(), NOW(), 0),
(700647, 22003, 'role-workbench-h5', 'aftersale:after-sale', 'view', '查看自身售后单', 1, 1, NOW(), NOW(), 0),
(700648, 22003, 'role-workbench-h5', 'aftersale:item', 'view', '查看自身售后商品', 1, 1, NOW(), NOW(), 0),
(700649, 22003, 'role-workbench-h5', 'aftersale:audit-log', 'view', '查看自身售后审核日志', 1, 1, NOW(), NOW(), 0),
(700650, 22003, 'role-workbench-h5', 'aftersale:return-record', 'view', '查看自身退货记录', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：补充供应商档案、资质合同、采购单、送货单只读权限，支持 mall-supplier 查询接口联调
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(701101, 20001, 'admin', 'supplier:profile', 'view', '查看供应商档案', 1, 1, NOW(), NOW(), 0),
(701102, 20001, 'admin', 'supplier:contact', 'view', '查看供应商联系人', 1, 1, NOW(), NOW(), 0),
(701103, 20001, 'admin', 'supplier:qualification', 'view', '查看供应商资质', 1, 1, NOW(), NOW(), 0),
(701104, 20001, 'admin', 'supplier:contract', 'view', '查看供应商合同', 1, 1, NOW(), NOW(), 0),
(701105, 20001, 'admin', 'purchase:order', 'view', '查看采购单', 1, 1, NOW(), NOW(), 0),
(701106, 20001, 'admin', 'purchase:item', 'view', '查看采购明细', 1, 1, NOW(), NOW(), 0),
(701107, 20001, 'admin', 'purchase:audit-log', 'view', '查看采购审核日志', 1, 1, NOW(), NOW(), 0),
(701108, 20001, 'admin', 'purchase:source-log', 'view', '查看采购来源记录', 1, 1, NOW(), NOW(), 0),
(701109, 20001, 'admin', 'delivery:order', 'view', '查看送货单', 1, 1, NOW(), NOW(), 0),
(701110, 20001, 'admin', 'delivery:item', 'view', '查看送货明细', 1, 1, NOW(), NOW(), 0),
(701155, 20002, 'admin', 'supplier:profile', 'view', '查看供应商档案', 1, 1, NOW(), NOW(), 0),
(701156, 20002, 'admin', 'supplier:contact', 'view', '查看供应商联系人', 1, 1, NOW(), NOW(), 0),
(701157, 20002, 'admin', 'supplier:qualification', 'view', '查看供应商资质', 1, 1, NOW(), NOW(), 0),
(701158, 20002, 'admin', 'supplier:contract', 'view', '查看供应商合同', 1, 1, NOW(), NOW(), 0),
(701159, 20002, 'admin', 'purchase:order', 'view', '查看采购单', 1, 1, NOW(), NOW(), 0),
(701160, 20002, 'admin', 'purchase:item', 'view', '查看采购明细', 1, 1, NOW(), NOW(), 0),
(701161, 20002, 'admin', 'purchase:audit-log', 'view', '查看采购审核日志', 1, 1, NOW(), NOW(), 0),
(701162, 20002, 'admin', 'purchase:source-log', 'view', '查看采购来源记录', 1, 1, NOW(), NOW(), 0),
(701163, 20002, 'admin', 'delivery:order', 'view', '查看送货单', 1, 1, NOW(), NOW(), 0),
(701164, 20002, 'admin', 'delivery:item', 'view', '查看送货明细', 1, 1, NOW(), NOW(), 0),
(701111, 20003, 'admin', 'supplier:profile', 'view', '查看城市供应商档案', 1, 1, NOW(), NOW(), 0),
(701112, 20003, 'admin', 'supplier:contact', 'view', '查看城市供应商联系人', 1, 1, NOW(), NOW(), 0),
(701113, 20003, 'admin', 'supplier:qualification', 'view', '查看城市供应商资质', 1, 1, NOW(), NOW(), 0),
(701114, 20003, 'admin', 'supplier:contract', 'view', '查看城市供应商合同', 1, 1, NOW(), NOW(), 0),
(701115, 20003, 'admin', 'purchase:order', 'view', '查看城市采购单', 1, 1, NOW(), NOW(), 0),
(701116, 20003, 'admin', 'purchase:item', 'view', '查看城市采购明细', 1, 1, NOW(), NOW(), 0),
(701117, 20003, 'admin', 'purchase:audit-log', 'view', '查看城市采购审核日志', 1, 1, NOW(), NOW(), 0),
(701118, 20003, 'admin', 'purchase:source-log', 'view', '查看城市采购来源记录', 1, 1, NOW(), NOW(), 0),
(701119, 20003, 'admin', 'delivery:order', 'view', '查看城市送货单', 1, 1, NOW(), NOW(), 0),
(701120, 20003, 'admin', 'delivery:item', 'view', '查看城市送货明细', 1, 1, NOW(), NOW(), 0),
(701121, 20005, 'admin', 'supplier:profile', 'view', '查看供应商档案', 1, 1, NOW(), NOW(), 0),
(701122, 20005, 'admin', 'supplier:contact', 'view', '查看供应商联系人', 1, 1, NOW(), NOW(), 0),
(701123, 20005, 'admin', 'supplier:qualification', 'view', '查看供应商资质', 1, 1, NOW(), NOW(), 0),
(701124, 20005, 'admin', 'supplier:contract', 'view', '查看供应商合同', 1, 1, NOW(), NOW(), 0),
(701125, 20005, 'admin', 'purchase:order', 'view', '查看采购单', 1, 1, NOW(), NOW(), 0),
(701126, 20005, 'admin', 'purchase:item', 'view', '查看采购明细', 1, 1, NOW(), NOW(), 0),
(701127, 20005, 'admin', 'purchase:audit-log', 'view', '查看采购审核日志', 1, 1, NOW(), NOW(), 0),
(701128, 20005, 'admin', 'purchase:source-log', 'view', '查看采购来源记录', 1, 1, NOW(), NOW(), 0),
(701129, 20005, 'admin', 'delivery:order', 'view', '查看送货单', 1, 1, NOW(), NOW(), 0),
(701130, 20005, 'admin', 'delivery:item', 'view', '查看送货明细', 1, 1, NOW(), NOW(), 0),
(701131, 20008, 'admin', 'supplier:profile', 'view', '查看自身供应商档案', 1, 1, NOW(), NOW(), 0),
(701132, 20008, 'admin', 'supplier:contact', 'view', '查看自身供应商联系人', 1, 1, NOW(), NOW(), 0),
(701133, 20008, 'admin', 'supplier:qualification', 'view', '查看自身供应商资质', 1, 1, NOW(), NOW(), 0),
(701134, 20008, 'admin', 'supplier:contract', 'view', '查看自身供应商合同', 1, 1, NOW(), NOW(), 0),
(701135, 20008, 'admin', 'purchase:order', 'view', '查看自身采购单', 1, 1, NOW(), NOW(), 0),
(701136, 20008, 'admin', 'purchase:item', 'view', '查看自身采购明细', 1, 1, NOW(), NOW(), 0),
(701137, 20008, 'admin', 'purchase:audit-log', 'view', '查看自身采购审核日志', 1, 1, NOW(), NOW(), 0),
(701138, 20008, 'admin', 'purchase:source-log', 'view', '查看自身采购来源记录', 1, 1, NOW(), NOW(), 0),
(701139, 20008, 'admin', 'delivery:order', 'view', '查看自身送货单', 1, 1, NOW(), NOW(), 0),
(701140, 20008, 'admin', 'delivery:item', 'view', '查看自身送货明细', 1, 1, NOW(), NOW(), 0),
(701141, 21001, 'wms-admin', 'purchase:order', 'view', '查看当前仓采购单', 1, 1, NOW(), NOW(), 0),
(701142, 21001, 'wms-admin', 'purchase:item', 'view', '查看当前仓采购明细', 1, 1, NOW(), NOW(), 0),
(701143, 21001, 'wms-admin', 'delivery:order', 'view', '查看当前仓送货单', 1, 1, NOW(), NOW(), 0),
(701144, 21001, 'wms-admin', 'delivery:item', 'view', '查看当前仓送货明细', 1, 1, NOW(), NOW(), 0),
(701145, 22003, 'role-workbench-h5', 'supplier:profile', 'view', '查看自身供应商档案', 1, 1, NOW(), NOW(), 0),
(701146, 22003, 'role-workbench-h5', 'supplier:contact', 'view', '查看自身供应商联系人', 1, 1, NOW(), NOW(), 0),
(701147, 22003, 'role-workbench-h5', 'supplier:qualification', 'view', '查看自身供应商资质', 1, 1, NOW(), NOW(), 0),
(701148, 22003, 'role-workbench-h5', 'supplier:contract', 'view', '查看自身供应商合同', 1, 1, NOW(), NOW(), 0),
(701149, 22003, 'role-workbench-h5', 'purchase:order', 'view', '查看自身采购单', 1, 1, NOW(), NOW(), 0),
(701150, 22003, 'role-workbench-h5', 'purchase:item', 'view', '查看自身采购明细', 1, 1, NOW(), NOW(), 0),
(701151, 22003, 'role-workbench-h5', 'purchase:audit-log', 'view', '查看自身采购审核日志', 1, 1, NOW(), NOW(), 0),
(701152, 22003, 'role-workbench-h5', 'purchase:source-log', 'view', '查看自身采购来源记录', 1, 1, NOW(), NOW(), 0),
(701153, 22003, 'role-workbench-h5', 'delivery:order', 'view', '查看自身送货单', 1, 1, NOW(), NOW(), 0),
(701154, 22003, 'role-workbench-h5', 'delivery:item', 'view', '查看自身送货明细', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-24
-- 操作人：codex
-- 用途说明：补充财务账户、佣金分账、提现押金、供应商结算和对账只读权限，支持 mall-finance 查询接口联调
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(701201, 20001, 'admin', 'finance:account', 'view', '查看财务账户', 1, 1, NOW(), NOW(), 0),
(701202, 20001, 'admin', 'finance:account-flow', 'view', '查看账户流水', 1, 1, NOW(), NOW(), 0),
(701203, 20001, 'admin', 'finance:commission', 'view', '查看佣金明细', 1, 1, NOW(), NOW(), 0),
(701204, 20001, 'admin', 'finance:split', 'view', '查看分账流水', 1, 1, NOW(), NOW(), 0),
(701205, 20001, 'admin', 'finance:withdraw', 'view', '查看提现申请', 1, 1, NOW(), NOW(), 0),
(701206, 20001, 'admin', 'finance:deposit', 'view', '查看押金记录', 1, 1, NOW(), NOW(), 0),
(701207, 20001, 'admin', 'finance:supplier-settlement', 'view', '查看供应商结算', 1, 1, NOW(), NOW(), 0),
(701208, 20001, 'admin', 'finance:reconciliation', 'view', '查看对账批次', 1, 1, NOW(), NOW(), 0),
(701209, 20002, 'admin', 'finance:account', 'view', '查看财务账户', 1, 1, NOW(), NOW(), 0),
(701210, 20002, 'admin', 'finance:account-flow', 'view', '查看账户流水', 1, 1, NOW(), NOW(), 0),
(701211, 20002, 'admin', 'finance:commission', 'view', '查看佣金明细', 1, 1, NOW(), NOW(), 0),
(701212, 20002, 'admin', 'finance:split', 'view', '查看分账流水', 1, 1, NOW(), NOW(), 0),
(701213, 20002, 'admin', 'finance:withdraw', 'view', '查看提现申请', 1, 1, NOW(), NOW(), 0),
(701214, 20002, 'admin', 'finance:deposit', 'view', '查看押金记录', 1, 1, NOW(), NOW(), 0),
(701215, 20002, 'admin', 'finance:supplier-settlement', 'view', '查看供应商结算', 1, 1, NOW(), NOW(), 0),
(701216, 20003, 'admin', 'finance:account', 'view', '查看城市财务账户', 1, 1, NOW(), NOW(), 0),
(701217, 20003, 'admin', 'finance:account-flow', 'view', '查看城市账户流水', 1, 1, NOW(), NOW(), 0),
(701218, 20003, 'admin', 'finance:commission', 'view', '查看城市佣金明细', 1, 1, NOW(), NOW(), 0),
(701219, 20003, 'admin', 'finance:split', 'view', '查看城市分账流水', 1, 1, NOW(), NOW(), 0),
(701220, 20003, 'admin', 'finance:withdraw', 'view', '查看城市提现申请', 1, 1, NOW(), NOW(), 0),
(701221, 20003, 'admin', 'finance:deposit', 'view', '查看城市押金记录', 1, 1, NOW(), NOW(), 0),
(701222, 20006, 'admin', 'finance:account', 'view', '查看财务账户', 1, 1, NOW(), NOW(), 0),
(701223, 20006, 'admin', 'finance:account-flow', 'view', '查看账户流水', 1, 1, NOW(), NOW(), 0),
(701224, 20006, 'admin', 'finance:commission', 'view', '查看佣金明细', 1, 1, NOW(), NOW(), 0),
(701225, 20006, 'admin', 'finance:split', 'view', '查看分账流水', 1, 1, NOW(), NOW(), 0),
(701226, 20006, 'admin', 'finance:withdraw', 'view', '查看提现申请', 1, 1, NOW(), NOW(), 0),
(701227, 20006, 'admin', 'finance:deposit', 'view', '查看押金记录', 1, 1, NOW(), NOW(), 0),
(701228, 20006, 'admin', 'finance:supplier-settlement', 'view', '查看供应商结算', 1, 1, NOW(), NOW(), 0),
(701229, 20006, 'admin', 'finance:reconciliation', 'view', '查看对账批次', 1, 1, NOW(), NOW(), 0),
(701230, 20008, 'admin', 'finance:account', 'view', '查看自身财务账户', 1, 1, NOW(), NOW(), 0),
(701231, 20008, 'admin', 'finance:account-flow', 'view', '查看自身账户流水', 1, 1, NOW(), NOW(), 0),
(701232, 20008, 'admin', 'finance:commission', 'view', '查看自身佣金明细', 1, 1, NOW(), NOW(), 0),
(701233, 20008, 'admin', 'finance:split', 'view', '查看自身分账流水', 1, 1, NOW(), NOW(), 0),
(701234, 20008, 'admin', 'finance:withdraw', 'view', '查看自身提现申请', 1, 1, NOW(), NOW(), 0),
(701235, 20008, 'admin', 'finance:deposit', 'view', '查看自身押金记录', 1, 1, NOW(), NOW(), 0),
(701236, 20008, 'admin', 'finance:supplier-settlement', 'view', '查看自身供应商结算', 1, 1, NOW(), NOW(), 0),
(701237, 22001, 'role-workbench-h5', 'finance:account', 'view', '查看自提点财务账户', 1, 1, NOW(), NOW(), 0),
(701238, 22001, 'role-workbench-h5', 'finance:account-flow', 'view', '查看自提点账户流水', 1, 1, NOW(), NOW(), 0),
(701239, 22001, 'role-workbench-h5', 'finance:commission', 'view', '查看自提点佣金明细', 1, 1, NOW(), NOW(), 0),
(701240, 22001, 'role-workbench-h5', 'finance:split', 'view', '查看自提点分账流水', 1, 1, NOW(), NOW(), 0),
(701241, 22001, 'role-workbench-h5', 'finance:withdraw', 'view', '查看自提点提现申请', 1, 1, NOW(), NOW(), 0),
(701242, 22002, 'role-workbench-h5', 'finance:account', 'view', '查看团长财务账户', 1, 1, NOW(), NOW(), 0),
(701243, 22002, 'role-workbench-h5', 'finance:account-flow', 'view', '查看团长账户流水', 1, 1, NOW(), NOW(), 0),
(701244, 22002, 'role-workbench-h5', 'finance:commission', 'view', '查看团长佣金明细', 1, 1, NOW(), NOW(), 0),
(701245, 22002, 'role-workbench-h5', 'finance:split', 'view', '查看团长分账流水', 1, 1, NOW(), NOW(), 0),
(701246, 22002, 'role-workbench-h5', 'finance:withdraw', 'view', '查看团长提现申请', 1, 1, NOW(), NOW(), 0),
(701247, 22003, 'role-workbench-h5', 'finance:account', 'view', '查看自身财务账户', 1, 1, NOW(), NOW(), 0),
(701248, 22003, 'role-workbench-h5', 'finance:account-flow', 'view', '查看自身账户流水', 1, 1, NOW(), NOW(), 0),
(701249, 22003, 'role-workbench-h5', 'finance:commission', 'view', '查看自身佣金明细', 1, 1, NOW(), NOW(), 0),
(701250, 22003, 'role-workbench-h5', 'finance:split', 'view', '查看自身分账流水', 1, 1, NOW(), NOW(), 0),
(701251, 22003, 'role-workbench-h5', 'finance:withdraw', 'view', '查看自身提现申请', 1, 1, NOW(), NOW(), 0),
(701252, 22003, 'role-workbench-h5', 'finance:deposit', 'view', '查看自身押金记录', 1, 1, NOW(), NOW(), 0),
(701253, 22003, 'role-workbench-h5', 'finance:supplier-settlement', 'view', '查看自身供应商结算', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-25
-- 操作人：codex
-- 用途说明：补充WMS仓储履约只读权限，支持 mall-wms 查询接口联调
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(701301, 20001, 'admin', 'wms:warehouse', 'view', '查看仓库', 1, 1, NOW(), NOW(), 0),
(701302, 20001, 'admin', 'wms:zone', 'view', '查看库区', 1, 1, NOW(), NOW(), 0),
(701303, 20001, 'admin', 'wms:location', 'view', '查看库位', 1, 1, NOW(), NOW(), 0),
(701304, 20001, 'admin', 'wms:line', 'view', '查看线路', 1, 1, NOW(), NOW(), 0),
(701305, 20001, 'admin', 'wms:line-station', 'view', '查看线路自提点', 1, 1, NOW(), NOW(), 0),
(701306, 20001, 'admin', 'wms:driver', 'view', '查看司机', 1, 1, NOW(), NOW(), 0),
(701307, 20001, 'admin', 'wms:inbound', 'view', '查看入库单', 1, 1, NOW(), NOW(), 0),
(701308, 20001, 'admin', 'wms:inbound-item', 'view', '查看入库明细', 1, 1, NOW(), NOW(), 0),
(701309, 20001, 'admin', 'wms:receive', 'view', '查看收货记录', 1, 1, NOW(), NOW(), 0),
(701310, 20001, 'admin', 'wms:quality', 'view', '查看质检文件', 1, 1, NOW(), NOW(), 0),
(701311, 20001, 'admin', 'wms:putaway', 'view', '查看上架任务', 1, 1, NOW(), NOW(), 0),
(701312, 20001, 'admin', 'wms:inventory', 'view', '查看库存', 1, 1, NOW(), NOW(), 0),
(701313, 20001, 'admin', 'wms:inventory-lock', 'view', '查看库存锁定', 1, 1, NOW(), NOW(), 0),
(701314, 20001, 'admin', 'wms:inventory-log', 'view', '查看库存流水', 1, 1, NOW(), NOW(), 0),
(701315, 20001, 'admin', 'wms:stocktake', 'view', '查看盘点单', 1, 1, NOW(), NOW(), 0),
(701316, 20001, 'admin', 'wms:wave', 'view', '查看波次', 1, 1, NOW(), NOW(), 0),
(701317, 20001, 'admin', 'wms:pick', 'view', '查看拣货任务', 1, 1, NOW(), NOW(), 0),
(701318, 20001, 'admin', 'wms:pick-item', 'view', '查看拣货明细', 1, 1, NOW(), NOW(), 0),
(701319, 20001, 'admin', 'wms:loading', 'view', '查看装车单', 1, 1, NOW(), NOW(), 0),
(701320, 20001, 'admin', 'wms:loading-item', 'view', '查看装车明细', 1, 1, NOW(), NOW(), 0),
(701321, 20001, 'admin', 'wms:outbound', 'view', '查看出库单', 1, 1, NOW(), NOW(), 0),
(701322, 20001, 'admin', 'wms:delivery', 'view', '查看配送单', 1, 1, NOW(), NOW(), 0),
(701323, 20001, 'admin', 'wms:delivery-station', 'view', '查看配送自提点', 1, 1, NOW(), NOW(), 0),
(701324, 20001, 'admin', 'wms:driver-sign', 'view', '查看司机签到', 1, 1, NOW(), NOW(), 0),
(701325, 20001, 'admin', 'wms:return', 'view', '查看回仓交接', 1, 1, NOW(), NOW(), 0),
(701326, 20001, 'admin', 'wms:operation-log', 'view', '查看WMS操作日志', 1, 1, NOW(), NOW(), 0),
(701327, 20003, 'admin', 'wms:warehouse', 'view', '查看仓库', 1, 1, NOW(), NOW(), 0),
(701328, 20003, 'admin', 'wms:zone', 'view', '查看库区', 1, 1, NOW(), NOW(), 0),
(701329, 20003, 'admin', 'wms:location', 'view', '查看库位', 1, 1, NOW(), NOW(), 0),
(701330, 20003, 'admin', 'wms:line', 'view', '查看线路', 1, 1, NOW(), NOW(), 0),
(701331, 20003, 'admin', 'wms:line-station', 'view', '查看线路自提点', 1, 1, NOW(), NOW(), 0),
(701332, 20003, 'admin', 'wms:driver', 'view', '查看司机', 1, 1, NOW(), NOW(), 0),
(701333, 20003, 'admin', 'wms:inbound', 'view', '查看入库单', 1, 1, NOW(), NOW(), 0),
(701334, 20003, 'admin', 'wms:inbound-item', 'view', '查看入库明细', 1, 1, NOW(), NOW(), 0),
(701335, 20003, 'admin', 'wms:receive', 'view', '查看收货记录', 1, 1, NOW(), NOW(), 0),
(701336, 20003, 'admin', 'wms:quality', 'view', '查看质检文件', 1, 1, NOW(), NOW(), 0),
(701337, 20003, 'admin', 'wms:putaway', 'view', '查看上架任务', 1, 1, NOW(), NOW(), 0),
(701338, 20003, 'admin', 'wms:inventory', 'view', '查看库存', 1, 1, NOW(), NOW(), 0),
(701339, 20003, 'admin', 'wms:inventory-lock', 'view', '查看库存锁定', 1, 1, NOW(), NOW(), 0),
(701340, 20003, 'admin', 'wms:inventory-log', 'view', '查看库存流水', 1, 1, NOW(), NOW(), 0),
(701341, 20003, 'admin', 'wms:stocktake', 'view', '查看盘点单', 1, 1, NOW(), NOW(), 0),
(701342, 20003, 'admin', 'wms:wave', 'view', '查看波次', 1, 1, NOW(), NOW(), 0),
(701343, 20003, 'admin', 'wms:pick', 'view', '查看拣货任务', 1, 1, NOW(), NOW(), 0),
(701344, 20003, 'admin', 'wms:pick-item', 'view', '查看拣货明细', 1, 1, NOW(), NOW(), 0),
(701345, 20003, 'admin', 'wms:loading', 'view', '查看装车单', 1, 1, NOW(), NOW(), 0),
(701346, 20003, 'admin', 'wms:loading-item', 'view', '查看装车明细', 1, 1, NOW(), NOW(), 0),
(701347, 20003, 'admin', 'wms:outbound', 'view', '查看出库单', 1, 1, NOW(), NOW(), 0),
(701348, 20003, 'admin', 'wms:delivery', 'view', '查看配送单', 1, 1, NOW(), NOW(), 0),
(701349, 20003, 'admin', 'wms:delivery-station', 'view', '查看配送自提点', 1, 1, NOW(), NOW(), 0),
(701350, 20003, 'admin', 'wms:driver-sign', 'view', '查看司机签到', 1, 1, NOW(), NOW(), 0),
(701351, 20003, 'admin', 'wms:return', 'view', '查看回仓交接', 1, 1, NOW(), NOW(), 0),
(701352, 20003, 'admin', 'wms:operation-log', 'view', '查看WMS操作日志', 1, 1, NOW(), NOW(), 0),
(701353, 21001, 'wms-admin', 'wms:warehouse', 'view', '查看仓库', 1, 1, NOW(), NOW(), 0),
(701354, 21001, 'wms-admin', 'wms:zone', 'view', '查看库区', 1, 1, NOW(), NOW(), 0),
(701355, 21001, 'wms-admin', 'wms:location', 'view', '查看库位', 1, 1, NOW(), NOW(), 0),
(701356, 21001, 'wms-admin', 'wms:line', 'view', '查看线路', 1, 1, NOW(), NOW(), 0),
(701357, 21001, 'wms-admin', 'wms:line-station', 'view', '查看线路自提点', 1, 1, NOW(), NOW(), 0),
(701358, 21001, 'wms-admin', 'wms:driver', 'view', '查看司机', 1, 1, NOW(), NOW(), 0),
(701359, 21001, 'wms-admin', 'wms:inbound', 'view', '查看入库单', 1, 1, NOW(), NOW(), 0),
(701360, 21001, 'wms-admin', 'wms:inbound-item', 'view', '查看入库明细', 1, 1, NOW(), NOW(), 0),
(701361, 21001, 'wms-admin', 'wms:receive', 'view', '查看收货记录', 1, 1, NOW(), NOW(), 0),
(701362, 21001, 'wms-admin', 'wms:quality', 'view', '查看质检文件', 1, 1, NOW(), NOW(), 0),
(701363, 21001, 'wms-admin', 'wms:putaway', 'view', '查看上架任务', 1, 1, NOW(), NOW(), 0),
(701364, 21001, 'wms-admin', 'wms:inventory', 'view', '查看库存', 1, 1, NOW(), NOW(), 0),
(701365, 21001, 'wms-admin', 'wms:inventory-lock', 'view', '查看库存锁定', 1, 1, NOW(), NOW(), 0),
(701366, 21001, 'wms-admin', 'wms:inventory-log', 'view', '查看库存流水', 1, 1, NOW(), NOW(), 0),
(701367, 21001, 'wms-admin', 'wms:stocktake', 'view', '查看盘点单', 1, 1, NOW(), NOW(), 0),
(701368, 21001, 'wms-admin', 'wms:wave', 'view', '查看波次', 1, 1, NOW(), NOW(), 0),
(701369, 21001, 'wms-admin', 'wms:pick', 'view', '查看拣货任务', 1, 1, NOW(), NOW(), 0),
(701370, 21001, 'wms-admin', 'wms:pick-item', 'view', '查看拣货明细', 1, 1, NOW(), NOW(), 0),
(701371, 21001, 'wms-admin', 'wms:loading', 'view', '查看装车单', 1, 1, NOW(), NOW(), 0),
(701372, 21001, 'wms-admin', 'wms:loading-item', 'view', '查看装车明细', 1, 1, NOW(), NOW(), 0),
(701373, 21001, 'wms-admin', 'wms:outbound', 'view', '查看出库单', 1, 1, NOW(), NOW(), 0),
(701374, 21001, 'wms-admin', 'wms:delivery', 'view', '查看配送单', 1, 1, NOW(), NOW(), 0),
(701375, 21001, 'wms-admin', 'wms:delivery-station', 'view', '查看配送自提点', 1, 1, NOW(), NOW(), 0),
(701376, 21001, 'wms-admin', 'wms:driver-sign', 'view', '查看司机签到', 1, 1, NOW(), NOW(), 0),
(701377, 21001, 'wms-admin', 'wms:return', 'view', '查看回仓交接', 1, 1, NOW(), NOW(), 0),
(701378, 21001, 'wms-admin', 'wms:operation-log', 'view', '查看WMS操作日志', 1, 1, NOW(), NOW(), 0),
(701379, 21002, 'warehouse-h5', 'wms:warehouse', 'view', '查看仓库', 1, 1, NOW(), NOW(), 0),
(701380, 21002, 'warehouse-h5', 'wms:zone', 'view', '查看库区', 1, 1, NOW(), NOW(), 0),
(701381, 21002, 'warehouse-h5', 'wms:location', 'view', '查看库位', 1, 1, NOW(), NOW(), 0),
(701382, 21002, 'warehouse-h5', 'wms:line', 'view', '查看线路', 1, 1, NOW(), NOW(), 0),
(701383, 21002, 'warehouse-h5', 'wms:inbound', 'view', '查看入库单', 1, 1, NOW(), NOW(), 0),
(701384, 21002, 'warehouse-h5', 'wms:inbound-item', 'view', '查看入库明细', 1, 1, NOW(), NOW(), 0),
(701385, 21002, 'warehouse-h5', 'wms:receive', 'view', '查看收货记录', 1, 1, NOW(), NOW(), 0),
(701386, 21002, 'warehouse-h5', 'wms:quality', 'view', '查看质检文件', 1, 1, NOW(), NOW(), 0),
(701387, 21002, 'warehouse-h5', 'wms:putaway', 'view', '查看上架任务', 1, 1, NOW(), NOW(), 0),
(701388, 21002, 'warehouse-h5', 'wms:operation-log', 'view', '查看WMS操作日志', 1, 1, NOW(), NOW(), 0),
(701389, 21003, 'warehouse-h5', 'wms:warehouse', 'view', '查看仓库', 1, 1, NOW(), NOW(), 0),
(701390, 21003, 'warehouse-h5', 'wms:zone', 'view', '查看库区', 1, 1, NOW(), NOW(), 0),
(701391, 21003, 'warehouse-h5', 'wms:location', 'view', '查看库位', 1, 1, NOW(), NOW(), 0),
(701392, 21003, 'warehouse-h5', 'wms:inbound', 'view', '查看入库单', 1, 1, NOW(), NOW(), 0),
(701393, 21003, 'warehouse-h5', 'wms:inbound-item', 'view', '查看入库明细', 1, 1, NOW(), NOW(), 0),
(701394, 21003, 'warehouse-h5', 'wms:receive', 'view', '查看收货记录', 1, 1, NOW(), NOW(), 0),
(701395, 21003, 'warehouse-h5', 'wms:quality', 'view', '查看质检文件', 1, 1, NOW(), NOW(), 0),
(701396, 21003, 'warehouse-h5', 'wms:putaway', 'view', '查看上架任务', 1, 1, NOW(), NOW(), 0),
(701397, 21003, 'warehouse-h5', 'wms:operation-log', 'view', '查看WMS操作日志', 1, 1, NOW(), NOW(), 0),
(701398, 21004, 'warehouse-h5', 'wms:warehouse', 'view', '查看仓库', 1, 1, NOW(), NOW(), 0),
(701399, 21004, 'warehouse-h5', 'wms:zone', 'view', '查看库区', 1, 1, NOW(), NOW(), 0),
(701400, 21004, 'warehouse-h5', 'wms:location', 'view', '查看库位', 1, 1, NOW(), NOW(), 0),
(701401, 21004, 'warehouse-h5', 'wms:inventory', 'view', '查看库存', 1, 1, NOW(), NOW(), 0),
(701402, 21004, 'warehouse-h5', 'wms:inventory-lock', 'view', '查看库存锁定', 1, 1, NOW(), NOW(), 0),
(701403, 21004, 'warehouse-h5', 'wms:inventory-log', 'view', '查看库存流水', 1, 1, NOW(), NOW(), 0),
(701404, 21004, 'warehouse-h5', 'wms:stocktake', 'view', '查看盘点单', 1, 1, NOW(), NOW(), 0),
(701405, 21004, 'warehouse-h5', 'wms:wave', 'view', '查看波次', 1, 1, NOW(), NOW(), 0),
(701406, 21004, 'warehouse-h5', 'wms:pick', 'view', '查看拣货任务', 1, 1, NOW(), NOW(), 0),
(701407, 21004, 'warehouse-h5', 'wms:pick-item', 'view', '查看拣货明细', 1, 1, NOW(), NOW(), 0),
(701408, 21004, 'warehouse-h5', 'wms:operation-log', 'view', '查看WMS操作日志', 1, 1, NOW(), NOW(), 0),
(701409, 21005, 'warehouse-h5', 'wms:warehouse', 'view', '查看仓库', 1, 1, NOW(), NOW(), 0),
(701410, 21005, 'warehouse-h5', 'wms:zone', 'view', '查看库区', 1, 1, NOW(), NOW(), 0),
(701411, 21005, 'warehouse-h5', 'wms:location', 'view', '查看库位', 1, 1, NOW(), NOW(), 0),
(701412, 21005, 'warehouse-h5', 'wms:line', 'view', '查看线路', 1, 1, NOW(), NOW(), 0),
(701413, 21005, 'warehouse-h5', 'wms:line-station', 'view', '查看线路自提点', 1, 1, NOW(), NOW(), 0),
(701414, 21005, 'warehouse-h5', 'wms:driver', 'view', '查看司机', 1, 1, NOW(), NOW(), 0),
(701415, 21005, 'warehouse-h5', 'wms:inventory', 'view', '查看库存', 1, 1, NOW(), NOW(), 0),
(701416, 21005, 'warehouse-h5', 'wms:inventory-log', 'view', '查看库存流水', 1, 1, NOW(), NOW(), 0),
(701417, 21005, 'warehouse-h5', 'wms:wave', 'view', '查看波次', 1, 1, NOW(), NOW(), 0),
(701418, 21005, 'warehouse-h5', 'wms:loading', 'view', '查看装车单', 1, 1, NOW(), NOW(), 0),
(701419, 21005, 'warehouse-h5', 'wms:loading-item', 'view', '查看装车明细', 1, 1, NOW(), NOW(), 0),
(701420, 21005, 'warehouse-h5', 'wms:outbound', 'view', '查看出库单', 1, 1, NOW(), NOW(), 0),
(701421, 21005, 'warehouse-h5', 'wms:delivery', 'view', '查看配送单', 1, 1, NOW(), NOW(), 0),
(701422, 21005, 'warehouse-h5', 'wms:delivery-station', 'view', '查看配送自提点', 1, 1, NOW(), NOW(), 0),
(701423, 21005, 'warehouse-h5', 'wms:return', 'view', '查看回仓交接', 1, 1, NOW(), NOW(), 0),
(701424, 21005, 'warehouse-h5', 'wms:operation-log', 'view', '查看WMS操作日志', 1, 1, NOW(), NOW(), 0),
(701425, 21006, 'warehouse-h5', 'wms:line', 'view', '查看线路', 1, 1, NOW(), NOW(), 0),
(701426, 21006, 'warehouse-h5', 'wms:line-station', 'view', '查看线路自提点', 1, 1, NOW(), NOW(), 0),
(701427, 21006, 'warehouse-h5', 'wms:loading', 'view', '查看装车单', 1, 1, NOW(), NOW(), 0),
(701428, 21006, 'warehouse-h5', 'wms:loading-item', 'view', '查看装车明细', 1, 1, NOW(), NOW(), 0),
(701429, 21006, 'warehouse-h5', 'wms:delivery', 'view', '查看配送单', 1, 1, NOW(), NOW(), 0),
(701430, 21006, 'warehouse-h5', 'wms:delivery-station', 'view', '查看配送自提点', 1, 1, NOW(), NOW(), 0),
(701431, 21006, 'warehouse-h5', 'wms:driver-sign', 'view', '查看司机签到', 1, 1, NOW(), NOW(), 0),
(701432, 21006, 'warehouse-h5', 'wms:return', 'view', '查看回仓交接', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-25
-- 操作人：codex
-- 用途说明：补充角色工作台自提点/团长接口只读权限，支持 mall-station 模块联调
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(701433, 22001, 'role-workbench-h5', 'station:workbench', 'view', '查看自提点工作台', 1, 1, NOW(), NOW(), 0),
(701434, 22001, 'role-workbench-h5', 'station:base', 'view', '查看自提点资料', 1, 1, NOW(), NOW(), 0),
(701435, 22001, 'role-workbench-h5', 'station:order', 'view', '查看自提点订单', 1, 1, NOW(), NOW(), 0),
(701436, 22001, 'role-workbench-h5', 'station:arrival', 'view', '查看自提点到货', 1, 1, NOW(), NOW(), 0),
(701437, 22001, 'role-workbench-h5', 'station:return', 'view', '查看自提点退货带回', 1, 1, NOW(), NOW(), 0),
(701438, 22001, 'role-workbench-h5', 'station:exception', 'view', '查看自提点异常', 1, 1, NOW(), NOW(), 0),
(701439, 22001, 'role-workbench-h5', 'station:leave', 'view', '查看自提点休假', 1, 1, NOW(), NOW(), 0),
(701440, 22001, 'role-workbench-h5', 'station:finance', 'view', '查看自提点资金佣金', 1, 1, NOW(), NOW(), 0),
(701441, 22001, 'role-workbench-h5', 'station:withdraw', 'view', '查看自提点提现', 1, 1, NOW(), NOW(), 0),
(701442, 22001, 'role-workbench-h5', 'station:message', 'view', '查看自提点消息', 1, 1, NOW(), NOW(), 0),
(701443, 22002, 'role-workbench-h5', 'station:workbench', 'view', '查看团长工作台', 1, 1, NOW(), NOW(), 0),
(701444, 22002, 'role-workbench-h5', 'station:base', 'view', '查看团长站点资料', 1, 1, NOW(), NOW(), 0),
(701445, 22002, 'role-workbench-h5', 'station:order', 'view', '查看团长订单', 1, 1, NOW(), NOW(), 0),
(701446, 22002, 'role-workbench-h5', 'station:arrival', 'view', '查看团长到货', 1, 1, NOW(), NOW(), 0),
(701447, 22002, 'role-workbench-h5', 'station:return', 'view', '查看团长退货带回', 1, 1, NOW(), NOW(), 0),
(701448, 22002, 'role-workbench-h5', 'station:exception', 'view', '查看团长异常', 1, 1, NOW(), NOW(), 0),
(701449, 22002, 'role-workbench-h5', 'station:leave', 'view', '查看团长休假', 1, 1, NOW(), NOW(), 0),
(701450, 22002, 'role-workbench-h5', 'station:finance', 'view', '查看团长资金佣金', 1, 1, NOW(), NOW(), 0),
(701451, 22002, 'role-workbench-h5', 'station:withdraw', 'view', '查看团长提现', 1, 1, NOW(), NOW(), 0),
(701452, 22002, 'role-workbench-h5', 'station:message', 'view', '查看团长消息', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-25
-- 操作人：codex
-- 用途说明：补充角色工作台自提点/团长动作权限，支持核销、到货确认、休假、提现和消息已读联调
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(701453, 22001, 'role-workbench-h5', 'station:order', 'pickup-confirm', '自提点订单提货核销', 2, 1, NOW(), NOW(), 0),
(701454, 22001, 'role-workbench-h5', 'station:arrival', 'confirm', '自提点到货确认', 2, 1, NOW(), NOW(), 0),
(701455, 22001, 'role-workbench-h5', 'station:leave', 'apply', '自提点提交休假', 2, 1, NOW(), NOW(), 0),
(701456, 22001, 'role-workbench-h5', 'station:leave', 'cancel', '自提点撤回休假', 2, 1, NOW(), NOW(), 0),
(701457, 22001, 'role-workbench-h5', 'station:withdraw', 'apply', '自提点提交提现', 2, 1, NOW(), NOW(), 0),
(701458, 22001, 'role-workbench-h5', 'station:withdraw', 'cancel', '自提点撤回提现', 2, 1, NOW(), NOW(), 0),
(701459, 22001, 'role-workbench-h5', 'station:message', 'read', '自提点消息已读', 1, 1, NOW(), NOW(), 0),
(701460, 22002, 'role-workbench-h5', 'station:order', 'pickup-confirm', '团长订单提货核销', 2, 1, NOW(), NOW(), 0),
(701461, 22002, 'role-workbench-h5', 'station:arrival', 'confirm', '团长到货确认', 2, 1, NOW(), NOW(), 0),
(701462, 22002, 'role-workbench-h5', 'station:leave', 'apply', '团长提交休假', 2, 1, NOW(), NOW(), 0),
(701463, 22002, 'role-workbench-h5', 'station:leave', 'cancel', '团长撤回休假', 2, 1, NOW(), NOW(), 0),
(701464, 22002, 'role-workbench-h5', 'station:withdraw', 'apply', '团长提交提现', 2, 1, NOW(), NOW(), 0),
(701465, 22002, 'role-workbench-h5', 'station:withdraw', 'cancel', '团长撤回提现', 2, 1, NOW(), NOW(), 0),
(701466, 22002, 'role-workbench-h5', 'station:message', 'read', '团长消息已读', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_dict_type, sys_dict_item
-- 日期：2026-06-25
-- 操作人：codex
-- 用途说明：补充角色工作台提现撤回解冻账户流水类型
-- ----------------------------
INSERT INTO sys_dict_type
(id, dict_code, dict_name, status, create_time, update_time, is_deleted)
VALUES
(19500, 'fin_account_flow_type', '账户流水类型', 1, NOW(), NOW(), 0);

INSERT INTO sys_dict_item
(id, dict_code, item_code, item_name, item_value, sort_no, status, create_time, update_time, is_deleted)
VALUES
(19502, 'fin_account_flow_type', 'COMMISSION_IN', '佣金入账', 1, 10, 1, NOW(), NOW(), 0),
(19503, 'fin_account_flow_type', 'REFUND_DEDUCT', '退款扣回', 2, 20, 1, NOW(), NOW(), 0),
(19504, 'fin_account_flow_type', 'WITHDRAW_FREEZE', '提现冻结', 3, 30, 1, NOW(), NOW(), 0),
(19505, 'fin_account_flow_type', 'WITHDRAW_SUCCESS', '提现成功', 4, 40, 1, NOW(), NOW(), 0),
(19506, 'fin_account_flow_type', 'WITHDRAW_REJECT_UNFREEZE', '提现驳回解冻', 5, 50, 1, NOW(), NOW(), 0),
(19507, 'fin_account_flow_type', 'DEPOSIT_RELEASE', '押金释放', 6, 60, 1, NOW(), NOW(), 0),
(19501, 'fin_account_flow_type', 'WITHDRAW_CANCEL_UNFREEZE', '提现撤回解冻', 7, 70, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-25
-- 操作人：codex
-- 用途说明：补充供应商工作台首页聚合权限，支持供应商小程序首页联调
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(701467, 22003, 'role-workbench-h5', 'supplier:workbench', 'view', '查看供应商工作台首页', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role, sys_role_button
-- 日期：2026-06-25
-- 操作人：codex
-- 用途说明：补充用户小程序首版接口权限，支持 mall-user 浏览、购物车、订单、售后、自提点和消息联调
-- ----------------------------
INSERT INTO sys_role
(id, role_code, role_name, portal_code, role_type, status, remark, create_time, update_time, is_deleted)
VALUES
(23001, 'mini_user', '用户小程序账号', 'user-mini-h5', 31, 1, 'C端用户小程序默认权限', NOW(), NOW(), 0);

INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(701468, 23001, 'user-mini-h5', 'user:home', 'view', '查看用户小程序首页', 1, 1, NOW(), NOW(), 0),
(701469, 23001, 'user-mini-h5', 'user:product', 'view', '查看商品与评价', 1, 1, NOW(), NOW(), 0),
(701470, 23001, 'user-mini-h5', 'user:cart', 'view', '查看购物车', 1, 1, NOW(), NOW(), 0),
(701471, 23001, 'user-mini-h5', 'user:cart', 'add', '加入购物车', 1, 1, NOW(), NOW(), 0),
(701472, 23001, 'user-mini-h5', 'user:cart', 'update', '更新购物车', 1, 1, NOW(), NOW(), 0),
(701473, 23001, 'user-mini-h5', 'user:cart', 'remove', '移除购物车', 1, 1, NOW(), NOW(), 0),
(701474, 23001, 'user-mini-h5', 'user:order', 'view', '查看用户订单', 1, 1, NOW(), NOW(), 0),
(701475, 23001, 'user-mini-h5', 'user:aftersale', 'view', '查看用户售后', 1, 1, NOW(), NOW(), 0),
(701476, 23001, 'user-mini-h5', 'user:station', 'view', '查看自提点', 1, 1, NOW(), NOW(), 0),
(701477, 23001, 'user-mini-h5', 'user:message', 'view', '查看用户消息', 1, 1, NOW(), NOW(), 0),
(701478, 23001, 'user-mini-h5', 'user:message', 'read', '用户消息已读', 1, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-25
-- 操作人：codex
-- 用途说明：补充用户小程序下单、取消、支付单创建和售后申请权限
-- ----------------------------
INSERT INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(701479, 23001, 'user-mini-h5', 'user:order', 'submit', '提交用户订单', 2, 1, NOW(), NOW(), 0),
(701480, 23001, 'user-mini-h5', 'user:order', 'cancel', '取消未支付订单', 2, 1, NOW(), NOW(), 0),
(701481, 23001, 'user-mini-h5', 'user:payment', 'create', '创建用户支付单', 2, 1, NOW(), NOW(), 0),
(701482, 23001, 'user-mini-h5', 'user:aftersale', 'apply', '提交用户售后申请', 2, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：修正
-- 涉及表：ord_order
-- 日期：2026-06-25
-- 操作人：codex
-- 用途说明：修正未支付关闭订单测试数据支付状态，避免误用部分退款状态
-- ----------------------------
UPDATE ord_order
SET pay_status = 10,
    update_time = NOW()
WHERE id = 761003
  AND trade_status = 30
  AND pay_status = 50;

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_task, ord_order, ord_order_item, wms_inventory_lock, sale_publish_period, sale_publish_sku, wms_inventory, sup_qualification, sup_contract, op_todo, prd_product, prd_sku
-- 日期：2026-06-26
-- 操作人：codex
-- 用途说明：补充第一版系统任务测试数据，覆盖超时关单、支付提醒、截团生成、失败重试、对账报表、待办超时、供应商到期、低库存和搜索联想词同步；不包含积分、优惠券、定时上架、定时下架
-- ----------------------------
INSERT INTO sys_task
(id, task_no, task_code, task_name, biz_type, biz_no, idempotent_key, retry_count, next_retry_time, status, fail_reason, create_time, update_time, is_deleted)
VALUES
(795100, 'TASK_JOB_ORDER_CLOSE_WAIT', 'ORDER_CLOSE_TIMEOUT', '未支付订单超时关闭', 'order', 'ALL', 'JOB_SAMPLE_ORDER_CLOSE_TIMEOUT_WAIT', 0, NOW(), 10, NULL, NOW(), NOW(), 0),
(795101, 'TASK_JOB_PAY_REMIND_RUNNING', 'PAY_REMIND_LAST_FIVE_MINUTES', '支付最后5分钟提醒', 'message', 'ALL', 'JOB_SAMPLE_PAY_REMIND_RUNNING', 1, DATE_ADD(NOW(), INTERVAL 5 MINUTE), 20, NULL, NOW(), NOW(), 0),
(795102, 'TASK_JOB_SALE_CUTOFF_SUCCESS', 'SALE_PERIOD_CUTOFF', '团期截团处理', 'sale_period', 'ALL', 'JOB_SAMPLE_SALE_CUTOFF_SUCCESS', 0, NULL, 30, NULL, NOW(), NOW(), 0),
(795103, 'TASK_JOB_MESSAGE_RETRY_FAILED', 'MESSAGE_SEND_RETRY', '消息发送失败重试', 'message', 'ALL', 'JOB_SAMPLE_MESSAGE_RETRY_FAILED', 2, DATE_ADD(NOW(), INTERVAL 10 MINUTE), 40, '消息通道返回失败', NOW(), NOW(), 0),
(795104, 'TASK_JOB_REFUND_RETRY_CLOSED', 'REFUND_RETRY', '退款失败重试', 'refund', 'ALL', 'JOB_SAMPLE_REFUND_RETRY_CLOSED', 3, NULL, 50, '人工关闭退款重试样例', NOW(), NOW(), 0),
(795105, 'TASK_JOB_SPLIT_RETRY_WAIT', 'SPLIT_RETRY', '分账失败重试', 'split', 'ALL', 'JOB_SAMPLE_SPLIT_RETRY_WAIT', 0, NOW(), 10, NULL, NOW(), NOW(), 0),
(795106, 'TASK_JOB_RECON_SUCCESS', 'RECONCILIATION_BATCH_CREATE', '资金对账批次生成', 'finance', 'ALL', 'JOB_SAMPLE_RECON_SUCCESS', 0, NULL, 30, NULL, NOW(), NOW(), 0),
(795107, 'TASK_JOB_REPORT_WAIT', 'REPORT_SNAPSHOT_REFRESH', '报表与驾驶舱快照刷新', 'report', 'ALL', 'JOB_SAMPLE_REPORT_WAIT', 0, NOW(), 10, NULL, NOW(), NOW(), 0),
(795108, 'TASK_JOB_TODO_OVERDUE_FAILED', 'TODO_SLA_OVERDUE', '待办SLA超时升级', 'todo', 'ALL', 'JOB_SAMPLE_TODO_OVERDUE_FAILED', 1, DATE_ADD(NOW(), INTERVAL 10 MINUTE), 40, '待办数据锁等待超时', NOW(), NOW(), 0),
(795109, 'TASK_JOB_SUPPLIER_EXPIRY_SUCCESS', 'SUPPLIER_EXPIRY_TODO', '供应商资质合同到期提醒', 'supplier', 'ALL', 'JOB_SAMPLE_SUPPLIER_EXPIRY_SUCCESS', 0, NULL, 30, NULL, NOW(), NOW(), 0),
(795110, 'TASK_JOB_LOW_INVENTORY_WAIT', 'LOW_INVENTORY_CHECK', '低库存补货检测', 'inventory', 'ALL', 'JOB_SAMPLE_LOW_INVENTORY_WAIT', 0, NOW(), 10, NULL, NOW(), NOW(), 0),
(795111, 'TASK_JOB_SEARCH_SYNC_SUCCESS', 'SEARCH_SUGGEST_SYNC', '搜索联想词同步补偿', 'search', 'ALL', 'JOB_SAMPLE_SEARCH_SYNC_SUCCESS', 0, NULL, 30, NULL, NOW(), NOW(), 0);

INSERT INTO ord_order
(id, order_no, user_id, region_id, city_id, station_id, leader_id, pickup_name, pickup_mobile, total_amount, pay_amount, trade_status, pay_status, fulfill_status, expire_time, pay_time, complete_time, cancel_reason, idempotent_key, create_time, update_time, is_deleted)
VALUES
(795001, 'ORD_JOB_TIMEOUT_WAIT', 740001, 440000, 440100, 720001, 730001, '任务测试提货人', '13800004101', 19.9000, 19.9000, 10, 10, 10, DATE_SUB(NOW(), INTERVAL 1 MINUTE), NULL, NULL, NULL, 'idem_job_timeout_wait', NOW(), NOW(), 0),
(795004, 'ORD_JOB_PAY_REMIND', 740001, 440000, 440100, 720001, 730001, '任务测试提货人', '13800004101', 12.9000, 12.9000, 10, 10, 10, DATE_ADD(NOW(), INTERVAL 4 MINUTE), NULL, NULL, NULL, 'idem_job_pay_remind', NOW(), NOW(), 0),
(795011, 'ORD_JOB_CUTOFF_STOCK', 740001, 440000, 440100, 720001, 730001, '任务测试提货人', '13800004101', 19.9000, 19.9000, 20, 30, 20, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 50 MINUTE), NULL, NULL, 'idem_job_cutoff_stock', NOW(), NOW(), 0);

INSERT INTO ord_order_item
(id, order_id, order_no, order_item_no, period_id, product_id, sku_id, supplier_id, warehouse_id, city_id, station_id, product_name, sku_name, sale_price, qty, picked_qty, refunded_qty, item_amount, fulfill_status, after_sale_status, expected_pickup_date, create_time, update_time, is_deleted)
VALUES
(795002, 795001, 'ORD_JOB_TIMEOUT_WAIT', 'ITEM_JOB_TIMEOUT_WAIT', 755002, 751001, 752001, 710001, 50001, 440100, 720001, '测试红富士苹果', '苹果1kg装', 19.9000, 1, 0, 0, 19.9000, 10, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(795005, 795004, 'ORD_JOB_PAY_REMIND', 'ITEM_JOB_PAY_REMIND', 755002, 751002, 752002, 710001, 50001, 440100, 720001, '测试香蕉待审核', '香蕉2kg装', 12.9000, 1, 0, 0, 12.9000, 10, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(795012, 795011, 'ORD_JOB_CUTOFF_STOCK', 'ITEM_JOB_CUTOFF_STOCK', 795010, 751001, 752001, 710001, 50001, 440100, 720001, '测试红富士苹果', '苹果1kg装', 19.9000, 1, 0, 0, 19.9000, 20, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0);

INSERT INTO wms_inventory_lock
(id, lock_no, warehouse_id, inventory_id, sku_id, biz_type, biz_no, lock_qty, release_qty, lock_status, create_time, update_time, is_deleted)
VALUES
(795003, 'LOCK_JOB_TIMEOUT_WAIT', 50001, 783002, 752001, 1, 'ORD_JOB_TIMEOUT_WAIT', 1, 0, 10, NOW(), NOW(), 0);

INSERT INTO sale_publish_period
(id, period_no, period_name, region_id, city_id, warehouse_id, sale_start_time, sale_end_time, actual_cutoff_time, delivery_date, stock_mode, status, publish_account_id, publish_time, create_time, update_time, is_deleted)
VALUES
(795010, 'PERIOD_JOB_CUTOFF_STOCK', '系统任务截团仓库库存团期', 440000, 440100, 50001, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 10 MINUTE), DATE_SUB(NOW(), INTERVAL 5 MINUTE), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 20, 700002, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0),
(795020, 'PERIOD_JOB_CUTOFF_VIRTUAL', '系统任务截团虚拟库存团期', 440000, 440100, 50001, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 10 MINUTE), DATE_SUB(NOW(), INTERVAL 5 MINUTE), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 2, 20, 700002, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0);

INSERT INTO sale_publish_sku
(id, period_id, product_id, sku_id, supplier_id, sale_price, purchase_price, limit_qty, min_buy_qty, planned_stock_qty, sold_qty, locked_qty, production_date, shelf_life_days, status, create_time, update_time, is_deleted)
VALUES
(795021, 795020, 751001, 752001, 710001, 19.9000, 12.0000, 0, 1, 80, 18, 2, CURDATE(), 7, 1, NOW(), NOW(), 0);

INSERT INTO wms_inventory
(id, warehouse_id, zone_id, location_id, sku_id, supplier_id, batch_no, production_date, shelf_life_days, in_stock_qty, locked_qty, available_qty, frozen_qty, inventory_status, version, create_time, update_time, is_deleted)
VALUES
(795030, 50001, 51003, 780003, 752004, 710003, 'BATCH_JOB_LOW_INV', CURDATE(), 180, 3, 0, 3, 0, 20, 1, NOW(), NOW(), 0);

INSERT INTO sup_qualification
(id, qualification_no, supplier_id, qualification_type, certificate_no, valid_start_date, valid_end_date, file_asset_id, audit_status, create_time, update_time, is_deleted)
VALUES
(795040, 'QUAL_JOB_EXPIRING', 710001, 1, 'CERT-JOB-EXPIRING', DATE_SUB(CURDATE(), INTERVAL 300 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 710201, 20, NOW(), NOW(), 0);

INSERT INTO sup_contract
(id, contract_no, supplier_id, contract_name, valid_start_date, valid_end_date, deposit_amount, file_asset_id, status, create_time, update_time, is_deleted)
VALUES
(795041, 'CON_JOB_EXPIRING', 710001, '系统任务到期提醒合同', DATE_SUB(CURDATE(), INTERVAL 300 DAY), DATE_ADD(CURDATE(), INTERVAL 25 DAY), 5000.0000, 710202, 20, NOW(), NOW(), 0);

INSERT INTO op_todo
(id, todo_no, todo_type, source_biz_type, source_biz_no, title, priority, process_role_code, process_account_id, city_id, supplier_id, warehouse_id, status, due_time, create_time, update_time, is_deleted)
VALUES
(795050, 'TODO_JOB_OVERDUE_WAIT', 6, 'order', 'ORD_JOB_TIMEOUT_WAIT', '系统任务待办超时样例', 1, 'city_operator', 700002, 440100, NULL, NULL, 10, DATE_SUB(NOW(), INTERVAL 20 MINUTE), NOW(), NOW(), 0);

INSERT INTO prd_product
(id, product_no, product_name, front_category_id, stat_category_id, supplier_id, main_image_url, image_json, audit_status, sale_status, city_id, create_account_id, create_time, update_time, is_deleted)
VALUES
(795060, 'PRD_JOB_SEARCH_SYNC', '系统任务搜索同步商品', 750001, 750003, 710001, '/mock/product/job-search.jpg', JSON_ARRAY('/mock/product/job-search-1.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0);

INSERT INTO prd_sku
(id, sku_no, product_id, sku_name, barcode, sale_spec_text, base_unit, supplier_id, weight_gram, status, create_time, update_time, is_deleted)
VALUES
(795061, 'SKU_JOB_SEARCH_SYNC', 795060, '系统任务搜索同步SKU', '690000009999', '1份', '份', 710001, 500, 1, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：修正
-- 涉及表：sys_task
-- 日期：2026-06-26
-- 操作人：codex
-- 用途说明：将早期系统任务测试样例编码统一到第一版已开发任务范围，避免非第一版任务误导测试
-- ----------------------------
UPDATE sys_task
SET task_code = 'ORDER_CLOSE_TIMEOUT',
    task_name = '未支付订单超时关闭',
    biz_type = 'order',
    update_time = NOW()
WHERE id = 700701;

UPDATE sys_task
SET task_code = 'SEARCH_SUGGEST_SYNC',
    task_name = '搜索联想词同步补偿',
    biz_type = 'search',
    update_time = NOW()
WHERE id = 700702;

UPDATE sys_task
SET task_code = 'MESSAGE_SEND_RETRY',
    task_name = '消息发送失败重试',
    biz_type = 'message',
    update_time = NOW()
WHERE id = 700703;

UPDATE sys_task
SET task_code = 'REPORT_SNAPSHOT_REFRESH',
    task_name = '报表与驾驶舱快照刷新',
    biz_type = 'report',
    update_time = NOW()
WHERE id = 700705;

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_menu
-- 日期：2026-06-27
-- 操作人：codex
-- 用途说明：补齐运营后台和仓库后台测试角色菜单权限，避免登录后菜单为空或访问后台页面 403
-- ----------------------------
INSERT IGNORE INTO sys_role_menu
(id, role_id, menu_id, create_time, update_time, is_deleted)
SELECT 802001, 20001, 30001, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802001)
UNION ALL SELECT 802002, 20001, 30002, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802002)
UNION ALL SELECT 802003, 20001, 30003, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802003)
UNION ALL SELECT 802004, 20001, 30004, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802004)
UNION ALL SELECT 802005, 20001, 30005, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802005)
UNION ALL SELECT 802006, 20001, 30006, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802006)
UNION ALL SELECT 802007, 20001, 30007, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802007)
UNION ALL SELECT 802008, 20001, 30008, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802008)
UNION ALL SELECT 802009, 20002, 30001, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802009)
UNION ALL SELECT 802010, 20002, 30002, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802010)
UNION ALL SELECT 802011, 20002, 30003, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802011)
UNION ALL SELECT 802012, 20002, 30004, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802012)
UNION ALL SELECT 802013, 20002, 30005, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802013)
UNION ALL SELECT 802014, 20002, 30007, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802014)
UNION ALL SELECT 802015, 20003, 30001, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802015)
UNION ALL SELECT 802016, 20003, 30003, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802016)
UNION ALL SELECT 802017, 20003, 30004, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802017)
UNION ALL SELECT 802018, 20003, 30005, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802018)
UNION ALL SELECT 802019, 20003, 30007, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802019)
UNION ALL SELECT 802020, 20004, 30002, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802020)
UNION ALL SELECT 802021, 20005, 30002, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802021)
UNION ALL SELECT 802022, 20005, 30005, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802022)
UNION ALL SELECT 802023, 20006, 30006, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802023)
UNION ALL SELECT 802024, 20007, 30003, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802024)
UNION ALL SELECT 802025, 20008, 30002, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802025)
UNION ALL SELECT 802026, 20008, 30003, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802026)
UNION ALL SELECT 802027, 20008, 30005, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802027)
UNION ALL SELECT 802028, 21001, 31001, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802028)
UNION ALL SELECT 802029, 21001, 31002, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802029)
UNION ALL SELECT 802030, 21001, 31003, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802030)
UNION ALL SELECT 802031, 21001, 31004, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802031)
UNION ALL SELECT 802032, 21001, 31005, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802032)
UNION ALL SELECT 802033, 21001, 31006, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802033)
UNION ALL SELECT 802034, 21001, 31007, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802034)
UNION ALL SELECT 802035, 21001, 31008, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802035)
UNION ALL SELECT 802036, 21001, 31009, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802036)
UNION ALL SELECT 802037, 21001, 31010, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802037)
UNION ALL SELECT 802038, 21001, 31011, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802038)
UNION ALL SELECT 802039, 21001, 31012, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802039)
UNION ALL SELECT 802040, 21001, 31013, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE id = 802040);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-27
-- 操作人：codex
-- 用途说明：补齐运营后台系统权限菜单只读权限，避免账号、角色、菜单、字典页面 403
-- ----------------------------
INSERT IGNORE INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
SELECT 802101, 20001, 'admin', 'system:account', 'view', '查看账号', 1, 1, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_button WHERE id = 802101)
UNION ALL SELECT 802102, 20001, 'admin', 'system:role', 'view', '查看角色', 1, 1, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_button WHERE id = 802102)
UNION ALL SELECT 802103, 20001, 'admin', 'system:menu', 'view', '查看菜单', 1, 1, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_button WHERE id = 802103)
UNION ALL SELECT 802104, 20001, 'admin', 'system:dict', 'view', '查看字典', 1, 1, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_button WHERE id = 802104)
UNION ALL SELECT 802105, 20002, 'admin', 'system:dict', 'view', '查看字典', 1, 1, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_button WHERE id = 802105)
UNION ALL SELECT 802106, 20003, 'admin', 'system:dict', 'view', '查看字典', 1, 1, NOW(), NOW(), 0 WHERE NOT EXISTS (SELECT 1 FROM sys_role_button WHERE id = 802106);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sup_supplier, sys_account, prd_product, prd_sku, sale_publish_period, sale_publish_sku, ord_cart, ord_order, ord_order_item, ord_fulfillment_track, pay_trade, pay_refund_trade, afs_after_sale, afs_after_sale_item, afs_return_record, pur_purchase_order, pur_purchase_item, pur_delivery_order, pur_delivery_item, wms_inbound_order, wms_inbound_item, wms_receive_record, wms_quality_file, wms_putaway_task, wms_inventory, wms_inventory_lock, wms_inventory_log, wms_wave_batch, wms_pick_task, wms_pick_item, wms_loading_order, wms_loading_item, wms_outbound_order, wms_delivery_order, wms_delivery_station, wms_driver_sign_record, wms_return_handover, wms_stocktake_order, wms_operation_log, fin_account, fin_account_flow, fin_commission_detail, fin_split_flow, fin_withdraw_apply, fin_supplier_settlement, fin_reconciliation_batch, op_todo, op_exception_record, msg_record, rpt_snapshot, rpt_export_task, search_suggest_word
-- 日期：2026-06-27
-- 操作人：codex
-- 用途说明：补充全页面全状态测试数据，覆盖用户端、角色工作台、供应商工作台、仓配H5、运营后台和仓库后台主要列表状态
-- ----------------------------
INSERT IGNORE INTO sup_supplier
(id, supplier_no, supplier_name, city_id, status, audit_status, deposit_required_amount, remark, create_time, update_time, is_deleted)
VALUES
(610001, 'SUP_PAGE_FIXED', '页面固定供应商610001', 440100, 1, 20, 6000.0000, '角色工作台供应商页固定ID测试数据', NOW(), NOW(), 0);

INSERT IGNORE INTO sup_supplier_contact
(id, supplier_id, contact_name, contact_mobile, contact_type, default_flag, status, create_time, update_time, is_deleted)
VALUES
(806001, 610001, '页面供应商业务联系人', '13800006101', 1, 1, 1, NOW(), NOW(), 0),
(806002, 610001, '页面供应商送货联系人', '13800006102', 3, 0, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO sup_qualification
(id, qualification_no, supplier_id, qualification_type, certificate_no, valid_start_date, valid_end_date, file_asset_id, audit_status, create_time, update_time, is_deleted)
VALUES
(806003, 'QUAL_PAGE_SUP_WAIT', 610001, 1, 'CERT-PAGE-WAIT', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 710201, 10, NOW(), NOW(), 0),
(806004, 'QUAL_PAGE_SUP_PASS', 610001, 1, 'CERT-PAGE-PASS', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 710201, 20, NOW(), NOW(), 0),
(806005, 'QUAL_PAGE_SUP_REJECT', 610001, 1, 'CERT-PAGE-REJECT', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 710201, 30, NOW(), NOW(), 0);

INSERT IGNORE INTO sup_contract
(id, contract_no, supplier_id, contract_name, valid_start_date, valid_end_date, deposit_amount, file_asset_id, status, create_time, update_time, is_deleted)
VALUES
(806006, 'CON_PAGE_SUP_WAIT', 610001, '页面供应商待生效合同', DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 6000.0000, 710202, 10, NOW(), NOW(), 0),
(806007, 'CON_PAGE_SUP_ACTIVE', 610001, '页面供应商生效中合同', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 6000.0000, 710202, 20, NOW(), NOW(), 0),
(806008, 'CON_PAGE_SUP_EXPIRED', 610001, '页面供应商已到期合同', DATE_SUB(CURDATE(), INTERVAL 1 YEAR), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 6000.0000, 710202, 30, NOW(), NOW(), 0),
(806009, 'CON_PAGE_SUP_CLOSED', 610001, '页面供应商已解除合同', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 6000.0000, 710202, 40, NOW(), NOW(), 0);

INSERT IGNORE INTO sys_account
(id, account_no, username, mobile, password_hash, account_type, login_portal_code, subject_type, subject_id, status, last_login_time, last_login_ip, remark, create_time, update_time, is_deleted)
VALUES
(806010, 'ACC_PAGE_SUPPLIER_610001', 'test_supplier_610001', '13800006110', '$2a$10$oxgynGL/z8Bad5ExH/or0eNrsHypVvBIttfG7BaymYuYhBint5Rhy', 4, 'role-workbench-h5', 2, 610001, 1, NOW(), '127.0.0.1', '供应商工作台固定ID测试账号', NOW(), NOW(), 0);

INSERT IGNORE INTO sys_account_subject
(id, account_id, subject_type, subject_id, subject_name_snapshot, default_flag, status, create_time, update_time, is_deleted)
VALUES
(806011, 806010, 2, 610001, '页面固定供应商610001', 1, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO sys_account_role
(id, account_id, role_id, create_time, update_time, is_deleted)
VALUES
(806012, 806010, 22003, NOW(), NOW(), 0);

INSERT IGNORE INTO sys_data_scope
(id, scope_code, scope_name, scope_type, scope_object_id, include_child_flag, status, create_time, update_time, is_deleted)
VALUES
(806013, 'SUPPLIER_PAGE_FIXED_610001', '页面固定供应商610001数据', 3, 610001, 0, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO sys_account_data_scope
(id, account_id, scope_id, create_time, update_time, is_deleted)
VALUES
(806014, 806010, 806013, NOW(), NOW(), 0);

INSERT IGNORE INTO prd_product
(id, product_no, product_name, front_category_id, stat_category_id, supplier_id, main_image_url, image_json, audit_status, sale_status, city_id, create_account_id, create_time, update_time, is_deleted)
VALUES
(806020, 'PRD_PAGE_ORANGE', '页面覆盖赣南鲜橙', 750001, 750003, 610001, '/mock/product/page-orange.jpg', JSON_ARRAY('/mock/product/page-orange-1.jpg', '/mock/product/page-orange-2.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(806021, 'PRD_PAGE_GRAPE_WAIT', '页面覆盖待审核葡萄', 750001, 750003, 610001, '/mock/product/page-grape.jpg', JSON_ARRAY('/mock/product/page-grape-1.jpg'), 10, 1, 440100, 700003, NOW(), NOW(), 0),
(806022, 'PRD_PAGE_MELON_STOP', '页面覆盖停售蜜瓜', 750001, 750003, 610001, '/mock/product/page-melon.jpg', JSON_ARRAY('/mock/product/page-melon-1.jpg'), 20, 2, 440100, 700003, NOW(), NOW(), 0);

INSERT IGNORE INTO prd_sku
(id, sku_no, product_id, sku_name, barcode, sale_spec_text, base_unit, supplier_id, weight_gram, status, create_time, update_time, is_deleted)
VALUES
(806030, 'SKU_PAGE_ORANGE_3KG', 806020, '鲜橙3kg装', '690000806030', '3kg/箱', '箱', 610001, 3000, 1, NOW(), NOW(), 0),
(806031, 'SKU_PAGE_GRAPE_1KG', 806021, '葡萄1kg装', '690000806031', '1kg/份', '份', 610001, 1000, 1, NOW(), NOW(), 0),
(806032, 'SKU_PAGE_MELON_1PC', 806022, '蜜瓜1个装', '690000806032', '1个/份', '份', 610001, 1200, 2, NOW(), NOW(), 0);

INSERT IGNORE INTO sale_publish_period
(id, period_no, period_name, region_id, city_id, warehouse_id, sale_start_time, sale_end_time, actual_cutoff_time, delivery_date, stock_mode, status, publish_account_id, publish_time, create_time, update_time, is_deleted)
VALUES
(806040, 'PERIOD_PAGE_DRAFT', '页面覆盖待发布团期', 440000, 440100, 50001, DATE_ADD(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 1, 10, NULL, NULL, NOW(), NOW(), 0),
(806041, 'PERIOD_PAGE_ONLINE', '页面覆盖在线团期', 440000, 440100, 50001, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 10 HOUR), DATE_ADD(NOW(), INTERVAL 10 HOUR), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 20, 700002, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0),
(806042, 'PERIOD_PAGE_OFFLINE', '页面覆盖已下架团期', 440000, 440100, 50001, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), CURDATE(), 1, 30, 700002, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), NOW(), 0),
(806043, 'PERIOD_PAGE_EXPIRED', '页面覆盖已过期团期', 440000, 440100, 50001, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 2, 40, 700002, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), NOW(), 0);

INSERT IGNORE INTO sale_publish_sku
(id, period_id, product_id, sku_id, supplier_id, sale_price, purchase_price, limit_qty, min_buy_qty, planned_stock_qty, sold_qty, locked_qty, production_date, shelf_life_days, status, create_time, update_time, is_deleted)
VALUES
(806050, 806041, 806020, 806030, 610001, 29.9000, 18.0000, 3, 1, 600, 160, 25, CURDATE(), 10, 1, NOW(), NOW(), 0),
(806051, 806041, 806021, 806031, 610001, 16.9000, 10.0000, 5, 1, 300, 40, 5, CURDATE(), 5, 1, NOW(), NOW(), 0),
(806052, 806040, 806022, 806032, 610001, 22.9000, 14.0000, 2, 1, 120, 0, 0, CURDATE(), 7, 2, NOW(), NOW(), 0);

INSERT IGNORE INTO ord_cart
(id, user_id, city_id, station_id, period_id, product_id, sku_id, qty, selected_flag, valid_status, create_time, update_time, is_deleted)
VALUES
(806060, 740001, 440100, 720001, 806041, 806020, 806030, 1, 1, 1, NOW(), NOW(), 0),
(806061, 740001, 440100, 720001, 806041, 806021, 806031, 2, 0, 1, NOW(), NOW(), 0),
(806062, 740001, 440100, 720001, 806040, 806022, 806032, 1, 0, 2, NOW(), NOW(), 0);

INSERT IGNORE INTO ord_order
(id, order_no, user_id, region_id, city_id, station_id, leader_id, pickup_name, pickup_mobile, total_amount, pay_amount, trade_status, pay_status, fulfill_status, expire_time, pay_time, complete_time, cancel_reason, idempotent_key, create_time, update_time, is_deleted)
VALUES
(806100, 'ORD_PAGE_WAIT_PAY', 740001, 440000, 440100, 720001, 730001, '页面测试提货人', '13800004101', 29.9000, 29.9000, 10, 10, 10, DATE_ADD(NOW(), INTERVAL 8 MINUTE), NULL, NULL, NULL, 'idem_page_order_wait_pay', NOW(), NOW(), 0),
(806101, 'ORD_PAGE_PAY_PROCESSING', 740001, 440000, 440100, 720001, 730001, '页面测试提货人', '13800004101', 16.9000, 16.9000, 10, 20, 10, DATE_ADD(NOW(), INTERVAL 6 MINUTE), NULL, NULL, '支付处理中', 'idem_page_order_pay_processing', NOW(), NOW(), 0),
(806102, 'ORD_PAGE_WAIT_INBOUND', 740001, 440000, 440100, 720001, 730001, '页面测试提货人', '13800004101', 29.9000, 29.9000, 20, 30, 30, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 50 MINUTE), NULL, NULL, 'idem_page_order_wait_inbound', NOW(), NOW(), 0),
(806103, 'ORD_PAGE_WAIT_PICK', 740001, 440000, 440100, 720001, 730001, '页面测试提货人', '13800004101', 29.9000, 29.9000, 20, 30, 40, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 50 MINUTE), NULL, NULL, 'idem_page_order_wait_pick', NOW(), NOW(), 0),
(806104, 'ORD_PAGE_WAIT_DELIVERY', 740001, 440000, 440100, 720001, 730001, '页面测试提货人', '13800004101', 29.9000, 29.9000, 20, 30, 50, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 50 MINUTE), NULL, NULL, 'idem_page_order_wait_delivery', NOW(), NOW(), 0),
(806105, 'ORD_PAGE_WAIT_PICKUP_FIXED', 740001, 440000, 440100, 720001, 730001, '页面测试提货人', '13800004101', 29.9000, 29.9000, 20, 30, 60, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 50 MINUTE), NULL, NULL, 'idem_page_order_wait_pickup_fixed', NOW(), NOW(), 0),
(806106, 'ORD_PAGE_PICKED_FIXED', 740001, 440000, 440100, 720001, 730001, '页面测试提货人', '13800004101', 16.9000, 16.9000, 50, 30, 70, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 'idem_page_order_picked_fixed', NOW(), NOW(), 0),
(806107, 'ORD_PAGE_CLOSED_USER', 740001, 440000, 440100, 720001, 730001, '页面测试提货人', '13800004101', 16.9000, 16.9000, 30, 10, 10, DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL, NULL, '用户取消未支付订单', 'idem_page_order_closed_user', NOW(), NOW(), 0),
(806108, 'ORD_PAGE_REFUNDING', 740001, 440000, 440100, 720001, 730001, '页面测试提货人', '13800004101', 29.9000, 29.9000, 40, 40, 20, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, '已支付取消退款中', 'idem_page_order_refunding', NOW(), NOW(), 0),
(806109, 'ORD_PAGE_PART_REFUND', 740001, 440000, 440100, 720001, 730001, '页面测试提货人', '13800004101', 59.8000, 59.8000, 50, 50, 70, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), '部分退款后完成', 'idem_page_order_part_refund', NOW(), NOW(), 0);

INSERT IGNORE INTO ord_order_item
(id, order_id, order_no, order_item_no, period_id, product_id, sku_id, supplier_id, warehouse_id, city_id, station_id, product_name, sku_name, sale_price, qty, picked_qty, refunded_qty, item_amount, fulfill_status, after_sale_status, expected_pickup_date, create_time, update_time, is_deleted)
VALUES
(806200, 806100, 'ORD_PAGE_WAIT_PAY', 'ITEM_PAGE_WAIT_PAY', 806041, 806020, 806030, 610001, 50001, 440100, 720001, '页面覆盖赣南鲜橙', '鲜橙3kg装', 29.9000, 1, 0, 0, 29.9000, 10, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(806201, 806101, 'ORD_PAGE_PAY_PROCESSING', 'ITEM_PAGE_PAY_PROCESSING', 806041, 806021, 806031, 610001, 50001, 440100, 720001, '页面覆盖待审核葡萄', '葡萄1kg装', 16.9000, 1, 0, 0, 16.9000, 10, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(806202, 806102, 'ORD_PAGE_WAIT_INBOUND', 'ITEM_PAGE_WAIT_INBOUND', 806041, 806020, 806030, 610001, 50001, 440100, 720001, '页面覆盖赣南鲜橙', '鲜橙3kg装', 29.9000, 1, 0, 0, 29.9000, 30, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(806203, 806103, 'ORD_PAGE_WAIT_PICK', 'ITEM_PAGE_WAIT_PICK', 806041, 806020, 806030, 610001, 50001, 440100, 720001, '页面覆盖赣南鲜橙', '鲜橙3kg装', 29.9000, 1, 0, 0, 29.9000, 40, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(806204, 806104, 'ORD_PAGE_WAIT_DELIVERY', 'ITEM_PAGE_WAIT_DELIVERY', 806041, 806020, 806030, 610001, 50001, 440100, 720001, '页面覆盖赣南鲜橙', '鲜橙3kg装', 29.9000, 1, 0, 0, 29.9000, 50, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(806205, 806105, 'ORD_PAGE_WAIT_PICKUP_FIXED', 'ITEM_PAGE_WAIT_PICKUP_FIXED', 806041, 806020, 806030, 610001, 50001, 440100, 720001, '页面覆盖赣南鲜橙', '鲜橙3kg装', 29.9000, 1, 0, 0, 29.9000, 60, 0, '2026-06-26', NOW(), NOW(), 0),
(806206, 806106, 'ORD_PAGE_PICKED_FIXED', 'ITEM_PAGE_PICKED_FIXED', 806041, 806021, 806031, 610001, 50001, 440100, 720001, '页面覆盖待审核葡萄', '葡萄1kg装', 16.9000, 1, 1, 0, 16.9000, 70, 0, '2026-06-26', NOW(), NOW(), 0),
(806207, 806107, 'ORD_PAGE_CLOSED_USER', 'ITEM_PAGE_CLOSED_USER', 806041, 806021, 806031, 610001, 50001, 440100, 720001, '页面覆盖待审核葡萄', '葡萄1kg装', 16.9000, 1, 0, 0, 16.9000, 10, 0, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(806208, 806108, 'ORD_PAGE_REFUNDING', 'ITEM_PAGE_REFUNDING', 806041, 806020, 806030, 610001, 50001, 440100, 720001, '页面覆盖赣南鲜橙', '鲜橙3kg装', 29.9000, 1, 0, 0, 29.9000, 20, 30, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW(), 0),
(806209, 806109, 'ORD_PAGE_PART_REFUND', 'ITEM_PAGE_PART_REFUND', 806041, 806020, 806030, 610001, 50001, 440100, 720001, '页面覆盖赣南鲜橙', '鲜橙3kg装', 29.9000, 2, 2, 1, 59.8000, 70, 40, '2026-06-26', NOW(), NOW(), 0);

INSERT IGNORE INTO ord_fulfillment_track
(id, track_no, order_id, order_item_id, node_type, node_name, biz_no, status, remark, city_id, station_id, warehouse_id, create_time, update_time, is_deleted)
VALUES
(806210, 'TRACK_PAGE_WAIT_PAY', 806100, 806200, 1, '待支付', 'ORD_PAGE_WAIT_PAY', 10, '用户端待支付轨迹', 440100, 720001, NULL, NOW(), NOW(), 0),
(806211, 'TRACK_PAGE_WAIT_INBOUND', 806102, 806202, 3, '待入库', 'INB_PAGE_STATUS_20', 30, '采购已送达待入库', 440100, 720001, 50001, NOW(), NOW(), 0),
(806212, 'TRACK_PAGE_WAIT_PICK', 806103, 806203, 4, '待分拣', 'PICK_PAGE_STATUS_10', 40, '仓库待分拣', 440100, 720001, 50001, NOW(), NOW(), 0),
(806213, 'TRACK_PAGE_WAIT_DELIVERY', 806104, 806204, 5, '待配送', 'DELIVERY_PAGE_STATUS_10', 50, '待司机配送', 440100, 720001, 50001, NOW(), NOW(), 0),
(806214, 'TRACK_PAGE_WAIT_PICKUP', 806105, 806205, 6, '待自提', 'DELIVERY_ST_PAGE_ARRIVED', 60, '已到自提点待核销', 440100, 720001, 50001, NOW(), NOW(), 0),
(806215, 'TRACK_PAGE_PICKED', 806106, 806206, 7, '已自提', 'PICKUP_PAGE_DONE', 70, '用户已提货', 440100, 720001, 50001, NOW(), NOW(), 0);

INSERT IGNORE INTO pay_trade
(id, pay_no, order_id, order_no, channel_type, pay_amount, trade_status, channel_trade_no, pay_time, idempotent_key, create_time, update_time, is_deleted)
VALUES
(806220, 'PAY_PAGE_WAIT', 806100, 'ORD_PAGE_WAIT_PAY', 1, 29.9000, 10, NULL, NULL, 'idem_page_pay_wait', NOW(), NOW(), 0),
(806221, 'PAY_PAGE_PROCESSING', 806101, 'ORD_PAGE_PAY_PROCESSING', 1, 16.9000, 20, 'WX_PAGE_PROCESSING', NULL, 'idem_page_pay_processing', NOW(), NOW(), 0),
(806222, 'PAY_PAGE_SUCCESS', 806102, 'ORD_PAGE_WAIT_INBOUND', 1, 29.9000, 30, 'WX_PAGE_SUCCESS', DATE_SUB(NOW(), INTERVAL 50 MINUTE), 'idem_page_pay_success', NOW(), NOW(), 0),
(806223, 'PAY_PAGE_FAILED', 806101, 'ORD_PAGE_PAY_PROCESSING', 1, 16.9000, 40, 'WX_PAGE_FAILED', NULL, 'idem_page_pay_failed', NOW(), NOW(), 0),
(806224, 'PAY_PAGE_CLOSED', 806107, 'ORD_PAGE_CLOSED_USER', 1, 16.9000, 50, NULL, NULL, 'idem_page_pay_closed', NOW(), NOW(), 0);

INSERT IGNORE INTO afs_after_sale
(id, after_sale_no, order_id, order_no, user_id, city_id, station_id, after_sale_type, status, apply_reason, refund_amount, responsibility_type, create_time, update_time, is_deleted)
VALUES
(806230, 'AFS_PAGE_WAIT_AUDIT', 806105, 'ORD_PAGE_WAIT_PICKUP_FIXED', 740001, 440100, 720001, 1, 10, '页面待审核售后', 29.9000, 1, NOW(), NOW(), 0),
(806231, 'AFS_PAGE_WAIT_RETURN', 806106, 'ORD_PAGE_PICKED_FIXED', 740001, 440100, 720001, 2, 20, '页面待退货售后', 16.9000, 2, NOW(), NOW(), 0),
(806232, 'AFS_PAGE_WAIT_REFUND', 806108, 'ORD_PAGE_REFUNDING', 740001, 440100, 720001, 1, 30, '页面待退款售后', 29.9000, 3, NOW(), NOW(), 0),
(806233, 'AFS_PAGE_REFUND_SUCCESS', 806109, 'ORD_PAGE_PART_REFUND', 740001, 440100, 720001, 1, 40, '页面退款成功售后', 29.9000, 1, NOW(), NOW(), 0),
(806234, 'AFS_PAGE_REFUND_FAILED', 806109, 'ORD_PAGE_PART_REFUND', 740001, 440100, 720001, 1, 50, '页面退款失败售后', 16.9000, 2, NOW(), NOW(), 0),
(806235, 'AFS_PAGE_REJECTED', 806106, 'ORD_PAGE_PICKED_FIXED', 740001, 440100, 720001, 2, 60, '页面售后驳回', 16.9000, 1, NOW(), NOW(), 0),
(806236, 'AFS_PAGE_CANCELLED', 806105, 'ORD_PAGE_WAIT_PICKUP_FIXED', 740001, 440100, 720001, 1, 70, '页面用户撤销售后', 29.9000, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO afs_after_sale_item
(id, after_sale_id, order_item_id, sku_id, supplier_id, apply_qty, approved_qty, refund_amount, create_time, update_time, is_deleted)
VALUES
(806240, 806230, 806205, 806030, 610001, 1, 0, 29.9000, NOW(), NOW(), 0),
(806241, 806231, 806206, 806031, 610001, 1, 1, 16.9000, NOW(), NOW(), 0),
(806242, 806232, 806208, 806030, 610001, 1, 1, 29.9000, NOW(), NOW(), 0),
(806243, 806233, 806209, 806030, 610001, 1, 1, 29.9000, NOW(), NOW(), 0),
(806244, 806234, 806209, 806031, 610001, 1, 1, 16.9000, NOW(), NOW(), 0);

INSERT IGNORE INTO pay_refund_trade
(id, refund_no, pay_no, order_id, after_sale_id, refund_amount, refund_status, channel_refund_no, refund_time, fail_reason, idempotent_key, create_time, update_time, is_deleted)
VALUES
(806250, 'RF_PAGE_WAIT', 'PAY_PAGE_SUCCESS', 806105, 806230, 29.9000, 10, NULL, NULL, NULL, 'idem_page_refund_wait', NOW(), NOW(), 0),
(806251, 'RF_PAGE_PROCESSING', 'PAY_PAGE_SUCCESS', 806108, 806232, 29.9000, 20, 'WX_PAGE_RF_PROCESSING', NULL, NULL, 'idem_page_refund_processing', NOW(), NOW(), 0),
(806252, 'RF_PAGE_SUCCESS', 'PAY_PAGE_SUCCESS', 806109, 806233, 29.9000, 30, 'WX_PAGE_RF_SUCCESS', NOW(), NULL, 'idem_page_refund_success', NOW(), NOW(), 0),
(806253, 'RF_PAGE_FAILED', 'PAY_PAGE_SUCCESS', 806109, 806234, 16.9000, 40, 'WX_PAGE_RF_FAILED', NULL, '页面测试渠道失败', 'idem_page_refund_failed', NOW(), NOW(), 0);

INSERT IGNORE INTO afs_return_record
(id, return_no, after_sale_id, order_item_id, sku_id, station_id, warehouse_id, return_qty, return_status, proof_file_json, create_time, update_time, is_deleted)
VALUES
(806260, 'AFS_RET_PAGE_WAIT', 806231, 806206, 806031, 720001, 50001, 1, 10, JSON_ARRAY('/mock/return/page-wait.jpg'), NOW(), NOW(), 0),
(806261, 'AFS_RET_PAGE_DONE', 806233, 806209, 806030, 720001, 50001, 1, 20, JSON_ARRAY('/mock/return/page-done.jpg'), NOW(), NOW(), 0),
(806262, 'AFS_RET_PAGE_CANCEL', 806235, 806206, 806031, 720001, 50001, 1, 30, JSON_ARRAY('/mock/return/page-cancel.jpg'), NOW(), NOW(), 0);

INSERT IGNORE INTO usr_user_favorite
(id, user_id, product_id, sku_id, station_id, status, create_time, update_time, is_deleted)
VALUES
(806270, 740001, 806020, 806030, 720001, 1, NOW(), NOW(), 0),
(806271, 740001, 806021, 806031, 720001, 2, NOW(), NOW(), 0);

INSERT IGNORE INTO usr_browse_history
(id, user_id, product_id, sku_id, station_id, browse_time, create_time, update_time, is_deleted)
VALUES
(806272, 740001, 806020, 806030, 720001, DATE_SUB(NOW(), INTERVAL 10 MINUTE), NOW(), NOW(), 0),
(806273, 740001, 806021, 806031, 720001, DATE_SUB(NOW(), INTERVAL 20 MINUTE), NOW(), NOW(), 0);

INSERT IGNORE INTO usr_comment
(id, comment_no, user_id, order_id, order_item_id, product_id, sku_id, station_id, score, content, image_json, responsibility_type, status, city_id, create_time, update_time, is_deleted)
VALUES
(806280, 'CMT_PAGE_SHOW_GOOD', 740001, 806106, 806206, 806020, 806030, 720001, 5, '页面覆盖评价：鲜橙很甜，提货顺利', JSON_ARRAY('/mock/comment/page-good.jpg'), 1, 1, 440100, NOW(), NOW(), 0),
(806281, 'CMT_PAGE_SHOW_NORMAL', 740001, 806109, 806209, 806020, 806030, 720001, 4, '页面覆盖评价：包装完整', JSON_ARRAY('/mock/comment/page-normal.jpg'), 1, 1, 440100, NOW(), NOW(), 0),
(806282, 'CMT_PAGE_HIDE', 740001, 806108, 806208, 806020, 806030, 720001, 2, '页面覆盖隐藏评价', NULL, 2, 2, 440100, NOW(), NOW(), 0),
(806283, 'CMT_PAGE_DELETE', 740001, 806107, 806207, 806021, 806031, 720001, 1, '页面覆盖删除评价', NULL, 3, 3, 440100, NOW(), NOW(), 0);

INSERT IGNORE INTO pur_purchase_order
(id, purchase_no, source_type, period_id, supplier_id, warehouse_id, region_id, city_id, delivery_date, purchase_status, audit_status, create_account_id, create_reason, expected_arrival_time, create_time, update_time, is_deleted)
VALUES
(806300, 'PUR_PAGE_610_WAIT_AUDIT', 1, 806041, 610001, 50001, 440000, 440100, CURDATE(), 10, 10, 700003, '供应商工作台待审核', DATE_ADD(NOW(), INTERVAL 3 HOUR), NOW(), NOW(), 0),
(806301, 'PUR_PAGE_610_AUDIT_PASS', 2, 806041, 610001, 50001, 440000, 440100, CURDATE(), 20, 20, 700003, '供应商工作台审核通过', DATE_ADD(NOW(), INTERVAL 4 HOUR), NOW(), NOW(), 0),
(806302, 'PUR_PAGE_610_WAIT_DELIVERY', 2, 806041, 610001, 50001, 440000, 440100, CURDATE(), 30, 20, 700003, '供应商工作台待发货', DATE_ADD(NOW(), INTERVAL 5 HOUR), NOW(), NOW(), 0),
(806303, 'PUR_PAGE_610_DELIVERING', 4, 806041, 610001, 50001, 440000, 440100, CURDATE(), 40, 20, 806010, '供应商工作台送货中', DATE_ADD(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0),
(806304, 'PUR_PAGE_610_RECEIVED', 5, 806041, 610001, 50001, 440000, 440100, CURDATE(), 50, 20, 700003, '供应商工作台已收货', DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0);

INSERT IGNORE INTO pur_purchase_item
(id, purchase_id, purchase_no, product_id, sku_id, supplier_id, warehouse_id, apply_qty, purchase_qty, received_qty, rejected_qty, purchase_price, status, create_time, update_time, is_deleted)
VALUES
(806310, 806300, 'PUR_PAGE_610_WAIT_AUDIT', 806020, 806030, 610001, 50001, 120, 120, 0, 0, 18.0000, 1, NOW(), NOW(), 0),
(806311, 806301, 'PUR_PAGE_610_AUDIT_PASS', 806021, 806031, 610001, 50001, 90, 90, 0, 0, 10.0000, 1, NOW(), NOW(), 0),
(806312, 806302, 'PUR_PAGE_610_WAIT_DELIVERY', 806020, 806030, 610001, 50001, 160, 160, 0, 0, 18.0000, 1, NOW(), NOW(), 0),
(806313, 806303, 'PUR_PAGE_610_DELIVERING', 806020, 806030, 610001, 50001, 180, 180, 0, 0, 18.0000, 1, NOW(), NOW(), 0),
(806314, 806304, 'PUR_PAGE_610_RECEIVED', 806020, 806030, 610001, 50001, 200, 200, 190, 10, 18.0000, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO pur_delivery_order
(id, delivery_no, purchase_id, purchase_no, supplier_id, warehouse_id, expected_arrival_time, arrival_proof_json, contact_name, contact_mobile, vehicle_no, status, create_time, update_time, is_deleted)
VALUES
(806320, 'PDEL_PAGE_610_WAIT', 806302, 'PUR_PAGE_610_WAIT_DELIVERY', 610001, 50001, DATE_ADD(CURDATE(), INTERVAL 10 HOUR), NULL, '页面供应商送货联系人', '13800006102', '粤A-P6101', 10, NOW(), NOW(), 0),
(806321, 'PDEL_PAGE_610_ONROAD', 806303, 'PUR_PAGE_610_DELIVERING', 610001, 50001, DATE_ADD(CURDATE(), INTERVAL 11 HOUR), JSON_OBJECT('photo', '/mock/pur/page-onroad.jpg'), '页面供应商送货联系人', '13800006102', '粤A-P6102', 20, NOW(), NOW(), 0),
(806322, 'PDEL_PAGE_610_ARRIVED', 806303, 'PUR_PAGE_610_DELIVERING', 610001, 50001, DATE_ADD(CURDATE(), INTERVAL 12 HOUR), JSON_OBJECT('photo', '/mock/pur/page-arrived.jpg'), '页面供应商送货联系人', '13800006102', '粤A-P6103', 30, NOW(), NOW(), 0),
(806323, 'PDEL_PAGE_610_RECEIVED', 806304, 'PUR_PAGE_610_RECEIVED', 610001, 50001, DATE_SUB(NOW(), INTERVAL 2 HOUR), JSON_OBJECT('photo', '/mock/pur/page-received.jpg'), '页面供应商送货联系人', '13800006102', '粤A-P6104', 40, NOW(), NOW(), 0),
(806324, 'PDEL_PAGE_610_CLOSED', 806300, 'PUR_PAGE_610_WAIT_AUDIT', 610001, 50001, DATE_ADD(CURDATE(), INTERVAL 1 DAY), NULL, '页面供应商送货联系人', '13800006102', '粤A-P6105', 50, NOW(), NOW(), 0);

INSERT IGNORE INTO pur_delivery_item
(id, delivery_id, purchase_item_id, sku_id, delivery_qty, received_qty, diff_qty, create_time, update_time, is_deleted)
VALUES
(806330, 806320, 806312, 806030, 160, 0, 0, NOW(), NOW(), 0),
(806331, 806321, 806313, 806030, 180, 0, 0, NOW(), NOW(), 0),
(806332, 806322, 806313, 806030, 180, 0, 0, NOW(), NOW(), 0),
(806333, 806323, 806314, 806030, 200, 190, 10, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inbound_order
(id, inbound_no, source_type, source_no, purchase_id, delivery_id, supplier_id, warehouse_id, status, receive_account_id, receive_time, create_time, update_time, is_deleted)
VALUES
(806400, 'INB_PAGE_STATUS_10', 1, 'PDEL_PAGE_610_WAIT', 806302, 806320, 610001, 50001, 10, NULL, NULL, NOW(), NOW(), 0),
(806401, 'INB_PAGE_STATUS_20', 1, 'PDEL_PAGE_610_ARRIVED', 806303, 806322, 610001, 50001, 20, NULL, NULL, NOW(), NOW(), 0),
(806402, 'INB_PAGE_STATUS_30', 1, 'PDEL_PAGE_610_ARRIVED', 806303, 806322, 610001, 50001, 30, 700008, NULL, NOW(), NOW(), 0),
(806403, 'INB_PAGE_STATUS_40', 1, 'PDEL_PAGE_610_RECEIVED', 806304, 806323, 610001, 50001, 40, 700008, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW(), 0),
(806404, 'INB_PAGE_STATUS_50', 1, 'PDEL_PAGE_610_RECEIVED', 806304, 806323, 610001, 50001, 50, 700008, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0),
(806405, 'INB_PAGE_STATUS_60', 1, 'PDEL_PAGE_610_RECEIVED', 806304, 806323, 610001, 50001, 60, 700008, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inbound_item
(id, inbound_id, sku_id, supplier_id, expected_qty, received_qty, rejected_qty, damaged_qty, batch_no, production_date, shelf_life_days, diff_reason, create_time, update_time, is_deleted)
VALUES
(806410, 806400, 806030, 610001, 160, 0, 0, 0, 'BATCH_PAGE_INB_10', CURDATE(), 10, NULL, NOW(), NOW(), 0),
(806411, 806401, 806030, 610001, 180, 0, 0, 0, 'BATCH_PAGE_INB_20', CURDATE(), 10, NULL, NOW(), NOW(), 0),
(806412, 806402, 806030, 610001, 180, 80, 0, 0, 'BATCH_PAGE_INB_30', CURDATE(), 10, '收货中', NOW(), NOW(), 0),
(806413, 806403, 806030, 610001, 200, 190, 5, 5, 'BATCH_PAGE_INB_40', CURDATE(), 10, '少收10份', NOW(), NOW(), 0),
(806414, 806405, 806030, 610001, 200, 190, 5, 5, 'BATCH_PAGE_INB_60', CURDATE(), 10, '已上架样例', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_receive_record
(id, receive_no, inbound_id, inbound_item_id, sku_id, receive_qty, reject_qty, diff_qty, receive_account_id, quality_status, remark, create_time, update_time, is_deleted)
VALUES
(806420, 'RCV_PAGE_PASS', 806403, 806413, 806030, 190, 0, 10, 700008, 1, '页面质检合格', NOW(), NOW(), 0),
(806421, 'RCV_PAGE_PART', 806402, 806412, 806030, 80, 0, 100, 700008, 2, '页面部分收货', NOW(), NOW(), 0),
(806422, 'RCV_PAGE_REJECT', 806403, 806413, 806030, 0, 5, 5, 700008, 3, '页面拒收破损', NOW(), NOW(), 0),
(806423, 'RCV_PAGE_CONFIRM', 806401, 806411, 806030, 0, 0, 0, 700008, 4, '页面待确认', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_quality_file
(id, quality_no, inbound_id, sku_id, quality_result, file_asset_id, remark, create_time, update_time, is_deleted)
VALUES
(806430, 'QUAL_FILE_PAGE_PASS', 806403, 806030, 1, 710204, '页面合格质检图片', NOW(), NOW(), 0),
(806431, 'QUAL_FILE_PAGE_PART', 806402, 806030, 2, 710204, '页面部分合格质检图片', NOW(), NOW(), 0),
(806432, 'QUAL_FILE_PAGE_REJECT', 806403, 806030, 3, 710204, '页面拒收质检图片', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_putaway_task
(id, putaway_no, inbound_id, sku_id, warehouse_id, from_zone_id, target_zone_id, target_location_id, putaway_qty, actual_qty, status, assign_account_id, complete_time, create_time, update_time, is_deleted)
VALUES
(806440, 'PUT_PAGE_STATUS_10', 806403, 806030, 50001, 51001, 51003, 780003, 50, 0, 10, 700008, NULL, NOW(), NOW(), 0),
(806441, 'PUT_PAGE_STATUS_20', 806404, 806030, 50001, 51002, 51003, 780003, 60, 20, 20, 700008, NULL, NOW(), NOW(), 0),
(806442, 'PUT_PAGE_STATUS_30', 806405, 806030, 50001, 51002, 51003, 780003, 70, 70, 30, 700008, NOW(), NOW(), NOW(), 0),
(806443, 'PUT_PAGE_STATUS_40', 806401, 806030, 50001, 51001, 51003, 780003, 30, 0, 40, 700008, NULL, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inventory
(id, warehouse_id, zone_id, location_id, sku_id, supplier_id, batch_no, production_date, shelf_life_days, in_stock_qty, locked_qty, available_qty, frozen_qty, inventory_status, version, create_time, update_time, is_deleted)
VALUES
(806450, 50001, 51002, 780002, 806030, 610001, 'BATCH_PAGE_INV_10', CURDATE(), 10, 50, 0, 0, 0, 10, 1, NOW(), NOW(), 0),
(806451, 50001, 51003, 780003, 806030, 610001, 'BATCH_PAGE_INV_20', CURDATE(), 10, 200, 20, 180, 0, 20, 1, NOW(), NOW(), 0),
(806452, 50001, 51004, 780004, 806030, 610001, 'BATCH_PAGE_INV_30', CURDATE(), 10, 40, 10, 30, 0, 30, 1, NOW(), NOW(), 0),
(806453, 50001, 51006, 780006, 806031, 610001, 'BATCH_PAGE_INV_40', CURDATE(), 5, 20, 0, 20, 0, 40, 1, NOW(), NOW(), 0),
(806454, 50001, 51001, 780001, 806031, 610001, 'BATCH_PAGE_INV_50', CURDATE(), 5, 10, 0, 0, 10, 50, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inventory_lock
(id, lock_no, warehouse_id, inventory_id, sku_id, biz_type, biz_no, lock_qty, release_qty, lock_status, create_time, update_time, is_deleted)
VALUES
(806460, 'LOCK_PAGE_STATUS_10', 50001, 806451, 806030, 1, 'ORD_PAGE_WAIT_PICK', 10, 0, 10, NOW(), NOW(), 0),
(806461, 'LOCK_PAGE_STATUS_20', 50001, 806451, 806030, 1, 'ORD_PAGE_CLOSED_USER', 8, 8, 20, NOW(), NOW(), 0),
(806462, 'LOCK_PAGE_STATUS_30', 50001, 806451, 806030, 2, 'WAVE_PAGE_STATUS_40', 12, 0, 30, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inventory_log
(id, log_no, warehouse_id, zone_id, location_id, sku_id, batch_no, change_type, before_qty, change_qty, after_qty, biz_type, biz_no, operator_account_id, remark, create_time, update_time, is_deleted)
VALUES
(806470, 'INV_LOG_PAGE_INBOUND', 50001, 51001, 780001, 806030, 'BATCH_PAGE_INV_10', 1, 0, 50, 50, 'inbound', 'INB_PAGE_STATUS_40', 700008, '页面入库流水', NOW(), NOW(), 0),
(806471, 'INV_LOG_PAGE_PUTAWAY', 50001, 51003, 780003, 806030, 'BATCH_PAGE_INV_20', 2, 50, 150, 200, 'putaway', 'PUT_PAGE_STATUS_30', 700008, '页面上架流水', NOW(), NOW(), 0),
(806472, 'INV_LOG_PAGE_LOCK', 50001, 51003, 780003, 806030, 'BATCH_PAGE_INV_20', 3, 200, -20, 180, 'lock', 'LOCK_PAGE_STATUS_10', 700009, '页面锁定流水', NOW(), NOW(), 0),
(806473, 'INV_LOG_PAGE_RELEASE', 50001, 51003, 780003, 806030, 'BATCH_PAGE_INV_20', 4, 180, 8, 188, 'release', 'LOCK_PAGE_STATUS_20', 700009, '页面释放流水', NOW(), NOW(), 0),
(806474, 'INV_LOG_PAGE_OUTBOUND', 50001, 51003, 780003, 806030, 'BATCH_PAGE_INV_20', 5, 188, -12, 176, 'outbound', 'OUT_PAGE_STATUS_20', 700010, '页面出库扣减流水', NOW(), NOW(), 0),
(806475, 'INV_LOG_PAGE_RETURN', 50001, 51006, 780006, 806031, 'BATCH_PAGE_INV_40', 6, 0, 20, 20, 'return', 'RET_PAGE_STATUS_20', 700011, '页面回仓流水', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_wave_batch
(id, wave_no, period_id, warehouse_id, line_id, delivery_date, required_qty, locked_qty, status, create_account_id, create_time, update_time, is_deleted)
VALUES
(806480, 'WAVE_PAGE_STATUS_10', 806041, 50001, 780201, CURDATE(), 60, 0, 10, 700007, NOW(), NOW(), 0),
(806481, 'WAVE_PAGE_STATUS_20', 806041, 50001, 780201, CURDATE(), 70, 70, 20, 700007, NOW(), NOW(), 0),
(806482, 'WAVE_PAGE_STATUS_30', 806041, 50001, 780201, CURDATE(), 80, 80, 30, 700007, NOW(), NOW(), 0),
(806483, 'WAVE_PAGE_STATUS_40', 806041, 50001, 780201, CURDATE(), 90, 90, 40, 700007, NOW(), NOW(), 0),
(806484, 'WAVE_PAGE_STATUS_50', 806041, 50001, 780201, CURDATE(), 100, 95, 50, 700007, NOW(), NOW(), 0),
(806485, 'WAVE_PAGE_STATUS_60', 806041, 50001, 780201, CURDATE(), 50, 0, 60, 700007, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_pick_task
(id, pick_no, wave_id, warehouse_id, line_id, assign_account_id, required_qty, actual_qty, shortage_qty, status, complete_time, create_time, update_time, is_deleted)
VALUES
(806490, 'PICK_PAGE_STATUS_10', 806480, 50001, 780201, 700009, 60, 0, 0, 10, NULL, NOW(), NOW(), 0),
(806491, 'PICK_PAGE_STATUS_20', 806481, 50001, 780201, 700009, 70, 30, 0, 20, NULL, NOW(), NOW(), 0),
(806492, 'PICK_PAGE_STATUS_30', 806483, 50001, 780201, 700009, 90, 90, 0, 30, NOW(), NOW(), NOW(), 0),
(806493, 'PICK_PAGE_STATUS_40', 806484, 50001, 780201, 700009, 100, 95, 5, 40, NOW(), NOW(), NOW(), 0);

INSERT IGNORE INTO wms_pick_item
(id, pick_id, sku_id, inventory_id, location_id, required_qty, actual_qty, shortage_qty, diff_reason, create_time, update_time, is_deleted)
VALUES
(806500, 806490, 806030, 806451, 780003, 60, 0, 0, NULL, NOW(), NOW(), 0),
(806501, 806491, 806030, 806451, 780003, 70, 30, 0, NULL, NOW(), NOW(), 0),
(806502, 806492, 806030, 806451, 780003, 90, 90, 0, NULL, NOW(), NOW(), 0),
(806503, 806493, 806030, 806451, 780003, 100, 95, 5, '页面测试少拣5份', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_loading_order
(id, loading_no, wave_id, warehouse_id, line_id, driver_account_id, delivery_date, required_qty, actual_qty, status, load_account_id, load_time, outbound_account_id, outbound_time, create_time, update_time, is_deleted)
VALUES
(806510, 'LOAD_PAGE_STATUS_10', 806480, 50001, 780201, 700011, '2026-06-26', 60, 0, 10, NULL, NULL, NULL, NULL, NOW(), NOW(), 0),
(806511, 'LOAD_PAGE_STATUS_20', 806481, 50001, 780201, 700011, '2026-06-26', 70, 70, 20, 700010, DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL, NULL, NOW(), NOW(), 0),
(806512, 'LOAD_PAGE_STATUS_30', 806483, 50001, 780201, 700011, '2026-06-26', 90, 90, 30, 700010, DATE_SUB(NOW(), INTERVAL 2 HOUR), 700007, DATE_SUB(NOW(), INTERVAL 90 MINUTE), NOW(), NOW(), 0);

INSERT IGNORE INTO wms_loading_item
(id, loading_id, station_id, sku_id, required_qty, actual_qty, outbound_qty, create_time, update_time, is_deleted)
VALUES
(806520, 806510, 720001, 806030, 30, 0, 0, NOW(), NOW(), 0),
(806521, 806511, 720001, 806030, 40, 40, 0, NOW(), NOW(), 0),
(806522, 806512, 720001, 806030, 50, 50, 50, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_outbound_order
(id, outbound_no, loading_id, warehouse_id, line_id, outbound_qty, status, outbound_account_id, outbound_time, idempotent_key, create_time, update_time, is_deleted)
VALUES
(806530, 'OUT_PAGE_STATUS_10', 806511, 50001, 780201, 0, 10, 700007, NOW(), 'idem_page_out_10', NOW(), NOW(), 0),
(806531, 'OUT_PAGE_STATUS_20', 806512, 50001, 780201, 90, 20, 700007, DATE_SUB(NOW(), INTERVAL 90 MINUTE), 'idem_page_out_20', NOW(), NOW(), 0),
(806532, 'OUT_PAGE_STATUS_30', 806510, 50001, 780201, 0, 30, 700007, NOW(), 'idem_page_out_30', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_delivery_order
(id, delivery_no, outbound_id, loading_id, warehouse_id, line_id, driver_account_id, delivery_date, status, start_time, complete_time, create_time, update_time, is_deleted)
VALUES
(806540, 'DELIVERY_PAGE_STATUS_10', 806530, 806511, 50001, 780201, 700011, '2026-06-26', 10, NULL, NULL, NOW(), NOW(), 0),
(806541, 'DELIVERY_PAGE_STATUS_20', 806531, 806512, 50001, 780201, 700011, '2026-06-26', 20, DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL, NOW(), NOW(), 0),
(806542, 'DELIVERY_PAGE_STATUS_30', 806531, 806512, 50001, 780201, 700011, '2026-06-26', 30, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW(), 0),
(806543, 'DELIVERY_PAGE_STATUS_40', 806531, 806512, 50001, 780201, 700011, '2026-06-26', 40, DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_delivery_station
(id, delivery_id, station_id, delivery_sort, status, arrive_time, complete_time, remark, create_time, update_time, is_deleted)
VALUES
(806550, 806540, 720001, 1, 10, NULL, NULL, '页面待配送', NOW(), NOW(), 0),
(806551, 806541, 720001, 1, 20, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NULL, '页面已到达待交接', NOW(), NOW(), 0),
(806552, 806542, 720001, 1, 30, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 20 MINUTE), '页面交接完成', NOW(), NOW(), 0),
(806553, 806543, 720001, 1, 40, NULL, NULL, '页面无法送达', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_driver_sign_record
(id, sign_no, delivery_id, station_id, driver_account_id, sign_type, longitude, latitude, remark, create_time, update_time, is_deleted)
VALUES
(806560, 'SIGN_PAGE_ARRIVE', 806541, 720001, 700011, 1, 113.320500, 23.120500, '页面司机到达', NOW(), NOW(), 0),
(806561, 'SIGN_PAGE_DONE', 806542, 720001, 700011, 2, 113.320600, 23.120600, '页面交接完成', NOW(), NOW(), 0),
(806562, 'SIGN_PAGE_FAIL', 806543, 720001, 700011, 3, 113.320700, 23.120700, '页面无法送达', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_return_handover
(id, return_no, delivery_id, station_id, sku_id, warehouse_id, return_qty, reason_type, driver_account_id, supervisor_account_id, status, confirm_time, remark, create_time, update_time, is_deleted)
VALUES
(806570, 'RET_PAGE_STATUS_10', 806543, 720001, 806030, 50001, 3, 1, 700011, NULL, 10, NULL, '页面待回仓确认', NOW(), NOW(), 0),
(806571, 'RET_PAGE_STATUS_20', 806542, 720001, 806031, 50001, 2, 2, 700011, 700007, 20, NOW(), '页面已回仓确认', NOW(), NOW(), 0),
(806572, 'RET_PAGE_STATUS_30', 806543, 720001, 806030, 50001, 1, 3, 700011, NULL, 30, NULL, '页面待主管处理', NOW(), NOW(), 0),
(806573, 'RET_PAGE_STATUS_40', 806542, 720001, 806031, 50001, 1, 1, 700011, 700007, 40, DATE_SUB(NOW(), INTERVAL 30 MINUTE), '页面关闭', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_stocktake_order
(id, stocktake_no, warehouse_id, sku_id, book_qty, actual_qty, diff_qty, status, create_account_id, audit_account_id, create_time, update_time, is_deleted)
VALUES
(806580, 'STK_PAGE_STATUS_10', 50001, 806030, 200, 0, 0, 10, 700007, NULL, NOW(), NOW(), 0),
(806581, 'STK_PAGE_STATUS_20', 50001, 806030, 200, 198, -2, 20, 700007, NULL, NOW(), NOW(), 0),
(806582, 'STK_PAGE_STATUS_30', 50001, 806030, 200, 198, -2, 30, 700007, 700007, NOW(), NOW(), 0),
(806583, 'STK_PAGE_STATUS_40', 50001, 806031, 80, 80, 0, 40, 700007, 700007, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_operation_log
(id, log_no, warehouse_id, account_id, role_code, module_code, action_code, biz_type, biz_no, before_status, after_status, reason, result_status, create_time, update_time, is_deleted)
VALUES
(806590, 'WMS_LOG_PAGE_RECEIVE', 50001, 700008, 'wms_receiver', 'wms_inbound', 'receive', 'inbound', 'INB_PAGE_STATUS_40', 20, 40, '页面收货成功', 1, NOW(), NOW(), 0),
(806591, 'WMS_LOG_PAGE_PICK_DIFF', 50001, 700009, 'wms_picker', 'wms_picking', 'pick_diff', 'pick', 'PICK_PAGE_STATUS_40', 20, 40, '页面拣货差异', 1, NOW(), NOW(), 0),
(806592, 'WMS_LOG_PAGE_OUT_FAIL', 50001, 700010, 'wms_loader', 'wms_loading', 'outbound', 'loading', 'LOAD_PAGE_STATUS_20', 20, 20, '页面出库失败', 2, NOW(), NOW(), 0);

INSERT IGNORE INTO fin_account
(id, account_no, subject_type, subject_id, available_amount, frozen_amount, deposit_amount, status, create_time, update_time, is_deleted)
VALUES
(806600, 'FIN_ACC_PAGE_SUPPLIER_610001', 1, 610001, 18000.0000, 1200.0000, 6000.0000, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO fin_account_flow
(id, flow_no, account_id, subject_type, subject_id, flow_type, direction, amount, before_amount, after_amount, biz_no, remark, create_time, update_time, is_deleted)
VALUES
(806610, 'ACC_FLOW_PAGE_COMMISSION_IN', 790002, 2, 720001, 1, 1, 8.0000, 800.0000, 808.0000, 'COM_PAGE_STATION_WAIT', '页面佣金入账', NOW(), NOW(), 0),
(806611, 'ACC_FLOW_PAGE_WITHDRAW_FREEZE', 790002, 2, 720001, 3, 2, 50.0000, 808.0000, 758.0000, 'WD_PAGE_WAIT', '页面提现冻结', NOW(), NOW(), 0),
(806612, 'ACC_FLOW_PAGE_SUPPLIER_SETTLE', 806600, 1, 610001, 1, 1, 1000.0000, 18000.0000, 19000.0000, 'SETTLE_PAGE_610_ACCOUNTED', '页面供应商结算入账', NOW(), NOW(), 0);

INSERT IGNORE INTO fin_commission_detail
(id, commission_no, order_id, order_item_id, subject_type, subject_id, supplier_id, station_id, commission_amount, status, create_time, update_time, is_deleted)
VALUES
(806620, 'COM_PAGE_STATION_WAIT', 806105, 806205, 1, 720001, 610001, 720001, 4.0000, 10, NOW(), NOW(), 0),
(806621, 'COM_PAGE_LEADER_WAIT_SPLIT', 806106, 806206, 2, 730001, 610001, 720001, 5.0000, 20, NOW(), NOW(), 0),
(806622, 'COM_PAGE_SUPPLIER_SPLIT', 806109, 806209, 3, 610001, 610001, 720001, 12.0000, 30, NOW(), NOW(), 0),
(806623, 'COM_PAGE_STATION_DEDUCT', 806108, 806208, 1, 720001, 610001, 720001, 3.0000, 40, NOW(), NOW(), 0);

INSERT IGNORE INTO fin_split_flow
(id, split_no, commission_id, order_id, subject_type, subject_id, split_amount, split_status, fail_reason, idempotent_key, create_time, update_time, is_deleted)
VALUES
(806630, 'SPLIT_PAGE_WAIT', 806621, 806106, 2, 730001, 5.0000, 10, NULL, 'idem_page_split_wait', NOW(), NOW(), 0),
(806631, 'SPLIT_PAGE_PROCESSING', 806621, 806106, 2, 730001, 5.0000, 20, NULL, 'idem_page_split_processing', NOW(), NOW(), 0),
(806632, 'SPLIT_PAGE_SUCCESS', 806622, 806109, 3, 610001, 12.0000, 30, NULL, 'idem_page_split_success', NOW(), NOW(), 0),
(806633, 'SPLIT_PAGE_FAILED', 806623, 806108, 1, 720001, 3.0000, 40, '页面分账账户异常', 'idem_page_split_failed', NOW(), NOW(), 0);

INSERT IGNORE INTO fin_withdraw_apply
(id, withdraw_no, account_id, subject_type, subject_id, withdraw_amount, receive_account_name, receive_account_no, audit_status, pay_status, audit_account_id, audit_time, audit_remark, create_time, update_time, is_deleted)
VALUES
(806640, 'WD_PAGE_WAIT', 790002, 2, 720001, 50.0000, '自提点负责人A', '622200806640', 10, 10, NULL, NULL, NULL, NOW(), NOW(), 0),
(806641, 'WD_PAGE_PASS_PAYING', 790002, 2, 720001, 60.0000, '自提点负责人A', '622200806641', 20, 20, 700004, NOW(), '页面审核通过打款中', NOW(), NOW(), 0),
(806642, 'WD_PAGE_SUCCESS', 790002, 2, 720001, 70.0000, '自提点负责人A', '622200806642', 20, 30, 700004, NOW(), '页面打款成功', NOW(), NOW(), 0),
(806643, 'WD_PAGE_PAY_FAILED', 806600, 1, 610001, 200.0000, '页面固定供应商610001', '622200806643', 20, 40, 700004, NOW(), '页面银行退回', NOW(), NOW(), 0),
(806644, 'WD_PAGE_REJECT', 790002, 2, 720001, 80.0000, '自提点负责人A', '622200806644', 30, 10, 700004, NOW(), '页面账户信息不一致', NOW(), NOW(), 0),
(806645, 'WD_PAGE_CANCEL', 790003, 3, 730001, 90.0000, '测试团长A', '622200806645', 40, 10, NULL, NULL, '页面申请人撤回', NOW(), NOW(), 0);

INSERT IGNORE INTO fin_supplier_settlement
(id, settlement_no, supplier_id, period_start_date, period_end_date, purchase_amount, diff_amount, payable_amount, status, create_time, update_time, is_deleted)
VALUES
(806650, 'SETTLE_PAGE_610_WAIT', 610001, '2026-06-01', '2026-06-30', 12000.0000, 0.0000, 12000.0000, 10, NOW(), NOW(), 0),
(806651, 'SETTLE_PAGE_610_CONFIRM', 610001, '2026-06-01', '2026-06-30', 12000.0000, -200.0000, 11800.0000, 20, NOW(), NOW(), 0),
(806652, 'SETTLE_PAGE_610_ACCOUNTED', 610001, '2026-06-01', '2026-06-30', 12000.0000, 0.0000, 12000.0000, 30, NOW(), NOW(), 0),
(806653, 'SETTLE_PAGE_610_EXCEPTION', 610001, '2026-06-01', '2026-06-30', 12000.0000, 300.0000, 12300.0000, 40, NOW(), NOW(), 0);

INSERT IGNORE INTO fin_reconciliation_batch
(id, reconcile_no, channel_type, reconcile_date, total_count, success_count, diff_count, total_amount, diff_amount, status, create_time, update_time, is_deleted)
VALUES
(806660, 'RECON_PAGE_WAIT', 1, CURDATE(), 0, 0, 0, 0.0000, 0.0000, 10, NOW(), NOW(), 0),
(806661, 'RECON_PAGE_DOING', 1, CURDATE(), 80, 70, 10, 8000.0000, 88.0000, 20, NOW(), NOW(), 0),
(806662, 'RECON_PAGE_DONE', 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 80, 80, 0, 8000.0000, 0.0000, 30, NOW(), NOW(), 0),
(806663, 'RECON_PAGE_EXCEPTION', 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 80, 76, 4, 8000.0000, 40.0000, 40, NOW(), NOW(), 0);

INSERT IGNORE INTO op_todo
(id, todo_no, todo_type, source_biz_type, source_biz_no, title, priority, process_role_code, process_account_id, city_id, supplier_id, warehouse_id, status, due_time, create_time, update_time, is_deleted)
VALUES
(806670, 'TODO_PAGE_STATUS_10', 1, 'purchase', 'PUR_PAGE_610_WAIT_AUDIT', '页面采购待审核', 1, 'buyer', 700003, 440100, 610001, 50001, 10, DATE_ADD(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0),
(806671, 'TODO_PAGE_STATUS_20', 2, 'after_sale', 'AFS_PAGE_WAIT_AUDIT', '页面售后处理中', 2, 'after_sale_operator', 700005, 440100, 610001, NULL, 20, DATE_ADD(NOW(), INTERVAL 4 HOUR), NOW(), NOW(), 0),
(806672, 'TODO_PAGE_STATUS_30', 3, 'station_leave', 'LEAVE_TEST_PASS', '页面休假已处理', 2, 'city_operator', 700002, 440100, NULL, NULL, 30, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW(), 0),
(806673, 'TODO_PAGE_STATUS_40', 4, 'supplier', 'QUAL_PAGE_SUP_REJECT', '页面资质驳回待办', 3, 'platform_operator', 700001, 440100, 610001, NULL, 40, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW(), 0),
(806674, 'TODO_PAGE_STATUS_50', 5, 'export', 'RPT_EXP_PAGE_FAILED', '页面导出取消待办', 3, 'platform_operator', 700001, 440100, NULL, NULL, 50, NULL, NOW(), NOW(), 0),
(806675, 'TODO_PAGE_STATUS_60', 6, 'order', 'ORD_PAGE_WAIT_PAY', '页面订单超时待办', 1, 'city_operator', 700002, 440100, NULL, NULL, 60, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW(), 0),
(806676, 'TODO_PAGE_STATUS_70', 7, 'exception', 'EXC_PAGE_STATUS_40', '页面待办关闭样例', 3, 'platform_operator', 700001, 440100, NULL, NULL, 70, NULL, NOW(), NOW(), 0);

INSERT IGNORE INTO op_exception_record
(id, exception_no, exception_type, priority, source_module, source_biz_type, source_biz_no, city_id, supplier_id, station_id, warehouse_id, owner_account_id, status, title, description, create_time, update_time, is_deleted)
VALUES
(806680, 'EXC_PAGE_STATUS_10', 1, 1, 'wms', 'pick', 'PICK_PAGE_STATUS_40', 440100, 610001, 720001, 50001, 700007, 10, '页面拣货差异待处理', '页面拣货数量不足', NOW(), NOW(), 0),
(806681, 'EXC_PAGE_STATUS_20', 2, 2, 'pay', 'refund', 'RF_PAGE_FAILED', 440100, 610001, 720001, NULL, 700004, 20, '页面退款失败处理中', '页面渠道退款失败', NOW(), NOW(), 0),
(806682, 'EXC_PAGE_STATUS_30', 3, 3, 'supplier', 'settlement', 'SETTLE_PAGE_610_EXCEPTION', 440100, 610001, NULL, NULL, 700003, 30, '页面结算差异已处理', '页面差异已确认', NOW(), NOW(), 0),
(806683, 'EXC_PAGE_STATUS_40', 4, 2, 'delivery', 'station', 'DELIVERY_PAGE_STATUS_40', 440100, NULL, 720001, 50001, 700007, 40, '页面配送异常关闭', '页面无法送达已关闭', NOW(), NOW(), 0);

INSERT IGNORE INTO msg_record
(id, msg_no, template_code, receiver_type, receiver_id, channel_type, title, content, biz_type, biz_no, read_status, send_status, fail_reason, create_time, update_time, is_deleted)
VALUES
(806690, 'MSG_PAGE_USER_WAIT', 'TPL_ORDER_PAY_REMIND', 1, 740001, 2, '页面用户待发送消息', 'ORD_PAGE_WAIT_PAY 即将关闭', 'order', 'ORD_PAGE_WAIT_PAY', 0, 10, NULL, NOW(), NOW(), 0),
(806691, 'MSG_PAGE_USER_SUCCESS_UNREAD', 'TPL_ORDER_PAY_REMIND', 1, 740001, 1, '页面用户未读成功消息', '页面订单已到自提点', 'order', 'ORD_PAGE_WAIT_PICKUP_FIXED', 0, 20, NULL, NOW(), NOW(), 0),
(806692, 'MSG_PAGE_USER_SUCCESS_READ', 'TPL_ORDER_PAY_REMIND', 1, 740001, 1, '页面用户已读成功消息', '页面订单已完成', 'order', 'ORD_PAGE_PICKED_FIXED', 1, 20, NULL, NOW(), NOW(), 0),
(806693, 'MSG_PAGE_USER_FAILED', 'TPL_ORDER_PAY_REMIND', 1, 740001, 2, '页面用户发送失败消息', '页面公众号发送失败', 'order', 'ORD_PAGE_CLOSED_USER', 0, 30, '用户未关注公众号', NOW(), NOW(), 0),
(806694, 'MSG_PAGE_STATION_UNREAD', 'TPL_STATION_ARRIVAL', 4, 720001, 1, '页面站点未读消息', '页面到货待接收', 'delivery_station', 'DELIVERY_PAGE_STATUS_20', 0, 20, NULL, NOW(), NOW(), 0),
(806695, 'MSG_PAGE_STATION_READ', 'TPL_STATION_ARRIVAL', 4, 720001, 1, '页面站点已读消息', '页面到货已完成', 'delivery_station', 'DELIVERY_PAGE_STATUS_30', 1, 20, NULL, NOW(), NOW(), 0),
(806696, 'MSG_PAGE_SUPPLIER_UNREAD', 'TPL_SUPPLIER_DELIVERY', 2, 610001, 1, '页面供应商送货提醒', 'PDEL_PAGE_610_WAIT 待送货', 'purchase_delivery', 'PDEL_PAGE_610_WAIT', 0, 20, NULL, NOW(), NOW(), 0);

INSERT IGNORE INTO rpt_snapshot
(id, snapshot_no, report_type, stat_date, city_id, supplier_id, station_id, warehouse_id, metric_json, status, create_time, update_time, is_deleted)
VALUES
(806700, 'RPT_PAGE_CITY_ACTIVE', 1, CURDATE(), 440100, NULL, NULL, NULL, JSON_OBJECT('order_count', 20, 'pay_amount', 899.0000), 1, NOW(), NOW(), 0),
(806701, 'RPT_PAGE_SUPPLIER_ACTIVE', 2, CURDATE(), 440100, 610001, NULL, NULL, JSON_OBJECT('purchase_qty', 750, 'received_qty', 190), 1, NOW(), NOW(), 0),
(806702, 'RPT_PAGE_STATION_ACTIVE', 3, '2026-06-26', 440100, NULL, 720001, NULL, JSON_OBJECT('pickup_count', 3, 'commission', 9.0000), 1, NOW(), NOW(), 0),
(806703, 'RPT_PAGE_WAREHOUSE_INVALID', 4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 440100, NULL, NULL, 50001, JSON_OBJECT('pick_count', 8, 'diff_count', 2), 2, NOW(), NOW(), 0);

INSERT IGNORE INTO rpt_export_task
(id, export_task_no, report_type, account_id, filter_json, file_asset_id, status, fail_reason, create_time, update_time, is_deleted)
VALUES
(806710, 'RPT_EXP_PAGE_CREATING', 1, 700002, JSON_OBJECT('city_id', 440100), NULL, 10, NULL, NOW(), NOW(), 0),
(806711, 'RPT_EXP_PAGE_DONE', 2, 700004, JSON_OBJECT('supplier_id', 610001), 710203, 20, NULL, NOW(), NOW(), 0),
(806712, 'RPT_EXP_PAGE_FAILED', 4, 700007, JSON_OBJECT('warehouse_id', 50001), NULL, 30, '页面导出超时', NOW(), NOW(), 0);

INSERT IGNORE INTO search_suggest_word
(id, word, source_type, product_id, city_id, hit_count, status, create_time, update_time, is_deleted)
VALUES
(806720, '鲜橙', 1, 806020, 440100, 180, 1, NOW(), NOW(), 0),
(806721, '页面葡萄', 1, 806021, 440100, 68, 1, NOW(), NOW(), 0),
(806722, '页面停用词', 2, NULL, 440100, 3, 2, NOW(), NOW(), 0);

-- ----------------------------
-- 操作类型：修正
-- 涉及表：sys_region, sys_city, wms_warehouse, wms_zone, sup_supplier, sup_supplier_contact, sup_contract, usr_station, usr_user, usr_pickup_person, usr_leader, usr_station_leave, sys_account, sys_account_subject, prd_category, prd_product, prd_sku, prd_stock_spec, prd_rank_tag, sale_publish_period, ord_order, ord_order_item, ord_fulfillment_track, pay_refund_trade, afs_after_sale, pur_purchase_order, pur_delivery_order, wms_receive_record, wms_quality_file, wms_pick_item, wms_delivery_station, wms_driver_sign_record, wms_return_handover, wms_operation_log, fin_account_flow, fin_withdraw_apply, fin_supplier_settlement, op_todo, op_exception_record, msg_template, msg_record, rpt_export_task, search_suggest_word
-- 日期：2026-06-27
-- 操作人：codex
-- 用途说明：修复已导入测试数据中文乱码，覆盖用户小程序、运营后台、仓库后台和角色工作台常用展示字段
-- ----------------------------
UPDATE sys_region SET region_name = '华南大区', update_time = NOW() WHERE id = 440000;

UPDATE sys_city
SET city_name = CASE id
    WHEN 440100 THEN '广州市'
    WHEN 440300 THEN '深圳市'
    WHEN 440600 THEN '佛山市'
    WHEN 441900 THEN '东莞市'
    ELSE city_name
END,
update_time = NOW()
WHERE id IN (440100, 440300, 440600, 441900);

UPDATE wms_warehouse SET warehouse_name = '广州中心仓', update_time = NOW() WHERE id = 50001;

UPDATE wms_zone
SET zone_name = CASE id
    WHEN 51001 THEN '收货暂存区'
    WHEN 51002 THEN '待上架区'
    WHEN 51003 THEN '可售存储区'
    WHEN 51004 THEN '拣货暂存区'
    WHEN 51005 THEN '装车暂存区'
    WHEN 51006 THEN '回仓暂存区'
    ELSE zone_name
END,
update_time = NOW()
WHERE id IN (51001, 51002, 51003, 51004, 51005, 51006);

UPDATE sup_supplier
SET supplier_name = CASE id
    WHEN 610001 THEN '页面固定供应商610001'
    WHEN 710001 THEN '测试鲜果供应商'
    WHEN 710002 THEN '测试蔬菜供应商'
    WHEN 710003 THEN '测试冻品供应商'
    ELSE supplier_name
END,
remark = CASE id
    WHEN 610001 THEN '角色工作台供应商页固定ID测试数据'
    WHEN 710001 THEN '正常合作供应商'
    WHEN 710002 THEN '待复核供应商'
    WHEN 710003 THEN '已驳回供应商'
    ELSE remark
END,
update_time = NOW()
WHERE id IN (610001, 710001, 710002, 710003);

UPDATE sup_supplier_contact
SET contact_name = CASE id
    WHEN 710101 THEN '鲜果业务联系人'
    WHEN 710102 THEN '鲜果财务联系人'
    WHEN 710103 THEN '鲜果送货联系人'
    WHEN 710104 THEN '蔬菜无效联系人'
    WHEN 806001 THEN '页面供应商业务联系人'
    WHEN 806002 THEN '页面供应商送货联系人'
    ELSE contact_name
END,
update_time = NOW()
WHERE id IN (710101, 710102, 710103, 710104, 806001, 806002);

UPDATE sup_contract
SET contract_name = CASE id
    WHEN 710401 THEN '蔬菜供应合同待生效'
    WHEN 710402 THEN '鲜果供应合同生效中'
    WHEN 710403 THEN '鲜果供应合同已到期'
    WHEN 710404 THEN '冻品供应合同已解除'
    WHEN 806006 THEN '页面供应商待生效合同'
    WHEN 806007 THEN '页面供应商生效中合同'
    WHEN 806008 THEN '页面供应商已到期合同'
    WHEN 806009 THEN '页面供应商已解除合同'
    ELSE contract_name
END,
update_time = NOW()
WHERE id IN (710401, 710402, 710403, 710404, 806006, 806007, 806008, 806009);

UPDATE usr_station
SET station_name = CASE id
    WHEN 720001 THEN '海珠测试自提点'
    WHEN 720002 THEN '天河测试休假点'
    WHEN 720003 THEN '深圳测试停用点'
    ELSE station_name
END,
address = CASE id
    WHEN 720001 THEN '广州市海珠区测试路1号'
    WHEN 720002 THEN '广州市天河区测试路2号'
    WHEN 720003 THEN '深圳市南山区测试路3号'
    ELSE address
END,
contact_name = CASE id
    WHEN 720001 THEN '自提点负责人A'
    WHEN 720002 THEN '自提点负责人B'
    WHEN 720003 THEN '自提点负责人C'
    ELSE contact_name
END,
update_time = NOW()
WHERE id IN (720001, 720002, 720003);

UPDATE usr_user
SET nickname = CASE id
    WHEN 740001 THEN '测试用户正常'
    WHEN 740002 THEN '测试用户禁用'
    WHEN 740003 THEN '测试用户注销'
    ELSE nickname
END,
update_time = NOW()
WHERE id IN (740001, 740002, 740003);

UPDATE usr_pickup_person
SET pickup_name = CASE id
    WHEN 740101 THEN '默认提货人'
    WHEN 740102 THEN '备用提货人'
    WHEN 740103 THEN '无效提货人'
    ELSE pickup_name
END,
update_time = NOW()
WHERE id IN (740101, 740102, 740103);

UPDATE usr_leader
SET leader_name = CASE id
    WHEN 730001 THEN '测试团长A'
    WHEN 730002 THEN '测试团长B'
    ELSE leader_name
END,
update_time = NOW()
WHERE id IN (730001, 730002);

UPDATE usr_station_leave
SET reason = CASE id
    WHEN 730201 THEN '临时休息待审核'
    WHEN 730202 THEN '已通过休假'
    WHEN 730203 THEN '休假时间冲突'
    WHEN 730204 THEN '申请人撤回'
    ELSE reason
END,
audit_remark = CASE id
    WHEN 730202 THEN '同意休假'
    WHEN 730203 THEN '当前站点有待提订单'
    WHEN 730204 THEN '用户撤回'
    ELSE audit_remark
END,
update_time = NOW()
WHERE id IN (730201, 730202, 730203, 730204);

UPDATE sys_account
SET remark = CASE id
    WHEN 700001 THEN '平台管理员测试账号'
    WHEN 700002 THEN '城市运营测试账号'
    WHEN 700003 THEN '采购员测试账号'
    WHEN 700004 THEN '财务测试账号'
    WHEN 700005 THEN '售后运营测试账号'
    WHEN 700006 THEN '供应商后台测试账号'
    WHEN 700007 THEN '仓库主管测试账号'
    WHEN 700008 THEN '收货员测试账号'
    WHEN 700009 THEN '拣货员测试账号'
    WHEN 700010 THEN '装车员测试账号'
    WHEN 700011 THEN '司机测试账号'
    WHEN 700012 THEN '自提点角色工作台测试账号'
    WHEN 700013 THEN '团长角色工作台测试账号'
    WHEN 700014 THEN '供应商工作台测试账号'
    WHEN 700015 THEN '禁用账号样例'
    WHEN 700016 THEN '锁定账号样例'
    WHEN 806010 THEN '供应商工作台固定ID测试账号'
    ELSE remark
END,
update_time = NOW()
WHERE id IN (700001, 700002, 700003, 700004, 700005, 700006, 700007, 700008, 700009, 700010, 700011, 700012, 700013, 700014, 700015, 700016, 806010);

UPDATE sys_account_subject
SET subject_name_snapshot = CASE id
    WHEN 700101 THEN '测试鲜果供应商'
    WHEN 700102 THEN '海珠测试自提点'
    WHEN 700103 THEN '测试团长A'
    WHEN 700104 THEN '海珠测试自提点'
    WHEN 700105 THEN '测试鲜果供应商'
    WHEN 700106 THEN '深圳测试停用点'
    WHEN 806011 THEN '页面固定供应商610001'
    ELSE subject_name_snapshot
END,
update_time = NOW()
WHERE id IN (700101, 700102, 700103, 700104, 700105, 700106, 806011);

UPDATE prd_category
SET category_name = CASE id
    WHEN 750001 THEN '水果鲜食'
    WHEN 750002 THEN '蔬菜豆制'
    WHEN 750003 THEN '生鲜统计'
    WHEN 750004 THEN '冻品统计'
    ELSE category_name
END,
update_time = NOW()
WHERE id IN (750001, 750002, 750003, 750004);

UPDATE prd_product
SET product_name = CASE id
    WHEN 751001 THEN '测试红富士苹果'
    WHEN 751002 THEN '测试香蕉待审核'
    WHEN 751003 THEN '测试白菜已驳回'
    WHEN 751004 THEN '测试冻鱼已停用'
    WHEN 806020 THEN '页面覆盖赣南鲜橙'
    WHEN 806021 THEN '页面覆盖待审核葡萄'
    WHEN 806022 THEN '页面覆盖停售蜜瓜'
    ELSE product_name
END,
update_time = NOW()
WHERE id IN (751001, 751002, 751003, 751004, 806020, 806021, 806022);

UPDATE prd_sku
SET sku_name = CASE id
    WHEN 752001 THEN '苹果1kg装'
    WHEN 752002 THEN '香蕉2kg装'
    WHEN 752003 THEN '白菜500g'
    WHEN 752004 THEN '冻鱼1条'
    WHEN 806030 THEN '鲜橙3kg装'
    WHEN 806031 THEN '葡萄1kg装'
    WHEN 806032 THEN '蜜瓜1个装'
    ELSE sku_name
END,
sale_spec_text = CASE id
    WHEN 752001 THEN '1kg/份'
    WHEN 752002 THEN '2kg/份'
    WHEN 752003 THEN '500g/份'
    WHEN 752004 THEN '1条/份'
    WHEN 806030 THEN '3kg/箱'
    WHEN 806031 THEN '1kg/份'
    WHEN 806032 THEN '1个/份'
    ELSE sale_spec_text
END,
base_unit = CASE id
    WHEN 752004 THEN '条'
    WHEN 806030 THEN '箱'
    ELSE '份'
END,
update_time = NOW()
WHERE id IN (752001, 752002, 752003, 752004, 806030, 806031, 806032);

UPDATE prd_stock_spec
SET spec_name = CASE id
    WHEN 753001 THEN '按份'
    WHEN 753002 THEN '按箱'
    WHEN 753003 THEN '停用规格'
    ELSE spec_name
END,
unit_name = CASE id
    WHEN 753002 THEN '箱'
    WHEN 753003 THEN '袋'
    ELSE '份'
END,
update_time = NOW()
WHERE id IN (753001, 753002, 753003);

UPDATE prd_rank_tag
SET tag_name = CASE id
    WHEN 754001 THEN '热销榜'
    WHEN 754002 THEN '新品榜'
    WHEN 754003 THEN '停用榜单'
    ELSE tag_name
END,
update_time = NOW()
WHERE id IN (754001, 754002, 754003);

UPDATE sale_publish_period
SET period_name = CASE id
    WHEN 755001 THEN '测试待发布团期'
    WHEN 755002 THEN '测试已发布团期'
    WHEN 755003 THEN '测试已下架团期'
    WHEN 755004 THEN '测试已过期团期'
    WHEN 806040 THEN '页面覆盖待发布团期'
    WHEN 806041 THEN '页面覆盖在线团期'
    WHEN 806042 THEN '页面覆盖已下架团期'
    WHEN 806043 THEN '页面覆盖已过期团期'
    ELSE period_name
END,
update_time = NOW()
WHERE id IN (755001, 755002, 755003, 755004, 806040, 806041, 806042, 806043);

UPDATE ord_order
SET pickup_name = '页面测试提货人',
cancel_reason = CASE id
    WHEN 806101 THEN '支付处理中'
    WHEN 806107 THEN '用户取消未支付订单'
    WHEN 806108 THEN '已支付取消退款中'
    WHEN 806109 THEN '部分退款后完成'
    ELSE cancel_reason
END,
update_time = NOW()
WHERE id BETWEEN 806100 AND 806109;

UPDATE ord_order_item
SET product_name = CASE product_id
    WHEN 751001 THEN '测试红富士苹果'
    WHEN 751002 THEN '测试香蕉待审核'
    WHEN 751003 THEN '测试白菜已驳回'
    WHEN 751004 THEN '测试冻鱼已停用'
    WHEN 806020 THEN '页面覆盖赣南鲜橙'
    WHEN 806021 THEN '页面覆盖待审核葡萄'
    WHEN 806022 THEN '页面覆盖停售蜜瓜'
    ELSE product_name
END,
sku_name = CASE sku_id
    WHEN 752001 THEN '苹果1kg装'
    WHEN 752002 THEN '香蕉2kg装'
    WHEN 752003 THEN '白菜500g'
    WHEN 752004 THEN '冻鱼1条'
    WHEN 806030 THEN '鲜橙3kg装'
    WHEN 806031 THEN '葡萄1kg装'
    WHEN 806032 THEN '蜜瓜1个装'
    ELSE sku_name
END,
update_time = NOW()
WHERE id IN (762001, 762002, 762003, 762004, 762005, 762006, 762007, 806200, 806201, 806202, 806203, 806204, 806205, 806206, 806207, 806208, 806209);

UPDATE ord_fulfillment_track
SET node_name = CASE id
    WHEN 806210 THEN '待支付'
    WHEN 806211 THEN '待入库'
    WHEN 806212 THEN '待分拣'
    WHEN 806213 THEN '待配送'
    WHEN 806214 THEN '待自提'
    WHEN 806215 THEN '已自提'
    ELSE node_name
END,
remark = CASE id
    WHEN 806210 THEN '用户端待支付轨迹'
    WHEN 806211 THEN '采购已送达待入库'
    WHEN 806212 THEN '仓库待分拣'
    WHEN 806213 THEN '待司机配送'
    WHEN 806214 THEN '已到自提点待核销'
    WHEN 806215 THEN '用户已提货'
    ELSE remark
END,
update_time = NOW()
WHERE id BETWEEN 806210 AND 806215;

UPDATE pay_refund_trade SET fail_reason = '页面测试渠道失败', update_time = NOW() WHERE id = 806253;

UPDATE afs_after_sale
SET apply_reason = CASE id
    WHEN 806230 THEN '页面待审核售后'
    WHEN 806231 THEN '页面待退货售后'
    WHEN 806232 THEN '页面待退款售后'
    WHEN 806233 THEN '页面退款成功售后'
    WHEN 806234 THEN '页面退款失败售后'
    WHEN 806235 THEN '页面售后驳回'
    WHEN 806236 THEN '页面用户撤销售后'
    ELSE apply_reason
END,
update_time = NOW()
WHERE id BETWEEN 806230 AND 806236;

UPDATE pur_purchase_order
SET create_reason = CASE id
    WHEN 806300 THEN '供应商工作台待审核'
    WHEN 806301 THEN '供应商工作台审核通过'
    WHEN 806302 THEN '供应商工作台待发货'
    WHEN 806303 THEN '供应商工作台送货中'
    WHEN 806304 THEN '供应商工作台已收货'
    ELSE create_reason
END,
update_time = NOW()
WHERE id BETWEEN 806300 AND 806304;

UPDATE pur_delivery_order
SET contact_name = '页面供应商送货联系人',
update_time = NOW()
WHERE id BETWEEN 806320 AND 806324;

UPDATE wms_receive_record
SET remark = CASE id
    WHEN 806420 THEN '页面质检合格'
    WHEN 806421 THEN '页面部分收货'
    WHEN 806422 THEN '页面拒收破损'
    ELSE remark
END,
update_time = NOW()
WHERE id IN (806420, 806421, 806422);

UPDATE wms_quality_file
SET remark = CASE id
    WHEN 806430 THEN '页面质检待处理'
    WHEN 806431 THEN '页面质检通过'
    WHEN 806432 THEN '页面质检拒收'
    ELSE remark
END,
update_time = NOW()
WHERE id IN (806430, 806431, 806432);

UPDATE wms_pick_item SET diff_reason = '页面测试少拣5份', update_time = NOW() WHERE id = 806503;

UPDATE wms_delivery_station
SET remark = CASE id
    WHEN 806550 THEN '页面待配送'
    WHEN 806551 THEN '页面已到达待交接'
    WHEN 806552 THEN '页面交接完成'
    WHEN 806553 THEN '页面无法送达'
    ELSE remark
END,
update_time = NOW()
WHERE id BETWEEN 806550 AND 806553;

UPDATE wms_driver_sign_record
SET remark = CASE id
    WHEN 806560 THEN '页面司机到达'
    WHEN 806561 THEN '页面交接完成'
    WHEN 806562 THEN '页面无法送达'
    ELSE remark
END,
update_time = NOW()
WHERE id BETWEEN 806560 AND 806562;

UPDATE wms_return_handover
SET remark = CASE id
    WHEN 806570 THEN '页面待回仓确认'
    WHEN 806571 THEN '页面已回仓确认'
    WHEN 806572 THEN '页面待主管处理'
    WHEN 806573 THEN '页面关闭'
    ELSE remark
END,
update_time = NOW()
WHERE id BETWEEN 806570 AND 806573;

UPDATE wms_operation_log
SET reason = CASE id
    WHEN 806590 THEN '页面收货成功'
    WHEN 806591 THEN '页面拣货差异'
    WHEN 806592 THEN '页面出库失败'
    ELSE reason
END,
update_time = NOW()
WHERE id BETWEEN 806590 AND 806592;

UPDATE fin_account_flow
SET remark = CASE id
    WHEN 806610 THEN '页面佣金入账'
    WHEN 806611 THEN '页面提现冻结'
    WHEN 806612 THEN '页面供应商结算入账'
    ELSE remark
END,
update_time = NOW()
WHERE id BETWEEN 806610 AND 806612;

UPDATE fin_withdraw_apply
SET receive_account_name = CASE id
    WHEN 806645 THEN '测试团长A'
    WHEN 806643 THEN '页面固定供应商610001'
    ELSE '自提点负责人A'
END,
audit_remark = CASE id
    WHEN 806641 THEN '页面审核通过打款中'
    WHEN 806642 THEN '页面打款成功'
    WHEN 806643 THEN '页面银行退回'
    WHEN 806644 THEN '页面账户信息不一致'
    WHEN 806645 THEN '页面申请人撤回'
    ELSE audit_remark
END,
update_time = NOW()
WHERE id BETWEEN 806640 AND 806645;

UPDATE fin_supplier_settlement SET update_time = NOW() WHERE id BETWEEN 806650 AND 806653;

UPDATE op_todo
SET title = CASE id
    WHEN 806670 THEN '页面采购待审核'
    WHEN 806671 THEN '页面售后处理中'
    WHEN 806672 THEN '页面休假已处理'
    WHEN 806673 THEN '页面资质驳回待办'
    WHEN 806674 THEN '页面导出取消待办'
    WHEN 806675 THEN '页面订单超时待办'
    WHEN 806676 THEN '页面待办关闭样例'
    ELSE title
END,
update_time = NOW()
WHERE id BETWEEN 806670 AND 806676;

UPDATE op_exception_record
SET title = CASE id
    WHEN 806680 THEN '页面拣货差异待处理'
    WHEN 806681 THEN '页面退款失败处理中'
    WHEN 806682 THEN '页面结算差异已处理'
    WHEN 806683 THEN '页面配送异常关闭'
    ELSE title
END,
description = CASE id
    WHEN 806680 THEN '页面拣货数量不足'
    WHEN 806681 THEN '页面渠道退款失败'
    WHEN 806682 THEN '页面差异已确认'
    WHEN 806683 THEN '页面无法送达已关闭'
    ELSE description
END,
update_time = NOW()
WHERE id BETWEEN 806680 AND 806683;

UPDATE msg_template
SET template_name = CASE id
    WHEN 792001 THEN '订单支付提醒'
    WHEN 792002 THEN '自提点到货通知'
    WHEN 792003 THEN '供应商送货提醒'
    WHEN 792004 THEN '停用模板'
    ELSE template_name
END,
title_template = CASE id
    WHEN 792001 THEN '订单待支付'
    WHEN 792002 THEN '商品已到达'
    WHEN 792003 THEN '送货任务提醒'
    WHEN 792004 THEN '停用模板'
    ELSE title_template
END,
content_template = CASE id
    WHEN 792001 THEN '您的订单即将关闭，请及时支付'
    WHEN 792002 THEN '请及时完成到货接收'
    WHEN 792003 THEN '请按时送货到仓'
    WHEN 792004 THEN '停用内容'
    ELSE content_template
END,
update_time = NOW()
WHERE id IN (792001, 792002, 792003, 792004);

UPDATE msg_record
SET title = CASE id
    WHEN 806690 THEN '页面用户待发送消息'
    WHEN 806691 THEN '页面用户未读成功消息'
    WHEN 806692 THEN '页面用户已读成功消息'
    WHEN 806693 THEN '页面用户发送失败消息'
    WHEN 806694 THEN '页面站点未读消息'
    WHEN 806695 THEN '页面站点已读消息'
    WHEN 806696 THEN '页面供应商送货提醒'
    WHEN 792101 THEN '订单待支付'
    WHEN 792102 THEN '商品已到达'
    WHEN 792103 THEN '送货任务提醒'
    WHEN 792104 THEN '发送失败样例'
    ELSE title
END,
content = CASE id
    WHEN 806690 THEN 'ORD_PAGE_WAIT_PAY 即将关闭'
    WHEN 806691 THEN '页面订单已到自提点'
    WHEN 806692 THEN '页面订单已完成'
    WHEN 806693 THEN '页面公众号发送失败'
    WHEN 806694 THEN '页面到货待接收'
    WHEN 806695 THEN '页面到货已完成'
    WHEN 806696 THEN 'PDEL_PAGE_610_WAIT 待送货'
    WHEN 792101 THEN 'ORD_TEST_WAIT_PAY 即将关闭'
    WHEN 792102 THEN '请接收今日到货商品'
    WHEN 792103 THEN 'PDEL_TEST_WAIT 待送货'
    WHEN 792104 THEN '公众号发送失败'
    ELSE content
END,
fail_reason = CASE id
    WHEN 806693 THEN '用户未关注公众号'
    WHEN 792104 THEN '用户未关注'
    ELSE fail_reason
END,
update_time = NOW()
WHERE id IN (806690, 806691, 806692, 806693, 806694, 806695, 806696, 792101, 792102, 792103, 792104);

UPDATE rpt_export_task SET fail_reason = '页面导出超时', update_time = NOW() WHERE id = 806712;

UPDATE search_suggest_word
SET word = CASE id
    WHEN 794001 THEN '苹果'
    WHEN 794002 THEN '香蕉'
    WHEN 794003 THEN '今日生鲜'
    WHEN 794004 THEN '停用联想词'
    WHEN 806720 THEN '鲜橙'
    WHEN 806721 THEN '页面葡萄'
    WHEN 806722 THEN '页面停用词'
    ELSE word
END,
update_time = NOW()
WHERE id IN (794001, 794002, 794003, 794004, 806720, 806721, 806722);

-- ----------------------------
-- 操作类型：初始化
-- 涉及表：sys_role_button
-- 日期：2026-06-27
-- 操作人：codex
-- 用途说明：幂等补强运营后台、仓库后台常用只读权限，避免历史库未执行早期权限段时页面 403
-- ----------------------------
INSERT IGNORE INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(802201, 20001, 'admin', 'operation:dashboard', 'view', '查看运营驾驶舱摘要', 1, 1, NOW(), NOW(), 0),
(802202, 20001, 'admin', 'operation:todo', 'view', '查看待办', 1, 1, NOW(), NOW(), 0),
(802203, 20001, 'admin', 'operation:exception', 'view', '查看异常', 1, 1, NOW(), NOW(), 0),
(802204, 20001, 'admin', 'product:product', 'view', '查看商品', 1, 1, NOW(), NOW(), 0),
(802205, 20001, 'admin', 'product:sku', 'view', '查看SKU', 1, 1, NOW(), NOW(), 0),
(802206, 20001, 'admin', 'product:category', 'view', '查看类目', 1, 1, NOW(), NOW(), 0),
(802207, 20001, 'admin', 'sale:period', 'view', '查看团期', 1, 1, NOW(), NOW(), 0),
(802208, 20001, 'admin', 'trade:order', 'view', '查看订单', 1, 1, NOW(), NOW(), 0),
(802209, 20001, 'admin', 'trade:order:item', 'view', '查看订单商品', 1, 1, NOW(), NOW(), 0),
(802210, 20001, 'admin', 'trade:fulfillment', 'view', '查看履约跟踪', 1, 1, NOW(), NOW(), 0),
(802211, 20001, 'admin', 'trade:stockout', 'view', '查看缺货记录', 1, 1, NOW(), NOW(), 0),
(802212, 20001, 'admin', 'payment:trade', 'view', '查看支付单', 1, 1, NOW(), NOW(), 0),
(802213, 20001, 'admin', 'payment:refund', 'view', '查看退款单', 1, 1, NOW(), NOW(), 0),
(802214, 20001, 'admin', 'payment:fund-flow', 'view', '查看资金流水', 1, 1, NOW(), NOW(), 0),
(802215, 20001, 'admin', 'aftersale:after-sale', 'view', '查看售后单', 1, 1, NOW(), NOW(), 0),
(802216, 20001, 'admin', 'aftersale:item', 'view', '查看售后商品', 1, 1, NOW(), NOW(), 0),
(802217, 20001, 'admin', 'aftersale:audit-log', 'view', '查看售后审核日志', 1, 1, NOW(), NOW(), 0),
(802218, 20001, 'admin', 'aftersale:return-record', 'view', '查看退货记录', 1, 1, NOW(), NOW(), 0),
(802219, 20001, 'admin', 'supplier:profile', 'view', '查看供应商档案', 1, 1, NOW(), NOW(), 0),
(802220, 20001, 'admin', 'supplier:contact', 'view', '查看供应商联系人', 1, 1, NOW(), NOW(), 0),
(802221, 20001, 'admin', 'supplier:qualification', 'view', '查看供应商资质', 1, 1, NOW(), NOW(), 0),
(802222, 20001, 'admin', 'supplier:contract', 'view', '查看供应商合同', 1, 1, NOW(), NOW(), 0),
(802223, 20001, 'admin', 'purchase:order', 'view', '查看采购单', 1, 1, NOW(), NOW(), 0),
(802224, 20001, 'admin', 'purchase:item', 'view', '查看采购明细', 1, 1, NOW(), NOW(), 0),
(802225, 20001, 'admin', 'purchase:audit-log', 'view', '查看采购审核日志', 1, 1, NOW(), NOW(), 0),
(802226, 20001, 'admin', 'purchase:source-log', 'view', '查看采购来源日志', 1, 1, NOW(), NOW(), 0),
(802227, 20001, 'admin', 'delivery:order', 'view', '查看送货单', 1, 1, NOW(), NOW(), 0),
(802228, 20001, 'admin', 'delivery:item', 'view', '查看送货明细', 1, 1, NOW(), NOW(), 0),
(802229, 20001, 'admin', 'finance:account', 'view', '查看财务账户', 1, 1, NOW(), NOW(), 0),
(802230, 20001, 'admin', 'finance:account-flow', 'view', '查看账户流水', 1, 1, NOW(), NOW(), 0),
(802231, 20001, 'admin', 'finance:commission', 'view', '查看佣金明细', 1, 1, NOW(), NOW(), 0),
(802232, 20001, 'admin', 'finance:split', 'view', '查看分账流水', 1, 1, NOW(), NOW(), 0),
(802233, 20001, 'admin', 'finance:withdraw', 'view', '查看提现申请', 1, 1, NOW(), NOW(), 0),
(802234, 20001, 'admin', 'finance:deposit', 'view', '查看押金记录', 1, 1, NOW(), NOW(), 0),
(802235, 20001, 'admin', 'finance:supplier-settlement', 'view', '查看供应商结算', 1, 1, NOW(), NOW(), 0),
(802236, 20001, 'admin', 'finance:reconciliation', 'view', '查看对账批次', 1, 1, NOW(), NOW(), 0),
(802237, 20003, 'admin', 'operation:dashboard', 'view', '查看运营驾驶舱摘要', 1, 1, NOW(), NOW(), 0),
(802238, 20003, 'admin', 'operation:todo', 'view', '查看城市待办', 1, 1, NOW(), NOW(), 0),
(802239, 20003, 'admin', 'operation:exception', 'view', '查看城市异常', 1, 1, NOW(), NOW(), 0),
(802240, 20003, 'admin', 'trade:order', 'view', '查看城市订单', 1, 1, NOW(), NOW(), 0),
(802241, 20003, 'admin', 'trade:order:item', 'view', '查看城市订单商品', 1, 1, NOW(), NOW(), 0),
(802242, 20003, 'admin', 'trade:fulfillment', 'view', '查看城市履约跟踪', 1, 1, NOW(), NOW(), 0),
(802243, 20003, 'admin', 'trade:stockout', 'view', '查看城市缺货记录', 1, 1, NOW(), NOW(), 0),
(802244, 20003, 'admin', 'payment:trade', 'view', '查看城市支付单', 1, 1, NOW(), NOW(), 0),
(802245, 20003, 'admin', 'payment:refund', 'view', '查看城市退款单', 1, 1, NOW(), NOW(), 0),
(802246, 20003, 'admin', 'payment:fund-flow', 'view', '查看城市资金流水', 1, 1, NOW(), NOW(), 0),
(802247, 20003, 'admin', 'supplier:profile', 'view', '查看城市供应商档案', 1, 1, NOW(), NOW(), 0),
(802248, 20003, 'admin', 'purchase:order', 'view', '查看城市采购单', 1, 1, NOW(), NOW(), 0),
(802249, 20003, 'admin', 'delivery:order', 'view', '查看城市送货单', 1, 1, NOW(), NOW(), 0),
(802250, 20003, 'admin', 'finance:account', 'view', '查看城市财务账户', 1, 1, NOW(), NOW(), 0),
(802251, 20003, 'admin', 'finance:commission', 'view', '查看城市佣金明细', 1, 1, NOW(), NOW(), 0),
(802252, 21001, 'wms-admin', 'wms:warehouse', 'view', '查看仓库', 1, 1, NOW(), NOW(), 0),
(802253, 21001, 'wms-admin', 'wms:zone', 'view', '查看库区', 1, 1, NOW(), NOW(), 0),
(802254, 21001, 'wms-admin', 'wms:location', 'view', '查看库位', 1, 1, NOW(), NOW(), 0),
(802255, 21001, 'wms-admin', 'wms:inbound', 'view', '查看入库单', 1, 1, NOW(), NOW(), 0),
(802256, 21001, 'wms-admin', 'wms:receive', 'view', '查看收货记录', 1, 1, NOW(), NOW(), 0),
(802257, 21001, 'wms-admin', 'wms:putaway', 'view', '查看上架任务', 1, 1, NOW(), NOW(), 0),
(802258, 21001, 'wms-admin', 'wms:inventory', 'view', '查看库存', 1, 1, NOW(), NOW(), 0),
(802259, 21001, 'wms-admin', 'wms:wave', 'view', '查看波次', 1, 1, NOW(), NOW(), 0),
(802260, 21001, 'wms-admin', 'wms:pick', 'view', '查看拣货任务', 1, 1, NOW(), NOW(), 0),
(802261, 21001, 'wms-admin', 'wms:loading', 'view', '查看装车单', 1, 1, NOW(), NOW(), 0),
(802262, 21001, 'wms-admin', 'wms:outbound', 'view', '查看出库单', 1, 1, NOW(), NOW(), 0),
(802263, 21001, 'wms-admin', 'wms:delivery', 'view', '查看配送单', 1, 1, NOW(), NOW(), 0),
(802264, 21001, 'wms-admin', 'wms:return', 'view', '查看回仓交接', 1, 1, NOW(), NOW(), 0),
(802265, 21001, 'wms-admin', 'wms:stocktake', 'view', '查看盘点单', 1, 1, NOW(), NOW(), 0),
(802266, 21001, 'wms-admin', 'wms:operation-log', 'view', '查看WMS操作日志', 1, 1, NOW(), NOW(), 0);
