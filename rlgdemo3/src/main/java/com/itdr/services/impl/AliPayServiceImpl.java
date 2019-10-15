package com.itdr.services.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.mappers.OrderMapper;
import com.itdr.mappers.OrderitemMapper;
import com.itdr.mappers.PayinfoMapper;
import com.itdr.pojo.Order;
import com.itdr.pojo.OrderItem;
import com.itdr.pojo.Payinfo;
import com.itdr.pojo.pay.Configs;
import com.itdr.pojo.pay.ZxingUtils;
import com.itdr.services.AlipayService;
import com.itdr.utils.DateUtils;
import com.itdr.utils.JsonUtils;
import com.itdr.utils.PoToVoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AliPayServiceImpl implements AlipayService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderitemMapper orderItemMapper;
    @Autowired
    private PayinfoMapper payInFoMapper;

    //订单支付
    @Override
    public ServerResponse alipay(Long orderno, Integer uid) {
        //参数非空判断
        if (orderno == null || orderno <= 0) {
            return ServerResponse.defeatedRS("非法参数");
        }

        //判断订单是否存在
        Order order = orderMapper.selectByOrderNo(orderno);
        if (order == null) {
            return ServerResponse.defeatedRS("该订单不存在");
        }

        //判断订单状态是否是未付款
        if (order.getStatus() != 10) {
            return ServerResponse.defeatedRS("该订单不是未付款状态");
        }

        //判断订单和用户是否匹配
        int i = orderMapper.selectByOrderNoAndUid(orderno, uid);
        if (i <= 0) {
            return ServerResponse.defeatedRS("订单和用户不匹配");
        }

        //根据订单号查询对应商品详情
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(order.getOrderNo());
        //调用支付宝接口获取支付二维码
        //使用封装方法获得预下单成功后返回的二维码信息串
        //将二维码信息串生成图片，并保存，（需要修改为运行机器上的路径）
        //预下单成功获取返回信息（二维码字符串）
        //返回生成二维码后的图片地址和订单号信息给前端

        //后期图片会放到图片服务器上

        try {
            //使用封装方法获得预下单成功后返回的二维码信息串
            AlipayTradePrecreateResponse response = test_trade_precreate(order, orderItems);
            //成功执行下一步
            if (response.isSuccess()) {
                // 将二维码信息串生成图片，并保存，（需要修改为运行机器上的路径）
                String filePath = String.format(Configs.getSavecode_test() + "qr-%s.png",
                        response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

                //预下单成功返回信息（订单编号+二维码图片地址）
                Map map = new HashMap();
                map.put("orderNo", order.getOrderNo());

                map.put("qrCode", filePath);
                return ServerResponse.successRS(map);

            } else {
                //预下单失败
                return ServerResponse.defeatedRS(Const.PaymentPlatformEnum.ALIPAY_FALSE.getCode(), Const.PaymentPlatformEnum.ALIPAY_FALSE.getDesc());
            }
        } catch (Exception e) {
            //出现异常，预下单失败
            e.printStackTrace();
            return ServerResponse.defeatedRS(Const.PaymentPlatformEnum.ALIPAY_FALSE.getCode(), Const.PaymentPlatformEnum.ALIPAY_FALSE.getDesc());
        }
    }

    //查询支付状态
    @Override
    public ServerResponse selectByOrderNo(Long orderno) {
        return null;
    }

    //支付宝回调
    @Override
    public ServerResponse alipayCallback(Map<String,String> map) {

        //获取ordrNo,就是订单编号
        Long orderNo = Long.parseLong(map.get("out_trade_no"));
        //获取流水号
        String tarde_no = map.get("trade_no");
        //获取支付状态
        String trade_status = map.get("trade_status");
        //获取支付时间
        String payment_time = map.get("gmt_payment");
        //获取订单金额
        BigDecimal totalAmount = new BigDecimal(map.get("total_amount"));
        //获取订单中的用户ID
//        Integer uid = Integer.parseInt(map.get("seller_id"));
//        Integer uid = Integer.parseInt(map.get("uid"));

        //验证订单是否存在
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.defeatedRS("不是要付款的订单");
        }

        //验证订单金额和数据库中订单金额是否相同
        if (!totalAmount.equals(order.getPayment())){
            return ServerResponse.defeatedRS("订单金额不匹配");
        }

        //订单和用户是否匹配
//        int i = orderMapper.selectByOrderNoAndUid(orderNo, uid);
//        if (i <= 0){
//            return ServerResponse.defeatedRS("订单和用户不匹配");
//        }

        //防止支付宝重复回调
        if (order.getStatus() != 10) {
            return ServerResponse.defeatedRS("订单不是未付款状态");
        }

        //交易状态判断
        if (trade_status.equals(Const.TRADE_SUCCESS)) {
            //校验状态码，支付成功
            //更改数据库中订单的状态+更改支付时间+更新时间+删除用过的本地二维码
            order.setStatus(20);
            order.setPaymentTime(DateUtils.strToDate(payment_time));
            orderMapper.updateByPrimaryKey(order);

            //支付成功，删除本地存在的二维码图片
            String str = String.format(Configs.getSavecode_test()+"qr-%s.png",
                    order.getOrderNo());
            File file = new File(str);
            boolean b = file.delete();

            //还可以判断图片删除是否成功
        }

        //保存支付宝支付信息(任何状态都保存)
        Payinfo payInfo = new Payinfo();
        payInfo.setOrderNo(orderNo);
        payInfo.setPayPlatform(Const.PaymentPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformStatus(trade_status);
        payInfo.setPlatformNumber(tarde_no);
        payInfo.setUserId(order.getUserId());

        //将支付信息插入到数据库表中
        int result = payInFoMapper.insert(payInfo);
        if (result <= 0) {
            //支付信息保存失败返回结果
            return ServerResponse.defeatedRS(Const.PaymentPlatformEnum.SAVEPAYMSG_FALSE.getCode(), Const.PaymentPlatformEnum.SAVEPAYMSG_FALSE.getDesc());
        }
        //支付信息保存成功返回结果SUCCESS，让支付宝不再回调
        return ServerResponse.successRS("SUCCESS");
    }

    //测试当面付2.0生成支付二维码
    private AlipayTradePrecreateResponse test_trade_precreate(Order order,List<OrderItem> orderItems)throws Exception {

        //读取配置文件信息
        Configs.init("zfbinfo.properties");

        //实例化支付宝客户端
        AlipayClient alipayClient = new DefaultAlipayClient(Configs.getOpenApiDomain(),
                Configs.getAppid(), Configs.getPrivateKey(), "json", "utf-8",
                Configs.getAlipayPublicKey(), Configs.getSignType());

        //创建API对应的request类
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();

        //获取一个BizContent对象,并转换成json格式
        String str = JsonUtils.obj2String(PoToVoUtil.getBizContent(order, orderItems));
        request.setBizContent(str);
        //设置支付宝回调路径
        request.setNotifyUrl(Configs.getNotifyUrl_test());
        //获取响应,这里要处理一下异常
        AlipayTradePrecreateResponse response = alipayClient.execute(request);

        //返回响应的结果
        return response;
    }

    //查询支付状态
    @Override
    public ServerResponse queryOrderPayStatus(Long orderno, Integer uid) {
        //参数非空判断
        if (orderno == null || orderno <= 0) {
            return ServerResponse.defeatedRS("非法参数");
        }

        //判断订单是否存在
        Order order = orderMapper.selectByOrderNo(orderno);
        if (order == null) {
            return ServerResponse.defeatedRS("非法参数");
        }

        //判断订单状态是否是未付款
        if (order.getStatus() == 20) {
            return ServerResponse.successRS("true");
        }
        return ServerResponse.defeatedRS("该用户并没有该订单,查询无效");
    }
}
