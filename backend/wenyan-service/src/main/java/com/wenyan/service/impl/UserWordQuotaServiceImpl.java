package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.UserWordQuota;
import com.wenyan.mapper.UserWordQuotaMapper;
import com.wenyan.service.UserWordQuotaService;
import org.springframework.stereotype.Service;

/** UserWordQuota Service 实现 */
@Service
public class UserWordQuotaServiceImpl extends ServiceImpl<UserWordQuotaMapper, UserWordQuota> implements UserWordQuotaService {
}
