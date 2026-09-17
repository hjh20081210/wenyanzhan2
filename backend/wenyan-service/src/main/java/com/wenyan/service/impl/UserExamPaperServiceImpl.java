package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.UserExamPaper;
import com.wenyan.mapper.UserExamPaperMapper;
import com.wenyan.service.UserExamPaperService;
import org.springframework.stereotype.Service;

/** UserExamPaper Service 实现 */
@Service
public class UserExamPaperServiceImpl extends ServiceImpl<UserExamPaperMapper, UserExamPaper> implements UserExamPaperService {
}
