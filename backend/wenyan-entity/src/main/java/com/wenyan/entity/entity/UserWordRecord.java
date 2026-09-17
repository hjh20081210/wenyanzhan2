package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** SRS字词记忆记录 */
@Data
@TableName("user_word_record")
public class UserWordRecord {
    @TableId(type = IdType.INPUT)
    private Long id;
    private Long userId;
    private Long wordId;
    private Integer memoryLevel;
    private Integer reviewInterval;
    private LocalDateTime nextReviewTime;
    private Integer isError;
    private Integer errorCount;
    private Integer reciteCount;
    private LocalDateTime submitTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
