drop PROCEDURE calcTwoLevelIncome;
create procedure calcTwoLevelIncome()
begin
	declare maxCreateTime TIMESTAMP;
	
	#获取最大时间点
	select max(ti.CREATED_TIME)  from t_trans_income ti  into maxCreateTime;
	
	#计算收益
	insert into T_CUST_INCOME_DETAIL(CUST_ID, CUST_NAME, CONTRIBUTOR_ID, CONTRIBUTOR_NAME, TRANS_TYPE, TRANS_INCOME_ID, INCOME_AMT)
	(
	select cust.inviterId as CUST_ID, cust.inviterName as CUST_NAME,
				 cust.custId as CONTRIBUTOR_ID, cust.custName as CONTRIBUTOR_NAME, ti.TRANS_TYPE,
				 ti.ID as TRANS_INCOME_ID, ti.INCOME_AMT*((feeRate-inviterFeeRate)/(feeRate-outRate)) as INCOME_AMT
	from (
select ci.CUST_ID as inviterId, ci.CUST_NAME as inviterName, ci.CUST_LEVEL_SAMPLE as inviterLevelSample, ci.CFEE_RATE as inviterFeeRate,
			 ci1.CUST_ID as custId, ci1.CUST_NAME as custName, ci1.CUST_LEVEL_SAMPLE as levelSample, ci1.CFEE_RATE as feeRate, ci1.OUT_RATE as outRate
from (select c1.CUST_ID, c1.CUST_NAME, c1.CUST_LEVEL_SAMPLE ,cl.LEVEL_NO, tfr.CFEE_RATE from T_CUSTOMER_INFO c1, t_customer_level cl,T_TRANS_FEE_RATE tfr where c1.CUST_LEVEL_SAMPLE = cl.LEVEL_SAMPLE and c1.CUST_LEVEL_SAMPLE = tfr.CLEVEL_SAMPLE) as ci 
				left join 
				(select c2.CUST_ID, c2.CUST_NAME, c2.INVITER_ID, cl.LEVEL_NO, c2.CUST_LEVEL_SAMPLE, tfr.CFEE_RATE, tfr.OUT_RATE from T_CUSTOMER_INFO c2, t_customer_level cl ,T_TRANS_FEE_RATE tfr where c2.CUST_LEVEL_SAMPLE = cl.LEVEL_SAMPLE and c2.CUST_LEVEL_SAMPLE = tfr.CLEVEL_SAMPLE) as ci1
				on ci.CUST_ID=ci1.INVITER_ID and ci.level_no = ci1.level_no-1
				where ci1.CUST_ID is not null and ci.CUST_LEVEL_SAMPLE<>'NORMAL'
) cust, T_TRANS_INCOME ti
	where cust.custId = ti.CUST_ID and ti.CREATED_TIME > (select ac.next_end_time from t_algorithm_config ac where ac.ALGORITHM_SAMPLE='CUST_TWOLEVEL_INCOME') and ti.CREATED_TIME <= maxCreateTime
	);
		
		
	#存储最大时间
	update t_algorithm_config ac set ac.next_end_time = maxCreateTime where ac.ALGORITHM_SAMPLE='CUST_TWOLEVEL_INCOME';
	
	commit;
	
end;