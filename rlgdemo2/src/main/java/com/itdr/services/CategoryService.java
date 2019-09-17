package com.itdr.services;

import com.itdr.common.ServerResponse;


public interface CategoryService {
    //根据分类ID查询所有子类（包括本身）
    ServerResponse getDeepCategory(Integer categoryId);

    //修改品类名字
    ServerResponse setCategoryName(Integer categoryId,String categoryName);

    //增加节点
    ServerResponse addCategory(Integer parentId,String categoryName);

    //获取品类子节点（平级）
    ServerResponse getCategory(Integer categoryId);
}


