drop table if exists T_CUSTOMER_INFO;

/*==============================================================*/
/* Table: T_CUSTOMER_INFO                                       */
/*==============================================================*/
create table T_CUSTOMER_INFO
(
   ID                   varchar(32) not null,
   CUST_ID              varchar(20) not null,
   INVITER_ID           varchar(12),
   CUST_PASSWORD        varchar(32),
   CUST_NAME            varchar(60),
   CUST_SEX             char,
   CUST_AGE             bigint,
   CUST_MOBILE          varchar(20),
   CUST_EMAIL           varchar(60),
   ID_CARD_NO           varchar(18),
   ADDRESS              varchar(120),
   CUST_LEVEL_SAMPLE    varchar(20),
   CUST_STATUS          varchar(10) comment 'REG：注册，AUTH：实名认证，FORBID：禁用，DEL：删除',
   LAST_LOGIN           timestamp default CURRENT_TIMESTAMP,
   CREATED_TIME         timestamp not null default CURRENT_TIMESTAMP,
   primary key (ID)
);

drop table if exists T_BANK_CARD;

/*==============================================================*/
/* Table: T_BANK_CARD                                           */
/*==============================================================*/
create table T_BANK_CARD
(
   ID                   varchar(32) not null,
   CUST_ID              varchar(20) not null,
   ACCOUNT_CODE         varchar(20) not null,
   ACCOUNT_NAME         varchar(20),
   BANK_CARD_TYPE       char comment 'C信用卡，D借记卡',
   IO_TYPE              char(2) default 'IO' comment 'I 收入，O支出，IO收入支出皆可',
   BANK_CODE            varchar(20),
   BANK_NAME            varchar(60),
   IS_DEFAULT           char comment '0否，1是',
   OPENING_SUB_BANK_NAME varchar(60),
   OPENNING_BANK_PROVINCE varchar(30),
   OPENNING_BANK_CITY   varchar(30),
   OPENING_BANK_ADDRESS varchar(120),
   MOBILE_NO            varchar(20),
   VALID                char default 'Y' comment 'Y:启用，N:禁用',
   OPEN_STATUS          varchar(20) comment '状态（INIT待开卡、OPEN_SUCCESS开卡成功、OPEN_FAIL开卡失败）',
   BIND_STATUS          varchar(20) comment '状态（INIT待绑卡、REG_SUCCESS 注册成功、REG_FAIL 注册失败）',
   CREATED_TIME         timestamp not null,
   primary key (ID)
);

drop table if exists CUSTOMER_LEVEL;

/*==============================================================*/
/* Table: CUSTOMER_LEVEL                                        */
/*==============================================================*/
create table CUSTOMER_LEVEL
(
   ID                   varchar(32) not null,
   LEVEL_SAMPLE         varchar(20),
   LEVEL_NAME           varchar(60),
   LEVEL_DESCR          varchar(256),
   primary key (ID)
);


drop table if exists T_VIRTUAL_MERCHANT;

/*==============================================================*/
/* Table: T_VIRTUAL_MERCHANT                                    */
/*==============================================================*/
create table T_VIRTUAL_MERCHANT
(
   ID                   varchar(32) not null,
   CUST_ID              varchar(20) not null,
   MERCHANT_CODE        varchar(20) not null,
   MERCHANT_NAME        varchar(60) not null,
   MERCHENT_ADDRESS     varchar(120),
   MERCHANT_STATUS      varchar(20) comment '状态（INIT 待注册、REG_ING 注册中、REG_SUCCESS 注册成功、REG_FAIL 注册失败）',
   MERCHANT_VALID       char comment 'Y有效，N无效',
   PLAT_MERCHANT_CODE   varchar(12),
   primary key (ID)
);

drop table if exists T_TRANS_TYPE;

/*==============================================================*/
/* Table: T_TRANS_TYPE                                          */
/*==============================================================*/
create table T_TRANS_TYPE
(
   ID                   varchar(32) not null,
   TRANS_TYPE_CODE      varchar(12) not null comment 'C信用卡交易，D借记卡',
   TRANS_TYPE_NAME      varchar(60) not null,
   TRANS_TYPE_DESCR     varchar(255),
   primary key (ID)
);


drop table if exists T_TRANS_FEE_RATE;

