package com.wenyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wenyan.entity.entity.UserExamRecord;
import com.wenyan.entity.entity.UserWordRecord;
import com.wenyan.entity.vo.CalendarVO;
import com.wenyan.service.CalendarService;
import com.wenyan.service.UserExamRecordService;
import com.wenyan.service.UserWordRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/** 打卡日历：复用做题/SRS记录自动判定当日打卡 */
@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final UserWordRecordService userWordRecordService;
    private final UserExamRecordService userExamRecordService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public CalendarVO getCalendar(Long userId, String month) {
        CalendarVO vo = new CalendarVO();
        if (month == null || !month.matches("\\d{4}-\\d{2}")) {
            month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        String start = month + "-01";
        LocalDate startDate = LocalDate.parse(start, FMT);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        Set<String> dates = new TreeSet<>();
        // SRS记录
        userWordRecordService.list(new LambdaQueryWrapper<UserWordRecord>()
                        .eq(UserWordRecord::getUserId, userId)
                        .between(UserWordRecord::getSubmitTime, startDate.atStartOfDay(), endDate.atTime(23,59,59)))
                .forEach(r -> dates.add(r.getSubmitTime().toLocalDate().format(FMT)));
        // 真题记录
        userExamRecordService.list(new LambdaQueryWrapper<UserExamRecord>()
                        .eq(UserExamRecord::getUserId, userId)
                        .between(UserExamRecord::getSubmitTime, startDate.atStartOfDay(), endDate.atTime(23,59,59)))
                .forEach(r -> dates.add(r.getSubmitTime().toLocalDate().format(FMT)));

        // 连续打卡（从今天往前）
        int continueDays = 0;
        LocalDate cursor = LocalDate.now();
        while (dates.contains(cursor.format(FMT))) {
            continueDays++;
            cursor = cursor.minusDays(1);
        }
        vo.setCheckDates(new java.util.ArrayList<>(dates));
        vo.setContinueDays(continueDays);
        vo.setMonthDays(dates.size());
        return vo;
    }
}
