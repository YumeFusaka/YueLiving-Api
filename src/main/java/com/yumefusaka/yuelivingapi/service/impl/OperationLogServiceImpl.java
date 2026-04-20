package com.yumefusaka.yuelivingapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yumefusaka.yuelivingapi.common.context.BaseContext;
import com.yumefusaka.yuelivingapi.mapper.OperationLogMapper;
import com.yumefusaka.yuelivingapi.pojo.Entity.OperationLog;
import com.yumefusaka.yuelivingapi.service.OperationLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public void record(String moduleName, String actionName, String targetType, Long targetId, String content) {
        OperationLog log = new OperationLog();
        log.setOperatorId(Long.valueOf(BaseContext.getCurrentId()));
        log.setOperatorRoleId(Long.valueOf(BaseContext.getCurrentRoleId()));
        log.setModuleName(moduleName);
        log.setActionName(actionName);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setContent(content);
        save(log);
    }

    @Override
    public List<OperationLog> listByOperator(Long operatorId) {
        return list(new LambdaQueryWrapper<OperationLog>()
                .eq(OperationLog::getOperatorId, operatorId)
                .orderByDesc(OperationLog::getCreateTime));
    }
}
