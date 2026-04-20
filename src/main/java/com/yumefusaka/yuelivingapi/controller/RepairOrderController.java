package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.pojo.Entity.RepairOrder;
import com.yumefusaka.yuelivingapi.service.RepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @GetMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<List<RepairOrder>> getAllRepairs() {
        List<RepairOrder> repairs = repairOrderService.list();
        return Result.success(repairs);
    }

    @PostMapping
    public Result<String> addRepair(@RequestBody RepairOrder repairOrder) {
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        repairOrder.setUserId(userId);
        repairOrderService.save(repairOrder);
        return Result.success("提交成功");
    }

    @PutMapping("/{id}/assign")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> assignRepair(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        Long assignUserId = request.get("assignUserId");
        RepairOrder repair = repairOrderService.getById(id);
        if (repair == null) {
            return Result.error("工单不存在");
        }
        repair.setAssignUserId(assignUserId);
        repair.setAssignTime(LocalDateTime.now());
        repair.setStatus(1); // 处理中
        repairOrderService.updateById(repair);
        return Result.success("分配成功");
    }

    @PutMapping("/{id}/complete")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> completeRepair(@PathVariable Long id) {
        RepairOrder repair = repairOrderService.getById(id);
        if (repair == null) {
            return Result.error("工单不存在");
        }
        repair.setStatus(2); // 已完成
        repair.setCompleteTime(LocalDateTime.now());
        repairOrderService.updateById(repair);
        return Result.success("工单已完成");
    }

    @PutMapping("/{id}/rate")
    public Result<String> rateRepair(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        RepairOrder repair = repairOrderService.getById(id);
        if (repair == null) {
            return Result.error("工单不存在");
        }

        // 检查是否是业主自己的工单
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        if (!repair.getUserId().equals(userId)) {
            return Result.error("无权评价此工单");
        }

        if (repair.getStatus() != 2) {
            return Result.error("只能评价已完成的工单");
        }

        Integer rating = (Integer) request.get("rating");
        String comment = (String) request.get("comment");

        repair.setRating(rating);
        repair.setComment(comment);
        repairOrderService.updateById(repair);
        return Result.success("评价成功");
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

    @PostMapping("/upload")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            String uploadDir = "uploads/repairs/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File destFile = new File(uploadDir + filename);
            file.transferTo(destFile);

            Map<String, String> data = new HashMap<>();
            data.put("url", "/uploads/repairs/" + filename);
            return Result.success(data);
        } catch (IOException e) {
            return Result.error("上传失败");
        }
    }
}