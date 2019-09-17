package com.itdr.controllers.backend;

import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.pojo.Users;
import com.itdr.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@ResponseBody
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    CategoryService categoryService;

    //根据分类ID查询所有子类（包括本身）
    @RequestMapping("get_deep_category.do")
    public ServerResponse getDeepCategory(Integer categoryId) {
        ServerResponse sr = categoryService.getDeepCategory(categoryId);
        return sr;
    }

    //修改品类名字
    @RequestMapping("set_category_name.do")
    public ServerResponse setCategoryName(Integer categoryId,String categoryName) {
        ServerResponse sr = categoryService.setCategoryName(categoryId,categoryName);
        return sr;
    }

    //增加节点
    @RequestMapping("add_category.do")
    public ServerResponse addCategory(Integer parentId,String categoryName) {
        ServerResponse sr = categoryService.addCategory(parentId,categoryName);
        return sr;
    }

    //获取品类子节点（平级）
    @RequestMapping("get_category.do")
    public ServerResponse getCategory(Integer categoryId,HttpSession session) {
        Users users = (Users)session.getAttribute(Const.LOGINUSER);
        if (users == null){
            return ServerResponse.defeatedRS(Const.UsersEnum.NO_LOGIN.getCode(),Const.UsersEnum.NO_LOGIN.getDesc());
        }

        ServerResponse sr = categoryService.getCategory(categoryId);
        return sr;
    }
}
