package com.itdr.mappers;

import com.itdr.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    //按订单编号查找订单
    Order selectByOrderNo(Long orderno);

    //根据订单编号和用户ID判断数据
    int selectByOrderNoAndUid(@Param("orderno") Long orderno, @Param("uid") Integer uid);

    //根据用户ID获取用户所有订单
    List<Order> selectByUid(Integer uid);

    //根据订单状态取消订单
    int updateToStatus(Order order);
}