package com.wenyan.common.constant;

/** 业务常量 */
public interface BusinessConstant {
    int DEFAULT_FREE_QUOTA = 150;   // 初始免费额度
    int QUOTA_MIN_BUY = 300;        // 最低购买额度
    int MEMORY_DONE = 99;           // 斩词完成等级
    // 学段
    int GRADE_JUNIOR = 1;
    int GRADE_SENIOR = 2;
    // 打卡推送 Redis Key
    String REDIS_TODAY_WORDS_KEY = "wenyan:srs:today:";
    String REDIS_TOKEN_KEY = "wenyan:token:";
    long TOKEN_EXPIRE = 30L * 24 * 3600; // 30天秒
}
