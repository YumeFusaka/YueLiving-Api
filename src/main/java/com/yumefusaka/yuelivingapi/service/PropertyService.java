package com.yumefusaka.yuelivingapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yumefusaka.yuelivingapi.pojo.Entity.Property;

import java.util.List;

public interface PropertyService extends IService<Property> {
    List<Property> getPropertiesByOwnerId(Long ownerId);
}