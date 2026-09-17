<template>
  <view :class="[theme === 'night' ? 'page-night' : 'page-paper']">
    <InkBackground seedText="文">
      <view class="content">
        <!-- 顶部标题 -->
        <view class="header">
          <view>
            <text class="logo">文言斩</text>
            <text class="slogan">朝吟暮诵 · 墨染书香</text>
          </view>
          <view class="search-btn" @tap="go('/pages/search/result')">
            <text class="search-icon">🔍</text>
            <text class="search-placeholder">搜索篇目/字词</text>
          </view>
        </view>

        <!-- 今日一篇 -->
        <view class="section-title">今日一篇</view>
        <view class="today-card ink-card" @tap="goDetail(daily?.articleId)">
          <text class="today-title font-xu">{{ daily?.title || '爱莲说' }}</text>
          <text class="today-author">{{ daily?.dynasty || '宋' }} · {{ daily?.author || '周敦颐' }}</text>
          <text class="today-excerpt">{{ excerpt }}</text>
          <view class="today-enter">开始学习 →</view>
        </view>

        <!-- 朝代分类 -->
        <view class="section-title">朝代分类</view>
        <scroll-view scroll-x class="dynasty-scroll">
          <view
            v-for="d in dynastys"
            :key="d"
            class="dynasty-chip"
            :class="{ active: curDynasty === d }"
            @tap="filterByDynasty(d)"
          >
            {{ d }}
          </view>
        </scroll-view>

        <!-- 学习进度 -->
        <view class="section-title">今日进度</view>
        <view class="progress-card ink-card">
          <view class="progress-info">
            <text class="progress-label">今日背诵</text>
            <text class="progress-num">{{ progress.todayDone || 0 }}<text class="sup">/{{ progress.target || 20 }}</text></text>
          </view>
          <view class="pb"><view class="pf" :style="{ width: progressPercent + '%' }"></view></view>
        </view>

        <!-- 热门篇目 -->
        <view class="section-title">热门篇目</view>
        <view
          v-for="a in hotList"
          :key="a.articleId"
          class="hot-item ink-card"
          @tap="goDetail(a.articleId)"
        >
          <view class="hot-main">
            <text class="hot-title font-xu">{{ a.title }}</text>
            <text class="hot-meta">{{ a.dynasty }} · {{ a.author }}</text>
          </view>
          <text class="hot-sub" v-if="a.isRequired === 1">必背</text>
        </view>
      </view>
    </InkBackground>
    <TabBar current="/pages/index/index" />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { articleApi, wordApi } from '@/api'
import TabBar from '@/components/TabBar.vue'
import InkBackground from '@/components/InkBackground/InkBackground.vue'

const userStore = useUserStore()
const theme = computed(() => userStore.theme)

const daily = ref<any>(null)
const hotList = ref<any[]>([])
const progress = ref<any>({})
const curDynasty = ref('')
const dynastys = ['全部', '先秦', '两汉', '魏晋', '唐代', '宋代', '明清']

const excerpt = computed(() => {
  const c = daily.value?.content || ''
  return c ? c.slice(0, 40) + '……' : '水陆草木之花，可爱者甚蕃。晋陶渊明独爱菊……'
})
const progressPercent = computed(() =>
  progress.value.target ? Math.min(100, Math.round(((progress.value.todayDone || 0) / progress.value.target) * 100)) : 0
)

function go(url: string) {
  uni.navigateTo({ url })
}
function goDetail(id?: number) {
  if (!id) return
  uni.navigateTo({ url: `/pages/article/detail?articleId=${id}` })
}
function filterByDynasty(d: string) {
  curDynasty.value = d
  uni.navigateTo({ url: `/pages/article/library?dynasty=${d === '全部' ? '' : d}` })
}

