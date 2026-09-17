package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 推送记录表 */
@Data
@TableName("user_push_record")
public class UserPushRecord {
    @TableId(type = IdType.INPUT)
    private Long id;
    private Long userId;
    private String pushDate;
    private String pushTime;
    private String pushType;
    private String content;
    private LocalDateTime createTime;
}
