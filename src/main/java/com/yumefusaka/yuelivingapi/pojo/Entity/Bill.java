package com.yumefusaka.yuelivingapi.pojo.Entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("bill")
public class Bill {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long propertyId;

    private String billType;

    private BigDecimal amount;

    @TableField(value = "`period`")
    private String period;

    private Integer status;

    private LocalDate dueDate;

    private LocalDateTime payTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}