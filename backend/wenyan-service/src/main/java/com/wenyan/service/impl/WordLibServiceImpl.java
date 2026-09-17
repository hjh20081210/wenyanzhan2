package com.wenyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenyan.entity.entity.WordLib;
import com.wenyan.mapper.WordLibMapper;
import com.wenyan.service.WordLibService;
import org.springframework.stereotype.Service;

/** WordLib Service 实现 */
@Service
public class WordLibServiceImpl extends ServiceImpl<WordLibMapper, WordLib> implements WordLibService {
}
