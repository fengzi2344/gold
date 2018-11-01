package com.goldgyro.platform.core.client.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_PRODUCT")
public class Product {
    @Id
    private String id;
    @Column(name = "PRODUCT_NAME", length = 255)
    private String productName;
    @Column(name = "PRICE", length = 10)
    private Integer price;
    @Column(name = "PRODUCT_PIC", length = 255)
    private String productPic;
    @Column(name = "DESCRIPTION", length = 1024)
    private String description;

    public Product(String id, String productName, Integer price, String productPic, String description) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.productPic = productPic;
        this.description = description;
    }

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getProductPic() {
        return productPic;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
