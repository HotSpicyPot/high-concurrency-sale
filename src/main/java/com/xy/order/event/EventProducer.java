package com.xy.order.event;

import com.xy.order.dao.Cache.OrderCacheDao;
import com.xy.order.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    @Autowired
    private OrderCacheDao orderCacheDao;

    public boolean fireEvent(Order order){
        try{
            orderCacheDao.pushToList(order);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
