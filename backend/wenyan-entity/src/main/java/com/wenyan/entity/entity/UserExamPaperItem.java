package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 试卷题目中间表 */
@Data
@TableName("user_exam_paper_item")
public class UserExamPaperItem {
    @TableId(type = IdType.INPUT)
    private Long id;
    private Long paperId;
    private Long questionId;
    private Integer sortNo;
    private LocalDateTime createTime;
}
