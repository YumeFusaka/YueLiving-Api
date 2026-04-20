package com.yumefusaka.yuelivingapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yumefusaka.yuelivingapi.pojo.Entity.Bill;

import java.util.List;

public interface BillService extends IService<Bill> {
    List<Bill> getBillsByUserId(Long userId);
}