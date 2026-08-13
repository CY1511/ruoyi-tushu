package com.ruoyi.system.service;

import com.ruoyi.system.domain.Category;

import java.util.List;

public interface ICategoryService {

    List<Category> selectCategoryList(Category category);

    Category selectCategoryById(Long id);

    int insertCategory(Category category);

    int updateCategory(Category category);

    int deleteCategoryById(Long id);
}