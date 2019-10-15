package com.itdr.services.impl;

import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.mappers.ShippingMapper;
import com.itdr.pojo.Shipping;
import com.itdr.services.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingServiceImpl implements ShippingService {


    @Autowired
    ShippingMapper shippingMapper;

    //查询所有的地址
    @Override
    public ServerResponse All(Integer id) {
       List<Shipping> li  =  shippingMapper.selectByLisrUid(id);
        return ServerResponse.defeatedRS(li);
    }
    //新增地址
    @Override
    public ServerResponse addShipping(Integer uid, Shipping shipping) {
        if (shipping==null){
            return ServerResponse.defeatedRS(Const.NOT_PARAMETER);
        }
        if ((shipping.getReceiverName()== null||shipping.getReceiverName().equals(""))&&
                (shipping.getReceiverPhone()== null||shipping.getReceiverPhone().equals(""))&&
                (shipping.getReceiverMobile()== null||shipping.getReceiverMobile().equals(""))&&
                (shipping.getReceiverProvince()==null||shipping.getReceiverProvince().equals(""))&&
                (shipping.getReceiverCity()==null||shipping.getReceiverCity().equals(""))&&
                (shipping.getReceiverAddress()==null||shipping.getReceiverAddress().equals(""))){
            return ServerResponse.defeatedRS("参数不可为空");
        }
        List<Shipping> shippings = shippingMapper.selectByLisrUid(uid);
        //判断是否地址数量上限
        if (shippings.size()>= Const.SHIPPING_AMOUNT){
            return ServerResponse.defeatedRS("地址数量上限");
        }
        shipping.setUserId(uid);
        //插入新的地址
        int insert = shippingMapper.insert(shipping);
        if (insert<=0){
            return ServerResponse.defeatedRS("添加失败");
        }
        return ServerResponse.successRS("添加成功");
    }
    //修改地址
    @Override
    public ServerResponse updateShipping(Integer uid,Shipping shipping) {
        if (shipping==null){
            return ServerResponse.defeatedRS(Const.NOT_PARAMETER);
        }
        Shipping shipping1 = shippingMapper.selectByIdAndUid(shipping.getId(),uid );
        if (shipping1==null){
            return ServerResponse.defeatedRS("地址信息不存在");
        }
        int i = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (i<=0){
            return ServerResponse.defeatedRS("更新失败");
        }
        return ServerResponse.successRS("更新成功");
    }

    @Override
    public ServerResponse deleteShipping(Integer uid, Integer id) {
        if (id==null || id<=0){
            return ServerResponse.defeatedRS("非法参数");
        }
        Shipping shipping = shippingMapper.selectByIdAndUid(id, uid);
        if (shipping==null){
            return ServerResponse.defeatedRS("查无此地址");
        }
        int i = shippingMapper.deleteByPrimaryKey(id);
        if (i<=0){
            return ServerResponse.defeatedRS("删除失败");
        }
        return ServerResponse.successRS("删除成功");
    }
}
