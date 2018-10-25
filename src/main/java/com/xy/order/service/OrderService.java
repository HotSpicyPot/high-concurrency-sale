package com.xy.order.service;

import com.xy.order.common.Response;

public interface OrderService {
    Response makeOrder(Integer seckillId, String phone);
}
