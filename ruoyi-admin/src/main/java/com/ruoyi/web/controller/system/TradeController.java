package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Trade;
import com.ruoyi.system.service.ITradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trade")
public class TradeController {

    @Autowired
    private ITradeService tradeService;

    @GetMapping("/list")
    public R getTradeList(Trade trade) {
        List<Trade> trades = tradeService.selectTradeList(trade);
        return R.ok(trades);
    }

    @GetMapping("/{id}")
    public R getTradeById(@PathVariable Long id) {
        Trade trade = tradeService.selectTradeById(id);
        return R.ok(trade);
    }

    @PostMapping("/add")
    public R addTrade(@RequestBody Trade trade) {
        tradeService.insertTrade(trade);
        return R.ok("添加成功");
    }
}