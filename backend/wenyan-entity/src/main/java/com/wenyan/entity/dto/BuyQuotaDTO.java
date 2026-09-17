package com.wenyan.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class BuyQuotaDTO implements Serializable {
    @NotNull(message = "购买额度不能为空")
    private Integer amount; // 最小300
}
