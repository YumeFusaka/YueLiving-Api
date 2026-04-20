package com.yumefusaka.yuelivingapi.pojo.Entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("repair_order")
public class RepairOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long propertyId;

    private Long userId;

    private String description;

    private String images;

    private Integer status;

    private Long assignUserId;

    private LocalDateTime assignTime;

    private LocalDateTime completeTime;

    private Integer rating;

    private String comment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}