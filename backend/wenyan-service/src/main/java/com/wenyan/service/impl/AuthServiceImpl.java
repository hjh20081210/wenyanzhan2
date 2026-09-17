package com.wenyan.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wenyan.common.constant.BusinessConstant;
import com.wenyan.common.util.JwtUtil;
import com.wenyan.common.util.SnowflakeIdUtil;
import com.wenyan.entity.entity.SysUser;
import com.wenyan.entity.entity.UserSetting;
import com.wenyan.entity.entity.UserWordQuota;
import com.wenyan.entity.vo.LoginVO;
import com.wenyan.service.AuthService;
import com.wenyan.service.UserService;
import com.wenyan.service.UserSettingService;
import com.wenyan.service.UserWordQuotaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OAuth登录服务。微信/QQ第三方登录，无手机号入口。
 * 生产环境需替换为真实 appid 与登录校验(微信登录code2Session等)。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserSettingService userSettingService;
    private final UserWordQuotaService userWordQuotaService;

    @Value("${oauth.wechat.app-id:}")
    private String wechatAppId;
    @Value("${oauth.wechat.app-secret:}")
    private String wechatAppSecret;
    @Value("${oauth.qq.app-id:}")
    private String qqAppId;
    @Value("${oauth.qq.app-secret:}")
    private String qqAppSecret;

    @Override
    public LoginVO wechatLogin(String code) {
        return login("WECHAT", code, wechatAppId, wechatAppSecret);
    }

    @Override
    public LoginVO qqLogin(String code) {
        return login("QQ", code, qqAppId, qqAppSecret);
    }

    /** 统一登录逻辑：解析openId，存在则返回，不存在则创建新用户(初始化额度/配置) */
    @Transactional
    public LoginVO login(String platform, String code, String appId, String appSecret) {
        String openId = resolveOpenId(platform, code, appId, appSecret);
        String nickname = platform + "_" + RandomUtil.randomString(6);

        SysUser user = null;
        if ("WECHAT".equals(platform)) {
            user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenId, openId));
        } else {
            user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getQqOpenId, openId));
        }

        if (user == null) {
            user = new SysUser();
            user.setId(SnowflakeIdUtil.nextId());
            user.setNickname(nickname);
            if ("WECHAT".equals(platform)) user.setOpenId(openId);
            else user.setQqOpenId(openId);
            user.setIsFirstInit(1);
            user.setQuotaTotal(BusinessConstant.DEFAULT_FREE_QUOTA);
            user.setQuotaUsed(0);
            userService.save(user);

            // 初始化配置
            UserSetting setting = new UserSetting();
            setting.setId(SnowflakeIdUtil.nextId());
            setting.setUserId(user.getId());
            setting.setStudyGrade(1);
            setting.setRemindSwitch(0);
            setting.setNewWordDaily(10);
            setting.setFontType("system");
            setting.setBgType("paper");
            setting.setFontSize(18);
            setting.setLineHeight(new java.math.BigDecimal("1.8"));
            userSettingService.save(setting);

            // 初始化额度
            UserWordQuota quota = new UserWordQuota();
            quota.setId(SnowflakeIdUtil.nextId());
            quota.setUserId(user.getId());
            quota.setFreeQuota(BusinessConstant.DEFAULT_FREE_QUOTA);
            quota.setBuyQuota(0);
            quota.setUsedQuota(0);
            userWordQuotaService.save(quota);
        }

        LoginVO vo = new LoginVO();
        vo.setToken(JwtUtil.createToken(user.getId()));
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setIsFirstInit(user.getIsFirstInit());
        return vo;
    }

    /** 解析第三方openId（微信code2Session / QQ）。原型阶段可由客户端传入openId。 */
    private String resolveOpenId(String platform, String code, String appId, String appSecret) {
        if (code == null || code.isBlank()) {
            // 原型兜底：无code时以随机标识模拟
            return "mock_" + RandomUtil.randomString(20);
        }
        // TODO 生产环境接入微信/QQ官方接口校验 code 换取 openId
        return "wx_" + code;
    }
}
