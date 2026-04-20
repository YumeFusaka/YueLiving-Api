package com.yumefusaka.yuelivingapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yumefusaka.yuelivingapi.pojo.Entity.OperationLog;

import java.util.List;

public interface OperationLogService extends IService<OperationLog> {
    void record(String moduleName, String actionName, String targetType, Long targetId, String content);
    List<OperationLog> listByOperator(Long operatorId);
}
