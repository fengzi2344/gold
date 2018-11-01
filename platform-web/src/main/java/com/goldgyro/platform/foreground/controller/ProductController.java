package com.goldgyro.platform.foreground.controller;

import com.goldgyro.platform.core.client.service.ProductService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ResourceLoader resourceLoader;

    @RequestMapping("/list")
    public InterfaceResponseInfo productList(){
        return new InterfaceResponseInfo(productService.findAll());
    }

    @GetMapping("/procductPic")
    public ResponseEntity<?> downloadFile(String fileName)
            throws IOException {
        return ResponseEntity.ok(resourceLoader.getResource("classpath:productPic/"+fileName+".png"));
    }
}
