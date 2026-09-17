package com.wenyan.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class ExamSubmitDTO implements Serializable {
    @NotNull(message = "题目ID不能为空")
    private Long questionId;
    private String userAnswer;
}
