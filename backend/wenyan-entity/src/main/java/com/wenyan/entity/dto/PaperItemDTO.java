package com.wenyan.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class PaperItemDTO implements Serializable {
    @NotNull(message = "试卷ID不能为空")
    private Long paperId;
    @NotNull(message = "题目ID不能为空")
    private Long questionId;
    private Integer action; // 1增加 2删除
}
