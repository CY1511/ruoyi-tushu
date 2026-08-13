package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Cart;
import com.ruoyi.system.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    @GetMapping("/list")
    public R getCartList(@RequestParam Long userId) {
        List<Cart> carts = cartService.selectCartList(userId);
        return R.ok(carts);
    }

    @PostMapping("/add")
    public R addCart(@RequestBody Cart cart) {
        Cart existing = cartService.selectCartByUserBook(cart.getUserId(), cart.getBookId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + cart.getQuantity());
            cartService.updateCart(existing);
        } else {
            cartService.insertCart(cart);
        }
        return R.ok("添加成功");
    }

    @PutMapping("/update")
    public R updateCart(@RequestBody Cart cart) {
        cartService.updateCart(cart);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public R deleteCart(@PathVariable Long id) {
        cartService.deleteCartById(id);
        return R.ok("删除成功");
    }

    @GetMapping("/count")
    public R countCart(@RequestParam Long userId) {
        int count = cartService.countCart(userId);
        return R.ok(count);
    }
}