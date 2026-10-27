package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.IdleFavorite;

/**
 * 商品收藏Mapper接口
 * 
 * @author ruoyi
 * @date 2026-07-14
 */
public interface IdleFavoriteMapper 
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
     * 删除商品收藏
     * 
     * @param favoriteId 商品收藏主键
     * @return 结果
     */
    public int deleteIdleFavoriteByFavoriteId(Long favoriteId);

    /**
     * 批量删除商品收藏
     *
     * @param favoriteIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIdleFavoriteByFavoriteIds(Long[] favoriteIds);

    /**
     * 查询用户收藏的商品列表（联表）
     *
     * @param userId 用户ID
     * @return 商品收藏集合
     */
    public List<IdleFavorite> selectFavoriteGoodsList(@org.apache.ibatis.annotations.Param("userId") Long userId);

    /**
     * 检查用户是否已收藏商品
     *
     * @param userId 用户ID
     * @param goodsId 商品ID
     * @return 收藏记录数
     */
    public int checkFavorite(@org.apache.ibatis.annotations.Param("userId") Long userId, @org.apache.ibatis.annotations.Param("goodsId") Long goodsId);

    /**
     * 根据用户ID和商品ID删除收藏
     *
     * @param userId 用户ID
     * @param goodsId 商品ID
     * @return 结果
     */
    public int deleteFavoriteByUserAndGoods(@org.apache.ibatis.annotations.Param("userId") Long userId, @org.apache.ibatis.annotations.Param("goodsId") Long goodsId);

    /**
     * 根据用户ID统计收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    public int countFavoritesByUserId(Long userId);

    /**
     * 删除某个商品上的全部收藏记录
     * <p>idle_favorite 没有外键约束，商品硬删除后收藏行不会跟着消失，
     * 会留在收藏列表里 join 出空行，所以删除商品时必须显式清理。</p>
     *
     * @param goodsId 商品ID
     * @return 结果
     */
    public int deleteIdleFavoriteByGoodsId(@org.apache.ibatis.annotations.Param("goodsId") Long goodsId);

    /**
     * 批量删除若干商品上的收藏记录
     *
     * @param goodsIds 商品主键数组
     * @return 结果
     */
    public int deleteIdleFavoriteByGoodsIds(@org.apache.ibatis.annotations.Param("goodsIds") Long[] goodsIds);
}
