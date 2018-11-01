package com.goldgyro.platform.core.client.dao;

import com.goldgyro.platform.core.client.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductDao extends JpaRepository<Product,String >,JpaSpecificationExecutor<Product> {
    @Query(nativeQuery = true, value = "select * from t_product")
    List<Product> findAll();
}
