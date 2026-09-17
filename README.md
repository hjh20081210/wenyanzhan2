# 文言斩 (WenyanZhan)

水墨国风·文言文学习 App —— 对标百词斩，融合斩词、默写、真题刷题、错题本、组卷打印与打卡日历。

> 仓库：`https://github.com/hjh20081210/Wenyanzhan.git`

## 技术栈
- **前端**：UniApp (Vue3 + TypeScript + Vite + UnoCSS + Pinia)，跨端 H5 / 微信小程序 / APP
- **后端**：Spring Boot 3 (Java17) + MyBatis-Plus + MySQL 8.0 + Redis + Maven 多模块
- **主题**：水墨画国风 · 宣纸米白 `#f5f1e8` · 墨黑 `#2c2c2c` · 赭石 `#9b5b3a`，支持夜间模式与霞鹭文楷/马叙伦楷字体切换

## 目录结构
```
wenyanzhan/
├── backend/                  # 后端 (Maven 多模块)
│   ├── pom.xml               # 父POM (SpringBoot3 + MyBatis-Plus + Redis)
│   ├── wenyan-common/        # 公共：Result / 异常 / 常量 / JWT / 雪花ID
│   ├── wenyan-entity/        # 实体 / DTO / VO
│   ├── wenyan-mapper/        # MyBatis-Plus Mapper
│   ├── wenyan-service/       # 业务逻辑 (SRS / 打卡 / 组卷 / 会员 / 批改)
│   ├── wenyan-api/           # Controller / 拦截器 / 定时任务 / 启动类
│   └── sql/wenyan_zhan.sql   # MySQL 完整建表脚本(含测试数据)
└── frontend/                 # 前端 (UniApp Vue3)
    └── src/
        ├── pages/            # 页面 (Tabbar4 + 二级页面)
        ├── components/       # TabBar / CardSwiper / CalendarCheck / InkBackground / NavBar
        ├── store/            # Pinia
        ├── api/              # request 封装 + API
        └── styles/           # 国风主题 SCSS
```

## 快速启动

### 1. 数据库
```bash
mysql -uroot -p < backend/sql/wenyan_zhan.sql
```
（建库 `wenyan_zhan`，自动建表并插入演示篇目/字词/题目）

### 2. 后端
```bash
cd backend
mvn clean package -DskipTests
java -jar wenyan-api/target/wenyan-api.jar
# 端口 8080，前缀 /api
```
> 需 JDK17 + Maven。数据库账号密码在 `wenyan-api/src/main/resources/application.yml` 中配置（默认 root/123456，可改）。

### 3. 前端
```bash
cd frontend
npm install
npm run dev:h5      # H5 开发 (http://localhost:5173)
npm run build:h5    # H5 构建
npm run dev:mp-weixin   # 微信小程序
npm run dev:app     # APP (需 HBuilderX)
```
前端已配置 `/api` 代理到 `http://127.0.0.1:8080`。

## REST 接口清单（前缀 /api）
| 模块 | 方法 | 路径 | 说明 |
|---|---|---|---|
| 登录 | GET | /oauth/wechat | 微信OAuth登录 |
| 登录 | GET | /oauth/qq | QQ OAuth登录 |
| 篇目 | GET | /article/recommend | 首页推荐 |
| 篇目 | GET | /article/list | 篇目库(学段/朝代/搜索分页) |
| 篇目 | GET | /article/detail | 篇目详情 |
| 篇目 | POST/DELETE | /article/favorite | 收藏/取消收藏 |
| 篇目 | GET | /article/favorites | 我的收藏 |
| 字词 | GET | /word/list | 字词库 |
| SRS | GET | /srs/today-list | 今日待复习 |
| SRS | POST | /srs/feedback | 斩词反馈(0/1/2) |
| SRS | GET | /srs/progress | 今日进度 |
| 默写 | GET | /write/list | 某篇目默写练习 |
| 默写 | POST | /write/submit | 默写提交批改 |
| 真题 | GET | /exam/question/list | 题库(中考/高考) |
| 真题 | POST | /exam/question/submit | 真题提交批改 |
| 错题 | GET | /exam/error-list | 我的真题错题 |
| 错题 | DELETE | /exam/error/clear | 清空错题 |
| 组卷 | POST | /paper/create | 创建试卷 |
| 组卷 | POST | /paper/item/save | 增删题目 |
| 组卷 | GET | /paper/list | 我的试卷 |
| 组卷 | GET | /paper/export-html | HTML打印(免费) |
| 组卷 | GET | /paper/export-pdf | PDF导出(会员+手机号) |
| 用户 | POST | /user/save-grade | 保存学段 |
| 用户 | POST | /user/setting/save-remind | 保存打卡提醒 |
| 用户 | POST | /user/setting/save-ui | 保存UI/字体配置 |
| 用户 | GET | /user/calendar/get | 打卡日历 |
| 会员 | POST | /member/buy-quota | 购买额度(≥300) |
| 会员 | POST | /member/subscribe | 开通会员 |
| 会员 | GET | /member/quota | 额度/会员状态 |

## 核心业务说明
- **SRS 斩词**：记忆等级 0~98，间隔天 = 2^等级，达 99 斩词完成；先忆/模糊/不认识反馈。
- **错题本双体系**：真题错题 + 字词错题，均自动录入，支持全选/组卷/清空。
- **组卷打印**：错题组卷/自定义组卷，HTML 打印免费，PDF 导出需会员 + 绑定手机号。
- **打卡日历**：按做题/SRS 记录自动判定当日打卡，含连续天数统计。
- **打卡提醒**：定时任务每分钟扫描，同一用户当日只推一次（含昵称 App/微信服务通知，生产接入推送通道）。
- **商业化**：初始免费额度 150，斩新词扣额度，会员无限量 + 高阶真题 + PDF 导出。

## 数据源
- 篇目库：`hefengbao/jingmo`（古诗文完整库，含注释译文赏析）
- 课内课文：`hantang/yuwen`
- 默写题库：`Binkic/Reciter`（MIT）
- 字词数据：`jiaeyan/Jiayan`

> 测试数据为少量样例，生产请按上述数据源导入到 `article` / `word_lib` / `write_exercise` / `exam_question` 表。