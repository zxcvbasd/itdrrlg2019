package com.itdr.controllers.portal;

import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.pojo.Shipping;
import com.itdr.pojo.Users;
import com.itdr.services.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/portal/site/")
public class ShippingController {
    @Autowired
    ShippingService shippingService;

    @RequestMapping("all.do")
    //查询所有的地址
    public ServerResponse All(HttpSession session){
        Users user = (Users) session.getAttribute(Const.LOGINUSER);
        if (user == null){
            return ServerResponse.defeatedRS(Const.UsersEnum.NEED_LOGIN.getDesc());
        }
        ServerResponse sr =  shippingService.All(user.getId());
        return sr;
    }
    @RequestMapping("add.do")
    //新增地址
    public ServerResponse addShipping(HttpSession session,Shipping shipping){
        Users user = (Users) session.getAttribute(Const.LOGINUSER);
        if (user == null){
            return ServerResponse.defeatedRS(Const.UsersEnum.NEED_LOGIN.getDesc());
        }
        ServerResponse sr =  shippingService.addShipping(user.getId(),shipping);
        return sr;
    }
    @RequestMapping("update_shipping.do")
    //修改地址中的信息
    public ServerResponse updateShipping(HttpSession session,Shipping shipping){
        Users user = (Users) session.getAttribute(Const.LOGINUSER);
        if (user==null){
            return ServerResponse.defeatedRS(Const.UsersEnum.NO_LOGIN.getDesc());
        }
        ServerResponse sr =   shippingService.updateShipping(user.getId(),shipping);
        return sr;
    }
    @RequestMapping("delete_shipping.do")
    //修改地址中的信息
    public ServerResponse deleteShipping(HttpSession session,Integer id){
        Users user = (Users) session.getAttribute(Const.LOGINUSER);
        if (user==null){
            return ServerResponse.defeatedRS(Const.UsersEnum.NO_LOGIN.getDesc());
        }
        ServerResponse sr =   shippingService.deleteShipping(user.getId(),id);
        return sr;
    }
}
