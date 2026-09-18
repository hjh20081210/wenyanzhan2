package com.wenyan.api.controller;

import com.wenyan.common.result.Result;
import com.wenyan.entity.entity.Dictionary;
import com.wenyan.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*** 文言文字典查询 */
@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
public class DictionaryController {

    private final DictionaryService dictionaryService;

    /*** 精准查询单字/词 */
    @GetMapping("/lookup")
    public Result<Dictionary> lookup(@RequestParam String entry) {
        Dictionary d = dictionaryService.lookup(entry);
        if (d == null) return Result.success(null);
        dictionaryService.hit(d.getDictId());
        return Result.success(d);
    }

    /*** 模糊搜索候选 */
    @GetMapping("/search")
    public Result<List<Dictionary>> search(@RequestParam String keyword,
                                           @RequestParam(required = false) Integer limit) {
        return Result.success(dictionaryService.search(keyword, limit));
    }

    /*** 常用字/热门 */
    @GetMapping("/hot")
    public Result<List<Dictionary>> hot(@RequestParam(defaultValue = "12") Integer limit) {
        return Result.success(dictionaryService.hot(limit));
    }
}
