package com.wenyan.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class WriteSubmitDTO implements Serializable {
    @NotNull(message = "练习ID不能为空")
    private Long exerciseId;
    private String userAnswer; // 填空答案，多个用逗号分隔
    private String articleTitle;
}
