package com.yumefusaka.yuelivingapi.common.ai;

import com.yumefusaka.yuelivingapi.pojo.VO.AiContext;

import java.util.List;

public final class AiPromptBuilder {

    private AiPromptBuilder() {
    }

    public static String buildSystemPrompt() {
        return """
                你是物业系统业主端智能客服。
                你只能回答物业系统相关问题，包括房产、账单、缴费、报修、公告和相关办事流程。
                你只能依据系统提供的业务数据和规则回答，不能编造不存在的信息。
                你只允许回答当前登录业主自己的信息，不能推测其他住户或管理员的数据。
                如果系统数据中没有相关内容，请明确说明“当前未查询到相关数据”。
                你不能代替用户执行操作，只能提供查询结果、流程说明和简短建议。
                回答尽量简洁、自然、适合中文用户阅读。
                """;
    }

    public static String buildUserPrompt(String message, AiContext context) {
        StringBuilder builder = new StringBuilder();
        builder.append("用户问题：").append(message).append("\n");
        builder.append("问题类别：").append(context.getCategory().name()).append("\n");
        appendSection(builder, "房产摘要", context.getPropertySummaries());
        appendSection(builder, "账单摘要", context.getBillSummaries());
        appendSection(builder, "报修摘要", context.getRepairSummaries());
        appendSection(builder, "公告摘要", context.getAnnouncementSummaries());
        builder.append("请严格根据以上信息回答。如果信息不足，请直接说明。");
        return builder.toString();
    }

    private static void appendSection(StringBuilder builder, String title, List<String> items) {
        builder.append(title).append("：\n");
        if (items == null || items.isEmpty()) {
            builder.append("- 当前未查询到相关数据\n");
            return;
        }
        for (String item : items) {
            builder.append("- ").append(item).append("\n");
        }
    }
}
