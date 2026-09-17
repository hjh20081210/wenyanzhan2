package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.Article;
import com.wenyan.mapper.ArticleMapper;
import com.wenyan.service.ArticleService;
import org.springframework.stereotype.Service;

/** Article Service 实现 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
}
