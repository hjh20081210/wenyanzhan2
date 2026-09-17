package com.wenyan.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/** 雪花ID生成器（禁止自增主键） */
public final class SnowflakeIdUtil {
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    private SnowflakeIdUtil() {}

    public static long nextId() {
        return SNOWFLAKE.nextId();
    }
}
