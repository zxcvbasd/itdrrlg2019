package com.itdr.mappers;

import com.itdr.pojo.Shipping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);
    //添加一条收货地址
    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    //根据ID查询用户的地址
    Shipping selectByIdAndUid(@Param("id") Integer id, @Param("uid") Integer uid);
    //根据用户ID查询用户所有的地址
    List<Shipping> selectByLisrUid(@Param("uid") Integer uid);

}