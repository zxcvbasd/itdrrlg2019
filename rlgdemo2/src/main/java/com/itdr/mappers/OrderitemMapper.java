package com.itdr.mappers;

import com.itdr.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderitemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    //根据订单号查对应商品详情
    List<OrderItem> selectByOrderNo(Long oid);

    int insertAll(@Param("orderItem")List<OrderItem> orderItem);
}