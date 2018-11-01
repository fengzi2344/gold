drop PROCEDURE calcLevelOfIncome;
create procedure calcLevelOfIncome()
begin
	declare maxCreateTime TIMESTAMP;
	
	#获取最大时间点
	select max(ti.CREATED_TIME)  from t_trans_income ti  into maxCreateTime;
	
	#计算收益
	insert into T_CUST_INCOME_DETAIL(CUST_ID, CUST_NAME, CONTRIBUTOR_ID, CONTRIBUTOR_NAME, TRANS_TYPE, TRANS_INCOME_ID, INCOME_AMT)
	(select cust.inviterId as CUST_ID, cust.inviterName as CUST_NAME,
				 cust.custId as CONTRIBUTOR_ID, cust.custName as CONTRIBUTOR_NAME, ti.TRANS_TYPE,
				 ti.ID as TRANS_INCOME_ID, ti.INCOME_AMT*(0.0002/(tfr.CFEE_RATE-tfr.OUT_RATE)) as INCOME_AMT
	from	(select ci.CUST_ID as inviterId, ci.CUST_NAME as inviterName, 
								ci1.CUST_ID as custId, ci1.CUST_NAME as custName, ci1.CUST_LEVEL_SAMPLE as levelSample
				 from T_CUSTOMER_INFO ci left join T_CUSTOMER_INFO ci1 on (ci.CUST_ID=ci1.INVITER_ID and ci.CUST_LEVEL_SAMPLE = ci1.CUST_LEVEL_SAMPLE)
				 where ci1.CUST_ID is not null and ci.CUST_LEVEL_SAMPLE<>'NORMAL') cust, T_TRANS_INCOME ti, T_TRANS_FEE_RATE tfr
	where cust.custId = ti.CUST_ID and cust.levelSample = tfr.CLEVEL_SAMPLE
				and ti.CREATED_TIME > (select ac.next_end_time from t_algorithm_config ac where ac.ALGORITHM_SAMPLE='CUST_LEVELOF_INCOME') and ti.CREATED_TIME <= maxCreateTime);
		
		
	#存储最大时间
	update t_algorithm_config ac set ac.next_end_time = maxCreateTime where ac.ALGORITHM_SAMPLE='CUST_LEVELOF_INCOME';
	
	commit;
	
end;