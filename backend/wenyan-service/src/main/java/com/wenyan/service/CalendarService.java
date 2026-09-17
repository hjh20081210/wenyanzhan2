package com.wenyan.service;

import com.wenyan.entity.vo.CalendarVO;

/** 打卡日历服务 */
public interface CalendarService {
    /** 获取指定年月打卡日历 data格式 YYYY-MM */
    CalendarVO getCalendar(Long userId, String month);
}
