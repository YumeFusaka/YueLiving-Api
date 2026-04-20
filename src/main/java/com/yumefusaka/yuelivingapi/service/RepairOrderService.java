package com.yumefusaka.yuelivingapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yumefusaka.yuelivingapi.pojo.Entity.RepairOrder;

import java.util.List;

public interface RepairOrderService extends IService<RepairOrder> {
    List<RepairOrder> getRepairsByUserId(Long userId);
}