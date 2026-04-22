package com.yumefusaka.yuelivingapi.service;

import com.yumefusaka.yuelivingapi.common.ai.AiCategory;
import com.yumefusaka.yuelivingapi.pojo.VO.AiChatResponse;
import com.yumefusaka.yuelivingapi.pojo.VO.AiContext;
import com.yumefusaka.yuelivingapi.service.impl.AiServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiServiceImplTest {

    @Test
    void shouldClassifyBillQuestions() {
        assertEquals(AiCategory.BILL, AiServiceImpl.classifyCategory("我有没有未缴费账单"));
        assertEquals(AiCategory.REPAIR, AiServiceImpl.classifyCategory("我的报修进度到哪了"));
        assertEquals(AiCategory.PROPERTY, AiServiceImpl.classifyCategory("我名下有哪些房产"));
        assertEquals(AiCategory.ANNOUNCEMENT, AiServiceImpl.classifyCategory("最近有哪些公告"));
        assertEquals(AiCategory.GENERAL, AiServiceImpl.classifyCategory("怎么提交报修"));
    }

    @Test
    void shouldRejectUnsupportedRole() {
        AiQueryService aiQueryService = mock(AiQueryService.class);
        QwenClient qwenClient = mock(QwenClient.class);
        AiServiceImpl service = new AiServiceImpl(aiQueryService, qwenClient, true, "qwen-plus");

        assertThrows(IllegalStateException.class, () -> service.chat("你好", 2L, "2"));
    }

    @Test
    void shouldBuildOwnerContextAndReturnModelAnswer() {
        AiQueryService aiQueryService = mock(AiQueryService.class);
        QwenClient qwenClient = mock(QwenClient.class);
        AiServiceImpl service = new AiServiceImpl(aiQueryService, qwenClient, true, "qwen-plus");
        AiContext context = new AiContext(
                AiCategory.BILL,
                List.of("我的房产：1号楼 1单元 101"),
                List.of("未缴费账单：2026-04 物业费 300元"),
                List.of(),
                List.of("最新公告：缴费系统维护通知")
        );

        when(aiQueryService.buildContext(8L, AiCategory.BILL)).thenReturn(context);
        when(qwenClient.chat(anyString(), anyString(), anyString())).thenReturn("您当前有1笔未缴费账单。");

        AiChatResponse response = service.chat("我有没有未缴费账单", 8L, "1");

        assertEquals("BILL", response.getCategory());
        assertEquals("您当前有1笔未缴费账单。", response.getAnswer());
        verify(aiQueryService).buildContext(8L, AiCategory.BILL);
        verify(qwenClient).chat(anyString(), anyString(), anyString());
    }
}
