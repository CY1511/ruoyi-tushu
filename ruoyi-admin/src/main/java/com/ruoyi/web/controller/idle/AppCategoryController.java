package com.ruoyi.web.controller.idle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.IdleCategory;
import com.ruoyi.system.service.IIdleCategoryService;

/**
 * C端-分类接口
 */
@RestController
@RequestMapping("/app/category")
public class AppCategoryController extends BaseController
{
    @Autowired
    private IIdleCategoryService idleCategoryService;

    /** 分类树（公开） */
    @Anonymous
    @GetMapping("/tree")
    public AjaxResult tree()
    {
        // 走带缓存的查询：分类属于低频变更数据，整份启用列表缓存在 Redis（idleCategory:all）
        List<IdleCategory> list = idleCategoryService.selectEnabledCategoryList();
        List<IdleCategory> tree = buildCategoryTree(list);
        return success(tree);
    }

    private List<IdleCategory> buildCategoryTree(List<IdleCategory> list)
    {
        Map<Long, IdleCategory> map = new HashMap<>();
        List<IdleCategory> tree = new ArrayList<>();
        for (IdleCategory cat : list)
        {
            map.put(cat.getCategoryId(), cat);
            cat.setChildren(new ArrayList<>());
        }
        for (IdleCategory cat : list)
        {
            Long parentId = cat.getParentId();
            if (parentId == null || parentId == 0L)
            {
                tree.add(cat);
            }
            else
            {
                IdleCategory parent = map.get(parentId);
                if (parent != null)
                {
                    @SuppressWarnings("unchecked")
                    List<IdleCategory> children = (List<IdleCategory>) parent.getChildren();
                    children.add(cat);
                }
            }
        }
        return tree;
    }
}
