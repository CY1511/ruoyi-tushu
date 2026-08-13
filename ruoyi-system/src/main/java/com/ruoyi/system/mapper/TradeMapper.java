package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.Trade;

@Mapper
public interface TradeMapper {
    List<Trade> selectTradeList(Trade trade);
    Trade selectTradeById(Long id);
    int insertTrade(Trade trade);
}