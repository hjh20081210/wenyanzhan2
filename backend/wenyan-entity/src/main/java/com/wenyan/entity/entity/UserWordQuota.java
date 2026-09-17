package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 斩词额度表 */
@Data
@TableName("user_word_quota")
public class UserWordQuota {
    @TableId(type = IdType.INPUT)
    private Long id;
    private Long userId;
    private Integer freeQuota;
    private Integer buyQuota;
    private Integer usedQuota;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
