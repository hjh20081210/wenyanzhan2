package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 题库题目表 */
@Data
@TableName("exam_question")
public class ExamQuestion {
    @TableId(type = IdType.INPUT)
    private Long questionId;
    private Long articleId;
    private Long wordId;
    private Integer examLevel;
    private String type;
    private String title;
    private String options;
    private String answer;
    private String analysis;
    private Integer difficulty;
    private String tagList;
    private Integer isFree;
    private Integer isHighOrder;
    private String source;
    private LocalDateTime createTime;
}
