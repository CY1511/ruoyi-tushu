package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.IdleCategory;

/**
 * 商品分类Mapper接口
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
public interface IdleCategoryMapper 
{
    /**
     * 查询商品分类
     * 
     * @param categoryId 商品分类主键
     * @return 商品分类
     */
    public IdleCategory selectIdleCategoryByCategoryId(Long categoryId);

    /**
     * 查询商品分类列表
     * 
     * @param idleCategory 商品分类
     * @return 商品分类集合
     */
    public List<IdleCategory> selectIdleCategoryList(IdleCategory idleCategory);

    /**
     * 新增商品分类
     * 
     * @param idleCategory 商品分类
     * @return 结果
     */
    public int insertIdleCategory(IdleCategory idleCategory);

    /**
     * 修改商品分类
     * 
     * @param idleCategory 商品分类
     * @return 结果
     */
    public int updateIdleCategory(IdleCategory idleCategory);

    /**
     * 删除商品分类
     * 
     * @param categoryId 商品分类主键
     * @return 结果
     */
    public int deleteIdleCategoryByCategoryId(Long categoryId);

    /**
     * 批量删除商品分类
     * 
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIdleCategoryByCategoryIds(Long[] categoryIds);
}
