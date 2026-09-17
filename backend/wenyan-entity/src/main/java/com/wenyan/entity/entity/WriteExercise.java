package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 默写练习题 */
@Data
@TableName("write_exercise")
public class WriteExercise {
    @TableId(type = IdType.INPUT)
    private Long exerciseId;
    private Long articleId;
    private String content;
    private String blankPos;
    private String answer;
    private Integer type;
    private String hint;
    private Integer grade;
    private LocalDateTime createTime;
}
