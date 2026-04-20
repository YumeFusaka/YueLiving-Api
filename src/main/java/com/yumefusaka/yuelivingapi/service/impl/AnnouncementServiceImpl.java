package com.yumefusaka.yuelivingapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yumefusaka.yuelivingapi.mapper.AnnouncementMapper;
import com.yumefusaka.yuelivingapi.pojo.Entity.Announcement;
import com.yumefusaka.yuelivingapi.service.AnnouncementService;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
}