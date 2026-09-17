package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.UserFavorite;
import com.wenyan.mapper.UserFavoriteMapper;
import com.wenyan.service.UserFavoriteService;
import org.springframework.stereotype.Service;

/** UserFavorite Service 实现 */
@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {
}
