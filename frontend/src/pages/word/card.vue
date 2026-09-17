<template>
  <view :class="[theme === 'night' ? 'page-night' : 'page-paper']">
    <view class="content">
      <view class="header">
        <text class="title">背诵广场</text>
        <text class="sub">实词 · 虚词 · 通假字</text>
      </view>

      <!-- 今日进度 -->
      <view class="progress-chip" @tap="go('/pages/word/package')">
        <text>{{ cards.length }} 张今日待复习卡片</text>
        <text class="chip-arrow">选择词包 ›</text>
      </view>

      <!-- SRS卡片滑动 -->
      <CardSwiper :list="cards" @feedback="onFeedback" />
    </view>
    <TabBar current="/pages/word/card" />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { wordApi, memberApi } from '@/api'
import TabBar from '@/components/TabBar.vue'
import CardSwiper from '@/components/CardSwiper/CardSwiper.vue'

const userStore = useUserStore()
const theme = computed(() => userStore.theme)
const cards = ref<any[]>([])

function go(url: string) {
  uni.navigateTo({ url })
}

async function onFeedback({ wordId, feedback }: { wordId: number; feedback: number }) {
  try {
    await wordApi.feedback(wordId, feedback)
  } catch (e) {
    uni.showToast({ title: '反馈失败', icon: 'none' })
  }
}

onMounted(async () => {
  try {
    const list = await wordApi.todayList(10)
    cards.value = list && list.length ? list : mockCards()
  } catch (e) {
    cards.value = mockCards()
  }
})

function mockCards() {
  return [
    { wordId: 1, word: '故', pos: '虚词', explain: '因此；所以 / 故意；旧时', example: '故天将降大任于是人也。' },
    { wordId: 2, word: '之', pos: '虚词', explain: '助词，的 / 代词 / 往，到', example: '予独爱莲之出淤泥而不染。' },
    { wordId: 3, word: '或', pos: '虚词', explain: '有时 / 有的，有的人 / 或许', example: '或异二者之为。' },
    { wordId: 4, word: '许', pos: '实词', explain: '大约 / 允许 / 赞许', example: '潭中鱼可百许头。' },
    { wordId: 5, word: '属', pos: '实词', explain: '连接 / 隶属 / 嘱托', example: '属引凄异。' },
  ]
}
</script>

<style lang="scss" scoped>
.page-paper { background-color: #f5f1e8; min-height: 100vh; }
.page-night { background-color: #1e1d1a; min-height: 100vh; }
.content { padding: 30rpx; padding-bottom: 140rpx; }
.header { padding: 30rpx 10rpx; }
.title { font-size: 46rpx; font-weight: bold; color: #2c2c2c; font-family: '霞鹭文楷', 'KaiTi', serif; }
.sub { display: block; font-size: 24rpx; color: #727272; margin-top: 8rpx; }
.progress-chip { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 26rpx; border-radius: 16rpx; background: rgba(155,91,58,0.08); border: 2rpx solid #ddd7c9; color: #2c2c2c; font-size: 26rpx; margin-bottom: 20rpx; }
.chip-arrow { color: #9b5b3a; }
</style>