package com.wenyan.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SaveUiDTO implements Serializable {
    private String fontType;   // unibest/system/yalnhan/maxulun
    private String bgType;     // paper/night
    private Integer fontSize;
    private BigDecimal lineHeight;
}
