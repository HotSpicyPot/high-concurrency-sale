package com.xy.order.event;

import com.xy.order.entity.Order;

public interface EventHandler {
    void doHandle(Order order);
}
