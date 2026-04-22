package com.yumefusaka.yuelivingapi.service;

import com.yumefusaka.yuelivingapi.common.ai.AiCategory;
import com.yumefusaka.yuelivingapi.pojo.VO.AiContext;

public interface AiQueryService {
    AiContext buildContext(Long userId, AiCategory category);
}
