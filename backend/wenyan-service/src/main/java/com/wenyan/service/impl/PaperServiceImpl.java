package com.wenyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wenyan.common.exception.BusinessException;
import com.wenyan.common.exception.ErrorCode;
import com.wenyan.common.util.SnowflakeIdUtil;
import com.wenyan.entity.dto.CreatePaperDTO;
import com.wenyan.entity.entity.ExamQuestion;
import com.wenyan.entity.entity.SysUser;
import com.wenyan.entity.entity.UserExamPaper;
import com.wenyan.entity.entity.UserExamPaperItem;
import com.wenyan.entity.entity.UserExamRecord;
import com.wenyan.entity.entity.UserWordRecord;
import com.wenyan.service.ExamQuestionService;
import com.wenyan.service.PaperService;
import com.wenyan.service.UserExamPaperItemService;
import com.wenyan.service.UserExamPaperService;
import com.wenyan.service.UserExamRecordService;
import com.wenyan.service.UserService;
import com.wenyan.service.UserWordRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/** 组卷：错题组卷 / 自定义题目组卷。仅使用exam_question真题。 */
@Service
@RequiredArgsConstructor
public class PaperServiceImpl implements PaperService {

    private final UserExamPaperService paperService;
    private final UserExamPaperItemService paperItemService;
    private final UserExamRecordService examRecordService;
    private final UserWordRecordService wordRecordService;
    private final ExamQuestionService questionService;
    private final UserService userService;

    @Override
    @Transactional
    public Long createPaper(Long userId, CreatePaperDTO dto) {
        UserExamPaper paper = new UserExamPaper();
        paper.setId(SnowflakeIdUtil.nextId());
        paper.setUserId(userId);
        paper.setPaperId(paper.getId());
        paper.setPaperName(dto.getPaperName());
        paper.setPaperType(dto.getPaperType() == null ? 2 : dto.getPaperType());
        paperService.save(paper);

        List<Long> questionIds = new java.util.ArrayList<>();
        if (dto.getPaperType() != null && dto.getPaperType() == 1) {
            // 错题组卷：真题错题 + 字词错题关联真题(匹配不到过滤)
            List<UserExamRecord> examErrors = examRecordService.list(new LambdaQueryWrapper<UserExamRecord>()
                    .eq(UserExamRecord::getUserId, userId).eq(UserExamRecord::getIsError, 1));
            questionIds = examErrors.stream().map(UserExamRecord::getQuestionId).distinct().collect(Collectors.toList());
            // 字词错题关联真题
            List<UserWordRecord> wordErrors = wordRecordService.list(new LambdaQueryWrapper<UserWordRecord>()
                    .eq(UserWordRecord::getUserId, userId).eq(UserWordRecord::getIsError, 1));
            for (UserWordRecord wr : wordErrors) {
                List<Long> matched = questionService.list(new LambdaQueryWrapper<ExamQuestion>()
                                .eq(ExamQuestion::getWordId, wr.getWordId()))
                        .stream().map(ExamQuestion::getQuestionId).collect(Collectors.toList());
                for (Long qid : matched) {
                    if (!questionIds.contains(qid)) questionIds.add(qid);
                }
            }
        } else if (dto.getQuestionIds() != null) {
            questionIds = dto.getQuestionIds();
        }
        // 写入题目
        int sort = 0;
        for (Long qid : questionIds) {
            UserExamPaperItem item = new UserExamPaperItem();
            item.setId(SnowflakeIdUtil.nextId());
            item.setPaperId(paper.getPaperId());
            item.setQuestionId(qid);
            item.setSortNo(sort++);
            paperItemService.save(item);
        }
        paper.setQuestionCount(sort);
        paperService.updateById(paper);
        return paper.getPaperId();
    }

    @Override
    @Transactional
    public void updateItem(Long userId, Long paperId, Long questionId, Integer action) {
        UserExamPaper paper = paperService.getById(paperId);
        if (paper == null || !paper.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "试卷不存在");
        }
        UserExamPaperItem exist = paperItemService.getOne(new LambdaQueryWrapper<UserExamPaperItem>()
                .eq(UserExamPaperItem::getPaperId, paperId)
                .eq(UserExamPaperItem::getQuestionId, questionId));
        if (action != null && action == 1) { // 增加
            if (exist == null) {
                UserExamPaperItem item = new UserExamPaperItem();
                item.setId(SnowflakeIdUtil.nextId());
                item.setPaperId(paperId);
                item.setQuestionId(questionId);
                item.setSortNo(paper.getQuestionCount());
                paperItemService.save(item);
                paper.setQuestionCount(paper.getQuestionCount() + 1);
                paperService.updateById(paper);
            }
        } else { // 删除
            if (exist != null) {
                paperItemService.removeById(exist.getId());
                paper.setQuestionCount(Math.max(0, paper.getQuestionCount() - 1));
                paperService.updateById(paper);
            }
        }
    }

    @Override
    @Transactional
    public void deletePaper(Long userId, Long paperId) {
        UserExamPaper paper = paperService.getById(paperId);
        if (paper == null || !paper.getUserId().equals(userId)) return;
        paperItemService.remove(new LambdaQueryWrapper<UserExamPaperItem>()
                .eq(UserExamPaperItem::getPaperId, paperId));
        paperService.removeById(paperId);
    }

    @Override
    public String exportHtml(Long userId, Long paperId) {
        List<ExamQuestion> questions = loadQuestions(paperId);
        if (questions.isEmpty()) throw new BusinessException(ErrorCode.PAPER_EMPTY);
        StringBuilder sb = new StringBuilder("<html><head><meta charset='utf-8'><title>试卷</title></head><body>");
        sb.append("<h1>文言斩·组卷</h1>");
        int idx = 1;
        for (ExamQuestion q : questions) {
            sb.append("<p><b>").append(idx++).append(". ").append(q.getTitle()).append("</b></p>");
            if (q.getOptions() != null) sb.append(q.getOptions());
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    @Override
    public String exportPdf(Long userId, Long paperId) {
        SysUser user = userService.getById(userId);
        if (user == null || user.getMemberExpireTime() == null
                || !user.getMemberExpireTime().isAfter(java.time.LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.NOT_VIP, "PDF导出需会员权益");
        }
        if (user.getPhone() == null || user.getPhone().isBlank()) {
            throw new BusinessException(ErrorCode.PHONE_NOT_BOUND);
        }
        return exportHtml(userId, paperId); // PDF导出在此基于HTML生成，简化
    }

    private List<ExamQuestion> loadQuestions(Long paperId) {
        List<UserExamPaperItem> items = paperItemService.list(new LambdaQueryWrapper<UserExamPaperItem>()
                .eq(UserExamPaperItem::getPaperId, paperId)
                .orderByAsc(UserExamPaperItem::getSortNo));
        List<Long> qids = items.stream().map(UserExamPaperItem::getQuestionId).collect(Collectors.toList());
        if (qids.isEmpty()) return new java.util.ArrayList<>();
        return questionService.listByIds(qids);
    }
}
