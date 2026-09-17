package com.wenyan.api.controller;

import com.wenyan.api.util.UserContext;
import com.wenyan.common.result.Result;
import com.wenyan.entity.dto.BuyQuotaDTO;
import com.wenyan.entity.entity.MemberOrder;
import com.wenyan.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 会员 / 额度商业化 */
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/buy-quota")
    public Result<MemberOrder> buyQuota(@Valid @RequestBody BuyQuotaDTO dto) {
        return Result.success(memberService.buyQuota(UserContext.getUserId(), dto.getAmount()));
    }

    @PostMapping("/subscribe")
    public Result<MemberOrder> subscribe(@RequestParam(defaultValue = "1") Integer months) {
        return Result.success(memberService.subscribe(UserContext.getUserId(), months));
    }

    @GetMapping("/quota")
    public Result<Object> quota() {
        return Result.success(memberService.quotaInfo(UserContext.getUserId()));
    }
}
