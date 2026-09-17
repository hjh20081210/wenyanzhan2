<template>
  <view :class="[theme === 'night' ? 'page-night' : 'page-paper']">
    <nav-bar :title="article?.title || '阅读'" back />
    <view class="read-toolbar">
      <text class="t-btn" @tap="toggleMock">译文</text>
      <text class="t-btn" @tap="toggleFont">字体</text>
      <text class="t-btn" @tap="toggleSize">字号 {{ fontSize }}</text>
      <text class="t-btn" @tap="toggleBg">背景</text>
      <text class="t-btn" @tap="addBookmark">书签</text>
    </view>
    <scroll-view scroll-y class="reader">
      <view class="article-meta">
        <text class="a-title font-xu">{{ article?.title }}</text>
        <text class="a-auth">{{ article?.dynasty }} · {{ article?.author }}</text>
      </view>
      <view class="content-body" :style="contentStyle">
        <block v-for="(seg, i) in segs" :key="i">
          <view class="seg" @tap="openNote(i)">
            <text class="seg-text">{{ seg }}</text>
            <!-- 划线标记区 -->
            <view class="seg-note" v-if="showTranslate">
              <text class="note-text">{{ translateSegs[i] || '' }}</text>
            </view>
          </view>
        </block>
      </view>
    </scroll-view>
    <!-- 逐句注释弹窗 -->
    <view class="note-mask" v-if="noteVisible" @tap="closeNote">
      <view class="note-pop" @tap.stop>
        <text class="note-pop-title">注释 / 译文</text>
        <text class="note-pop-content">{{ currentNote }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { articleApi } from '@/api'
import NavBar from '@/components/NavBar.vue'

const userStore = useUserStore()
const theme = computed(() => userStore.theme)
const article = ref<any>(null)
const fontSize = ref(20)
const showTranslate = ref(false)
const fontIdx = ref(0)
const bgIdx = ref(0)
const fonts = ['font-xu', 'font-ma', 'font-ya']
const bgMap = ['#f5f1e8', '#e8efe2', '#1e1d1a']

const translateSegs = ref<string[]>([])
const segs = computed(() => {
  const c = article.value?.content || ''
  return c.split(/[。；\n]/).filter((s: string) => s.trim())
})
const noteVisible = ref(false)
const currentNote = ref('')

const contentStyle = computed(() => ({
  fontSize: fontSize.value + 'px',
  lineHeight: '1.9',
  color: '#2c2c2c',
  fontClass: fonts[fontIdx.value],
}))

function toggleMock() {
  showTranslate.value = !showTranslate.value
  // 演示译文
  translateSegs.value = (article.value?.translate || '').split(/。|；/).filter((s: any) => s.trim())
}
function toggleFont() {
  fontIdx.value = (fontIdx.value + 1) % 3
  uni.showToast({ title: fonts[fontIdx.value] === 'font-xu' ? '霞鹭文楷' : fonts[fontIdx.value] === 'font-ma' ? '马叙伦楷' : '系统字体', icon: 'none' })
}
function toggleSize() {
  fontSize.value = fontSize.value === 18 ? 22 : 18
}
function toggleBg() {
  bgIdx.value = (bgIdx.value + 1) % 3
}
function addBookmark() {
  uni.showToast({ title: '已添加书签', icon: 'none' })
}
function openNote(i: number) {
  currentNote.value = `第${i + 1}句注释：\n` + (article.value?.notes || '此处为重点句式。\n（演示注释，正式数据由后端返回）')
  noteVisible.value = true
}
function closeNote() {
  noteVisible.value = false
}

onLoad((q: any) => {
  const id = q?.articleId
  if (id) {
    articleApi.detail(Number(id)).then((a) => { article.value = a }).catch(() => { article.value = demo() })
  } else {
    article.value = demo()
  }
})
function demo() {
  return {
    title: '爱莲说',
    dynasty: '宋',
    author: '周敦颐',
    content: '水陆草木之花，可爱者甚蕃。晋陶渊明独爱菊。自李唐来，世人甚爱牡丹。予独爱莲之出淤泥而不染，濯清涟而不妖，中通外直，不蔓不枝，香远益清，亭亭净植，可远观而不可亵玩焉。',
    translate: '水上陆上各种草木的花，可爱的很多。晋代的陶渊明只爱菊花。自从李氏的唐朝以来，世上的人都很喜爱牡丹。我却只爱莲花从淤泥中生长而不沾染污秽，经过清水的洗涤却不显得妖艳……',
    notes: '托物言志，以莲喻高洁品格。',
  }
}
</script>

<style lang="scss" scoped>
.page-paper, .page-night { min-height: 100vh; }
.page-paper { background-color: #f5f1e8; }
.page-night { background-color: #1e1d1a; }
.read-toolbar { display: flex; gap: 12rpx; padding: 16rpx 30rpx; background: rgba(255,255,255,0.7); position: sticky; top: 0; z-index: 20; }
.t-btn { font-size: 24rpx; color: #9b5b3a; padding: 10rpx 20rpx; border: 2rpx solid #ddd7c9; border-radius: 30rpx; }
.reader { padding: 30rpx; height: calc(100vh - 200rpx); }
.article-meta { text-align: center; margin-bottom: 30rpx; }
.a-title { font-size: 44rpx; color: #2c2c2c; }
.a-auth { display: block; font-size: 24rpx; color: #9b5b3a; margin-top: 10rpx; }
.content-body { color: #2c2c2c; }
.seg { margin-bottom: 30rpx; border-bottom: 2rpx dashed #ddd7c9; padding-bottom: 16rpx; }
.seg-text { font-size: inherit; }
.seg-note { margin-top: 10rpx; }
.note-text { font-size: 26rpx; color: #727272; }
.note-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: flex-end; }
.note-pop { width: 100%; background: #fff; border-radius: 30rpx 30rpx 0 0; padding: 40rpx; }
.note-pop-title { display: block; font-size: 30rpx; color: #9b5b3a; margin-bottom: 20rpx; font-weight: 600; }
.note-pop-content { font-size: 28rpx; color: #2c2c2c; line-height: 1.8; white-space: pre-wrap; }
</style>