package com.yumefusaka.yuelivingapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yumefusaka.yuelivingapi.pojo.Entity.Bill;

import java.util.List;
import java.util.Map;

public interface BillService extends IService<Bill> {
    List<Bill> getBillsByUserId(Long userId);
    List<Bill> getBillsWithFilter(Map<String, Object> params);
    boolean payBill(Long billId, Long currentUserId);
    int generatePropertyFeeBills(String period);
}
