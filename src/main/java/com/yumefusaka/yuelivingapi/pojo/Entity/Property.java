package com.yumefusaka.yuelivingapi.pojo.Entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("property")
public class Property {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String buildingNo;

    private String unitNo;

    private String roomNo;

    private BigDecimal area;

    private String propertyType;

    private Long ownerId;

    private String ownerNameSnapshot;

    private LocalDateTime bindTime;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
