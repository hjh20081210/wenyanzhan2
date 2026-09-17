package com.wenyan.entity.vo;

import lombok.Data;

import java.io.Serializable;

/** 作答提交批改返回 */
@Data
public class SubmitResultVO implements Serializable {
    private Boolean correct;        // 是否全部正确
    private Integer score;          // 得分
    private String analysis;        // 解析
    private Boolean showRemindPopup; // 是否弹出打卡邀约弹窗
    private Boolean isError;        // 是否错题
    private Integer firstSubmit;    // 是否首次作答 1是
}
