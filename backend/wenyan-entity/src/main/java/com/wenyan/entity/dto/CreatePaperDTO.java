package com.wenyan.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CreatePaperDTO implements Serializable {
    @NotBlank(message = "试卷名称不能为空")
    private String paperName;
    private Integer paperType; // 1错题组卷 2自定义组卷
    private List<Long> questionIds; // 自定义选题
}
