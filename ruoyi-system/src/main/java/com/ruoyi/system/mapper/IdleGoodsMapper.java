package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.IdleGoods;

/**
 * 商品信息Mapper接口
 *
 * @author ruoyi
 * @date 2026-07-12
 */
public interface IdleGoodsMapper
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
     * 删除商品信息
     *
     * @param goodsId 商品信息主键
     * @return 结果
     */
    public int deleteIdleGoodsByGoodsId(Long goodsId);

    /**
     * 批量删除商品信息
     *
     * @param goodsIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIdleGoodsByGoodsIds(Long[] goodsIds);

    /**
     * 根据发布人ID统计商品数量
     *
     * @param publisherId 发布人ID
     * @return 商品数量
     */
    public int countGoodsByPublisherId(Long publisherId);

    /**
     * 条件更新商品状态（乐观更新，用于并发场景）
     *
     * <p>下单时用 {@code where goods_status = '1'} 保证"同一件商品只能被一个买家抢到"：
     * 并发下只有一条 update 影响行数为 1，其余为 0，从而不需要在应用层加锁。</p>
     *
     * @param goodsId    商品ID
     * @param fromStatus 期望的当前状态
     * @param toStatus   目标状态
     * @return 影响行数（0 表示状态不匹配，被别人抢先了）
     */
    public int updateGoodsStatusIfMatch(@Param("goodsId") Long goodsId,
                                        @Param("fromStatus") String fromStatus,
                                        @Param("toStatus") String toStatus);
}