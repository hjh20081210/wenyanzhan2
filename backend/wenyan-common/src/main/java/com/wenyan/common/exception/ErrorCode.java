package com.wenyan.common.exception;

/** 业务错误码 */
public enum ErrorCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或Token失效"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    QUOTA_NOT_ENOUGH(1001, "斩词额度不足，请购买额度或开通会员"),
    QUOTA_LESS_THAN_300(1002, "购买额度最小为300"),
    NOT_VIP(1003, "该功能需会员权益"),
    PHONE_NOT_BOUND(1004, "请先绑定手机号"),
    QUESTION_NOT_FOUND(2001, "题目不存在"),
    PAPER_EMPTY(2002, "试卷为空，无法导出"),
    SERVER_ERROR(500, "服务异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int getCode() { return code; }
    public String getMessage() { return message; }
}
