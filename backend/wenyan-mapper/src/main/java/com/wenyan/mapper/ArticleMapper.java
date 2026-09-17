package com.wenyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wenyan.entity.entity.Article;
import org.apache.ibatis.annotations.Mapper;

/** Article Mapper */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}
