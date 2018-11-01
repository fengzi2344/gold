package com.goldgyro.platform.foreground.controller;

import com.goldgyro.platform.core.client.service.DictionaryService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 字典
 */
@RestController
@RequestMapping(value = "/dictionarty")
public class DictionaryController {
    @Autowired
    private DictionaryService dictionaryService;

    @RequestMapping(value = "/getAnge", method = {RequestMethod.GET, RequestMethod.POST})
    public InterfaceResponseInfo getAnge(@RequestParam(value = "parentCode", defaultValue = "000000") String parentCode, @RequestParam(value = "bankName", defaultValue = "%%") String bankName) {
        List<Map<String, String>> objs = dictionaryService.findByTpeAndLevel("%" + bankName + "%", parentCode);
        return new InterfaceResponseInfo(objs);
    }
}
