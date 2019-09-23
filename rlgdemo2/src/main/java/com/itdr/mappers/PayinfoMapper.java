package com.itdr.mappers;

import com.itdr.pojo.Payinfo;

public interface PayinfoMapper {
    int deleteByPrimaryKey(Integer id);

    //插入一条支付信息
    int insert(Payinfo record);

    int insertSelective(Payinfo record);

    Payinfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Payinfo record);

    int updateByPrimaryKey(Payinfo record);
}