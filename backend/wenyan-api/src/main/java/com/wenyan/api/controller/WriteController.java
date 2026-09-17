package com.wenyan.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wenyan.api.util.UserContext;
import com.wenyan.common.result.Result;
import com.wenyan.entity.dto.WriteSubmitDTO;
import com.wenyan.entity.entity.WriteExercise;
import com.wenyan.entity.vo.SubmitResultVO;
import com.wenyan.service.SubmitService;
import com.wenyan.service.WriteExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 默写练习 */
@RestController
@RequestMapping("/api/write")
@RequiredArgsConstructor
public class WriteController {

    private final WriteExerciseService writeExerciseService;
    private final SubmitService submitService;

    /** 某篇目的默写练习 */
    @GetMapping("/list")
    public Result<List<WriteExercise>> list(@RequestParam(required = false) Long articleId) {
        return Result.success(writeExerciseService.list(new LambdaQueryWrapper<WriteExercise>()
                .eq(articleId != null, WriteExercise::getArticleId, articleId)));
    }

    /** 提交默写批改 */
    @PostMapping("/submit")
    public Result<SubmitResultVO> submit(@Valid @RequestBody WriteSubmitDTO dto) {
        return Result.success(submitService.submitWrite(UserContext.getUserId(), dto));
    }
}
