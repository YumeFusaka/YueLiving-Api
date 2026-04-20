package com.yumefusaka.yuelivingapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yumefusaka.yuelivingapi.mapper.PropertyMapper;
import com.yumefusaka.yuelivingapi.mapper.UserMapper;
import com.yumefusaka.yuelivingapi.pojo.DTO.BindOwnerDTO;
import com.yumefusaka.yuelivingapi.pojo.Entity.Property;
import com.yumefusaka.yuelivingapi.pojo.Entity.User;
import com.yumefusaka.yuelivingapi.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PropertyServiceImpl extends ServiceImpl<PropertyMapper, Property> implements PropertyService {

    @Autowired
    private PropertyMapper propertyMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Property> getPropertiesByOwnerId(Long ownerId) {
        LambdaQueryWrapper<Property> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Property::getOwnerId, ownerId);
        return propertyMapper.selectList(wrapper);
    }

    @Override
    public void bindOwner(BindOwnerDTO dto) {
        Property property = propertyMapper.selectById(dto.getPropertyId());
        User owner = userMapper.selectById(dto.getOwnerId());
        if (property == null || owner == null) {
            throw new RuntimeException("房产或业主不存在");
        }
        property.setOwnerId(owner.getId());
        property.setOwnerNameSnapshot(owner.getRealName() != null ? owner.getRealName() : owner.getUsername());
        property.setBindTime(LocalDateTime.now());
        property.setStatus(1);
        propertyMapper.updateById(property);
    }

    @Override
    public void unbindOwner(Long propertyId) {
        Property property = propertyMapper.selectById(propertyId);
        if (property == null) {
            throw new RuntimeException("房产不存在");
        }
        property.setOwnerId(null);
        property.setOwnerNameSnapshot(null);
        property.setBindTime(null);
        property.setStatus(0);
        propertyMapper.updateById(property);
    }
}
