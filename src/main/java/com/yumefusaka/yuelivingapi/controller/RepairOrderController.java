package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.pojo.Entity.RepairOrder;
import com.yumefusaka.yuelivingapi.service.RepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repair")
public class RepairOrderController {

    @Autowired
    private RepairOrderService repairOrderService;

    @GetMapping("/my")
    public Result<List<RepairOrder>> getMyRepairs() {
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        List<RepairOrder> repairs = repairOrderService.getRepairsByUserId(userId);
        return Result.success(repairs);
    }

    @PostMapping
    public Result<String> addRepair(@RequestBody RepairOrder repairOrder) {
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        repairOrder.setUserId(userId);
        repairOrderService.save(repairOrder);
        return Result.success("提交成功");
    }

    @PutMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> updateRepair(@RequestBody RepairOrder repairOrder) {
        repairOrderService.updateById(repairOrder);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> deleteRepair(@PathVariable Long id) {
        repairOrderService.removeById(id);
        return Result.success("删除成功");
    }
}