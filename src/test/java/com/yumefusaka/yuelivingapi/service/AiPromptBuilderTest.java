package com.yumefusaka.yuelivingapi.service;

import com.yumefusaka.yuelivingapi.common.ai.AiCategory;
import com.yumefusaka.yuelivingapi.common.ai.AiPromptBuilder;
import com.yumefusaka.yuelivingapi.pojo.VO.AiContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AiPromptBuilderTest {

    @Test
    void shouldBuildPromptWithOwnerDataSummary() {
        AiContext context = new AiContext(
                AiCategory.REPAIR,
                List.of("我的房产：1号楼 2单元 301"),
                List.of("未缴费账单：2026-04 物业费 260元"),
                List.of("最近报修：厨房水龙头漏水，状态=处理中"),
                List.of("最新公告：五一期间物业值班安排")
        );

        String prompt = AiPromptBuilder.buildUserPrompt("我最近的报修进度", context);

        assertTrue(prompt.contains("用户问题：我最近的报修进度"));
        assertTrue(prompt.contains("问题类别：REPAIR"));
        assertTrue(prompt.contains("最近报修：厨房水龙头漏水，状态=处理中"));
        assertTrue(prompt.contains("最新公告：五一期间物业值班安排"));
    }
}
