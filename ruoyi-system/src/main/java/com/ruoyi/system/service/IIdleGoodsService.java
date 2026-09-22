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

    // =========================================================================
    // C 端接口：归属校验放在 Service 层
    // 原来这些校验散落在 Controller 里手写，漏一处就是越权（能改别人的商品），
    // 而且校验逻辑和业务逻辑不在同一层，改动时容易只改一边。统一收口到这里。
    // =========================================================================

    /**
     * 发布商品（C 端）
     * <p>发布人固定取当前登录用户，忽略请求体里的 publisherId；初始状态为待审核。</p>
     *
     * @param idleGoods 商品信息
     * @return 结果
     */
    public int publishGoods(IdleGoods idleGoods);

    /**
     * 修改我发布的商品（C 端）
     * <p>服务端校验归属，非本人抛 {@code ServiceException}；修改后重新进入待审核。</p>
     *
     * @param idleGoods 商品信息
     * @return 结果
     */
    public int updateMyGoods(IdleGoods idleGoods);

    /**
     * 删除我发布的商品（C 端）
     * <p>服务端校验归属，非本人抛 {@code ServiceException}。</p>
     *
     * @param goodsId 商品信息主键
     * @return 结果
     */
    public int deleteMyGoods(Long goodsId);

    /**
     * 查询商品详情（C 端可见性规则）
     * <p>已审核通过且未下架的商品对所有人可见；其余状态只有发布者本人能看，
     * 避免未过审 / 已下架商品被按 ID 直接访问。</p>
     *
     * @param goodsId 商品信息主键
     * @return 商品信息
     */
    public IdleGoods selectVisibleGoodsByGoodsId(Long goodsId);
}
