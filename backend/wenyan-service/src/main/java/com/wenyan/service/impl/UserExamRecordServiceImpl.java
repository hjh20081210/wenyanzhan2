package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.UserExamRecord;
import com.wenyan.mapper.UserExamRecordMapper;
import com.wenyan.service.UserExamRecordService;
import org.springframework.stereotype.Service;

/** UserExamRecord Service 实现 */
@Service
public class UserExamRecordServiceImpl extends ServiceImpl<UserExamRecordMapper, UserExamRecord> implements UserExamRecordService {
}
