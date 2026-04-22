package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.context.BaseContext;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.pojo.DTO.AiChatRequest;
import com.yumefusaka.yuelivingapi.pojo.VO.AiChatResponse;
import com.yumefusaka.yuelivingapi.service.AiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public Result<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        Long userId = Long.valueOf(BaseContext.getCurrentId());
        String roleId = BaseContext.getCurrentRoleId();
        return Result.success(aiService.chat(request.getMessage(), userId, roleId));
    }
}
