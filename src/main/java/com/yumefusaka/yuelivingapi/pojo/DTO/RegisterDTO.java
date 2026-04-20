package com.yumefusaka.yuelivingapi.pojo.DTO;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String phone;
    private String email;
    private String realName;
}