package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
        if ("1".equals(idleGoods.getAuditStatus())) {
            idleGoods.setGoodsStatus("1");
        } else if ("2".equals(idleGoods.getAuditStatus())) {
            idleGoods.setGoodsStatus("4");
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
        idleGoods.setGoodsStatus("1");
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
        idleGoods.setGoodsStatus("4");
        idleGoods.setUpdateTime(DateUtils.getNowDate());
        return idleGoodsMapper.updateIdleGoods(idleGoods);
    }

    @Override
    public int countGoodsByPublisherId(Long publisherId)
    {
        return idleGoodsMapper.countGoodsByPublisherId(publisherId);
    }
}
