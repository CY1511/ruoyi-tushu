package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.Category;
import com.ruoyi.system.mapper.CategoryMapper;
import com.ruoyi.system.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> selectCategoryList(Category category) {
        return categoryMapper.selectCategoryList(category);
    }

    @Override
    public Category selectCategoryById(Long id) {
        return categoryMapper.selectCategoryById(id);
    }

    @Override
    @Transactional
    public int insertCategory(Category category) {
        category.setCreateTime(DateUtils.getNowDate());
        category.setUpdateTime(DateUtils.getNowDate());
        return categoryMapper.insertCategory(category);
    }

    @Override
    @Transactional
    public int updateCategory(Category category) {
        category.setUpdateTime(DateUtils.getNowDate());
        return categoryMapper.updateCategory(category);
    }

    @Override
    @Transactional
    public int deleteCategoryById(Long id) {
        return categoryMapper.deleteCategoryById(id);
    }
}