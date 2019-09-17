package com.itdr.mappers;

import com.itdr.pojo.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    //根据父id查询所有一级子ID
    List<Category> selectByParentId(@Param("pid") Integer pid);

    //根据ID修改品类名字
    int updateByIdAndName(@Param("categoryId") Integer categoryId, @Param("categoryName") String categoryName);

    //根据ID查询是否存在分类
    int selectById(@Param("categoryId") Integer categoryId);

    //根据ID和品类名称增加节点
    int addCategoryByIdAndName(@Param("parentId") Integer parentId, @Param("categoryName") String categoryName);

    //根据ID获取品类子节点（平级）
    List<Category> selectByCategoryId(@Param("pid") Integer pid);
}