package com.wenyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wenyan.common.constant.BusinessConstant;
import com.wenyan.common.util.SnowflakeIdUtil;
import com.wenyan.entity.entity.UserWordRecord;
import com.wenyan.entity.entity.WordLib;
import com.wenyan.service.SrsService;
import com.wenyan.service.UserWordRecordService;
import com.wenyan.service.WordLibService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/** SRS艾宾浩斯记忆：memory_level+1，间隔=2^level天；99斩词完成 */
@Service
@RequiredArgsConstructor
public class SrsServiceImpl implements SrsService {

    private final UserWordRecordService recordService;
    private final WordLibService wordLibService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public List<WordLib> todayList(Long userId, Integer limit) {
        // 优先读缓存
        String key = BusinessConstant.REDIS_TODAY_WORDS_KEY + userId;
        // 查询今日到期待复习记录
        LocalDateTime now = LocalDateTime.now();
        List<UserWordRecord> records = recordService.list(new LambdaQueryWrapper<UserWordRecord>()
                .eq(UserWordRecord::getUserId, userId)
                .le(UserWordRecord::getNextReviewTime, now)
                .ne(UserWordRecord::getMemoryLevel, BusinessConstant.MEMORY_DONE)
                .orderByAsc(UserWordRecord::getNextReviewTime)
                .last("limit " + (limit == null ? 20 : limit)));
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> wordIds = records.stream().map(UserWordRecord::getWordId).distinct().collect(Collectors.toList());
        return wordLibService.listByIds(wordIds);
    }

    @Override
    public void feedback(Long userId, Long wordId, Integer feedback) {
        // feedback: 0不认识 1模糊 2认识
        UserWordRecord record = recordService.getOne(new LambdaQueryWrapper<UserWordRecord>()
                .eq(UserWordRecord::getUserId, userId)
                .eq(UserWordRecord::getWordId, wordId));
        if (record == null) {
            record = new UserWordRecord();
            record.setId(SnowflakeIdUtil.nextId());
            record.setUserId(userId);
            record.setWordId(wordId);
            record.setMemoryLevel(0);
            record.setReviewInterval(1);
            record.setErrorCount(0);
            record.setReciteCount(0);
        }
        record.setSubmitTime(LocalDateTime.now());
        if (feedback == 0) { // 不认识
            record.setMemoryLevel(0);
            record.setReviewInterval(1);
            record.setIsError(1);
            record.setErrorCount(record.getErrorCount() + 1);
        } else if (feedback == 1) { // 模糊: 等级不变，间隔1天
            record.setReviewInterval(1);
            record.setIsError(1);
            record.setErrorCount(record.getErrorCount() + 1);
        } else { // 认识
            int level = record.getMemoryLevel() + 1;
            if (level >= BusinessConstant.MEMORY_DONE) {
                record.setMemoryLevel(BusinessConstant.MEMORY_DONE);
                record.setReviewInterval(-1); // 不再复习
            } else {
                record.setMemoryLevel(level);
                record.setReviewInterval(1 << level); // 2^level 天
            }
            record.setIsError(0);
            record.setReciteCount(record.getReciteCount() + 1);
        }
        record.setNextReviewTime(LocalDateTime.now().plusDays(Math.max(record.getReviewInterval(), 1)));
        if (record.getId() == null) {
            recordService.save(record);
        } else {
            recordService.updateById(record);
        }
        // 刷新缓存
        redisTemplate.delete(BusinessConstant.REDIS_TODAY_WORDS_KEY + userId);
    }

    @Override
    public Object todayProgress(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);
        long done = recordService.count(new LambdaQueryWrapper<UserWordRecord>()
                .eq(UserWordRecord::getUserId, userId)
                .between(UserWordRecord::getSubmitTime, start, end));
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("todayDone", done);
        map.put("target", 20);
        return map;
    }
}
