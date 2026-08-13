package com.ruoyi.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.IdleGoods;
import com.ruoyi.system.service.IIdleGoodsService;

/**
 * C端-商品接口
 */
@RestController
@RequestMapping("/app/goods")
public class AppGoodsController extends BaseController
{
    @Autowired
    private IIdleGoodsService idleGoodsService;

    /** 商品列表（公开） */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(IdleGoods idleGoods)
    {
        startPage();
        // 只显示已审核通过且上架的商品
        idleGoods.setAuditStatus("1");
        idleGoods.setGoodsStatus("1");
        List<IdleGoods> list = idleGoodsService.selectIdleGoodsList(idleGoods);
        return getDataTable(list);
    }

    /** 商品详情（公开） */
    @Anonymous
    @GetMapping("/{goodsId}")
    public AjaxResult getInfo(@PathVariable Long goodsId)
    {
        return success(idleGoodsService.selectIdleGoodsByGoodsId(goodsId));
    }

    /** 发布商品（需登录） */
    @PostMapping
    public AjaxResult add(@RequestBody IdleGoods idleGoods)
    {
        idleGoods.setPublisherId(SecurityUtils.getUserId());
        idleGoods.setAuditStatus("0");
        idleGoods.setGoodsStatus("0");
        idleGoods.setViewCount(0L);
        idleGoods.setCollectCount(0L);
        return toAjax(idleGoodsService.insertIdleGoods(idleGoods));
    }

    /** 修改我的商品（需登录） */
    @PutMapping
    public AjaxResult edit(@RequestBody IdleGoods idleGoods)
    {
        // 只能修改自己的商品
        IdleGoods existing = idleGoodsService.selectIdleGoodsByGoodsId(idleGoods.getGoodsId());
        if (existing == null || !existing.getPublisherId().equals(SecurityUtils.getUserId()))
        {
            return error("只能修改自己的商品");
        }
        // 修改后重新审核
        idleGoods.setAuditStatus("0");
        idleGoods.setGoodsStatus("0");
        return toAjax(idleGoodsService.updateIdleGoods(idleGoods));
    }

    /** 删除我的商品（需登录） */
    @DeleteMapping("/{goodsId}")
    public AjaxResult remove(@PathVariable Long goodsId)
    {
        IdleGoods existing = idleGoodsService.selectIdleGoodsByGoodsId(goodsId);
        if (existing == null || !existing.getPublisherId().equals(SecurityUtils.getUserId()))
        {
            return error("只能删除自己的商品");
        }
        return toAjax(idleGoodsService.deleteIdleGoodsByGoodsId(goodsId));
    }
}
