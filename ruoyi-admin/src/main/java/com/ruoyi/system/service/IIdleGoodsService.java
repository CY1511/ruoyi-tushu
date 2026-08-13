package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.IdleGoods;

/**
 * 商品信息Service接口
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
public interface IIdleGoodsService 
{
    /**
     * 查询商品信息
     * 
     * @param goodsId 商品信息主键
     * @return 商品信息
     */
    public IdleGoods selectIdleGoodsByGoodsId(Long goodsId);

    /**
     * 查询商品信息列表
     * 
     * @param idleGoods 商品信息
     * @return 商品信息集合
     */
    public List<IdleGoods> selectIdleGoodsList(IdleGoods idleGoods);

    /**
     * 新增商品信息
     * 
     * @param idleGoods 商品信息
     * @return 结果
     */
    public int insertIdleGoods(IdleGoods idleGoods);

    /**
     * 修改商品信息
     * 
     * @param idleGoods 商品信息
     * @return 结果
     */
    public int updateIdleGoods(IdleGoods idleGoods);

    /**
     * 批量删除商品信息
     * 
     * @param goodsIds 需要删除的商品信息主键集合
     * @return 结果
     */
    public int deleteIdleGoodsByGoodsIds(Long[] goodsIds);

    /**
     * 删除商品信息信息
     * 
     * @param goodsId 商品信息主键
     * @return 结果
     */
    public int deleteIdleGoodsByGoodsId(Long goodsId);

    /**
     * 审核商品信息
     * 
     * @param idleGoods 商品信息
     * @return 结果
     */
    public int auditIdleGoods(IdleGoods idleGoods);

    /**
     * 上架商品信息
     * 
     * @param goodsId 商品信息主键
     * @return 结果
     */
    public int onlineIdleGoods(Long goodsId);

    /**
     * 下架商品信息
     * 
     * @param goodsId 商品信息主键
     * @return 结果
     */
    public int offlineIdleGoods(Long goodsId);

    /**
     * 根据发布人ID统计商品数量
     *
     * @param publisherId 发布人ID
     * @return 商品数量
     */
    public int countGoodsByPublisherId(Long publisherId);
}
