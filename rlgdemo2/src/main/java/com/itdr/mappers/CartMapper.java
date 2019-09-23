package com.itdr.mappers;

import com.itdr.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    //根据用户ID和商品ID判断数据是否存在
    Cart selectByUidAndProductID(@Param("uid") Integer uid, @Param("productId") Integer productId);

    //根据用户ID查询所有购物数据
    List<Cart> selectByUid(Integer uid);

    //根据用户ID判断用户购物车是否全选
    int selectByUidCheck(@Param("uid") Integer uid, @Param("check") Integer check);

    //根据产品ID从购物车移除产品
    int deleteByProducts(@Param("productIds") List<String> productIds, @Param("id") Integer id);

    //根据ID更新购物车
    int updateById(Cart cartForQuantity);

    //根据ID改变购物车中商品选中状态
    int selectOrUnSelect(@Param("id") Integer id,@Param("check") Integer check, @Param("productId") Integer productId);
}