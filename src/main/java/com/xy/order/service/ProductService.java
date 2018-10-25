package com.xy.order.service;

import com.xy.order.common.Response;
import com.xy.order.entity.Product;

import java.util.List;

public interface ProductService {
    Response<List<Product>> getAllProducts();
    Response<Product> getProduct(Integer seckillId);
    boolean checkExistence(Integer seckillId);
    boolean checkPhoneAndSeckill(Integer seckillId, String phone);
    int getStock(Integer seckillId);
}
