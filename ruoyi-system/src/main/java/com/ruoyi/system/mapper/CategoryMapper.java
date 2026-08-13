package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.Category;

@Mapper
public interface CategoryMapper {
    List<Category> selectCategoryList(Category category);
    Category selectCategoryById(Long id);
    int insertCategory(Category category);
    int updateCategory(Category category);
    int deleteCategoryById(Long id);
}