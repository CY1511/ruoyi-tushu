package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.Trade;
import com.ruoyi.system.mapper.TradeMapper;
import com.ruoyi.system.service.ITradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TradeServiceImpl implements ITradeService {

    @Autowired
    private TradeMapper tradeMapper;

    @Override
    public List<Trade> selectTradeList(Trade trade) {
        return tradeMapper.selectTradeList(trade);
    }

    @Override
    public Trade selectTradeById(Long id) {
        return tradeMapper.selectTradeById(id);
    }

    @Override
    @Transactional
    public int insertTrade(Trade trade) {
        trade.setCreateTime(DateUtils.getNowDate());
        return tradeMapper.insertTrade(trade);
    }
}