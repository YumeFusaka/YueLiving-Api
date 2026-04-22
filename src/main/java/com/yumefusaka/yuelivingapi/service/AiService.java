package com.yumefusaka.yuelivingapi.service;

import com.yumefusaka.yuelivingapi.pojo.VO.AiChatResponse;

public interface AiService {
    AiChatResponse chat(String message, Long userId, String roleId);
}
