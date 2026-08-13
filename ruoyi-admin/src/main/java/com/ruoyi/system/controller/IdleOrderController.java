package com.ruoyi.system.controller;

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
import com.ruoyi.system.domain.IdleOrder;
import com.ruoyi.system.service.IIdleOrderService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商品交易订单Controller
 * 
 * @author ruoyi
 * @date 2026-07-13
 */
@RestController
@RequestMapping("/system/order")
public class IdleOrderController extends BaseController
{
    @Autowired
    private IIdleOrderService idleOrderService;

    /**
     * 查询商品交易订单列表
     */
    @PreAuthorize("@ss.hasPermi('system:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(IdleOrder idleOrder)
    {
        startPage();
        List<IdleOrder> list = idleOrderService.selectIdleOrderList(idleOrder);
        return getDataTable(list);
    }

    /**
     * 导出商品交易订单列表
     */
    @PreAuthorize("@ss.hasPermi('system:order:export')")
    @Log(title = "商品交易订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IdleOrder idleOrder)
    {
        List<IdleOrder> list = idleOrderService.selectIdleOrderList(idleOrder);
        ExcelUtil<IdleOrder> util = new ExcelUtil<IdleOrder>(IdleOrder.class);
        util.exportExcel(response, list, "商品交易订单数据");
    }

    /**
     * 获取商品交易订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(idleOrderService.selectIdleOrderByOrderId(orderId));
    }

    /**
     * 新增商品交易订单
     */
    @PreAuthorize("@ss.hasPermi('system:order:add')")
    @Log(title = "商品交易订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IdleOrder idleOrder)
    {
        return toAjax(idleOrderService.insertIdleOrder(idleOrder));
    }

    /**
     * 修改商品交易订单
     */
    @PreAuthorize("@ss.hasPermi('system:order:edit')")
    @Log(title = "商品交易订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IdleOrder idleOrder)
    {
        return toAjax(idleOrderService.updateIdleOrder(idleOrder));
    }

    /**
     * 删除商品交易订单
     */
    @PreAuthorize("@ss.hasPermi('system:order:remove')")
    @Log(title = "商品交易订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(idleOrderService.deleteIdleOrderByOrderIds(orderIds));
    }
}
