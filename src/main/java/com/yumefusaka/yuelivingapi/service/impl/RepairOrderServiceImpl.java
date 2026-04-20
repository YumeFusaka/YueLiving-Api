package com.yumefusaka.yuelivingapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yumefusaka.yuelivingapi.mapper.RepairOrderMapper;
import com.yumefusaka.yuelivingapi.pojo.Entity.RepairOrder;
import com.yumefusaka.yuelivingapi.service.RepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService {

    @Autowired
    private RepairOrderMapper repairOrderMapper;

    @Override
    public List<RepairOrder> getRepairsByUserId(Long userId) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getUserId, userId);
        wrapper.orderByDesc(RepairOrder::getCreateTime);
        return repairOrderMapper.selectList(wrapper);
    }

    @Override
    public List<RepairOrder> getRepairsWithFilter(Map<String, Object> params) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("status") && params.get("status") != null) {
            wrapper.eq(RepairOrder::getStatus, params.get("status"));
        }
        wrapper.orderByDesc(RepairOrder::getCreateTime);
        return repairOrderMapper.selectList(wrapper);
    }
}
