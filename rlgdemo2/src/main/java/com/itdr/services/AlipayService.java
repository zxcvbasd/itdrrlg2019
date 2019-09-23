package com.itdr.services;

import com.itdr.common.ServerResponse;

import java.util.Map;

public interface AlipayService {
    //订单支付
    ServerResponse alipay(Long orderno, Integer uid);

    //查询订单支付状态
    ServerResponse selectByOrderNo(Long orderno);

    //回调成功后做的处理
    ServerResponse alipayCallback(Map<String,String> params);
}
