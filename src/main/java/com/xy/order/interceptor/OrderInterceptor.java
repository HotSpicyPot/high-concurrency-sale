package com.xy.order.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xy.order.common.Response;
import com.xy.order.common.ResponseCode;
import com.xy.order.dao.Cache.ProductCacheDao;
import com.xy.order.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

@Controller
public class OrderInterceptor implements HandlerInterceptor {

    @Autowired
    private ProductService productService;

    private static Logger logger=LoggerFactory.getLogger(OrderInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Integer seckillId = Integer.valueOf((String)pathVariables.get("seckillId"));
        String phone=request.getParameter("phone");

        logger.info("user {} trying to buy product {}", phone, seckillId);

        // illegal arguments
        if (phone==null){
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer=response.getWriter();
            ObjectMapper mapper=new ObjectMapper();
            String jsonStr=mapper.writeValueAsString(Response.createByError(ResponseCode.ILLEGAL_ARGUMENT));
            writer.write(jsonStr);
            return false;
        }
        // product not exists
        if (!productService.checkExistence(seckillId)){
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer=response.getWriter();
            ObjectMapper mapper=new ObjectMapper();
            String jsonStr=mapper.writeValueAsString(Response.createByError(ResponseCode.PRODUCT_NOT_EXISTS));
            writer.write(jsonStr);
            return false;
        }
        // this phone has purchased one
        if (productService.checkPhoneAndSeckill(seckillId, phone)){
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer=response.getWriter();
            ObjectMapper mapper=new ObjectMapper();
            String jsonStr=mapper.writeValueAsString(Response.createByError(ResponseCode.REPEAT_PURCHASE));
            writer.write(jsonStr);
            return false;
        }
        // product sold out
        if (productService.getStock(seckillId)<=0){
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer=response.getWriter();
            ObjectMapper mapper=new ObjectMapper();
            String jsonStr=mapper.writeValueAsString(Response.createByError(ResponseCode.OUT_OF_STOCK));
            writer.write(jsonStr);
            return false;
        }

        return true;
    }
}
