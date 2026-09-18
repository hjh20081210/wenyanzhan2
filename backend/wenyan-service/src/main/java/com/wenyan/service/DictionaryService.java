package com.wenyan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wenyan.entity.entity.Dictionary;

import java.util.List;

/*** 字典查询服务 */
public interface DictionaryService extends IService<Dictionary> {
    /*** 精准查词 */
    Dictionary lookup(String entry);
    /*** 模糊搜索(前缀/包含)，limit条 */
    List<Dictionary> search(String keyword, Integer limit);
    /*** 记录一次查询命中 */
    void hit(Long dictId);
    /*** 常用字列表(按查询热度) */
    List<Dictionary> hot(int limit);
}
