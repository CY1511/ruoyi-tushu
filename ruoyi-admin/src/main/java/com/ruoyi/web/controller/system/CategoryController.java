package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Category;
import com.ruoyi.system.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/list")
    public R getCategoryList(Category category) {
        List<Category> categories = categoryService.selectCategoryList(category);
        return R.ok(categories);
    }

    @GetMapping("/{id}")
    public R getCategoryById(@PathVariable Long id) {
        Category category = categoryService.selectCategoryById(id);
        return R.ok(category);
    }

    @PostMapping("/add")
    public R addCategory(@RequestBody Category category) {
        categoryService.insertCategory(category);
        return R.ok("添加成功");
    }

    @PutMapping("/update")
    public R updateCategory(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public R deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return R.ok("删除成功");
    }
}