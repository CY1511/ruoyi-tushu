package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.IdleFavoriteMapper;
import com.ruoyi.system.domain.IdleFavorite;
import com.ruoyi.system.service.IIdleFavoriteService;

/**
 * 商品收藏Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-14
 */
@Service
public class IdleFavoriteServiceImpl implements IIdleFavoriteService 
{
    @Autowired
    private IdleFavoriteMapper idleFavoriteMapper;

    /**
     * 查询商品收藏
     * 
     * @param favoriteId 商品收藏主键
     * @return 商品收藏
     */
    @Override
    public IdleFavorite selectIdleFavoriteByFavoriteId(Long favoriteId)
    {
        return idleFavoriteMapper.selectIdleFavoriteByFavoriteId(favoriteId);
    }

    /**
     * 查询商品收藏列表
     * 
     * @param idleFavorite 商品收藏
     * @return 商品收藏
     */
    @Override
    public List<IdleFavorite> selectIdleFavoriteList(IdleFavorite idleFavorite)
    {
        return idleFavoriteMapper.selectIdleFavoriteList(idleFavorite);
    }

    /**
     * 新增商品收藏
     * 
     * @param idleFavorite 商品收藏
     * @return 结果
     */
    @Override
    public int insertIdleFavorite(IdleFavorite idleFavorite)
    {
        idleFavorite.setCreateTime(DateUtils.getNowDate());
        return idleFavoriteMapper.insertIdleFavorite(idleFavorite);
    }

    /**
     * 修改商品收藏
     * 
     * @param idleFavorite 商品收藏
     * @return 结果
     */
    @Override
    public int updateIdleFavorite(IdleFavorite idleFavorite)
    {
        return idleFavoriteMapper.updateIdleFavorite(idleFavorite);
    }

    /**
     * 批量删除商品收藏
     * 
     * @param favoriteIds 需要删除的商品收藏主键
     * @return 结果
     */
    @Override
    public int deleteIdleFavoriteByFavoriteIds(Long[] favoriteIds)
    {
        return idleFavoriteMapper.deleteIdleFavoriteByFavoriteIds(favoriteIds);
    }

    /**
     * 删除商品收藏信息
     *
     * @param favoriteId 商品收藏主键
     * @return 结果
     */
    @Override
    public int deleteIdleFavoriteByFavoriteId(Long favoriteId)
    {
        return idleFavoriteMapper.deleteIdleFavoriteByFavoriteId(favoriteId);
    }

    /**
     * 查询用户收藏的商品列表（联表）
     *
     * @param userId 用户ID
     * @return 商品收藏集合
     */
    @Override
    public List<IdleFavorite> selectFavoriteGoodsList(Long userId)
    {
        return idleFavoriteMapper.selectFavoriteGoodsList(userId);
    }

    /**
     * 检查用户是否已收藏商品
     *
     * @param userId 用户ID
     * @param goodsId 商品ID
     * @return 是否已收藏
     */
    @Override
    public boolean isFavorited(Long userId, Long goodsId)
    {
        return idleFavoriteMapper.checkFavorite(userId, goodsId) > 0;
    }

    /**
     * 切换收藏状态
     *
     * @param userId 用户ID
     * @param goodsId 商品ID
     * @return 收藏后状态（true=已收藏）
     */
    @Override
    public boolean toggleFavorite(Long userId, Long goodsId)
    {
        if (isFavorited(userId, goodsId))
        {
            idleFavoriteMapper.deleteFavoriteByUserAndGoods(userId, goodsId);
            return false;
        }
        else
        {
            IdleFavorite favorite = new IdleFavorite();
            favorite.setUserId(userId);
            favorite.setGoodsId(goodsId);
            favorite.setCreateTime(DateUtils.getNowDate());
            idleFavoriteMapper.insertIdleFavorite(favorite);
            return true;
        }
    }

    @Override
    public int countFavoritesByUserId(Long userId)
    {
        return idleFavoriteMapper.countFavoritesByUserId(userId);
    }
}
