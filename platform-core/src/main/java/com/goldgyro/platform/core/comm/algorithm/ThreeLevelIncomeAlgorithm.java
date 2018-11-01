package com.goldgyro.platform.core.comm.algorithm;

import org.springframework.beans.factory.annotation.Autowired;

import com.goldgyro.platform.core.client.service.AlgorithmService;

public class ThreeLevelIncomeAlgorithm implements IncomeAlgorithm{

	@Autowired
	private AlgorithmService algorithmSerivce;
	
	@Override
	public void execute() {
		algorithmSerivce.calcThreeLevelAlgorithm();
	}
	

}
