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
import com.ruoyi.system.domain.IdleFavorite;
import com.ruoyi.system.service.IIdleFavoriteService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商品收藏Controller
 * 
 * @author ruoyi
 * @date 2026-07-14
 */
@RestController
@RequestMapping("/system/favorite")
public class IdleFavoriteController extends BaseController
{
    @Autowired
    private IIdleFavoriteService idleFavoriteService;

    /**
     * 查询商品收藏列表
     */
    @PreAuthorize("@ss.hasPermi('system:favorite:list')")
    @GetMapping("/list")
    public TableDataInfo list(IdleFavorite idleFavorite)
    {
        startPage();
        List<IdleFavorite> list = idleFavoriteService.selectIdleFavoriteList(idleFavorite);
        return getDataTable(list);
    }

    /**
     * 导出商品收藏列表
     */
    @PreAuthorize("@ss.hasPermi('system:favorite:export')")
    @Log(title = "商品收藏", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IdleFavorite idleFavorite)
    {
        List<IdleFavorite> list = idleFavoriteService.selectIdleFavoriteList(idleFavorite);
        ExcelUtil<IdleFavorite> util = new ExcelUtil<IdleFavorite>(IdleFavorite.class);
        util.exportExcel(response, list, "商品收藏数据");
    }

    /**
     * 获取商品收藏详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:favorite:query')")
    @GetMapping(value = "/{favoriteId}")
    public AjaxResult getInfo(@PathVariable("favoriteId") Long favoriteId)
    {
        return success(idleFavoriteService.selectIdleFavoriteByFavoriteId(favoriteId));
    }

    /**
     * 新增商品收藏
     */
    @PreAuthorize("@ss.hasPermi('system:favorite:add')")
    @Log(title = "商品收藏", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IdleFavorite idleFavorite)
    {
        return toAjax(idleFavoriteService.insertIdleFavorite(idleFavorite));
    }

    /**
     * 修改商品收藏
     */
    @PreAuthorize("@ss.hasPermi('system:favorite:edit')")
    @Log(title = "商品收藏", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IdleFavorite idleFavorite)
    {
        return toAjax(idleFavoriteService.updateIdleFavorite(idleFavorite));
    }

    /**
     * 删除商品收藏
     */
    @PreAuthorize("@ss.hasPermi('system:favorite:remove')")
    @Log(title = "商品收藏", businessType = BusinessType.DELETE)
	@DeleteMapping("/{favoriteIds}")
    public AjaxResult remove(@PathVariable Long[] favoriteIds)
    {
        return toAjax(idleFavoriteService.deleteIdleFavoriteByFavoriteIds(favoriteIds));
    }
}
