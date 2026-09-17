package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.MemberOrder;
import com.wenyan.mapper.MemberOrderMapper;
import com.wenyan.service.MemberOrderService;
import org.springframework.stereotype.Service;

/** MemberOrder Service 实现 */
@Service
public class MemberOrderServiceImpl extends ServiceImpl<MemberOrderMapper, MemberOrder> implements MemberOrderService {
}
