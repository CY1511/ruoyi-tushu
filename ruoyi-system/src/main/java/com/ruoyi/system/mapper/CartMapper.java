package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.Cart;

@Mapper
public interface CartMapper {
    List<Cart> selectCartList(Long userId);
    Cart selectCartById(Long id);
    Cart selectCartByUserBook(Long userId, Long bookId);
    int insertCart(Cart cart);
    int updateCart(Cart cart);
    int deleteCartById(Long id);
    int deleteCartByUserId(Long userId);
    int countCart(Long userId);
}