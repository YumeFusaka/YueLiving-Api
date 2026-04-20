package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.pojo.Entity.Bill;
import com.yumefusaka.yuelivingapi.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping("/my")
    public Result<List<Bill>> getMyBills(@RequestParam(required = false) Map<String, Object> params) {
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        List<Bill> bills = billService.getBillsByUserId(userId);
        if (params != null && params.get("status") != null) {
            bills = bills.stream().filter(bill -> bill.getStatus().equals(Integer.valueOf(params.get("status").toString()))).toList();
        }
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
        if (bill.getPaidAmount() == null) {
            bill.setPaidAmount(java.math.BigDecimal.ZERO);
        }
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
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        if (billService.payBill(id, userId)) {
            return Result.success("缴费成功");
        }
        return Result.error("无权操作此账单或账单已缴费");
    }

    @PostMapping("/generate")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> generateBills(@RequestBody Map<String, String> payload) {
        String period = payload.get("period");
        int createdCount = billService.generatePropertyFeeBills(period);
        return Result.success("生成账单 " + createdCount + " 条");
    }
}
