package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.UserSetting;
import com.wenyan.mapper.UserSettingMapper;
import com.wenyan.service.UserSettingService;
import org.springframework.stereotype.Service;

/** UserSetting Service 实现 */
@Service
public class UserSettingServiceImpl extends ServiceImpl<UserSettingMapper, UserSetting> implements UserSettingService {
}
