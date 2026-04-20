package com.yumefusaka.yuelivingapi.common.role;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoleRequired {
    long[] value();
}
