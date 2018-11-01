package com.goldgyro.platform.core.client.service;

import com.goldgyro.platform.core.client.entity.Dictionary;

import java.util.List;
import java.util.Map;

public interface DictionaryService {
    Dictionary findById(String id);
    List<Dictionary> findByParentCode(String parentCode);
    List<Map<String,String>> findByTpeAndLevel(String li,String type);
    Dictionary findByCode(String code);
}
