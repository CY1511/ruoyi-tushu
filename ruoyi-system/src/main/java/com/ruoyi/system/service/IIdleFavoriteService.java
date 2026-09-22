package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.IdleFavorite;

/**
 * 商品收藏Service接口
 * 
 * @author ruoyi
 * @date 2026-07-14
 */
public interface IIdleFavoriteService 
{
    /**
     * 查询商品收藏
     * 
     * @param favoriteId 商品收藏主键
     * @return 商品收藏
     */
    public IdleFavorite selectIdleFavoriteByFavoriteId(Long favoriteId);

    /**
     * 查询商品收藏列表
     * 
     * @param idleFavorite 商品收藏
     * @return 商品收藏集合
     */
    public List<IdleFavorite> selectIdleFavoriteList(IdleFavorite idleFavorite);

    /**
     * 新增商品收藏
     * 
     * @param idleFavorite 商品收藏
     * @return 结果
     */
    public int insertIdleFavorite(IdleFavorite idleFavorite);

    /**
     * 修改商品收藏
     * 
     * @param idleFavorite 商品收藏
     * @return 结果
     */
    public int updateIdleFavorite(IdleFavorite idleFavorite);

    /**
     * 批量删除商品收藏
     * 
     * @param favoriteIds 需要删除的商品收藏主键集合
     * @return 结果
     */
    public int deleteIdleFavoriteByFavoriteIds(Long[] favoriteIds);

    /**
     * 删除商品收藏信息
     *
     * @param favoriteId 商品收藏主键
     * @return 结果
     */
    public int deleteIdleFavoriteByFavoriteId(Long favoriteId);

    /**
     * 查询用户收藏的商品列表（联表）
     *
     * @param userId 用户ID
     * @return 商品收藏集合
     */
    public List<IdleFavorite> selectFavoriteGoodsList(Long userId);

    /**
     * 检查用户是否已收藏商品
     *
     * @param userId 用户ID
     * @param goodsId 商品ID
     * @return 是否已收藏
     */
    public boolean isFavorited(Long userId, Long goodsId);

    /**
     * 切换收藏状态
     *
     * @param userId 用户ID
     * @param goodsId 商品ID
     * @return 收藏后状态（true=已收藏）
     */
    public boolean toggleFavorite(Long userId, Long goodsId);

    /**
     * 根据用户ID统计收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    public int countFavoritesByUserId(Long userId);
}
