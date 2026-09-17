import { defineConfig, presetUno, presetAttributify, transformerDirectives } from 'unocss'

export default defineConfig({
  presets: [presetUno(), presetAttributify()],
  transformers: [transformerDirectives()],
  theme: {
    colors: {
      paper: '#f5f1e8',   // 宣纸米白底色
      paperdark: '#efe8d8',
      ink: '#2c2c2c',      // 墨黑正文
      ochre: '#9b5b3a',    // 赭石强调色
      ochrelight: '#b5785a',
      gray: '#727272',     // 次要文字/注释
      divider: '#ddd7c9',  // 分割线
      nightbg: '#1e1d1a',  // 夜间背景
      nightcard: '#2a2620',
      gold: '#c9a86a',
    },
  },
  shortcuts: {
    'card': 'rounded-xl shadow-md bg-white/90',
    'card-paper': 'rounded-2xl shadow-lg bg-paper',
    'btn-primary': 'rounded-full bg-ochre text-white px-6 py-3 text-center',
    'btn-ghost': 'rounded-full border border-divider text-ink px-6 py-3 text-center',
    'text-title': 'text-ink font-xu',
    'text-annot': 'text-gray text-sm',
  },
  rules: [
    ['font-xu', { 'font-family': "'霞鹭文楷','XiaLuWenKai','FZKTK','KaiTi',serif" }],
    ['font-ma', { 'font-family': "'马叙伦楷','MaXuLunKai','KaiTi',serif" }],
    ['font-ya', { 'font-family': "'system','PingFang SC','Microsoft YaHei',sans-serif" }],
  ],
})