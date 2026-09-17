package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 用户配置表 */
@Data
@TableName("user_setting")
public class UserSetting {
    @TableId(type = IdType.INPUT)
    private Long id;
    private Long userId;
    private Integer studyGrade;
    private Integer remindSwitch;
    private String remindTime;
    private Integer newWordDaily;
    private String fontType;
    private String bgType;
    private Integer fontSize;
    private BigDecimal lineHeight;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
