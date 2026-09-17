package com.wenyan.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wenyan.api.util.UserContext;
import com.wenyan.common.result.Result;
import com.wenyan.entity.dto.ExamSubmitDTO;
import com.wenyan.entity.entity.ExamQuestion;
import com.wenyan.entity.entity.UserExamRecord;
import com.wenyan.entity.vo.SubmitResultVO;
import com.wenyan.service.ExamQuestionService;
import com.wenyan.service.SubmitService;
import com.wenyan.service.UserExamRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 中考高考题库刷题 */
@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamQuestionService questionService;
    private final UserExamRecordService recordService;
    private final SubmitService submitService;

    /** 题库列表 exam_level 2中考/3高考 */
    @GetMapping("/question/list")
    public Result<Page<ExamQuestion>> list(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam Integer examLevel,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(required = false) Long articleId) {
        return Result.success(questionService.page(new Page<>(page, size), new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamLevel, examLevel)
                .eq(type != null && !type.isEmpty(), ExamQuestion::getType, type)
                .eq(articleId != null, ExamQuestion::getArticleId, articleId)
                .orderByAsc(ExamQuestion::getQuestionId)));
    }

    /** 提交真题批改 */
    @PostMapping("/question/submit")
    public Result<SubmitResultVO> submit(@Valid @RequestBody ExamSubmitDTO dto) {
        return Result.success(submitService.submitExam(UserContext.getUserId(), dto));
    }

    /** 我的真题错题 */
    @GetMapping("/error-list")
    public Result<List<ExamQuestion>> errorList() {
        List<UserExamRecord> records = recordService.list(new LambdaQueryWrapper<UserExamRecord>()
                .eq(UserExamRecord::getUserId, UserContext.getUserId())
                .eq(UserExamRecord::getIsError, 1));
        List<Long> qids = records.stream().map(UserExamRecord::getQuestionId).distinct().toList();
        return Result.success(qids.isEmpty() ? List.of() : questionService.listByIds(qids));
    }

    /** 清空真题错题 */
    @DeleteMapping("/error/clear")
    public Result<Void> clearError() {
        recordService.remove(new LambdaQueryWrapper<UserExamRecord>()
                .eq(UserExamRecord::getUserId, UserContext.getUserId()));
        return Result.success();
    }
}
