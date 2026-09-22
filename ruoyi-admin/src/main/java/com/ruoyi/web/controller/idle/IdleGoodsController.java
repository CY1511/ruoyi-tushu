package com.ruoyi.web.controller.idle;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.IdleGoods;
import com.ruoyi.system.service.IIdleGoodsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商品信息Controller
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
@RestController
@RequestMapping("/system/goods")
public class IdleGoodsController extends BaseController
{
    @Autowired
    private IIdleGoodsService idleGoodsService;

    /**
     * 查询商品信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:goods:list')")
    @GetMapping("/list")
    public TableDataInfo list(IdleGoods idleGoods)
    {
        startPage();
        List<IdleGoods> list = idleGoodsService.selectIdleGoodsList(idleGoods);
        return getDataTable(list);
    }

    /**
     * 导出商品信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:goods:export')")
    @Log(title = "商品信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IdleGoods idleGoods)
    {
        List<IdleGoods> list = idleGoodsService.selectIdleGoodsList(idleGoods);
        ExcelUtil<IdleGoods> util = new ExcelUtil<IdleGoods>(IdleGoods.class);
        util.exportExcel(response, list, "商品信息数据");
    }

    /**
     * 获取商品信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:goods:query')")
    @GetMapping(value = "/{goodsId}")
    public AjaxResult getInfo(@PathVariable("goodsId") Long goodsId)
    {
        return success(idleGoodsService.selectIdleGoodsByGoodsId(goodsId));
    }

    /**
     * 新增商品信息
     */
    @PreAuthorize("@ss.hasPermi('system:goods:add')")
    @Log(title = "商品信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IdleGoods idleGoods)
    {
        return toAjax(idleGoodsService.insertIdleGoods(idleGoods));
    }

    /**
     * 修改商品信息
     */
    @PreAuthorize("@ss.hasPermi('system:goods:edit')")
    @Log(title = "商品信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IdleGoods idleGoods)
    {
        return toAjax(idleGoodsService.updateIdleGoods(idleGoods));
    }

    /**
     * 删除商品信息
     */
    @PreAuthorize("@ss.hasPermi('system:goods:remove')")
    @Log(title = "商品信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{goodsIds}")
    public AjaxResult remove(@PathVariable Long[] goodsIds)
    {
        return toAjax(idleGoodsService.deleteIdleGoodsByGoodsIds(goodsIds));
    }

    /**
     * 审核商品信息
     */
    @PreAuthorize("@ss.hasPermi('system:goods:audit')")
    @Log(title = "商品信息", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody IdleGoods idleGoods)
    {
        return toAjax(idleGoodsService.auditIdleGoods(idleGoods));
    }

    /**
     * 上架商品信息
     */
    @PreAuthorize("@ss.hasPermi('system:goods:online')")
    @Log(title = "商品信息", businessType = BusinessType.UPDATE)
    @PutMapping("/online/{goodsId}")
    public AjaxResult online(@PathVariable Long goodsId)
    {
        return toAjax(idleGoodsService.onlineIdleGoods(goodsId));
    }

    /**
     * 下架商品信息
     */
    @PreAuthorize("@ss.hasPermi('system:goods:offline')")
    @Log(title = "商品信息", businessType = BusinessType.UPDATE)
    @PutMapping("/offline/{goodsId}")
    public AjaxResult offline(@PathVariable Long goodsId)
    {
        return toAjax(idleGoodsService.offlineIdleGoods(goodsId));
    }
}
