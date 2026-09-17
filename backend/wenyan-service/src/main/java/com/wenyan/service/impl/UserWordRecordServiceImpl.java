package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.UserWordRecord;
import com.wenyan.mapper.UserWordRecordMapper;
import com.wenyan.service.UserWordRecordService;
import org.springframework.stereotype.Service;

/** UserWordRecord Service 实现 */
@Service
public class UserWordRecordServiceImpl extends ServiceImpl<UserWordRecordMapper, UserWordRecord> implements UserWordRecordService {
}
