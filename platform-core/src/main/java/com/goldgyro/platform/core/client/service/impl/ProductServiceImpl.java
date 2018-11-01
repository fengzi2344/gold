package com.goldgyro.platform.core.client.service.impl;

import com.goldgyro.platform.core.client.dao.ProductDao;
import com.goldgyro.platform.core.client.entity.Product;
import com.goldgyro.platform.core.client.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }
}
