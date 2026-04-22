package com.yumefusaka.yuelivingapi.service.impl;

import com.yumefusaka.yuelivingapi.common.ai.AiCategory;
import com.yumefusaka.yuelivingapi.pojo.Entity.Announcement;
import com.yumefusaka.yuelivingapi.pojo.Entity.Bill;
import com.yumefusaka.yuelivingapi.pojo.Entity.Property;
import com.yumefusaka.yuelivingapi.pojo.Entity.RepairOrder;
import com.yumefusaka.yuelivingapi.pojo.VO.AiContext;
import com.yumefusaka.yuelivingapi.service.AiQueryService;
import com.yumefusaka.yuelivingapi.service.AnnouncementService;
import com.yumefusaka.yuelivingapi.service.BillService;
import com.yumefusaka.yuelivingapi.service.PropertyService;
import com.yumefusaka.yuelivingapi.service.RepairOrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class AiQueryServiceImpl implements AiQueryService {

    private final PropertyService propertyService;
    private final BillService billService;
    private final RepairOrderService repairOrderService;
    private final AnnouncementService announcementService;

    public AiQueryServiceImpl(PropertyService propertyService,
                              BillService billService,
                              RepairOrderService repairOrderService,
                              AnnouncementService announcementService) {
        this.propertyService = propertyService;
        this.billService = billService;
        this.repairOrderService = repairOrderService;
        this.announcementService = announcementService;
    }

    @Override
    public AiContext buildContext(Long userId, AiCategory category) {
        List<Property> properties = propertyService.getPropertiesByOwnerId(userId);
        List<Bill> bills = billService.getBillsByUserId(userId);
        List<RepairOrder> repairs = repairOrderService.getRepairsByUserId(userId);
        List<Announcement> announcements = announcementService.listPublishedAnnouncements();

        return new AiContext(
                category,
                summarizeProperties(properties),
                summarizeBills(bills),
                summarizeRepairs(repairs),
                summarizeAnnouncements(announcements)
        );
    }

    private List<String> summarizeProperties(List<Property> properties) {
        return properties.stream()
                .limit(3)
                .map(property -> String.format("我的房产：%s号楼 %s单元 %s，类型=%s",
                        nullToPlaceholder(property.getBuildingNo()),
                        nullToPlaceholder(property.getUnitNo()),
                        nullToPlaceholder(property.getRoomNo()),
                        nullToPlaceholder(property.getPropertyType())))
                .toList();
    }

    private List<String> summarizeBills(List<Bill> bills) {
        return bills.stream()
                .sorted(Comparator.comparing(Bill::getStatus).thenComparing(Bill::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(5)
                .map(bill -> String.format("%s账单：%s %s %s元，状态=%s",
                        bill.getStatus() != null && bill.getStatus() == 1 ? "已缴费" : "未缴费",
                        nullToPlaceholder(bill.getPeriod()),
                        nullToPlaceholder(bill.getBillItemName()),
                        formatAmount(bill.getAmount()),
                        formatBillStatus(bill.getStatus())))
                .toList();
    }

    private List<String> summarizeRepairs(List<RepairOrder> repairs) {
        return repairs.stream()
                .sorted(Comparator.comparing(RepairOrder::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(repair -> String.format("最近报修：%s，状态=%s",
                        nullToPlaceholder(repair.getDescription()),
                        formatRepairStatus(repair.getStatus())))
                .toList();
    }

    private List<String> summarizeAnnouncements(List<Announcement> announcements) {
        return announcements.stream()
                .limit(3)
                .map(announcement -> "最新公告：" + nullToPlaceholder(announcement.getTitle()))
                .toList();
    }

    private String formatAmount(BigDecimal amount) {
        return amount == null ? "0" : amount.stripTrailingZeros().toPlainString();
    }

    private String nullToPlaceholder(String value) {
        return value == null || value.isBlank() ? "未填写" : value;
    }

    private String formatBillStatus(Integer status) {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "已缴费" : "未缴费";
    }

    private String formatRepairStatus(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "待受理";
            case 1 -> "处理中";
            case 2 -> "待评价";
            case 3 -> "已完成";
            default -> "未知";
        };
    }
}
