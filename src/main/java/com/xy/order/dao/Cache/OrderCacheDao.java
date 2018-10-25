package com.xy.order.dao.Cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.xy.order.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderCacheDao {

    private JedisPool jedisPool;
    private Jedis jedis;

    private static Logger logger=LoggerFactory.getLogger(OrderCacheDao.class);

    private static final String LIST="order";

    private RuntimeSchema<Order> schema=RuntimeSchema.createFrom(Order.class);

    public OrderCacheDao(){
        jedisPool=new JedisPool();
        jedis=jedisPool.getResource();
    }

    public void pushToList(Order order){
        try{
            byte[] orderBytes=ProtostuffIOUtil.toByteArray(order, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            jedis.lpush(LIST.getBytes(), orderBytes);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public Order popFromList(){
        try {
            if (jedis.llen(LIST)<1)
                return null;
            byte[] orderBytes= jedis.rpop(LIST.getBytes());
            Order order=schema.newMessage();
            ProtostuffIOUtil.mergeFrom(orderBytes, order, schema);
            return order;
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }

}
