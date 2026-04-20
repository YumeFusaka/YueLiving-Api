package com.yumefusaka.yuelivingapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yumefusaka.yuelivingapi.mapper.BillMapper;
import com.yumefusaka.yuelivingapi.mapper.PropertyMapper;
import com.yumefusaka.yuelivingapi.pojo.Entity.Bill;
import com.yumefusaka.yuelivingapi.pojo.Entity.Property;
import com.yumefusaka.yuelivingapi.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private PropertyMapper propertyMapper;

    @Override
    public List<Bill> getBillsByUserId(Long userId) {
        // 获取用户房产ID
        LambdaQueryWrapper<Property> propertyWrapper = new LambdaQueryWrapper<>();
        propertyWrapper.eq(Property::getOwnerId, userId);
        List<Property> properties = propertyMapper.selectList(propertyWrapper);
        List<Long> propertyIds = properties.stream().map(Property::getId).collect(Collectors.toList());

        if (propertyIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<Bill> billWrapper = new LambdaQueryWrapper<>();
        billWrapper.in(Bill::getPropertyId, propertyIds);
        return billMapper.selectList(billWrapper);
    }

    @Override
    public List<Bill> getBillsWithFilter(Map<String, Object> params) {
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("status") && params.get("status") != null) {
            wrapper.eq(Bill::getStatus, params.get("status"));
        }
        if (params.containsKey("type") && params.get("type") != null) {
            wrapper.eq(Bill::getBillType, params.get("type"));
        }
        // 可以添加更多筛选条件，如日期范围等
        return billMapper.selectList(wrapper);
    }
}