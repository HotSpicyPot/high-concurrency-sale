package com.xy.order.dao;

import com.xy.order.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    int deleteByPrimaryKey(Long seckillId);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Long seckillId);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectAll();
}