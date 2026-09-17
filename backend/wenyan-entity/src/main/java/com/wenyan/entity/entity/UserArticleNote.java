package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 用户书签/划线笔记 */
@Data
@TableName("user_article_note")
public class UserArticleNote {
    @TableId(type = IdType.INPUT)
    private Long id;
    private Long userId;
    private Long articleId;
    private String noteType;
    private Integer position;
    private Integer startPos;
    private Integer endPos;
    private String content;
    private String text;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
