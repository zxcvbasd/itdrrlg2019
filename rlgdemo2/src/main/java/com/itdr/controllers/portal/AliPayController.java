package com.itdr.controllers.portal;

import com.alipay.api.internal.util.AlipaySignature;
import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.pojo.Users;
import com.itdr.pojo.pay.Configs;
import com.itdr.services.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Controller
@ResponseBody
@RequestMapping("/pay/")
public class AliPayController {

    //日志有关

    //注入支付业务层
    @Autowired
    AlipayService aliPayService;

    //订单支付
    @RequestMapping("alipay.do")
    private ServerResponse alipay(Long orderno, HttpSession session) {
        //用户是否登录
        Users users = (Users) session.getAttribute(Const.LOGINUSER);
        if (users == null){
            return ServerResponse.defeatedRS(Const.UsersEnum.NO_LOGIN.getCode(),Const.UsersEnum.NO_LOGIN.getDesc());
        }
        return aliPayService.alipay(orderno, users.getId());
    }

    //查询支付状态
//    @RequestMapping("query_order_pay_status.do")
//    private ServerResponse queryOrderPayStatus(Long orderno, HttpSession session) {
//        //用户是否登录
//        Users users = (Users) session.getAttribute(Const.LOGINUSER);
//        if (users == null){
//            return ServerResponse.defeatedRS(Const.UsersEnum.NO_LOGIN.getCode(),Const.UsersEnum.NO_LOGIN.getDesc());
//        }
//        return aliPayService.queryOrderPayStatus(orderno, users.getId());
//    }

    //支付宝回调
    @RequestMapping("alipay_callback.do")
    private String alipay_callback(HttpServletRequest request) {

        //获取支付宝返回的参数，返回一个map集合
        Map<String, String[]> parameterMap = request.getParameterMap();
        //获取上面集合的键的set集合
        Set<String> strings = parameterMap.keySet();
        //使用迭代器遍历键集合获得值
        Iterator<String> iterator = strings.iterator();
        //创建一个接受参数的集合
        Map<String, String> newMap = new HashMap<>();


        //遍历迭代器，重新组装参数
        while (iterator.hasNext()) {
            //根据键获取parameterMap中的值
            String key = iterator.next();
            String[] strings1 = parameterMap.get(key);
            //遍历值的数组，重新拼装数据
            StringBuffer ss = new StringBuffer("");
            for (int i = 0; i < strings1.length; i++) {
                //如果只有一个元素，就保存一个元素
                //有多个元素时，每个元素之间用逗号隔开
                ss=(i == strings1.length - 1) ? ss.append(strings1[i]):ss.append(strings1[i]+",");
            }
            //把新的数据以键值对的方式放入一个新的集合中
            newMap.put(key,ss.toString());
        }

        //支付宝验签，是不是支付宝发送的请求，避免重复请求
        try {
            //去除参数中的这个参数（官方提示不需要）
            newMap.remove("sign_type");
            //调用支付宝封装的方法进行验签操作，需要返回数据+公钥+编码集+类型定义
            boolean b = AlipaySignature.rsaCheckV2(newMap, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!b) {
                //验签失败，返回错误信息
                return "{'msg':''验签失败}";
                //数据转换成json数据格式
                //数据返回给浏览器
            }
        } catch (Exception e) {
            e.printStackTrace();
            //验签失败，返回错误信息
            return "{'msg':''验签失败}";
        }
        //官方文档中还有很多需要验证的参数
        ServerResponse sr = aliPayService.alipayCallback(newMap);

//            业务层处理完，返回对应的状态信息，这个信息是直接返回给支付宝服务器的，所以必须严格要求准确
            if (sr.isSuccess()) {
                return "SUCCESS";
            } else {
                return "FAILED";
            }
        //数据转换成json数据格式
        //数据返回给浏览器
    }
}

