/*==============================================================*/
/* Table: admin                                                 */
/*==============================================================*/
create table admin
(
    id          bigint(20) not null auto_increment,
    username    varchar(64) comment '用户名',
    password    varchar(64) comment '密码',
    email       varchar(100) comment '邮箱',
    nick_name   varchar(200) comment '昵称',
    note        varchar(500) comment '备注信息',
    create_time datetime comment '创建时间',
    login_time  datetime comment '最后登录时间',
    status      int comment '帐号启用状态：0->禁用；1->启用',
    primary key (id)
);

alter table admin
    comment '后台用户表';

/*==============================================================*/
/* Index: uk_admin_username                                     */
/*==============================================================*/
create unique index uk_admin_username on admin
    (
     username
        );

/*==============================================================*/
/* Table: admin_role_relation                                   */
/*==============================================================*/
CREATE TABLE `admin_role_relation`
(
    `id`       bigint(11) NOT NULL AUTO_INCREMENT,
    `admin_id` bigint(11) NOT NULL COMMENT '用户编号',
    `role_id`  bigint(11) NOT NULL COMMENT '角色编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_role_id` (`admin_id`, `role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='用户角色表';

/*==============================================================*/
/* Table: group_name                                            */
/*==============================================================*/
create table group_name
(
    id          bigint(11) not null auto_increment,
    name        varchar(64) comment '组名称',
    create_time datetime comment '创建时间',
    deleteable  int(1) comment '是否可删除  0->可以删除 1->不可以删除',
    primary key (id)
);

alter table group_name
    comment '组名称';

/*==============================================================*/
/* Table: permission                                            */
/*==============================================================*/
create table permission
(
    id          bigint(11) not null auto_increment,
    pid         bigint(11) comment '父级权限id',
    name        varchar(100) comment '名称',
    value       varchar(100) comment '权限值',
    icon        varchar(100) comment '图标',
    type        int(1) comment '权限类型：0->目录；1->菜单；2->按钮（接口绑定权限）',
    uri         varchar(200) comment '前段资源路径',
    status      int(1) comment '启用状态；0->禁用；1->启用',
    create_time datetime comment '创建时间',
    sort        int(11) comment '排序',
    primary key (id)
);

alter table permission
    comment '权限菜单表';

/*==============================================================*/
/* Table: personal_info                                         */
/*==============================================================*/
create table personal_info
(
    id          bigint(11) not null auto_increment,
    name        varchar(32) comment '姓名',
    gender      int(1) comment '性别 0->男;1->女',
    birthday    date comment '生日',
    group_id    bigint(11) comment '组名',
    employee_no varchar(64) comment '工号',
    title       varchar(64) comment '职称',
    department  varchar(64) comment '部门',
    company     varchar(64) comment '公司',
    icon        varchar(500) comment '头像',
    note        varchar(512) comment '备注',
    create_time date comment '创建时间',
    deleted     int(1) comment '是否删除 0->正常;1->删除',
    primary key (id)
);

alter table personal_info
    comment '人员信息';

/*==============================================================*/
/* Table: personal_monitor_log                                  */
/*==============================================================*/
create table personal_monitor_log
(
    id          bigint(20) not null auto_increment,
    personal_id bigint(11) comment '人员编号',
    action      int(1) comment '动作 0->入；1->出',
    create_time datetime comment '监控时间',
    primary key (id)
);

alter table personal_monitor_log
    comment '人员监控信息';

/*==============================================================*/
/* Index: idx_personal_monitor_create_time                      */
/*==============================================================*/
create index idx_personal_monitor_create_time on personal_monitor_log
    (
     create_time
        );

/*==============================================================*/
/* Table: personal_monitor_statictis                            */
/*==============================================================*/
create table personal_monitor_statictis
(
    id              bigint(20) not null auto_increment,
    personal_id     bigint(11) comment '人员编号',
    out_count       int(5) comment '出次数',
    in_count        int(5) comment '入次数',
    stand_time      bigint(10) comment '停留时间',
    earliest_time   time comment '最早进入时间',
    latest_time     time comment '最晚出去时间',
    statistics_time date comment '日期',
    primary key (id)
);

alter table personal_monitor_statictis
    comment '人员监控统计信息';

/*==============================================================*/
/* Table: role                                                  */
/*==============================================================*/
create table role
(
    id          bigint(11) not null auto_increment,
    code        varchar(100) comment '角色编号',
    name        varchar(100) comment '角色',
    description varchar(500) comment '描述',
    create_time datetime comment '创建时间',
    status      int(1) comment '启用状态：0->禁用；1->启用',
    sort        int(11) comment '排序',
    primary key (id)
);

alter table role
    comment '角色';

/*==============================================================*/
/* Index: idx_role_name                                         */
/*==============================================================*/
create index idx_role_name on role
    (
     name
        );

/*==============================================================*/
/* Table: role_permission_relation                              */
/*==============================================================*/
CREATE TABLE `role_permission_relation`
(
    `id`            bigint(11) NOT NULL AUTO_INCREMENT,
    `role_id`       bigint(11) NOT NULL COMMENT '角色编号',
    `permission_id` bigint(11) NOT NULL COMMENT '权限编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission_id` (`role_id`, `permission_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='角色权限表';

-- 权限初始化数据
DELETE
FROM admin_role_relation;
DELETE
FROM role;
DELETE
FROM permission;
DELETE
FROM role_permission_relation;

INSERT INTO `admin`
VALUES (1, 'admin', '$2a$10$d19r8pa4ubNuGk9Yr8x6vuujQQjD.xpKkr0n4Gd69wBI4op6GtfmK', 'admin@126.com', '管理员', '系统管理员',
        '2019-07-17 10:56:27', '2019-08-14 15:59:06', 0);

INSERT INTO `admin_role_relation`
VALUES ('1', '1', '1');

INSERT INTO `role`
VALUES ('1', 'super', '超级管理员', '超级管理员', now(), '1', '1');

INSERT INTO `permission`
VALUES (1, NULL, '人员管理', '', 'form', 1, '', 1, '2019-08-05 08:54:28', 1);
INSERT INTO `permission`
VALUES (2, NULL, '监控信息', '', 'area-chart', 1, '', 1, '2019-08-05 08:54:28', 2);
INSERT INTO `permission`
VALUES (3, NULL, '系统管理', '', 'setting', 1, '', 1, '2019-08-05 08:54:28', 3);
INSERT INTO `permission`
VALUES (4, 1, '人员列表', 'personinfo', 'team', 1, '/api/person', 1, '2019-08-05 08:54:28', 1);
INSERT INTO `permission`
VALUES (5, 2, '监控事件', 'monitor', 'eye', 1, '/api/monitor', 1, '2019-08-05 08:54:28', 1);
INSERT INTO `permission`
VALUES (6, 2, '统计分析', 'statistic', 'bar-chat', 1, '/api/statistic', 1, '2019-08-05 08:54:28', 2);
INSERT INTO `permission`
VALUES (7, 3, '账号管理', 'admin', 'user', 1, '/api/admin', 1, '2019-08-05 08:54:28', 1);
INSERT INTO `permission`
VALUES (8, 3, '角色管理', 'role', 'control', 1, '/api/role', 1, '2019-08-05 08:54:28', 2);

INSERT INTO `role_permission_relation`
VALUES ('1', '1', '1');
INSERT INTO `role_permission_relation`
VALUES ('2', '1', '2');
INSERT INTO `role_permission_relation`
VALUES ('3', '1', '3');
INSERT INTO `role_permission_relation`
VALUES ('4', '1', '4');
INSERT INTO `role_permission_relation`
VALUES ('5', '1', '5');
INSERT INTO `role_permission_relation`
VALUES ('6', '1', '6');
INSERT INTO `role_permission_relation`
VALUES ('7', '1', '7');
INSERT INTO `role_permission_relation`
VALUES ('8', '1', '8');

-- 设置权限status默认值 --
alter table permission
    alter column status drop default;
alter table permission
    alter column status set default 1;

-- 人员监控日志新增来源字段 --
ALTER TABLE personal_monitor_log
    ADD COLUMN source INT(1) NOT NULL DEFAULT 0 COMMENT '来源 0:人脸识别,1:mac识别' AFTER action;

-- ----------------------------
-- Table structure for personal_icon
-- ----------------------------
DROP TABLE IF EXISTS `personal_icon`;
CREATE TABLE `personal_icon`
(
    `id`          bigint(11)                                             NOT NULL AUTO_INCREMENT,
    `icon`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '图片名称',
    `personal_id` bigint(11)                                             NOT NULL COMMENT '人员id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin
  ROW_FORMAT = Dynamic;

-- 新增人员mac表 --
DROP TABLE IF EXISTS `personal_mac`;
CREATE TABLE `personal_mac`
(
    `id`          bigint(11)                                            NOT NULL AUTO_INCREMENT,
    `mac`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'mac地址',
    `personal_id` bigint(11)                                            NOT NULL COMMENT '人员id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_bin
  ROW_FORMAT = Dynamic;

-- 人员图片增加特征值字段 --
alter table personal_icon
    add column feature blob COMMENT '特征值';