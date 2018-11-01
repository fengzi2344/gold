package com.goldgyro.platform.core.client.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goldgyro.platform.core.client.dao.AlgorithmConfigDao;
import com.goldgyro.platform.core.client.dao.AlgorithmDao;
import com.goldgyro.platform.core.client.domain.AlgorithmConfig;
import com.goldgyro.platform.core.client.entity.AlgorithmConfigPO;
import com.goldgyro.platform.core.client.service.AlgorithmService;

/**
 * 算法配置服务
 * @author wg2993
 */
@Service
public class AlgorithmServiceImpl implements AlgorithmService {
	@Autowired
	private AlgorithmConfigDao algorithmConfigDao;
	
	@Autowired
	private AlgorithmDao algorithmDao; 
	
	
	/**
	 * 获取算法配置
	 *  
	 * @return
	 */
	@Override
	public List<AlgorithmConfig> findAlgorithmConfig() {
		List<AlgorithmConfigPO> algorithmConfigPOList = algorithmConfigDao.findAlgorithmConfigBy();
		
		List<AlgorithmConfig> algorithmConfigList = new ArrayList<AlgorithmConfig>();
		for(AlgorithmConfigPO algorithmConfigPO: algorithmConfigPOList) {
			AlgorithmConfig algorithmConfig = new AlgorithmConfig();
			BeanUtils.copyProperties(algorithmConfigPO, algorithmConfig);
			
			algorithmConfigList.add(algorithmConfig);
		}
		
		return algorithmConfigList;
	}
	
	/**
	 * 计算平级收益
	 */
	@Transactional
	public void calcLevelOfIncome() {
		algorithmDao.calcLevelOfIncome();
	}
	

	/**
	 * 计算下层收益
	 */
	@Override
	public void calcTwoLevelIncome() {
		algorithmDao.calcTwoLevelIncome();
	}

	@Override
	public void calcThreeLevelAlgorithm() {
		algorithmDao.calcThreeLevelAlgorithm();
	}
}
