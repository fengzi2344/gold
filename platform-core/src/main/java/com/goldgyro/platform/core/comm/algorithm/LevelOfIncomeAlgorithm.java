package com.goldgyro.platform.core.comm.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.goldgyro.platform.core.client.service.AlgorithmService;


@Component(value="calcLevelOfIcome")
public class LevelOfIncomeAlgorithm implements IncomeAlgorithm{
	
	@Autowired
	private AlgorithmService algorithmSerivce;
	
	public void execute() {
		algorithmSerivce.calcLevelOfIncome();
	}
}
