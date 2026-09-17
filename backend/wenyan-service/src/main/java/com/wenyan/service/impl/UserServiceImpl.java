package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.SysUser;
import com.wenyan.mapper.SysUserMapper;
import com.wenyan.service.UserService;
import org.springframework.stereotype.Service;

/** SysUser Service 实现 */
@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {
}
