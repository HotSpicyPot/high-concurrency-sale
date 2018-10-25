package com.xy.order.controller;

import com.xy.order.common.Response;
import com.xy.order.entity.Product;
import com.xy.order.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public List<Product> productList(){
        Response<List<Product>> response=productService.getAllProducts();
        if (response.isSuccess()){
            return response.getData();
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/{seckillId}", method = RequestMethod.GET)
    public Product product(@PathVariable("seckillId") Integer seckillId){
        Response<Product> response=productService.getProduct(seckillId);
        if (response.isSuccess()){
            return response.getData();
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}
