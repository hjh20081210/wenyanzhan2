package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 真题做题记录 */
@Data
@TableName("user_exam_record")
public class UserExamRecord {
    @TableId(type = IdType.INPUT)
    private Long id;
    private Long userId;
    private Long questionId;
    private String userAnswer;
    private Integer isCorrect;
    private Integer isError;
    private LocalDateTime submitTime;
    private LocalDateTime createTime;
}
