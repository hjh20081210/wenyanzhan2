package com.wenyan.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 会员/额度购买订单 */
@Data
@TableName("member_order")
public class MemberOrder {
    @TableId(type = IdType.INPUT)
    private Long orderId;
    private Long userId;
    private String orderNo;
    private String orderType;
    private Integer amount;
    private BigDecimal fee;
    private Integer payStatus;
    private LocalDateTime payTime;
    private Integer memberMonths;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
