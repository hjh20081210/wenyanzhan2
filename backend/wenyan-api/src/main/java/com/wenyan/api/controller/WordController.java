package com.wenyan.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wenyan.api.util.UserContext;
import com.wenyan.common.result.Result;
import com.wenyan.entity.entity.WordLib;
import com.wenyan.service.MemberService;
import com.wenyan.service.SrsService;
import com.wenyan.service.WordLibService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 背诵广场 / 字词库 / SRS记忆 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WordController {

    private final WordLibService wordLibService;
    private final SrsService srsService;
    private final MemberService memberService;

    /** 字词库列表 */
    @GetMapping("/word/list")
    public Result<Page<WordLib>> list(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam(required = false) Integer wordType,
                                      @RequestParam(required = false) String keyword) {
        return Result.success(wordLibService.page(new Page<>(page, size), new LambdaQueryWrapper<WordLib>()
                .eq(wordType != null, WordLib::getWordType, wordType)
                .like(keyword != null && !keyword.isEmpty(), WordLib::getWord, keyword)));
    }

    /** 今日SRS复习列表 */
    @GetMapping("/srs/today-list")
    public Result<List<WordLib>> todayList(@RequestParam(required = false) Integer limit) {
        return Result.success(srsService.todayList(UserContext.getUserId(), limit));
    }

    /** 斩词反馈 0不认识 1模糊 2认识 */
    @PostMapping("/srs/feedback")
    public Result<Void> feedback(@RequestParam Long wordId, @RequestParam Integer feedback) {
        Long userId = UserContext.getUserId();
        // 非会员斩词扣额度(去掉初始的"斩"仅扣一次)
        if (feedback == 2) {
            memberService.deductQuota(userId, 1);
        }
        srsService.feedback(userId, wordId, feedback);
        return Result.success();
    }

    @GetMapping("/srs/progress")
    public Result<Object> progress() {
        return Result.success(srsService.todayProgress(UserContext.getUserId()));
    }
}
