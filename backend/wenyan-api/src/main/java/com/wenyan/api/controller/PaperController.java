package com.wenyan.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wenyan.api.util.UserContext;
import com.wenyan.common.result.Result;
import com.wenyan.entity.dto.CreatePaperDTO;
import com.wenyan.entity.dto.PaperItemDTO;
import com.wenyan.entity.entity.UserExamPaper;
import com.wenyan.service.PaperService;
import com.wenyan.service.UserExamPaperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 组卷打印 */
@RestController
@RequestMapping("/api/paper")
@RequiredArgsConstructor
public class PaperController {

    private final PaperService paperService;
    private final UserExamPaperService paperService2;

    @PostMapping("/create")
    public Result<Long> create(@Valid @RequestBody CreatePaperDTO dto) {
        return Result.success(paperService.createPaper(UserContext.getUserId(), dto));
    }

    @PostMapping("/item/save")
    public Result<Void> itemSave(@Valid @RequestBody PaperItemDTO dto) {
        paperService.updateItem(UserContext.getUserId(), dto.getPaperId(), dto.getQuestionId(), dto.getAction());
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<UserExamPaper>> list() {
        return Result.success(paperService2.list(new LambdaQueryWrapper<UserExamPaper>()
                .eq(UserExamPaper::getUserId, UserContext.getUserId())
                .orderByDesc(UserExamPaper::getCreateTime)));
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam Long paperId) {
        paperService.deletePaper(UserContext.getUserId(), paperId);
        return Result.success();
    }

    /** HTML打印(全部免费)，返回可直接打开的HTML */
    @GetMapping("/export-html")
    public Result<String> exportHtml(@RequestParam Long paperId) {
        return Result.success(paperService.exportHtml(UserContext.getUserId(), paperId));
    }

    /** PDF导出(会员+手机号双重校验) */
    @GetMapping("/export-pdf")
    public Result<byte[]> exportPdf(@RequestParam Long paperId) {
        return Result.success(paperService.exportPdf(UserContext.getUserId(), paperId).getBytes());
    }
}
