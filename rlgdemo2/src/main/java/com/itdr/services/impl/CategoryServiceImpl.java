package com.itdr.services.impl;

import com.itdr.common.ServerResponse;
import com.itdr.mappers.CategoryMapper;
import com.itdr.pojo.Category;
import com.itdr.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    //根据分类ID查询所有的子类（包括本身）
    @Override
    public ServerResponse getDeepCategory(Integer categoryId) {
        if (categoryId == null || categoryId < 0) {
            return ServerResponse.defeatedRS("非法的参数");
        }

        List<Integer> li = new ArrayList<>();
        li.add(categoryId);
        getAll(categoryId, li);

        ServerResponse rs = ServerResponse.successRS(li);
        return rs;
    }

    private void getAll(Integer pid, List<Integer> list) {

        List<Category> li = categoryMapper.selectByParentId(pid);

        if (li != null && li.size() != 0) {
            for (Category categorys : li) {
                list.add(categorys.getId());
                getAll(categorys.getId(), list);
            }
        }
    }

    //修改品类名字
    @Override
    public ServerResponse setCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || categoryId < 0) {
            return ServerResponse.defeatedRS("参数错误");
        }
        if (categoryName == null || categoryName.equals("")) {
            return ServerResponse.defeatedRS("品类名为空");
        }
        //查询ID是否存在
        int a = categoryMapper.selectById(categoryId);
        if (a <= 0) {
            return ServerResponse.defeatedRS("分类不存在");
        }
        int s = categoryMapper.updateByIdAndName(categoryId, categoryName);
        if (s <= 0) {
            return ServerResponse.defeatedRS("更新品类名字失败");
        }
        return ServerResponse.successRS("更新品类名字成功");
    }

    //增加节点
    @Override
    public ServerResponse addCategory(Integer parentId, String categoryName) {
        if (parentId == null || parentId < 0) {
            return ServerResponse.defeatedRS("非法的参数");
        }
        if (categoryName == null || categoryName.equals("")) {
            return ServerResponse.defeatedRS("品类名为空");
        }

        //判断节点是否已经存在
        int a = categoryMapper.selectById(parentId);
        if (a > 0) {
            return ServerResponse.defeatedRS("节点已存在");
        }
        int s = categoryMapper.addCategoryByIdAndName(parentId, categoryName);
        if (s <= 0) {
            return ServerResponse.defeatedRS("添加品类失败");
        }
        return ServerResponse.successRS("添加品类成功");
    }

    //获取品类子节点（平级）
    @Override
    public ServerResponse getCategory(Integer categoryId) {
        //判断非空
        if (categoryId == null || categoryId < 0) {
            return ServerResponse.defeatedRS("非法的参数");
        }
        //判断ID是否已经存在
        int a = categoryMapper.selectById(categoryId);
        if (a <= 0) {
            return ServerResponse.defeatedRS("未找到该品类");
        }

        List<Category> li = new ArrayList<Category>();
        li = categoryMapper.selectByCategoryId(categoryId);
        return ServerResponse.successRS(li);
    }
}