onMounted(async () => {
  try {
    const recs = await articleApi.recommend()
    if (recs && recs.length) {
      daily.value = recs[0]
      hotList.value = recs.slice(0, 4)
    }
  } catch (e) {
    // 填入演示数据
    daily.value = { articleId: 1, title: '爱莲说', author: '周敦颐', dynasty: '宋', content: '水陆草木之花，可爱者甚蕃……', isRequired: 1 }
    hotList.value = [
      { articleId: 1, title: '爱莲说', author: '周敦颐', dynasty: '宋', isRequired: 1 },
      { articleId: 2, title: '岳阳楼记', author: '范仲淹', dynasty: '宋', isRequired: 1 },
      { articleId: 3, title: '出师表', author: '诸葛亮', dynasty: '三国', isRequired: 1 },
      { articleId: 4, title: '桃花源记', author: '陶渊明', dynasty: '东晋', isRequired: 1 },
    ]
  }
  try {
    progress.value = await wordApi.progress()
  } catch (e) {
    progress.value = {}
  }
})
</script>

<style lang="scss" scoped>
.page-paper { background-color: #f5f1e8; min-height: 100vh; }
.page-night { background-color: #1e1d1a; min-height: 100vh; }
.content { position: relative; padding: 30rpx; padding-bottom: 140rpx; }
.header { display: flex; justify-content: space-between; align-items: center; padding: 40rpx 10rpx 10rpx; }
.logo { font-size: 52rpx; font-weight: bold; color: #2c2c2c; font-family: '霞鹭文楷', 'KaiTi', serif; }
.slogan { display: block; font-size: 22rpx; color: #727272; margin-top: 6rpx; }
.search-btn { display: flex; align-items: center; gap: 8rpx; width: 240rpx; height: 64rpx; border-radius: 32rpx; background: rgba(255,255,255,0.8); border: 2rpx solid #ddd7c9; padding: 0 20rpx; }
.search-icon { font-size: 26rpx; }
.search-placeholder { font-size: 24rpx; color: #727272; }
.section-title { font-size: 30rpx; color: #9b5b3a; font-weight: 600; margin: 30rpx 0 16rpx 10rpx; }
.today-card { display: flex; flex-direction: column; }
.today-title { font-size: 44rpx; color: #2c2c2c; margin-bottom: 8rpx; }
.today-author { font-size: 24rpx; color: #9b5b3a; margin-bottom: 16rpx; }
.today-excerpt { font-size: 28rpx; color: #727272; line-height: 1.7; }
.today-enter { margin-top: 20rpx; font-size: 26rpx; color: #9b5b3a; text-align: right; }
.dynasty-scroll { white-space: nowrap; }
.dynasty-chip { display: inline-block; padding: 14rpx 30rpx; margin-right: 16rpx; border-radius: 40rpx; background: rgba(255,255,255,0.8); border: 2rpx solid #ddd7c9; font-size: 26rpx; color: #2c2c2c; }
.dynasty-chip.active { background: #9b5b3a; color: #f5f1e8; border-color: #9b5b3a; }
.progress-info { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 16rpx; }
.progress-label { font-size: 26rpx; color: #727272; }
.progress-num { font-size: 44rpx; color: #2c2c2c; font-weight: bold; }
.sup { font-size: 24rpx; color: #727272; }
.pb { height: 16rpx; border-radius: 8rpx; background: #e5ddcd; overflow: hidden; }
.pf { height: 100%; border-radius: 8rpx; background: linear-gradient(90deg, #c9a86a, #9b5b3a); }
.hot-item { display: flex; justify-content: space-between; align-items: center; }
.hot-main { display: flex; flex-direction: column; }
.hot-title { font-size: 34rpx; color: #2c2c2c; }
.hot-meta { font-size: 24rpx; color: #727272; margin-top: 8rpx; }
.hot-sub { font-size: 22rpx; color: #f5f1e8; background: #9b5b3a; padding: 6rpx 18rpx; border-radius: 20rpx; }
</style>