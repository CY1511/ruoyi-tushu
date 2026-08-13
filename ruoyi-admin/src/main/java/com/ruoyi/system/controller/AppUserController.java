package com.ruoyi.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.IdleGoods;
import com.ruoyi.system.service.IIdleGoodsService;
import com.ruoyi.system.service.IIdleOrderService;
import com.ruoyi.system.service.IIdleFavoriteService;

/**
 * C端-当前用户接口
 */
@RestController
@RequestMapping("/app/user")
public class AppUserController extends BaseController
{
    @Autowired
    private IIdleGoodsService idleGoodsService;

    @Autowired
    private IIdleOrderService idleOrderService;

    @Autowired
    private IIdleFavoriteService idleFavoriteService;

    /** 我发布的商品（按商品状态筛选） */
    @GetMapping("/goods/list")
    public TableDataInfo myGoods(IdleGoods idleGoods)
    {
        startPage();
        idleGoods.setPublisherId(SecurityUtils.getUserId());
        List<IdleGoods> list = idleGoodsService.selectIdleGoodsList(idleGoods);
        return getDataTable(list);
    }

    /** 获取用户统计数据（摆摊数、订单数、收藏数） */
    @GetMapping("/stats")
    public AjaxResult getStats()
    {
        Long userId = SecurityUtils.getUserId();
        Map<String, Integer> stats = new HashMap<>();
        stats.put("goodsCount", idleGoodsService.countGoodsByPublisherId(userId));
        stats.put("orderCount", idleOrderService.countOrdersByUserId(userId));
        stats.put("favCount", idleFavoriteService.countFavoritesByUserId(userId));
        return AjaxResult.success(stats);
    }
}
