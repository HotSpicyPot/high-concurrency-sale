package com.xy.order.event;

import com.xy.order.dao.OrderMapper;
import com.xy.order.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsertEventHandler implements EventHandler{

    @Autowired
    private OrderMapper orderMapper;

    private static Logger logger=LoggerFactory.getLogger(InsertEventHandler.class);

    public void doHandle(Order order){

        orderMapper.insert(order);
        logger.info("insert order {} into the database", order.getOrderId());
    }
}
