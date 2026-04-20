package com.yumefusaka.yuelivingapi.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindOwnerDTO {
    private Long propertyId;
    private Long ownerId;
}
