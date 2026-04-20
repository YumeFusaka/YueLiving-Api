package com.yumefusaka.yuelivingapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yumefusaka.yuelivingapi.common.context.BaseContext;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.pojo.Entity.Announcement;
import com.yumefusaka.yuelivingapi.service.OperationLogService;
import com.yumefusaka.yuelivingapi.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    public Result<List<Announcement>> getAnnouncements() {
        String currentRoleId = BaseContext.getCurrentRoleId();
        List<Announcement> announcements;
        if (currentRoleId != null &&
                (String.valueOf(RoleEnum.PROPERTY_MANAGER).equals(currentRoleId) || String.valueOf(RoleEnum.SYSTEM_ADMIN).equals(currentRoleId))) {
            announcements = announcementService.list(new LambdaQueryWrapper<Announcement>()
                    .orderByDesc(Announcement::getIsTop)
                    .orderByDesc(Announcement::getPublishTime));
        } else {
            announcements = announcementService.listPublishedAnnouncements();
        }
        return Result.success(announcements);
    }

    @PostMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> addAnnouncement(@RequestBody Announcement announcement) {
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        announcement.setPublishUserId(userId);
        if (announcement.getStatus() == null || announcement.getStatus().isBlank()) {
            announcement.setStatus("PUBLISHED");
        }
        announcementService.save(announcement);
        operationLogService.record("公告管理", "发布公告", "announcement", announcement.getId(), "发布公告 " + announcement.getTitle());
        return Result.success("发布成功");
    }

    @PutMapping
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> updateAnnouncement(@RequestBody Announcement announcement) {
        announcementService.updateById(announcement);
        operationLogService.record("公告管理", "更新公告", "announcement", announcement.getId(), "更新公告 " + announcement.getTitle());
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        announcementService.removeById(id);
        operationLogService.record("公告管理", "删除公告", "announcement", id, "删除公告");
        return Result.success("删除成功");
    }
}
