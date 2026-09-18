package com.wenyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.Dictionary;
import com.wenyan.mapper.DictionaryMapper;
import com.wenyan.service.DictionaryService;
import org.springframework.stereotype.Service;

import java.util.List;

/*** 字典查询服务实现 */
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary>
        implements DictionaryService {

    @Override
    public Dictionary lookup(String entry) {
        if (entry == null || entry.isBlank()) return null;
        return this.getOne(new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getEntry, entry.trim())
                .last("limit 1"), false);
    }

    @Override
    public List<Dictionary> search(String keyword, Integer limit) {
        if (keyword == null || keyword.isBlank()) return List.of();
        int size = limit == null ? 20 : Math.min(limit, 50);
        return this.list(new LambdaQueryWrapper<Dictionary>()
                .likeRight(Dictionary::getEntry, keyword.trim()) // 前缀优先
                .or(o -> o.like(Dictionary::getEntry, keyword.trim()))
                .orderByDesc(Dictionary::getIdf)
                .last("limit " + size));
    }

    @Override
    public void hit(Long dictId) {
        Dictionary d = new Dictionary();
        d.setDictId(dictId);
        d.setHitCount(1);
        this.update(d, new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getDictId, dictId)
                .setSql("hit_count = hit_count + 1"));
    }

    @Override
    public List<Dictionary> hot(int limit) {
        return this.list(new LambdaQueryWrapper<Dictionary>()
                .orderByDesc(Dictionary::getHitCount)
                .last("limit " + limit));
    }
}
