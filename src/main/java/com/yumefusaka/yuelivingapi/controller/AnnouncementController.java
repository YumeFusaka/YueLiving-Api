package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.pojo.Entity.Announcement;
import com.yumefusaka.yuelivingapi.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping
    public Result<List<Announcement>> getAnnouncements() {
        List<Announcement> announcements = announcementService.list();
        return Result.success(announcements);
    }

    @PostMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> addAnnouncement(@RequestBody Announcement announcement) {
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        announcement.setPublishUserId(userId);
        announcementService.save(announcement);
        return Result.success("发布成功");
    }

    @PutMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> updateAnnouncement(@RequestBody Announcement announcement) {
        announcementService.updateById(announcement);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        announcementService.removeById(id);
        return Result.success("删除成功");
    }
}