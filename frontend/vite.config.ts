import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import UnoCSS from 'unocss/vite'
import { presetWind, presetAttributify, transformerDirectives } from 'unocss'

export default defineConfig({
  plugins: [
    uni(),
    UnoCSS({
      presets: [presetWind(), presetAttributify()],
      transformers: [transformerDirectives()],
      safelist: ['font-zhong', 'font-ya', 'font-xu', 'font-ma', 'bg-paper', 'bg-ink', 'bg-ochre', 'bg-night'],
    }),
  ],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
    },
  },
})