package com.yumefusaka.yuelivingapi.pojo.VO;

import com.yumefusaka.yuelivingapi.common.ai.AiCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiContext {
    private AiCategory category;
    private List<String> propertySummaries;
    private List<String> billSummaries;
    private List<String> repairSummaries;
    private List<String> announcementSummaries;
}
