package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 文言字词库 */
@Data
@TableName("word_lib")
public class WordLib {
    @TableId(type = IdType.INPUT)
    private Long wordId;
    private String word;
    private Integer wordType;
    private String pos;
    private String explain;
    private String example;
    private String tagList;
    private Integer difficulty;
    private LocalDateTime createTime;
}
