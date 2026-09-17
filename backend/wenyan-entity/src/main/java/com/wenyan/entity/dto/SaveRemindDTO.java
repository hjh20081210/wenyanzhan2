package com.wenyan.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

@Data
public class SaveRemindDTO implements Serializable {
    @Min(0) @Max(1)
    private Integer remindSwitch;
    private String remindTime; // HH:mm
}
