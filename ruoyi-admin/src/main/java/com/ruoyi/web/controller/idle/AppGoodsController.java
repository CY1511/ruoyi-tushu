package com.ruoyi.web.controller.idle;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.IdleGoods;
import com.ruoyi.system.service.IIdleGoodsService;

/**
 * C端-商品接口
 *
 * <p>安全约定（与 B 端 IdleGoodsController 保持同一套规则）：</p>
 * <ul>
 *   <li>公开接口用 {@link Anonymous} 显式声明，其余接口一律要求登录，不再靠"没人调用"来兜底；</li>
 *   <li>写操作加 {@link Log} 落操作日志，管理端早就有了，C 端原来一条都没有；</li>
 *   <li><b>商品归属校验放在 Service 层</b>（{@code IIdleGoodsService#updateMyGoods / deleteMyGoods}），
 *       Controller 不再手写 if 判断 —— 手写校验最大的问题是"漏一处就是越权"，
 *       而且校验和业务不在同一层，改一处忘一处。</li>
 * </ul>
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
        idleGoods.setAuditStatus(IdleGoods.AUDIT_PASSED);
        idleGoods.setGoodsStatus(IdleGoods.STATUS_ON_SALE);
        List<IdleGoods> list = idleGoodsService.selectIdleGoodsList(idleGoods);
        return getDataTable(list);
    }

    /** 商品详情（公开，但未过审/已下架仅发布者本人可见） */
    @Anonymous
    @GetMapping("/{goodsId}")
    public AjaxResult getInfo(@PathVariable Long goodsId)
    {
        return success(idleGoodsService.selectVisibleGoodsByGoodsId(goodsId));
    }

    /** 发布商品（需登录） */
    @Log(title = "C端商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IdleGoods idleGoods)
    {
        return toAjax(idleGoodsService.publishGoods(idleGoods));
    }

    /** 修改我的商品（需登录，归属由 Service 校验） */
    @Log(title = "C端商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IdleGoods idleGoods)
    {
        return toAjax(idleGoodsService.updateMyGoods(idleGoods));
    }

    /** 删除我的商品（需登录，归属由 Service 校验） */
    @Log(title = "C端商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{goodsId}")
    public AjaxResult remove(@PathVariable Long goodsId)
    {
        return toAjax(idleGoodsService.deleteMyGoods(goodsId));
    }
}
