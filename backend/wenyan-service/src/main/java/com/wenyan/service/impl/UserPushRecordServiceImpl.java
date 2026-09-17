package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.UserPushRecord;
import com.wenyan.mapper.UserPushRecordMapper;
import com.wenyan.service.UserPushRecordService;
import org.springframework.stereotype.Service;

/** UserPushRecord Service 实现 */
@Service
public class UserPushRecordServiceImpl extends ServiceImpl<UserPushRecordMapper, UserPushRecord> implements UserPushRecordService {
}