/*==============================================================*/
/* Table: T_TRANS_FEE_RATE                                      */
/*==============================================================*/
create table T_TRANS_FEE_RATE
(
   ID                   varchar(32) not null,
   CUST_ID              varchar(20),
   CUST_LEVEL_SAMPLE    varchar(32) not null,
   TRANS_TYPE_CODE      varchar(1) not null comment 'C信用卡交易',
   FEE_RATE             numeric(10,2) default 0,
   DESCRIPTION          varchar(255),
   primary key (ID)
);

drop table if exists T_TRANS_INCOME;

/*==============================================================*/
/* Table: T_TRANS_INCOME                                        */
/*==============================================================*/
create table T_TRANS_INCOME
(
   ID                   varchar(32) not null,
   APPLY_NO             varchar(32),
   TRANS_NO             varchar(32) not null,
   TRANS_TYPE           char not null comment 'C信用卡交易',
   CUST_ID              varchar(20) not null,
   CUST_NAME            varchar(20),
   BANK_NO              varchar(20) not null,
   TRANS_TIME           timestamp,
   INCOME_AMT           numeric(10,2),
   INCOME_STATUS        varchar(10) not null comment 'PLAN计划收益，ACTUAL实际收益',
   CTREATED_TIME        timestamp default CURRENT_TIMESTAMP,
   primary key (ID)
);

drop table if exists T_PAYMENT_APPLY;

/*==============================================================*/
/* Table: T_PAYMENT_APPLY                                       */
/*==============================================================*/
create table T_PAYMENT_APPLY
(
   ID                   varchar(32) not null,
   CUST_ID              varchar(20) not null,
   CARD_CODE            varchar(20) not null,
   PAYMENT_AMT          bigint not null,
   PAYMENT_DATE         varchar(200) not null,
   CREATED_TIME         timestamp default CURRENT_TIMESTAMP,
   primary key (ID)
);

drop table if exists T_PAYMENT_PLAN;

/*==============================================================*/
/* Table: T_PAYMENT_PLAN                                        */
/*==============================================================*/
create table T_PAYMENT_PLAN
(
   ID                   varchar(32) not null,
   CUST_ID              varchar(20) not null,
   APPLY_ID             varchar(32) not null,
   CARD_NO              varchar(20) not null,
   PAYMENT_AMT          bigint not null,
   PAYMENT_TIME         timestamp not null,
   PLAN_STATUS          varchar(20) not null comment '状态（INIT待确认、CONFIRMED已生效、CANCELED计划被撤销、REPAY_ING 还款中、REPAY_SUCCESS还款成功、REPAY_FAIL 还款失败）',
   TRANS_NO             varchar(32) not null,
   CREATED_TIME         timestamp,
   primary key (ID)
);

drop table if exists T_INTERFACE_LOG;

/*==============================================================*/
/* Table: T_INTERFACE_LOG                                       */
/*==============================================================*/
create table T_INTERFACE_LOG
(
   ID                   varchar(32) not null,
   BUSI_TYPE            varchar(20),
   CUST_ID              varchar(20),
   CUST_NAME            varchar(30),
   CARD_NO              varchar(20),
   SERIAL_NO            varchar(24) not null,
   INPUT_PARAMS         varchar(4000) not null,
   OUT_PARAMS           varchar(4000) not null,
   CREATED_TIME         timestamp default CURRENT_TIMESTAMP,
   primary key (ID)
);

drop table if exists T_CREDIT_TRANS_RECORD;

/*==============================================================*/
/* Table: T_CREDIT_TRANS_RECORD                                 */
/*==============================================================*/
create table T_CREDIT_TRANS_RECORD
(
   ID                   varchar(32) not null,
   CUST_ID              varchar(20),
   CUST_NAME            varchar(30),
   CARD_NO              varchar(20),
   APPLY_NO             varchar(24),
   PLAN_NO              varchar(24),
   SERIAL_NO            varchar(24),
   TRANS_TYPE           varchar(20),
   TRANS_AMT            numeric(10,2),
   TRANS_DATE           timestamp,
   ACTUAL_AMT           numeric(10,2),
   TRANS_STATUS         varchar(20),
   CREATED_TIME         timestamp default CURRENT_TIMESTAMP,
   primary key (ID)
);

