package com.wenyan.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/** 打卡日历数据 */
@Data
public class CalendarVO implements Serializable {
    private List<String> checkDates; // 当月打卡日期 YYYY-MM-DD
    private Integer continueDays;    // 连续打卡天数
    private Integer monthDays;       // 当月打卡总天数
}
