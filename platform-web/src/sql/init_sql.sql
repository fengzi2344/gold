#初始化根账户，用于分享其他代理账户
insert t_customer_info(ID, CUST_ID, CUST_PASSWORD, CUST_NAME) values(REPLACE(UUID(), '-', ''), 'root', 'goldgyro', '根账户');


#初始化费率
delete from T_TRANS_FEE_RATE where id='1';
insert T_TRANS_FEE_RATE(ID, CLEVEL_SAMPLE, TTYPE_CODE, CFEE_RATE, OUT_RATE, DESCRIPTION) values(1, 'NORMAL', 'C', 0.0068, 0.0045, '普通客户费率');
delete from T_TRANS_FEE_RATE where id='2';
insert T_TRANS_FEE_RATE(ID, CLEVEL_SAMPLE, TTYPE_CODE, CFEE_RATE, OUT_RATE, DESCRIPTION) values(2, 'VIP', 'C', 0.0058, 0.0045, 'VIP会员');
delete from T_TRANS_FEE_RATE where id='3';
insert T_TRANS_FEE_RATE(ID, CLEVEL_SAMPLE, TTYPE_CODE, CFEE_RATE, OUT_RATE, DESCRIPTION) values(3, 'AGENT', 'C', 0.0056,  0.0045, '代理');