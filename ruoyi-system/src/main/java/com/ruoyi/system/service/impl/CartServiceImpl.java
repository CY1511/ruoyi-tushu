package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Cart;
import com.ruoyi.system.mapper.CartMapper;
import com.ruoyi.system.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public List<Cart> selectCartList(Long userId) {
        return cartMapper.selectCartList(userId);
    }

    @Override
    public Cart selectCartById(Long id) {
        return cartMapper.selectCartById(id);
    }

    @Override
    @Transactional
    public int insertCart(Cart cart) {
        return cartMapper.insertCart(cart);
    }

    @Override
    @Transactional
    public int updateCart(Cart cart) {
        return cartMapper.updateCart(cart);
    }

    @Override
    @Transactional
    public int deleteCartById(Long id) {
        return cartMapper.deleteCartById(id);
    }

    @Override
    @Transactional
    public int deleteCartByUserId(Long userId) {
        return cartMapper.deleteCartByUserId(userId);
    }

    @Override
    public int countCart(Long userId) {
        return cartMapper.countCart(userId);
    }

    @Override
    public Cart selectCartByUserBook(Long userId, Long bookId) {
        return cartMapper.selectCartByUserBook(userId, bookId);
    }
}