package com.itdr.controllers.portal;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.Product;
import com.itdr.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/product/")
public class ProductController {

    @Autowired
    ProductService productService;

    //获取商品分类信息
    @PostMapping("topcategory.do")
    public ServerResponse<Product> topCategory(Integer pid) {
        return productService.topCategory(pid);
    }

    //获取一个商品详情
    @PostMapping("detail.do")
    public ServerResponse<Product> detail(Integer productId,
                                          @RequestParam(value = "is_new",required = false,defaultValue = "0") Integer is_new,
                                          @RequestParam(value = "is_hot",required = false,defaultValue = "0") Integer is_hot,
                                          @RequestParam(value = "is_banner",required = false,defaultValue = "0") Integer is_banner) {
        return productService.detail(productId,is_new,is_hot,is_banner);
    }

    //获取最新，最热，banner商品详情
    @PostMapping("detailNewOrHotOrBanner.do")
    public ServerResponse<Product> detailNewOrHotOrBanner(
                                          @RequestParam(value = "is_new",required = false,defaultValue = "0") Integer is_new,
                                          @RequestParam(value = "is_hot",required = false,defaultValue = "0") Integer is_hot,
                                          @RequestParam(value = "is_banner",required = false,defaultValue = "0") Integer is_banner) {
        return productService.detailNewOrHotOrBanner(is_new,is_hot,is_banner);
    }

    //商品搜索+动态排序
    @PostMapping("list.do")
    public ServerResponse<Product> listProduct(Integer productId,String keyword,
                                             @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                             @RequestParam(value = "orderBy",required = false,defaultValue = "") String orderBy) {
        return productService.listProduct(productId,keyword,pageNum,pageSize,orderBy);
    }
}