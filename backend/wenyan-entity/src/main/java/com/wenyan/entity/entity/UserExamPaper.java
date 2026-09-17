package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 用户自定义试卷 */
@Data
@TableName("user_exam_paper")
public class UserExamPaper {
    @TableId(type = IdType.INPUT)
    private Long id;
    private Long userId;
    private Long paperId;
    private String paperName;
    private Integer paperType;
    private Integer questionCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
