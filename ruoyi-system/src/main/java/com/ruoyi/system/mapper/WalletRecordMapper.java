package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.WalletRecord;

public interface WalletRecordMapper {
    List<WalletRecord> selectWalletRecordList(Long userId);
    WalletRecord selectWalletRecordById(Long id);
    int insertWalletRecord(WalletRecord record);
}