package com.xy.order.service.impl;

import com.xy.order.common.Response;
import com.xy.order.common.ResponseCode;
import com.xy.order.dao.Cache.ProductCacheDao;

import com.xy.order.entity.Order;
import com.xy.order.event.EventProducer;
import com.xy.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductCacheDao productCacheDao;

    @Autowired
    private EventProducer producer;

    public Response makeOrder(Integer seckillId, String phone){
        boolean resultReduceStock=productCacheDao.reduceStock(seckillId);
        if (resultReduceStock){
            // record the phone
            productCacheDao.putPhoneAndSeckill(seckillId,phone);
            // put the order into message queue
            Order order=new Order();
            order.setSeckillId((long)seckillId);
            order.setUserPhone(Long.valueOf(phone));
            order.setState((byte)1);
            producer.fireEvent(order);
        } else {
            return Response.createByError(ResponseCode.OUT_OF_STOCK);
        }
        return Response.createBySuccess();
    }
}
