package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.IdleCategoryMapper;
import com.ruoyi.system.domain.IdleCategory;
import com.ruoyi.system.service.IIdleCategoryService;

/**
 * 商品分类Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
@Service
public class IdleCategoryServiceImpl implements IIdleCategoryService 
{
    @Autowired
    private IdleCategoryMapper idleCategoryMapper;

    /**
     * 查询商品分类
     * 
     * @param categoryId 商品分类主键
     * @return 商品分类
     */
    @Override
    public IdleCategory selectIdleCategoryByCategoryId(Long categoryId)
    {
        return idleCategoryMapper.selectIdleCategoryByCategoryId(categoryId);
    }

    /**
     * 查询商品分类列表
     * 
     * @param idleCategory 商品分类
     * @return 商品分类
     */
    @Override
    public List<IdleCategory> selectIdleCategoryList(IdleCategory idleCategory)
    {
        return idleCategoryMapper.selectIdleCategoryList(idleCategory);
    }

    /**
     * 新增商品分类
     * 
     * @param idleCategory 商品分类
     * @return 结果
     */
    @Override
    public int insertIdleCategory(IdleCategory idleCategory)
    {
        idleCategory.setCreateTime(DateUtils.getNowDate());
        return idleCategoryMapper.insertIdleCategory(idleCategory);
    }

    /**
     * 修改商品分类
     * 
     * @param idleCategory 商品分类
     * @return 结果
     */
    @Override
    public int updateIdleCategory(IdleCategory idleCategory)
    {
        idleCategory.setUpdateTime(DateUtils.getNowDate());
        return idleCategoryMapper.updateIdleCategory(idleCategory);
    }

    /**
     * 批量删除商品分类
     * 
     * @param categoryIds 需要删除的商品分类主键
     * @return 结果
     */
    @Override
    public int deleteIdleCategoryByCategoryIds(Long[] categoryIds)
    {
        return idleCategoryMapper.deleteIdleCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除商品分类信息
     * 
     * @param categoryId 商品分类主键
     * @return 结果
     */
    @Override
    public int deleteIdleCategoryByCategoryId(Long categoryId)
    {
        return idleCategoryMapper.deleteIdleCategoryByCategoryId(categoryId);
    }
}
