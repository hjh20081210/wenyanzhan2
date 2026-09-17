package com.wenyan.service;

import com.wenyan.entity.dto.CreatePaperDTO;

/** 组卷服务 */
public interface PaperService {
    /** 创建试卷 */
    Long createPaper(Long userId, CreatePaperDTO dto);
    /** 增删题目 */
    void updateItem(Long userId, Long paperId, Long questionId, Integer action);
    /** 删除试卷 */
    void deletePaper(Long userId, Long paperId);
    /** 导出HTML打印页(免费) */
    String exportHtml(Long userId, Long paperId);
    /** 导出PDF(会员+手机号双重校验) */
    String exportPdf(Long userId, Long paperId);
}
