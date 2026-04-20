package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.pojo.Entity.Bill;
import com.yumefusaka.yuelivingapi.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping("/my")
    public Result<List<Bill>> getMyBills() {
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        List<Bill> bills = billService.getBillsByUserId(userId);
        return Result.success(bills);
    }

    @GetMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<List<Bill>> getAllBills(@RequestParam Map<String, Object> params) {
        List<Bill> bills = billService.getBillsWithFilter(params);
        return Result.success(bills);
    }

    @PostMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> addBill(@RequestBody Bill bill) {
        billService.save(bill);
        return Result.success("添加成功");
    }

    @PutMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> updateBill(@RequestBody Bill bill) {
        billService.updateById(bill);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> deleteBill(@PathVariable Long id) {
        billService.removeById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/pay")
    public Result<String> payBill(@PathVariable Long id) {
        Bill bill = billService.getById(id);
        if (bill == null) {
            return Result.error("账单不存在");
        }

        // 检查是否是业主自己的账单
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        if (!bill.getPropertyId().equals(userId)) {
            return Result.error("无权操作此账单");
        }

        if (bill.getStatus() == 1) {
            return Result.error("账单已缴费");
        }

        bill.setStatus(1);
        bill.setPayTime(LocalDateTime.now());
        billService.updateById(bill);
        return Result.success("缴费成功");
    }
}