package com.xy.order.event;


import com.xy.order.dao.Cache.OrderCacheDao;
import com.xy.order.dao.Cache.ProductCacheDao;
import com.xy.order.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer implements CommandLineRunner {

    @Autowired
    private OrderCacheDao orderCacheDao;

    @Autowired
    private ProductCacheDao productCacheDao;

    @Autowired
    private EventHandler eventHandler;

    private static Logger logger=LoggerFactory.getLogger(EventConsumer.class);


    @Override
    public void run(String... args) throws Exception {
        logger.info("Preloading all products...............");
        productCacheDao.preloadAllProducts();
        logger.info("start to put order into database");

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Order order=orderCacheDao.popFromList();
                    if (order==null){
                        try{
                            Thread.sleep(1000);
                        }catch (Exception e){
                            logger.error(e.getMessage());
                        }
                    }
                    else
                        eventHandler.doHandle(order);
                }
            }
        });
        thread.run();
    }
}
