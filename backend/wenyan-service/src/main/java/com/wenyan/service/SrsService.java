package com.wenyan.service;

import com.wenyan.entity.entity.WordLib;

import java.util.List;

/** SRS记忆服务 */
public interface SrsService {
    /** 今日待复习字词列表 */
    List<WordLib> todayList(Long userId, Integer limit);
    /** 斩词反馈：认识/模糊/不认识 */
    void feedback(Long userId, Long wordId, Integer feedback);
    /** 今日任务进度 */
    Object todayProgress(Long userId);
}
