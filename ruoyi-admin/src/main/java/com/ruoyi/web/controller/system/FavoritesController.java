package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.service.IFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    @Autowired
    private IFavoritesService favoritesService;

    @GetMapping("/list")
    public R getFavoritesList(@RequestParam Long userId) {
        List<Map<String, Object>> favorites = (List<Map<String, Object>>) (List<?>) favoritesService.selectFavoritesList(userId);
        return R.ok(favorites);
    }

    @PostMapping("/toggle")
    public R toggleFavorites(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        Long bookId = Long.parseLong(params.get("bookId").toString());
        favoritesService.toggleFavorites(userId, bookId);
        return R.ok("操作成功");
    }

    @DeleteMapping("/{userId}/{bookId}")
    public R deleteFavorites(@PathVariable Long userId, @PathVariable Long bookId) {
        favoritesService.deleteFavoritesByUserBook(userId, bookId);
        return R.ok("删除成功");
    }

    @GetMapping("/count")
    public R countFavorites(@RequestParam Long bookId) {
        int count = favoritesService.countFavorites(bookId);
        return R.ok(count);
    }
}