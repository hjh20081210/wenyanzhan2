package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 用户主表 */
@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String nickname;
    private String avatar;
    private String openId;
    private String qqOpenId;
    private String phone;
    private LocalDateTime memberExpireTime;
    private Integer isFirstInit;
    private Integer quotaTotal;
    private Integer quotaUsed;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
