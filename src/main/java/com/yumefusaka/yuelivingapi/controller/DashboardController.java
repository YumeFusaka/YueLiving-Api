package com.yumefusaka.yuelivingapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yumefusaka.yuelivingapi.common.context.BaseContext;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.pojo.Entity.Announcement;
import com.yumefusaka.yuelivingapi.pojo.Entity.Bill;
import com.yumefusaka.yuelivingapi.pojo.Entity.Property;
import com.yumefusaka.yuelivingapi.pojo.Entity.RepairOrder;
import com.yumefusaka.yuelivingapi.pojo.Entity.User;
import com.yumefusaka.yuelivingapi.service.AnnouncementService;
import com.yumefusaka.yuelivingapi.service.BillService;
import com.yumefusaka.yuelivingapi.service.PropertyService;
import com.yumefusaka.yuelivingapi.service.RepairOrderService;
import com.yumefusaka.yuelivingapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private BillService billService;

    @Autowired
    private RepairOrderService repairOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/owner")
    public Result<Map<String, Object>> ownerDashboard() {
        Long userId = Long.valueOf(BaseContext.getCurrentId());
        Map<String, Object> data = new HashMap<>();
        data.put("totalProperties", propertyService.getPropertiesByOwnerId(userId).size());
        data.put("unpaidBills", billService.getBillsByUserId(userId).stream().filter(bill -> bill.getStatus() != 1).count());
        data.put("pendingRepairs", repairOrderService.getRepairsByUserId(userId).stream().filter(repair -> repair.getStatus() != 3).count());
        List<Announcement> announcements = announcementService.listPublishedAnnouncements();
        data.put("latestAnnouncements", announcements.stream().limit(5).toList());
        return Result.success(data);
    }

    @GetMapping("/statistics/manager")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<Map<String, Object>> managerStatistics() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalProperties", propertyService.count());
        data.put("unpaidBills", billService.count(new LambdaQueryWrapper<Bill>().ne(Bill::getStatus, 1)));
        data.put("pendingRepairs", repairOrderService.count(new LambdaQueryWrapper<RepairOrder>().ne(RepairOrder::getStatus, 3)));
        data.put("totalUsers", userService.count(new LambdaQueryWrapper<User>().eq(User::getRoleId, RoleEnum.OWNER)));
        return Result.success(data);
    }

    @GetMapping("/statistics/system")
    @RoleRequired({RoleEnum.SYSTEM_ADMIN})
    public Result<Map<String, Object>> systemStatistics() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalProperties", propertyService.count());
        data.put("unpaidBills", billService.count(new LambdaQueryWrapper<Bill>().ne(Bill::getStatus, 1)));
        data.put("pendingRepairs", repairOrderService.count(new LambdaQueryWrapper<RepairOrder>().ne(RepairOrder::getStatus, 3)));
        data.put("totalUsers", userService.count());
        data.put("managerUsers", userService.count(new LambdaQueryWrapper<User>().eq(User::getRoleId, RoleEnum.PROPERTY_MANAGER)));
        return Result.success(data);
    }
}
