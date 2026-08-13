package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Wallet;

public interface WalletMapper {
    Wallet selectWalletByUserId(Long userId);
    int insertWallet(Wallet wallet);
    int updateWallet(Wallet wallet);
    int updateWalletBalance(Long userId, java.math.BigDecimal amount);
}