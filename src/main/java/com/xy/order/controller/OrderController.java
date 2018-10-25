package com.xy.order.controller;

import com.xy.order.common.Response;
import com.xy.order.service.OrderService;
import com.xy.order.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    @RequestMapping("/{seckillId}")
    public Response makeOrder(@PathVariable("seckillId") Integer seckillId, @RequestParam("phone") String phone){
        return orderService.makeOrder(seckillId, phone);
    }
}
