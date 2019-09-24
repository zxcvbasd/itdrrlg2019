package com.itdr.services.impl;

import com.itdr.common.ServerResponse;
import com.itdr.mappers.*;
import com.itdr.pojo.*;
import com.itdr.pojo.vo.OrderItemVO;
import com.itdr.pojo.vo.OrderVO;
import com.itdr.pojo.vo.ShippingVO;
import com.itdr.services.OrderService;
import com.itdr.utils.BigDecimalUtils;
import com.itdr.utils.PoToVoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderitemMapper orderitemMapper;
    @Autowired
    ShippingMapper shippingMapper;

    //创建订单
    @Override
    public ServerResponse createOrder(Integer uid, Integer shippingId) {
        //参数判断
        if (shippingId == null || shippingId <= 0) {
            return ServerResponse.defeatedRS("非法参数");
        }

        //存储订单选中的商品数据
        List<Product> productList = new ArrayList<>();

        //获取用户购物车选中的商品数据
        List<Cart> li = cartMapper.selectByUidAll(uid);
        if (li.size() == 0) {
            return ServerResponse.defeatedRS("至少选中一件商品");
        }
//        BigDecimal payment = this.getPayment(li);
//        if (payment.equals(new BigDecimal(0))) {
//            return ServerResponse.defeatedRS("商品不存在");
//        }

        //获取用户地址信息
        Shipping shipping = shippingMapper.selectByIdAndUid(shippingId,uid);
        if (shipping == null){
            return ServerResponse.defeatedRS("用户收获地址不存在");
        }

        //计算订单总价
        BigDecimal payment = new BigDecimal("0");
        for (Cart cart : li) {
            //判断商品是否失效
            Integer productId = cart.getProductId();
            //根据商品ID获取商品数据
            Product p = productMapper.selectByProductId(productId);
            if (p == null){
                return ServerResponse.defeatedRS("商品不存在");
            }
            if (p.getStatus() != 1) {
                return ServerResponse.defeatedRS(p.getName() + "商品已下架");
            }
            //校验库存
            if (cart.getQuantity() > p.getStock()) {
                return ServerResponse.defeatedRS(p.getName() + "超出库存数量");
            }

            //根据购物车购物数量和商品单价计算一条购物车信息的总价
            BigDecimal mul = BigDecimalUtils.mul(p.getPrice().doubleValue(), cart.getQuantity());
            //把每一条购物车信息总价相加，就是订单总价
            payment = BigDecimalUtils.add(payment.doubleValue(), mul.doubleValue());
            //放在集合中备用
            productList.add(p);
        }

        //创建订单，没有问题要存到数据库中，这里要使用批量插入的方式
        Order order = this.getOrder(uid, shippingId);
        int insert = orderMapper.insert(order);
        if (insert <= 0) {
            return ServerResponse.defeatedRS(order.getOrderNo() + "订单创建失败");
        }

        //创建订单详情，没有问题要存到数据库中，这里要使用批量插入的方式
        List<OrderItem> orderItemList = this.getOrderItem(uid, order.getOrderNo(), productList, li);
        int insertAll = orderitemMapper.insertAll(orderItemList);
        if (insertAll <= 0) {
            return ServerResponse.defeatedRS(order.getOrderNo() + "订单详情创建失败");
        }

        //插入成功，减少商品库存
        for (OrderItem item : orderItemList) {
            for (Product product : productList) {
                if (item.getProductId() == product.getId()) {
                    Integer count = product.getStock() - item.getQuantity();
                    if (count < 0) {
                        return ServerResponse.defeatedRS("库存不能为负数");
                    }
                    product.setStock(count);
                    //更新数据到数据库中
                    int inProduct = productMapper.updateById(product);
                    if (inProduct < 0) {
                        return ServerResponse.defeatedRS("更新商品库存失败");
                    }
                }
            }
        }
        //清空购物车
        int cartDelete = cartMapper.deleteAllByIdAndUid(li,uid);
        if (cartDelete <= 0) {
            return ServerResponse.defeatedRS("清空购物车失败");
        }

        //拼接VO类，返回数据
        List<OrderItemVO> itemVOList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = PoToVoUtil.orderItemToOrderItemVo(orderItem);
            itemVOList.add(orderItemVO);
        }

        //封装地址VO类
        ShippingVO shippingVO = PoToVoUtil.shippingToShippingVO(shipping);

        OrderVO orderVO = new OrderVO();
        orderVO.setOrderItemVoList(itemVOList);
        orderVO.setShippingVO(shippingVO);
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setShippingId(shippingId);
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        orderVO.setPostage(order.getPostage());
        orderVO.setStatus(order.getStatus());
        orderVO.setPaymentTime(order.getPaymentTime());
        orderVO.setSendTime(order.getSendTime());
        orderVO.setEndTime(order.getEndTime());
        orderVO.setCloseTime(order.getCloseTime());
        orderVO.setCreateTime(order.getCreateTime());


        return ServerResponse.successRS(orderVO);
    }

    //创建一个订单对象
    private Order getOrder(Integer uid, Integer shippingId) {
        Order o = new Order();
        o.setUserId(uid);
        o.setOrderNo(this.getOrderNo());
        o.setShippingId(shippingId);
        o.setPaymentType(1);
        o.setPostage(0);
        o.setStatus(10);
        return o;
    }

    //创建一个订单详情对象
    private List<OrderItem> getOrderItem(Integer uid, Long orderNo, List<Product> productList, List<Cart> li) {
        List<OrderItem> itemList = new ArrayList<>();

        for (Cart cart : li) {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(cart.getQuantity());
            for (Product product : productList) {
                if (product.getId().equals(cart.getProductId())) {
                    orderItem.setUserId(uid);
                    orderItem.setOrderNo(orderNo);
                    orderItem.setProductId(product.getId());
                    orderItem.setProductName(product.getName());
                    orderItem.setProductImage(product.getMainImage());
                    orderItem.setCurrentUnitPrice(product.getPrice());
                    //根据购物车购物数量和商品单价计算一条购物车信息的总价
                    BigDecimal mul = BigDecimalUtils.mul(product.getPrice().doubleValue(), cart.getQuantity());
                    orderItem.setTotalPrice(mul);
                    itemList.add(orderItem);
                }
            }
        }

        return itemList;
    }

    //计算订单总价
    private BigDecimal getPayment(List<Cart> li) {
        return null;
    }

    //生成订单编号
    private Long getOrderNo() {
        long l = System.currentTimeMillis();
        long orderNo = l + Math.round(Math.random() * 100);
        return orderNo;
    }
}
