package com.yumefusaka.yuelivingapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yumefusaka.yuelivingapi.mapper.RepairOrderMapper;
import com.yumefusaka.yuelivingapi.pojo.Entity.RepairOrder;
import com.yumefusaka.yuelivingapi.service.RepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService {

    @Autowired
    private RepairOrderMapper repairOrderMapper;

    @Override
    public List<RepairOrder> getRepairsByUserId(Long userId) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getUserId, userId);
        return repairOrderMapper.selectList(wrapper);
    }
}