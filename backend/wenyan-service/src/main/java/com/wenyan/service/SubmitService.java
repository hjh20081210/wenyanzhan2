package com.wenyan.service;

import com.wenyan.entity.dto.ExamSubmitDTO;
import com.wenyan.entity.dto.WriteSubmitDTO;
import com.wenyan.entity.vo.SubmitResultVO;

/** 作答批改服务 */
public interface SubmitService {
    /** 默写提交批改 */
    SubmitResultVO submitWrite(Long userId, WriteSubmitDTO dto);
    /** 真题提交批改 */
    SubmitResultVO submitExam(Long userId, ExamSubmitDTO dto);
}
