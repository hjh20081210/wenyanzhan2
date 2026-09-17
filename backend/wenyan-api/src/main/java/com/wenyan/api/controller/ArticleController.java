package com.wenyan.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wenyan.api.util.UserContext;
import com.wenyan.common.result.Result;
import com.wenyan.common.util.SnowflakeIdUtil;
import com.wenyan.entity.entity.Article;
import com.wenyan.entity.entity.UserFavorite;
import com.wenyan.service.ArticleService;
import com.wenyan.service.UserFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 篇目库 / 阅读 */
@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final UserFavoriteService favoriteService;

    /** 首页推荐 */
    @GetMapping("/recommend")
    public Result<List<Article>> recommend() {
        Page<Article> page = articleService.page(new Page<>(1, 6),
                new LambdaQueryWrapper<Article>().orderByDesc(Article::getReadCount));
        return Result.success(page.getRecords());
    }

    /** 篇目列表(库) 支持学段/朝代/体裁筛选、关键词搜索 */
    @GetMapping("/list")
    public Result<Page<Article>> list(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam(required = false) Integer grade,
                                      @RequestParam(required = false) String dynasty,
                                      @RequestParam(required = false) String genre,
                                      @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(grade != null, Article::getGrade, grade)
                .eq(dynasty != null && !dynasty.isEmpty(), Article::getDynasty, dynasty)
                .eq(genre != null && !genre.isEmpty(), Article::getGenre, genre)
                .and(keyword != null && !keyword.isEmpty(), w -> w
                        .like(Article::getTitle, keyword)
                        .or().like(Article::getAuthor, keyword))
                .orderByDesc(Article::getReadCount);
        return Result.success(articleService.page(new Page<>(page, size), wrapper));
    }

    /** 篇目详情 */
    @GetMapping("/detail")
    public Result<Article> detail(@RequestParam Long articleId) {
        Article article = articleService.getById(articleId);
        if (article != null) {
            article.setReadCount(article.getReadCount() + 1);
            articleService.updateById(article);
        }
        return Result.success(article);
    }

    /** 收藏篇目 */
    @PostMapping("/favorite")
    public Result<Void> favorite(@RequestParam Long articleId) {
        Long userId = UserContext.getUserId();
        if (favoriteService.count(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getArticleId, articleId)) == 0) {
            UserFavorite f = new UserFavorite();
            f.setId(SnowflakeIdUtil.nextId());
            f.setUserId(userId);
            f.setArticleId(articleId);
            favoriteService.save(f);
        }
        return Result.success();
    }

    @DeleteMapping("/favorite")
    public Result<Void> unFavorite(@RequestParam Long articleId) {
        favoriteService.remove(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, UserContext.getUserId())
                .eq(UserFavorite::getArticleId, articleId));
        return Result.success();
    }

    /** 我的收藏 */
    @GetMapping("/favorites")
    public Result<List<Article>> favorites() {
        List<UserFavorite> favs = favoriteService.list(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, UserContext.getUserId()));
        List<Long> ids = favs.stream().map(UserFavorite::getArticleId).toList();
        return Result.success(ids.isEmpty() ? List.of() : articleService.listByIds(ids));
    }
}
