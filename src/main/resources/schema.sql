-- 创建数据库
CREATE DATABASE IF NOT EXISTS yue_living DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE yue_living;

-- 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar VARCHAR(255) COMMENT '头像',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    last_login_time DATETIME COMMENT '最近登录时间',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';

-- 角色表
CREATE TABLE role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '角色表';

-- 权限表
CREATE TABLE permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '权限名称',
    description VARCHAR(255) COMMENT '权限描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '权限表';

-- 角色权限关联表
CREATE TABLE role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) COMMENT '角色权限关联表';

-- 房产表
CREATE TABLE property (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房产ID',
    building_no VARCHAR(20) NOT NULL COMMENT '楼栋号',
    unit_no VARCHAR(20) NOT NULL COMMENT '单元号',
    room_no VARCHAR(20) NOT NULL COMMENT '房号',
    area DECIMAL(10,2) COMMENT '面积',
    property_type VARCHAR(20) DEFAULT '住宅' COMMENT '房产类型',
    owner_id BIGINT COMMENT '业主ID',
    owner_name_snapshot VARCHAR(50) COMMENT '业主姓名快照',
    bind_time DATETIME COMMENT '绑定时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-空置，1-已入住，2-装修中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_property (building_no, unit_no, room_no)
) COMMENT '房产表';

-- 费用账单表
CREATE TABLE bill (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账单ID',
    property_id BIGINT NOT NULL COMMENT '房产ID',
    owner_id BIGINT COMMENT '账单所属业主',
    bill_type VARCHAR(50) NOT NULL COMMENT '账单类型：物业费、水费、电费等',
    bill_item_name VARCHAR(100) COMMENT '账单项目名称',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    unit_price DECIMAL(10,2) COMMENT '单价',
    usage_amount DECIMAL(10,2) COMMENT '用量',
    `period` VARCHAR(20) COMMENT '账期',
    generate_type VARCHAR(20) DEFAULT 'MANUAL' COMMENT '生成方式',
    status TINYINT DEFAULT 0 COMMENT '状态：0-未缴费，1-已缴费，2-逾期，3-已作废',
    due_date DATE COMMENT '到期日期',
    remark VARCHAR(255) COMMENT '费用说明',
    paid_amount DECIMAL(10,2) DEFAULT 0 COMMENT '已缴金额',
    pay_time DATETIME COMMENT '缴费时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '费用账单表';

-- 报修工单表
CREATE TABLE repair_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '工单ID',
    property_id BIGINT NOT NULL COMMENT '房产ID',
    user_id BIGINT NOT NULL COMMENT '报修用户ID',
    repair_type_id BIGINT COMMENT '报修分类ID',
    contact_name VARCHAR(50) COMMENT '联系人姓名',
    contact_phone VARCHAR(20) COMMENT '联系人电话',
    description TEXT NOT NULL COMMENT '故障描述',
    images VARCHAR(1000) COMMENT '图片路径，逗号分隔',
    priority_level TINYINT DEFAULT 1 COMMENT '优先级：1-普通，2-加急',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待受理，1-已受理，2-处理中，3-已完成，4-已取消',
    assign_user_id BIGINT COMMENT '分配维修人员ID',
    assign_time DATETIME COMMENT '分配时间',
    processing_result TEXT COMMENT '处理结果',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    complete_time DATETIME COMMENT '完成时间',
    rating TINYINT COMMENT '评价星级',
    satisfaction_label VARCHAR(50) COMMENT '满意度标签',
    comment TEXT COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '报修工单表';

-- 公告通知表
CREATE TABLE announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    summary VARCHAR(255) COMMENT '摘要',
    content TEXT NOT NULL COMMENT '内容',
    category_code VARCHAR(50) COMMENT '公告分类',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    status VARCHAR(20) DEFAULT 'PUBLISHED' COMMENT '状态：DRAFT/PUBLISHED',
    publish_user_id BIGINT NOT NULL COMMENT '发布用户ID',
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '公告通知表';

CREATE TABLE system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(50) COMMENT '配置类型',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '系统配置表';

CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_role_id BIGINT NOT NULL COMMENT '操作人角色ID',
    module_name VARCHAR(100) NOT NULL COMMENT '模块名称',
    action_name VARCHAR(100) NOT NULL COMMENT '操作名称',
    target_type VARCHAR(100) COMMENT '对象类型',
    target_id BIGINT COMMENT '对象ID',
    content TEXT COMMENT '操作内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '操作日志表';

CREATE TABLE repair_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    type_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    sort_no INT DEFAULT 0 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '报修分类表';

CREATE TABLE billing_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID',
    bill_type VARCHAR(50) NOT NULL COMMENT '账单类型',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    cycle_type VARCHAR(20) NOT NULL COMMENT '周期类型',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '计费规则表';

-- 插入初始数据
-- 角色
INSERT INTO role (name, description) VALUES
('业主', '小区业主'),
('物业管理员', '物业管理人员'),
('系统管理员', '系统管理员');

-- 权限
INSERT INTO permission (name, description) VALUES
('user:view', '查看用户'),
('user:edit', '编辑用户'),
('property:view', '查看房产'),
('property:edit', '编辑房产'),
('bill:view', '查看账单'),
('bill:edit', '编辑账单'),
('repair:view', '查看报修'),
('repair:edit', '编辑报修'),
('announcement:view', '查看公告'),
('announcement:edit', '编辑公告'),
('statistics:view', '查看统计');

-- 角色权限关联
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 1), (1, 3), (1, 5), (1, 7), (1, 9), -- 业主权限
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10), (2, 11); -- 物业管理员权限
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 8), (3, 9), (3, 10), (3, 11); -- 系统管理员权限
