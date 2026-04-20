package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.pojo.DTO.BindOwnerDTO;
import com.yumefusaka.yuelivingapi.pojo.Entity.Property;
import com.yumefusaka.yuelivingapi.service.OperationLogService;
import com.yumefusaka.yuelivingapi.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/property")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/my")
    public Result<List<Property>> getMyProperties() {
        // 从上下文获取当前用户ID
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        List<Property> properties = propertyService.getPropertiesByOwnerId(userId);
        return Result.success(properties);
    }

    @GetMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.list();
        return Result.success(properties);
    }

    @PostMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> addProperty(@RequestBody Property property) {
        propertyService.save(property);
        operationLogService.record("房产管理", "新增房产", "property", property.getId(), "新增房产");
        return Result.success("添加成功");
    }

    @PutMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> updateProperty(@RequestBody Property property) {
        propertyService.updateById(property);
        operationLogService.record("房产管理", "更新房产", "property", property.getId(), "更新房产");
        return Result.success("更新成功");
    }

    @PutMapping("/bind-owner")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> bindOwner(@RequestBody BindOwnerDTO dto) {
        propertyService.bindOwner(dto);
        operationLogService.record("房产管理", "绑定业主", "property", dto.getPropertyId(), "绑定业主 " + dto.getOwnerId());
        return Result.success("绑定成功");
    }

    @PutMapping("/{id}/unbind-owner")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> unbindOwner(@PathVariable Long id) {
        propertyService.unbindOwner(id);
        operationLogService.record("房产管理", "解绑业主", "property", id, "解绑业主");
        return Result.success("解绑成功");
    }

    @DeleteMapping("/{id}")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> deleteProperty(@PathVariable Long id) {
        propertyService.removeById(id);
        operationLogService.record("房产管理", "删除房产", "property", id, "删除房产");
        return Result.success("删除成功");
    }
}
