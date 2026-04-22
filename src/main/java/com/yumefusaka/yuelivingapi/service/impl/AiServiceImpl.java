package com.yumefusaka.yuelivingapi.service.impl;

import com.yumefusaka.yuelivingapi.common.ai.AiCategory;
import com.yumefusaka.yuelivingapi.common.ai.AiPromptBuilder;
import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.pojo.VO.AiChatResponse;
import com.yumefusaka.yuelivingapi.pojo.VO.AiContext;
import com.yumefusaka.yuelivingapi.service.AiQueryService;
import com.yumefusaka.yuelivingapi.service.AiService;
import com.yumefusaka.yuelivingapi.service.QwenClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    private final AiQueryService aiQueryService;
    private final QwenClient qwenClient;
    private final boolean aiEnabled;
    private final String model;

    public AiServiceImpl(AiQueryService aiQueryService,
                         QwenClient qwenClient,
                         @Value("${ai.enabled:false}") boolean aiEnabled,
                         @Value("${ai.model:qwen-plus}") String model) {
        this.aiQueryService = aiQueryService;
        this.qwenClient = qwenClient;
        this.aiEnabled = aiEnabled;
        this.model = model;
    }

    @Override
    public AiChatResponse chat(String message, Long userId, String roleId) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("问题不能为空");
        }
        if (!String.valueOf(RoleEnum.OWNER).equals(roleId)) {
            throw new IllegalStateException("仅业主可使用智能客服");
        }

        AiCategory category = classifyCategory(message);
        AiContext context = aiQueryService.buildContext(userId, category);
        String answer = generateAnswer(message, context);
        return new AiChatResponse(category.name(), answer);
    }

    public static AiCategory classifyCategory(String message) {
        String normalized = message == null ? "" : message.trim();
        if (containsAny(normalized, "怎么", "如何", "流程", "步骤")) {
            return AiCategory.GENERAL;
        }
        if (containsAny(normalized, "账单", "物业费", "缴费", "欠费")) {
            return AiCategory.BILL;
        }
        if (containsAny(normalized, "报修", "维修", "工单", "进度")) {
            return AiCategory.REPAIR;
        }
        if (containsAny(normalized, "房产", "房屋", "绑定", "名下")) {
            return AiCategory.PROPERTY;
        }
        if (containsAny(normalized, "公告", "通知")) {
            return AiCategory.ANNOUNCEMENT;
        }
        return AiCategory.GENERAL;
    }

    private String generateAnswer(String message, AiContext context) {
        if (aiEnabled) {
            try {
                return qwenClient.chat(model, AiPromptBuilder.buildSystemPrompt(), AiPromptBuilder.buildUserPrompt(message, context));
            } catch (RuntimeException ex) {
                return buildFallbackAnswer(context, true);
            }
        }
        return buildFallbackAnswer(context, false);
    }

    private String buildFallbackAnswer(AiContext context, boolean modelFailed) {
        StringBuilder builder = new StringBuilder();
        if (modelFailed) {
            builder.append("当前智能客服模型调用失败，以下为系统整理的参考信息：");
        } else {
            builder.append("当前为简化回答模式，以下是系统整理的信息：");
        }
        builder.append("\n");
        switch (context.getCategory()) {
            case PROPERTY -> appendSection(builder, "房产信息", context.getPropertySummaries());
            case BILL -> appendSection(builder, "账单信息", context.getBillSummaries());
            case REPAIR -> appendSection(builder, "报修信息", context.getRepairSummaries());
            case ANNOUNCEMENT -> appendSection(builder, "公告信息", context.getAnnouncementSummaries());
            case GENERAL -> {
                appendSection(builder, "房产信息", context.getPropertySummaries());
                appendSection(builder, "账单信息", context.getBillSummaries());
                appendSection(builder, "报修信息", context.getRepairSummaries());
                appendSection(builder, "公告信息", context.getAnnouncementSummaries());
                builder.append("如果你想问得更准确，可以直接提问“我的未缴费账单”“我的报修进度”“最新公告”等。\n");
            }
        }
        return builder.toString().trim();
    }

    private void appendSection(StringBuilder builder, String title, List<String> items) {
        builder.append(title).append("：");
        if (items == null || items.isEmpty()) {
            builder.append("当前未查询到相关数据。\n");
            return;
        }
        builder.append(String.join("；", items)).append("\n");
    }

    private static boolean containsAny(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
