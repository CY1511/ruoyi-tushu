package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.IdleGoodsMapper;
import com.ruoyi.system.domain.IdleGoods;
import com.ruoyi.system.service.IIdleGoodsService;

/**
 * 商品信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
@Service
public class IdleGoodsServiceImpl implements IIdleGoodsService 
{
    @Autowired
    private IdleGoodsMapper idleGoodsMapper;

    /**
     * 查询商品信息
     * 
     * @param goodsId 商品信息主键
     * @return 商品信息
     */
    @Override
    public IdleGoods selectIdleGoodsByGoodsId(Long goodsId)
    {
        return idleGoodsMapper.selectIdleGoodsByGoodsId(goodsId);
    }

    /**
     * 查询商品信息列表
     * 
     * @param idleGoods 商品信息
     * @return 商品信息
     */
    @Override
    public List<IdleGoods> selectIdleGoodsList(IdleGoods idleGoods)
    {
        return idleGoodsMapper.selectIdleGoodsList(idleGoods);
    }

    /**
     * 新增商品信息
     * 
     * @param idleGoods 商品信息
     * @return 结果
     */
    @Override
    public int insertIdleGoods(IdleGoods idleGoods)
    {
        idleGoods.setCreateTime(DateUtils.getNowDate());
        return idleGoodsMapper.insertIdleGoods(idleGoods);
    }

    /**
     * 修改商品信息
     * 
     * @param idleGoods 商品信息
     * @return 结果
     */
    @Override
    public int updateIdleGoods(IdleGoods idleGoods)
    {
        idleGoods.setUpdateTime(DateUtils.getNowDate());
        return idleGoodsMapper.updateIdleGoods(idleGoods);
    }

    /**
     * 批量删除商品信息
     * 
     * @param goodsIds 需要删除的商品信息主键
     * @return 结果
     */
    @Override
    public int deleteIdleGoodsByGoodsIds(Long[] goodsIds)
    {
        return idleGoodsMapper.deleteIdleGoodsByGoodsIds(goodsIds);
    }

    /**
     * 删除商品信息信息
     * 
     * @param goodsId 商品信息主键
     * @return 结果
     */
    @Override
    public int deleteIdleGoodsByGoodsId(Long goodsId)
    {
        return idleGoodsMapper.deleteIdleGoodsByGoodsId(goodsId);
    }

    /**
     * 审核商品信息
     * 
     * @param idleGoods 商品信息
     * @return 结果
     */
    @Override
    public int auditIdleGoods(IdleGoods idleGoods)
    {
        // audit_user 存的是审核人ID：IdleGoodsMapper 用 `g.audit_user = u2.user_id` 关联 sys_user
        // 取用户名，前端审核人下拉框传的也是 userId。原来写入 SecurityUtils.getUsername()，
        // 与关联条件语义相反，导致列表里「审核人名称」永远为空。
        idleGoods.setAuditUser(String.valueOf(SecurityUtils.getUserId()));
        idleGoods.setAuditUserName(SecurityUtils.getUsername());
        idleGoods.setAuditTime(DateUtils.getNowDate());
        idleGoods.setUpdateTime(DateUtils.getNowDate());
        if (IdleGoods.AUDIT_PASSED.equals(idleGoods.getAuditStatus())) {
            idleGoods.setGoodsStatus(IdleGoods.STATUS_ON_SALE);
        } else if (IdleGoods.AUDIT_REJECTED.equals(idleGoods.getAuditStatus())) {
            idleGoods.setGoodsStatus(IdleGoods.STATUS_OFFLINE);
        }
        return idleGoodsMapper.updateIdleGoods(idleGoods);
    }

    /**
     * 上架商品信息
     * 
     * @param goodsId 商品信息主键
     * @return 结果
     */
    @Override
    public int onlineIdleGoods(Long goodsId)
    {
        IdleGoods idleGoods = new IdleGoods();
        idleGoods.setGoodsId(goodsId);
        idleGoods.setGoodsStatus(IdleGoods.STATUS_ON_SALE);
        idleGoods.setUpdateTime(DateUtils.getNowDate());
        return idleGoodsMapper.updateIdleGoods(idleGoods);
    }

    /**
     * 下架商品信息
     * 
     * @param goodsId 商品信息主键
     * @return 结果
     */
    @Override
    public int offlineIdleGoods(Long goodsId)
    {
        IdleGoods idleGoods = new IdleGoods();
        idleGoods.setGoodsId(goodsId);
        idleGoods.setGoodsStatus(IdleGoods.STATUS_OFFLINE);
        idleGoods.setUpdateTime(DateUtils.getNowDate());
        return idleGoodsMapper.updateIdleGoods(idleGoods);
    }

    @Override
    public int countGoodsByPublisherId(Long publisherId)
    {
        return idleGoodsMapper.countGoodsByPublisherId(publisherId);
    }

    // =========================================================================
    // C 端接口实现（归属校验收口在这一层）
    // =========================================================================

    /**
     * 发布商品（C 端）
     */
    @Override
    public int publishGoods(IdleGoods idleGoods)
    {
        // 发布人只认当前登录用户，请求体里传什么都不算
        idleGoods.setGoodsId(null);
        idleGoods.setPublisherId(SecurityUtils.getUserId());
        idleGoods.setAuditStatus(IdleGoods.AUDIT_PENDING);
        idleGoods.setGoodsStatus(IdleGoods.STATUS_PENDING);
        idleGoods.setViewCount(0L);
        idleGoods.setCollectCount(0L);
        // 审核信息由审核动作写入，发布时先清空，防止请求体伪造"已过审"
        idleGoods.setAuditUser(null);
        idleGoods.setAuditTime(null);
        idleGoods.setAuditRemark(null);
        idleGoods.setCreateBy(SecurityUtils.getUsername());
        return insertIdleGoods(idleGoods);
    }

    /**
     * 修改我发布的商品（C 端）
     */
    @Override
    public int updateMyGoods(IdleGoods idleGoods)
    {
        IdleGoods existing = requireOwnerGoods(idleGoods.getGoodsId());
        // 归属不可改：用库里的值覆盖请求体，防止把商品"过继"给别人
        idleGoods.setPublisherId(existing.getPublisherId());
        // 商品信息变更后需要重新审核
        idleGoods.setAuditStatus(IdleGoods.AUDIT_PENDING);
        idleGoods.setGoodsStatus(IdleGoods.STATUS_PENDING);
        // 浏览量、收藏量是系统累加值，不允许客户端覆盖
        idleGoods.setViewCount(null);
        idleGoods.setCollectCount(null);
        idleGoods.setUpdateBy(SecurityUtils.getUsername());
        return updateIdleGoods(idleGoods);
    }

    /**
     * 删除我发布的商品（C 端）
     */
    @Override
    public int deleteMyGoods(Long goodsId)
    {
        requireOwnerGoods(goodsId);
        return deleteIdleGoodsByGoodsId(goodsId);
    }

    /**
     * 查询商品详情（C 端可见性规则）
     */
    @Override
    public IdleGoods selectVisibleGoodsByGoodsId(Long goodsId)
    {
        IdleGoods goods = idleGoodsMapper.selectIdleGoodsByGoodsId(goodsId);
        if (goods == null)
        {
            throw new ServiceException("商品不存在");
        }
        boolean publicVisible = IdleGoods.AUDIT_PASSED.equals(goods.getAuditStatus())
                && !IdleGoods.STATUS_OFFLINE.equals(goods.getGoodsStatus());
        if (publicVisible)
        {
            return goods;
        }
        // 未过审 / 已下架：只有发布者本人能看（详情接口是匿名可访问的，所以这里要容错取用户）
        Long currentUserId = currentUserIdOrNull();
        if (currentUserId != null && currentUserId.equals(goods.getPublisherId()))
        {
            return goods;
        }
        throw new ServiceException("该商品尚未通过审核或已下架");
    }

    // =========================================================================
    // 商品状态流转（由订单状态机驱动）
    // =========================================================================

    @Override
    public int bookGoods(Long goodsId)
    {
        // 条件更新：只有"上架"状态才能被预订，并发下单时天然只有一个成功
        return idleGoodsMapper.updateGoodsStatusIfMatch(goodsId,
                IdleGoods.STATUS_ON_SALE, IdleGoods.STATUS_BOOKED);
    }

    @Override
    public int markGoodsSold(Long goodsId)
    {
        return idleGoodsMapper.updateGoodsStatusIfMatch(goodsId,
                IdleGoods.STATUS_BOOKED, IdleGoods.STATUS_SOLD);
    }

    @Override
    public int releaseGoods(Long goodsId)
    {
        return idleGoodsMapper.updateGoodsStatusIfMatch(goodsId,
                IdleGoods.STATUS_BOOKED, IdleGoods.STATUS_ON_SALE);
    }

    /**
     * 校验商品归属：不存在或不是当前用户的商品，一律拒绝
     *
     * @param goodsId 商品ID
     * @return 数据库中的商品
     */
    private IdleGoods requireOwnerGoods(Long goodsId)
    {
        if (goodsId == null)
        {
            throw new ServiceException("商品ID不能为空");
        }
        IdleGoods goods = idleGoodsMapper.selectIdleGoodsByGoodsId(goodsId);
        if (goods == null)
        {
            throw new ServiceException("商品不存在");
        }
        Long currentUserId = SecurityUtils.getUserId();
        if (!currentUserId.equals(goods.getPublisherId()))
        {
            throw new ServiceException("无权操作他人发布的商品", HttpStatus.FORBIDDEN.value());
        }
        return goods;
    }

    /**
     * 取当前登录用户ID，未登录返回 null（匿名可访问的接口用）
     */
    private Long currentUserIdOrNull()
    {
        try
        {
            return SecurityUtils.getUserId();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
