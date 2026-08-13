package com.ruoyi.system.service;

import com.ruoyi.system.domain.Trade;

import java.util.List;

public interface ITradeService {

    List<Trade> selectTradeList(Trade trade);

    Trade selectTradeById(Long id);

    int insertTrade(Trade trade);
}