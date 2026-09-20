package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 课文篇目表 */
@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.INPUT)
    private Long articleId;
    private String title;
    private String author;
    private String dynasty;
    private String content;
    private String translate;
    private String appreciate;
    private String notes;
    private String genre;
    private Integer grade;
    private String book;
    private String pinyin;
    private Integer isRequired;
    private Integer readCount;
    private Integer likeCount;
    private String coverUrl;
    private String source;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
