package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 用户收藏篇目 */
@Data
@TableName("user_favorite")
public class UserFavorite {
    @TableId(type = IdType.INPUT)
    private Long id;
    private Long userId;
    private Long articleId;
    private LocalDateTime createTime;
}
