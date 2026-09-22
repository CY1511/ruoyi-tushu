package com.ruoyi.web.controller.idle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.IdleOrder;
import com.ruoyi.system.enums.IdleOrderStatus;
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
     * <p>不含订单状态 —— 状态请走 /changeStatus，避免绕过流转校验。</p>
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

    /**
     * 查询某订单当前可流转到的状态
     * <p>前端据此渲染下拉框，保证界面上根本选不出非法状态（合法流转表以后端为准）。</p>
     */
    @PreAuthorize("@ss.hasPermi('system:order:query')")
    @GetMapping("/nextStatus/{orderId}")
    public AjaxResult nextStatus(@PathVariable Long orderId)
    {
        IdleOrder order = idleOrderService.selectIdleOrderByOrderId(orderId);
        if (order == null)
        {
            return error("订单不存在");
        }
        List<Map<String, Object>> options = new ArrayList<>();
        for (IdleOrderStatus status : IdleOrderStatus.nextOf(order.getOrderStatus()))
        {
            Map<String, Object> option = new LinkedHashMap<>(2);
            option.put("code", status.getCode());
            option.put("label", status.getLabel());
            options.add(option);
        }
        return success(options);
    }

    /**
     * 管理端流转订单状态
     * <p>管理端可以发起流转，但仍必须符合合法流转表，不能把"已完成"改回"待确认"。</p>
     */
    @PreAuthorize("@ss.hasPermi('system:order:changeStatus')")
    @Log(title = "商品交易订单", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody IdleOrder idleOrder,
                                   @RequestParam(value = "remark", required = false) String remark)
    {
        return toAjax(idleOrderService.changeStatusByAdmin(
                idleOrder.getOrderId(), idleOrder.getOrderStatus(), remark));
    }

    /**
     * 查询订单状态流转记录
     */
    @PreAuthorize("@ss.hasPermi('system:order:query')")
    @GetMapping("/statusLog/{orderId}")
    public AjaxResult statusLog(@PathVariable Long orderId)
    {
        return success(idleOrderService.selectStatusLogs(orderId));
    }
}
