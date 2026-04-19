package com.yumefusaka.yuelivingapi.controller;


import com.yumefusaka.yuelivingapi.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/test")
@Tag(name = "测试")
public class TestController {

    @Operation(summary = "测试")
    @GetMapping("/test")
    public Result<String> login () throws Exception {
        return Result.success("测试成功");
    }

}