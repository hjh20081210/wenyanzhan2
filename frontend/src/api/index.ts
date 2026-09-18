import { get, post, del } from './request'

export interface Article {
  articleId: number
  title: string
  author: string
  dynasty: string
  content: string
  translate?: string
  appreciate?: string
  notes?: string
  genre?: string
  grade: number
  isRequired: number
  readCount: number
  likeCount: number
  coverUrl?: string
}

export const articleApi = {
  recommend: () => get<Article[]>('/api/article/recommend'),
  list: (params: any) => get('/api/article/list', params),
  detail: (articleId: number) => get<Article>('/api/article/detail', { articleId }),
  favorite: (articleId: number) => post('/api/article/favorite', { articleId }),
  unFavorite: (articleId: number) => del('/api/article/favorite', { articleId }),
  favorites: () => get<Article[]>('/api/article/favorites'),
  search: (params: any) => get('/api/article/list', params),
}

export const authApi = {
  wechat: (code?: string) => get('/api/oauth/wechat', { code }),
  qq: (code?: string) => get('/api/oauth/qq', { code }),
}

export const userApi = {
  saveGrade: (studyGrade: number) => post('/api/user/save-grade', { studyGrade }),
  saveRemind: (data: any) => post('/api/user/setting/save-remind', data),
  saveUi: (data: any) => post('/api/user/setting/save-ui', data),
  getSetting: () => get('/api/user/setting'),
  info: () => get('/api/user/info'),
  calendar: (month?: string) => get('/api/user/calendar/get', { month }),
  updateFirstInit: () => post('/api/user/save-grade', { studyGrade: 1 }),
}

export const wordApi = {
  list: (params: any) => get('/api/word/list', params),
  todayList: (limit?: number) => get('/api/srs/today-list', { limit }),
  feedback: (wordId: number, feedback: number) => post('/api/srs/feedback', { wordId, feedback }),
  progress: () => get('/api/srs/progress'),
}

export const writeApi = {
  list: (articleId?: number) => get('/api/write/list', { articleId }),
  submit: (data: any) => post('/api/write/submit', data),
}

export const examApi = {
  list: (params: any) => get('/api/exam/question/list', params),
  submit: (data: any) => post('/api/exam/question/submit', data),
  errorList: () => get('/api/exam/error-list'),
  clearError: () => del('/api/exam/error/clear'),
}

export const paperApi = {
  create: (data: any) => post('/api/paper/create', data),
  itemSave: (data: any) => post('/api/paper/item/save', data),
  list: () => get('/api/paper/list'),
  delete: (paperId: number) => del('/api/paper/delete', { paperId }),
  exportHtml: (paperId: number) => get('/api/paper/export-html', { paperId }),
  exportPdf: (paperId: number) => get('/api/paper/export-pdf', { paperId }),
}

export const memberApi = {
  buyQuota: (amount: number) => post('/api/member/buy-quota', { amount }),
  subscribe: (months: number) => post('/api/member/subscribe', { months }),
  quota: () => get('/api/member/quota'),
}

export const dictApi = {
  lookup: (entry: string) => get('/api/dict/lookup', { entry }),
  search: (keyword: string, limit?: number) => get('/api/dict/search', { keyword, limit }),
  hot: (limit?: number) => get('/api/dict/hot', { limit }),
}