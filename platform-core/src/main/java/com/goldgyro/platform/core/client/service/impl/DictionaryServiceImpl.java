package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.DictionaryDao;
import com.goldgyro.platform.core.client.entity.Dictionary;
import com.goldgyro.platform.core.client.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DictionaryServiceImpl implements DictionaryService {
    @Autowired
    private DictionaryDao dictionaryDao;
    @Override
    public Dictionary findById(String id) {
        return dictionaryDao.findOne(id);
    }

    @Override
    public List<Dictionary> findByParentCode(String parentCode) {
        return dictionaryDao.findAllByParentCodeOrderByCodeAsc(parentCode);
    }

    @Override
    public List<Map<String,String>> findByTpeAndLevel(String li,String type) {
        return dictionaryDao.findByTypeAndLevelOrderByCode(li,type);
    }

    @Override
    public Dictionary findByCode(String code) {
        return dictionaryDao.finByCode(code);
    }
}
