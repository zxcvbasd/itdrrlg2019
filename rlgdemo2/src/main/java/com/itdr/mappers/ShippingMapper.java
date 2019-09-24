package com.itdr.mappers;

import com.itdr.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    //根据ID获取用户地址信息
    Shipping selectByIdAndUid(@Param("shippingId") Integer shippingId,@Param("uid") Integer uid);
}