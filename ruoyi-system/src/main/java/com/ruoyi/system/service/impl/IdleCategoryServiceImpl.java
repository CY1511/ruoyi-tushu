package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.IdleCategoryMapper;
import com.ruoyi.system.domain.IdleCategory;
import com.ruoyi.system.service.IIdleCategoryService;

/**
 * 商品分类Service业务层处理
 *
 * <p>分类属于"读多写少、几乎不变"的数据，整份启用分类缓存在 Redis
 * （key：{@code idleCategory:all}，TTL 2 小时兜底）。
 * 任何分类写操作都会主动删掉这份缓存。缓存实现方式与原因见
 * {@link IdleGoodsServiceImpl} 的类注释。</p>
 *
 * @author ruoyi
 * @date 2026-07-12
 */
@Service
public class IdleCategoryServiceImpl implements IIdleCategoryService 
{
    /** 启用分类列表的缓存 key */
    private static final String CACHE_KEY = "idleCategory:all";

    /** 分类缓存时长（小时），仅兜底，正确性靠写路径主动删除 */
    private static final int CACHE_TTL_HOURS = 2;

    @Autowired
    private IdleCategoryMapper idleCategoryMapper;

    @Autowired
    private RedisCache redisCache;

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
     * 查询全部启用的分类（整表缓存）
     */
    @Override
    public List<IdleCategory> selectEnabledCategoryList()
    {
        List<IdleCategory> cached = redisCache.getCacheObject(CACHE_KEY);
        if (cached != null && !cached.isEmpty())
        {
            return cached;
        }
        IdleCategory query = new IdleCategory();
        // 0 = 正常（与框架字典 sys_normal_disable 一致）
        query.setStatus("0");
        List<IdleCategory> list = idleCategoryMapper.selectIdleCategoryList(query);
        if (list != null && !list.isEmpty())
        {
            redisCache.setCacheObject(CACHE_KEY, list, CACHE_TTL_HOURS, TimeUnit.HOURS);
        }
        return list;
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
        int rows = idleCategoryMapper.insertIdleCategory(idleCategory);
        clearCategoryCache();
        return rows;
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
        int rows = idleCategoryMapper.updateIdleCategory(idleCategory);
        clearCategoryCache();
        return rows;
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
        int rows = idleCategoryMapper.deleteIdleCategoryByCategoryIds(categoryIds);
        clearCategoryCache();
        return rows;
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
        int rows = idleCategoryMapper.deleteIdleCategoryByCategoryId(categoryId);
        clearCategoryCache();
        return rows;
    }

    /**
     * 清空分类缓存
     */
    private void clearCategoryCache()
    {
        redisCache.deleteObject(CACHE_KEY);
    }
}
