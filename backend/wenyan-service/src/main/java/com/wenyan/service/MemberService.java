package com.wenyan.service;

import com.wenyan.entity.entity.MemberOrder;

/** 会员/额度服务 */
public interface MemberService {
    /** 购买额度 最小300 */
    MemberOrder buyQuota(Long userId, Integer amount);
    /** 开通会员下单 */
    MemberOrder subscribe(Long userId, Integer months);
    /** 校验额度是否充足 */
    boolean deductQuota(Long userId, int num);
    /** 查询用户额度信息 */
    Object quotaInfo(Long userId);
}
