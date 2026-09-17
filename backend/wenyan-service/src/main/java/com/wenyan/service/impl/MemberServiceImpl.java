package com.wenyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wenyan.common.constant.BusinessConstant;
import com.wenyan.common.exception.BusinessException;
import com.wenyan.common.exception.ErrorCode;
import com.wenyan.common.util.SnowflakeIdUtil;
import com.wenyan.entity.entity.MemberOrder;
import com.wenyan.entity.entity.SysUser;
import com.wenyan.entity.entity.UserWordQuota;
import com.wenyan.service.MemberOrderService;
import com.wenyan.service.MemberService;
import com.wenyan.service.UserService;
import com.wenyan.service.UserWordQuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** 会员与额度商业化 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberOrderService memberOrderService;
    private final UserWordQuotaService quotaService;
    private final UserService userService;

    @Override
    @Transactional
    public MemberOrder buyQuota(Long userId, Integer amount) {
        if (amount == null || amount < BusinessConstant.QUOTA_MIN_BUY) {
            throw new BusinessException(ErrorCode.QUOTA_LESS_THAN_300);
        }
        MemberOrder order = new MemberOrder();
        order.setOrderId(SnowflakeIdUtil.nextId());
        order.setUserId(userId);
        order.setOrderNo("Q" + System.currentTimeMillis());
        order.setOrderType("quota");
        order.setAmount(amount);
        order.setFee(new BigDecimal(amount).divide(new BigDecimal(100)));
        order.setPayStatus(0);
        memberOrderService.save(order);
        return order;
    }

    @Override
    @Transactional
    public MemberOrder subscribe(Long userId, Integer months) {
        if (months == null || months < 1) months = 1;
        MemberOrder order = new MemberOrder();
        order.setOrderId(SnowflakeIdUtil.nextId());
        order.setUserId(userId);
        order.setOrderNo("M" + System.currentTimeMillis());
        order.setOrderType("member");
        order.setMemberMonths(months);
        order.setFee(new BigDecimal(19).multiply(new BigDecimal(months)));
        order.setPayStatus(0);
        memberOrderService.save(order);
        return order;
    }

    @Override
    @Transactional
    public boolean deductQuota(Long userId, int num) {
        SysUser user = userService.getById(userId);
        if (user == null) return false;
        // 会员不限额度
        if (user.getMemberExpireTime() != null && user.getMemberExpireTime().isAfter(LocalDateTime.now())) {
            return true;
        }
        UserWordQuota quota = quotaService.getOne(new LambdaQueryWrapper<UserWordQuota>()
                .eq(UserWordQuota::getUserId, userId));
        if (quota == null) return false;
        int remaining = quota.getFreeQuota() + quota.getBuyQuota() - quota.getUsedQuota();
        if (remaining < num) {
            throw new BusinessException(ErrorCode.QUOTA_NOT_ENOUGH);
        }
        quota.setUsedQuota(quota.getUsedQuota() + num);
        quotaService.updateById(quota);
        // 同步更新用户冗余
        SysUser update = new SysUser();
        update.setId(userId);
        update.setQuotaUsed(quota.getUsedQuota());
        userService.updateById(update);
        return true;
    }

    @Override
    public Object quotaInfo(Long userId) {
        UserWordQuota quota = quotaService.getOne(new LambdaQueryWrapper<UserWordQuota>()
                .eq(UserWordQuota::getUserId, userId));
        Map<String, Object> map = new HashMap<>();
        if (quota != null) {
            map.put("freeQuota", quota.getFreeQuota());
            map.put("buyQuota", quota.getBuyQuota());
            map.put("usedQuota", quota.getUsedQuota());
            map.put("remaining", quota.getFreeQuota() + quota.getBuyQuota() - quota.getUsedQuota());
        }
        // 会员状态
        SysUser user = userService.getById(userId);
        map.put("isVip", user != null && user.getMemberExpireTime() != null
                && user.getMemberExpireTime().isAfter(LocalDateTime.now()));
        return map;
    }

    /** 根据订单回调处理支付结果(简化) */
    @Transactional
    public void handlePayCallback(Long orderId, boolean success) {
        MemberOrder order = memberOrderService.getById(orderId);
        if (order == null) return;
        if (success) {
            order.setPayStatus(1);
            order.setPayTime(LocalDateTime.now());
            memberOrderService.updateById(order);
            if ("quota".equals(order.getOrderType())) {
                UserWordQuota quota = quotaService.getOne(new LambdaQueryWrapper<UserWordQuota>()
                        .eq(UserWordQuota::getUserId, order.getUserId()));
                if (quota != null) {
                    quota.setBuyQuota(quota.getBuyQuota() + order.getAmount());
                    quotaService.updateById(quota);
                }
            } else if ("member".equals(order.getOrderType())) {
                SysUser user = userService.getById(order.getUserId());
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime base = (user.getMemberExpireTime() != null
                        && user.getMemberExpireTime().isAfter(now)) ? user.getMemberExpireTime() : now;
                user.setMemberExpireTime(base.plusMonths(order.getMemberMonths()));
                userService.updateById(user);
            }
        }
    }
}
