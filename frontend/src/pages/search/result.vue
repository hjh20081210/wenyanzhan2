<template>
  <view class="page">
    <NavBar title="搜索" />
    <view class="search-box">
      <input
        class="search-input"
        v-model="keyword"
        placeholder="搜索篇目 / 作者 / 字词"
        confirm-type="search"
        @confirm="doSearch"
      />
      <text class="search-submit" @tap="doSearch">搜索</text>
    </view>
    <!-- 搜索历史 -->
    <view v-if="history.length && !searched" class="history">
      <text class="his-title">搜索历史</text>
      <view class="his-tags">
        <text v-for="h in history" :key="h" class="his-tag" @tap="quickSearch(h)">{{ h }}</text>
      </view>
    </view>
    <!-- 结果 -->
    <scroll-view scroll-y class="result-list" v-if="searched">
      <view v-for="a in results" :key="a.articleId" class="result-item ink-card" @tap="goDetail(a.articleId)">
        <text class="r-title font-xu">{{ a.title }}</text>
        <text class="r-meta">{{ a.dynasty }} · {{ a.author }}</text>
      </view>
      <view v-if="!results.length" class="empty">未找到相关篇目</view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { articleApi } from '@/api'
import NavBar from '@/components/NavBar.vue'

const keyword = ref('')
const results = ref<any[]>([])
const searched = ref(false)
const history = ref<string[]>(uni.getStorageSync('searchHistory') || [])

function saveHistory(k: string) {
  if (!k) return
  const h = history.value.filter((x) => x !== k)
  h.unshift(k)
  history.value = h.slice(0, 10)
  uni.setStorageSync('searchHistory', history.value)
}
function doSearch() {
  const k = (keyword.value || '').trim()
  if (!k) return
  saveHistory(k)
  searched.value = true
  articleApi.search({ keyword: k, page: 1, size: 20 }).then((res: any) => {
    results.value = res.records || []
  }).catch(() => {
    results.value = [
      { articleId: 1, title: '爱莲说', author: '周敦颐', dynasty: '宋' },
    ]
  })
}
function quickSearch(k: string) {
  keyword.value = k
  doSearch()
}
function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/article/detail?articleId=${id}` })
}
</script>

<style lang="scss" scoped>
.page { background-color: #f5f1e8; min-height: 100vh; }
.search-box { display: flex; align-items: center; gap: 16rpx; padding: 20rpx 30rpx; }
.search-input { flex: 1; height: 76rpx; background: #fff; border-radius: 38rpx; padding: 0 30rpx; font-size: 28rpx; border: 2rpx solid #ddd7c9; }
.search-submit { font-size: 28rpx; color: #f5f1e8; background: #9b5b3a; padding: 16rpx 34rpx; border-radius: 38rpx; }
.history { padding: 30rpx; }
.his-title { font-size: 28rpx; color: #9b5b3a; font-weight: 600; }
.his-tags { display: flex; flex-wrap: wrap; gap: 16rpx; margin-top: 20rpx; }
.his-tag { font-size: 26rpx; color: #2c2c2c; background: #fff; padding: 12rpx 26rpx; border-radius: 30rpx; border: 2rpx solid #ddd7c9; }
.result-list { padding: 10rpx 30rpx; height: calc(100vh - 220rpx); }
.result-item { display: flex; flex-direction: column; }
.r-title { font-size: 34rpx; color: #2c2c2c; }
.r-meta { font-size: 24rpx; color: #727272; margin-top: 8rpx; }
.empty { text-align: center; color: #727272; padding: 60rpx; }
</style>