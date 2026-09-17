package com.wenyan.service.impl;

import com.wenyan.common.exception.BusinessException;
import com.wenyan.common.exception.ErrorCode;
import com.wenyan.common.util.SnowflakeIdUtil;
import com.wenyan.entity.dto.ExamSubmitDTO;
import com.wenyan.entity.dto.WriteSubmitDTO;
import com.wenyan.entity.entity.ExamQuestion;
import com.wenyan.entity.entity.SysUser;
import com.wenyan.entity.entity.UserExamRecord;
import com.wenyan.entity.entity.UserWordRecord;
import com.wenyan.entity.entity.WordLib;
import com.wenyan.entity.entity.WriteExercise;
import com.wenyan.entity.vo.SubmitResultVO;
import com.wenyan.service.ExamQuestionService;
import com.wenyan.service.SubmitService;
import com.wenyan.service.UserExamRecordService;
import com.wenyan.service.UserService;
import com.wenyan.service.UserWordRecordService;
import com.wenyan.service.WordLibService;
import com.wenyan.service.WriteExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/** 作答批改：默写 + 真题。答错自动入错题本。首次作答触发打卡邀约。 */
@Service
@RequiredArgsConstructor
public class SubmitServiceImpl implements SubmitService {

    private final WriteExerciseService writeExerciseService;
    private final ExamQuestionService examQuestionService;
    private final UserWordRecordService wordRecordService;
    private final UserExamRecordService examRecordService;
    private final WordLibService wordLibService;
    private final UserService userService;

    @Override
    public SubmitResultVO submitWrite(Long userId, WriteSubmitDTO dto) {
        WriteExercise exercise = writeExerciseService.getById(dto.getExerciseId());
        SubmitResultVO vo = new SubmitResultVO();
        if (exercise == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "练习不存在");
        }
        // 简化判分：去掉空白后比较（生产环境按挖空逐空比对）
        String userAnswer = dto.getUserAnswer() == null ? "" : dto.getUserAnswer().replaceAll("\\s", "");
        String stdAnswer = exercise.getAnswer() == null ? "" : exercise.getAnswer().replaceAll("\\s", "");
        boolean correct = userAnswer.equalsIgnoreCase(stdAnswer);
        vo.setCorrect(correct);
        vo.setScore(correct ? 100 : 0);
        vo.setAnalysis(correct ? "回答正确" : "参考答案：" + exercise.getAnswer());
        vo.setError(!correct);
        if (!correct) {
            recordWordError(userId, exercise);
        }
        vo.setShowRemindPopup(checkFirstSubmit(userId));
        return vo;
    }

    @Override
    public SubmitResultVO submitExam(Long userId, ExamSubmitDTO dto) {
        ExamQuestion question = examQuestionService.getById(dto.getQuestionId());
        SubmitResultVO vo = new SubmitResultVO();
        if (question == null) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }
        boolean correct = false;
        if (("SINGLE".equals(question.getType()) || "TRUE_FALSE".equals(question.getType())
                || "WORD_EXPLAIN".equals(question.getType())) && question.getAnswer() != null) {
            correct = question.getAnswer().trim().equalsIgnoreCase(
                    dto.getUserAnswer() == null ? "" : dto.getUserAnswer().trim());
        }
        vo.setCorrect(correct);
        vo.setScore(correct ? 100 : 0);
        vo.setAnalysis(question.getAnalysis());
        vo.setError(!correct);
        // 写真题记录
        UserExamRecord rec = new UserExamRecord();
        rec.setId(SnowflakeIdUtil.nextId());
        rec.setUserId(userId);
        rec.setQuestionId(dto.getQuestionId());
        rec.setUserAnswer(dto.getUserAnswer());
        rec.setIsCorrect(correct ? 1 : 0);
        rec.setIsError(correct ? 0 : 1);
        rec.setSubmitTime(LocalDateTime.now());
        examRecordService.save(rec);
        vo.setShowRemindPopup(checkFirstSubmit(userId));
        return vo;
    }

    /** 字词错题写入SRS记录 */
    private void recordWordError(Long userId, WriteExercise exercise) {
        if (exercise.getArticleId() == null) return;
        // 简化：将默写错误的篇目关联的练习错误登记为字词错题数据
        UserWordRecord record = new UserWordRecord();
        record.setId(SnowflakeIdUtil.nextId());
        record.setUserId(userId);
        record.setWordId(exercise.getArticleId());
        record.setMemoryLevel(0);
        record.setReviewInterval(1);
        record.setNextReviewTime(LocalDateTime.now());
        record.setIsError(1);
        record.setErrorCount(1);
        record.setReciteCount(0);
        record.setSubmitTime(LocalDateTime.now());
        wordRecordService.save(record);
    }

    /** 首次作答触发打卡邀约：仅首次返回true，之后始终false */
    private boolean checkFirstSubmit(Long userId) {
        SysUser user = userService.getById(userId);
        boolean first = user != null && user.getIsFirstInit() != null && user.getIsFirstInit() == 1;
        if (first) {
            SysUser update = new SysUser();
            update.setId(userId);
            update.setIsFirstInit(0);
            userService.updateById(update);
            return true;
        }
        return false;
    }
}
