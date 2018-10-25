package com.xy.order.service.impl;

import com.xy.order.common.Response;
import com.xy.order.common.ResponseCode;
import com.xy.order.dao.Cache.ProductCacheDao;
import com.xy.order.entity.Product;
import com.xy.order.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductCacheDao productCacheDao;


    @Override
    public Response<List<Product>> getAllProducts(){
        List<Product> productList=productCacheDao.getAllProducts();
        return Response.createBySuccess(productList);
    }

    @Override
    public Response<Product> getProduct(Integer seckillId){
        if (seckillId==null)
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        Product product=productCacheDao.getProduct(seckillId);
        return Response.createBySuccess(product);
    }

    public boolean checkExistence(Integer seckillId){
        return productCacheDao.checkExistence(seckillId);
    }

    public boolean checkPhoneAndSeckill(Integer seckillId, String phone){
        return productCacheDao.checkPhoneAndSeckill(seckillId, phone);
    }

    public int getStock(Integer seckillId){
        return productCacheDao.getStock(seckillId);
    }
}
