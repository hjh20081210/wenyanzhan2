package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.UserArticleNote;
import com.wenyan.mapper.UserArticleNoteMapper;
import com.wenyan.service.UserArticleNoteService;
import org.springframework.stereotype.Service;

/** UserArticleNote Service 实现 */
@Service
public class UserArticleNoteServiceImpl extends ServiceImpl<UserArticleNoteMapper, UserArticleNote> implements UserArticleNoteService {
}
