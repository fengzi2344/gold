package com.goldgyro.platform.core.comm.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.goldgyro.platform.core.client.service.AlgorithmService;

@Component(value="calcTwoLevelIncome")
public class TwoLevelIncomeAlgorithm  implements IncomeAlgorithm{
	@Autowired
	private AlgorithmService algorithmSerivce;
	
	@Override
	public void execute() {
		algorithmSerivce.calcTwoLevelIncome();
	}
}
