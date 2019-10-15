package com.itdr.services;

import com.itdr.common.ServerResponse;

public interface OrderService {
    //创建订单
    ServerResponse createOrder(Integer uid, Integer shippingId);

    //获取订单详情信息
    ServerResponse getOrderItems(Integer uid, Long orderNo);

    //获取用户订单列表
    ServerResponse getOrderList(Integer uid, Integer pageNum, Integer pageSize);

    //用户取消订单
    ServerResponse countermandOrder(Integer id, Long orderNo);
}
