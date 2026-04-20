package com.yumefusaka.yuelivingapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yumefusaka.yuelivingapi.mapper.PropertyMapper;
import com.yumefusaka.yuelivingapi.pojo.Entity.Property;
import com.yumefusaka.yuelivingapi.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyServiceImpl extends ServiceImpl<PropertyMapper, Property> implements PropertyService {

    @Autowired
    private PropertyMapper propertyMapper;

    @Override
    public List<Property> getPropertiesByOwnerId(Long ownerId) {
        LambdaQueryWrapper<Property> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Property::getOwnerId, ownerId);
        return propertyMapper.selectList(wrapper);
    }
}