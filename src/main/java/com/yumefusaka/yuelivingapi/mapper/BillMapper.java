package com.yumefusaka.yuelivingapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yumefusaka.yuelivingapi.pojo.Entity.Bill;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BillMapper extends BaseMapper<Bill> {
}