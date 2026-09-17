package com.wenyan.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class SaveGradeDTO implements Serializable {
    @NotNull(message = "学段不能为空")
    @Min(value = 1, message = "学段无效")
    @Max(value = 3, message = "学段无效")
    private Integer studyGrade;
}
