package com.xy.order.dao.Cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.xy.order.dao.ProductMapper;
import com.xy.order.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ProductCacheDao {

    @Autowired
    private ProductMapper productMapper;

    private static JedisPool jedisPool;
    private static Jedis jedis;

    private static Logger logger=LoggerFactory.getLogger(ProductCacheDao.class);

    private static final String PREFIX="product";
    private static final String STOCK="stock";
    private static final String PHONE="phone";

    private RuntimeSchema<Product> schema=RuntimeSchema.createFrom(Product.class);

    public ProductCacheDao(){
        jedisPool = new JedisPool();
        jedis=jedisPool.getResource();
    }

    public void preloadAllProducts(){
        List<Product> productList=productMapper.selectAll();
        for (Product product:productList)
            putProduct(product);
    }

    public List<Product> getAllProducts(){
        try{
            List<Product> productList=new ArrayList<>();
            Set<String> productKeySet=jedis.keys((PREFIX+"*"));
            for (String productKey:productKeySet){
                if (productKey!=null){
                    byte[] productBytes=jedis.get(productKey.getBytes());
                    Product product=schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(productBytes, product, schema);
                    product.setNumber(Integer.valueOf(jedis.get(STOCK+product.getSeckillId())));
                    productList.add(product);
                }
            }
            return productList;
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }

    public boolean deleteProduct(Integer sekillId){
        try {
            String productKey=PREFIX+sekillId;
            String stockKey=STOCK+sekillId;
            jedis.del(productKey.getBytes());
            jedis.del(stockKey.getBytes());
            return true;
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return false;
    }

    public Product getProduct(Integer sekillId){
        try {
            String productKey=PREFIX+sekillId;
            if (!checkExistence(sekillId))
                return null;
            String stockKey=STOCK+sekillId;
            byte[] productBytes=jedis.get(productKey.getBytes());
            String stock=jedis.get(stockKey);
            if (productBytes!=null){
                Product product=schema.newMessage();
                ProtostuffIOUtil.mergeFrom(productBytes, product, schema);
                product.setNumber(Integer.valueOf(stock));
                return product;
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }

    public void putProduct(Product product){
        try {
            int timeout=60*60*24;
            String productKey=PREFIX+product.getSeckillId();
            String stockKey=STOCK+product.getSeckillId();
            jedis.setex(stockKey, timeout, String.valueOf(product.getNumber()));
            product.setNumber(0);  // useless in cache
            byte[] productBytes=ProtostuffIOUtil.toByteArray(product, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            jedis.setex(productKey.getBytes(), timeout, productBytes);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public int getStock(Integer seckillId){
        String key=STOCK+seckillId;
        String stock=jedis.get(key);
        return Integer.valueOf(stock);
    }

    // use lua scripts to guarantee the atomic of operations.
    /*
    local val=redis.call('GET',KEYS[1]);
    if val>0 then
        redis.call("DECR", KEYS[1])
        return 1;
    end
    return 0;
     */
    public boolean reduceStock(Integer seckillId){
        String luaScrpts="    local val=redis.call('GET',KEYS[1]);\n" +
                "    if tonumber(val)>0 then\n" +
                "        redis.call('DECR', KEYS[1])\n" +
                "        return 1;\n" +
                "    end\n" +
                "    return 0;";
        try{
            Object result=jedis.eval(luaScrpts, 1, STOCK+seckillId);
            if (Integer.parseInt(result.toString())==1){
                return true;
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return false;
    }

    public void putPhoneAndSeckill(Integer seckillId, String phone){
        try{
            String member=String.valueOf(seckillId)+"-"+phone;
            jedis.sadd(PHONE, member);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public boolean checkPhoneAndSeckill(Integer seckillId, String phone){
        try{
            String member=String.valueOf(seckillId)+"-"+phone;
            return jedis.sismember(PHONE, member);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return true;
    }

    public boolean checkExistence(Integer seckillId){
        try{
            String key=PREFIX+seckillId;
            return jedis.exists(key);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return false;
    }


}
