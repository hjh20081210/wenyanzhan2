package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/*** 文言文字典 */
@Data
@TableName("dictionary")
public class Dictionary {
    @TableId(type = IdType.INPUT)
    private Long dictId;
    private String entry;
    private Integer type;
    private String pinyin;
    private String radical;
    private Integer stroke;
    private String wubi;
    private String explain; // JSON数组: [{释义,例句,出处}]
    private String source;
    private Double idf;
    private Integer hitCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
