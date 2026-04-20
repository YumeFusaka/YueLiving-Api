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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        if (params.containsKey("billType") && params.get("billType") != null && !params.get("billType").toString().isBlank()) {
            wrapper.eq(Bill::getBillType, params.get("billType"));
        }
        if (params.containsKey("period") && params.get("period") != null && !params.get("period").toString().isBlank()) {
            wrapper.eq(Bill::getPeriod, params.get("period"));
        }
        wrapper.orderByDesc(Bill::getCreateTime);
        return billMapper.selectList(wrapper);
    }

    @Override
    public boolean payBill(Long billId, Long currentUserId) {
        Bill bill = getById(billId);
        if (bill == null || !Objects.equals(bill.getOwnerId(), currentUserId) || bill.getStatus() == 1) {
            return false;
        }
        bill.setStatus(1);
        bill.setPaidAmount(bill.getAmount());
        bill.setPayTime(LocalDateTime.now());
        return updateById(bill);
    }

    @Override
    public int generatePropertyFeeBills(String period) {
        List<Property> properties = propertyMapper.selectList(new LambdaQueryWrapper<Property>()
                .isNotNull(Property::getOwnerId)
                .eq(Property::getStatus, 1));
        int createdCount = 0;
        for (Property property : properties) {
            boolean exists = count(new LambdaQueryWrapper<Bill>()
                    .eq(Bill::getPropertyId, property.getId())
                    .eq(Bill::getBillType, "物业费")
                    .eq(Bill::getPeriod, period)) > 0;
            if (exists) {
                continue;
            }
            BigDecimal area = property.getArea() == null ? BigDecimal.ZERO : property.getArea();
            BigDecimal unitPrice = new BigDecimal("2.80");
            Bill bill = new Bill();
            bill.setPropertyId(property.getId());
            bill.setOwnerId(property.getOwnerId());
            bill.setBillType("物业费");
            bill.setBillItemName(period + " 物业费");
            bill.setUnitPrice(unitPrice);
            bill.setUsageAmount(area);
            bill.setAmount(area.multiply(unitPrice).setScale(2, BigDecimal.ROUND_HALF_UP));
            bill.setPeriod(period);
            bill.setGenerateType("AUTO");
            bill.setStatus(0);
            bill.setPaidAmount(BigDecimal.ZERO);
            bill.setDueDate(LocalDate.now().plusDays(15));
            save(bill);
            createdCount++;
        }
        return createdCount;
    }
}
