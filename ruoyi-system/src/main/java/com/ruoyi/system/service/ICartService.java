package com.ruoyi.system.service;

import com.ruoyi.system.domain.Cart;

import java.util.List;

public interface ICartService {

    List<Cart> selectCartList(Long userId);

    Cart selectCartById(Long id);

    int insertCart(Cart cart);

    int updateCart(Cart cart);

    int deleteCartById(Long id);

    int deleteCartByUserId(Long userId);

    int countCart(Long userId);

    Cart selectCartByUserBook(Long userId, Long bookId);
}