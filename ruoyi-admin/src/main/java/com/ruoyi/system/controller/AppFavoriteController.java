package com.ruoyi.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.IdleFavorite;
import com.ruoyi.system.service.IIdleFavoriteService;

/**
 * C端-收藏接口
 */
@RestController
@RequestMapping("/app/favorite")
public class AppFavoriteController extends BaseController
{
    @Autowired
    private IIdleFavoriteService idleFavoriteService;

    /** 收藏/取消收藏 */
    @PostMapping("/{goodsId}")
    public AjaxResult toggle(@PathVariable Long goodsId)
    {
        boolean favorited = idleFavoriteService.toggleFavorite(SecurityUtils.getUserId(), goodsId);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("data", favorited);
        return ajax;
    }

    /** 检查是否已收藏（公开接口） */
    @Anonymous
    @GetMapping("/check/{goodsId}")
    public AjaxResult check(@PathVariable Long goodsId)
    {
        Long userId = null;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            userId = null;
        }
        boolean favorited = userId != null && idleFavoriteService.isFavorited(userId, goodsId);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("data", favorited);
        return ajax;
    }

    /** 我的收藏列表（返回商品信息） */
    @GetMapping("/list")
    public TableDataInfo list()
    {
        startPage();
        List<IdleFavorite> list = idleFavoriteService.selectFavoriteGoodsList(SecurityUtils.getUserId());
        return getDataTable(list);
    }
}
