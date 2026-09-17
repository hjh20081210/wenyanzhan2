package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.ExamQuestion;
import com.wenyan.mapper.ExamQuestionMapper;
import com.wenyan.service.ExamQuestionService;
import org.springframework.stereotype.Service;

/** ExamQuestion Service 实现 */
@Service
public class ExamQuestionServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements ExamQuestionService {
}
