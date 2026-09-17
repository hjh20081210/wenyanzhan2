<template>
  <view class="lib-page">
    <!-- 筛选栏 -->
    <view class="filter-bar">
      <scroll-view scroll-x class="filter-scroll">
        <view
          v-for="g in grades"
          :key="g.value"
          class="filter-chip"
          :class="{ on: grade === g.value }"
          @tap="setGrade(g.value)"
        >
          {{ g.label }}
        </view>
      </scroll-view>
    </view>
    <!-- 列表 -->
    <scroll-view scroll-y class="list-scroll">
      <view
        v-for="a in list"
        :key="a.articleId"
        class="lib-item ink-card"
        @tap="goDetail(a.articleId)"
      >
        <view class="lib-cover" :style="[genBg(a)]">
          <text class="cover-char">{{ a.title?.charAt(0) }}</text>
        </view>
        <view class="lib-main">
          <view class="lib-title-row">
            <text class="lib-title font-xu">{{ a.title }}</text>
            <text v-if="a.isRequired === 1" class="lib-required">必背</text>
          </view>
          <text class="lib-author">{{ a.dynasty }} · {{ a.author }}</text>
          <text class="lib-genre">{{ a.genre }}</text>
        </view>
        <text class="lib-arrow">›</text>
      </view>
      <view v-if="!list.length" class="empty">暂无篇目</view>
      <view class="load-more" @tap="loadMore">加载更多</view>
    </scroll-view>
    <TabBar current="/pages/article/library" />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { articleApi } from '@/api'
import TabBar from '@/components/TabBar.vue'

const grades = [
  { label: '全部', value: 0 },
  { label: '小学', value: 3 },
  { label: '初中', value: 1 },
  { label: '高中', value: 2 },
]
const grade = ref(0)
const list = ref<any[]>([])
const page = ref(1)

function setGrade(g: number) {
  grade.value = g
  page.value = 1
  list.value = []
  load()
}
function genBg(a: any) {
  const colors = ['#e8dcc8', '#d9cfbb', '#efe4d2', '#ddd0ba']
  const i = (a.articleId || 0) % colors.length
  return { backgroundColor: colors[i] }
}
function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/article/detail?articleId=${id}` })
}
async function load() {
  try {
    const res = await articleApi.list({ page: page.value, size: 10, grade: grade.value || undefined })
    list.value = res.records || []
  } catch (e) {
    // 演示数据
    list.value = [
      { articleId: 1, title: '爱莲说', author: '周敦颐', dynasty: '宋', genre: '散文', isRequired: 1, grade: 1 },
      { articleId: 2, title: '岳阳楼记', author: '范仲淹', dynasty: '宋', genre: '散文', isRequired: 1, grade: 1 },
      { articleId: 3, title: '出师表', author: '诸葛亮', dynasty: '三国', genre: '表', isRequired: 1, grade: 1 },
      { articleId: 4, title: '桃花源记', author: '陶渊明', dynasty: '东晋', genre: '散文', isRequired: 1, grade: 1 },
      { articleId: 5, title: '小石潭记', author: '柳宗元', dynasty: '唐', genre: '游记', isRequired: 1, grade: 1 },
      { articleId: 6, title: '鱼我所欲也', author: '孟子', dynasty: '战国', genre: '古文', isRequired: 1, grade: 1 },
    ]
  }
}
function loadMore() {
  page.value++
  load()
}
onMounted(load)
</script>

<style lang="scss" scoped>
.lib-page { background-color: #f5f1e8; min-height: 100vh; padding-bottom: 140rpx; }
.filter-bar { padding: 20rpx 30rpx; position: sticky; top: 0; background: #f5f1e8; z-index: 10; }
.filter-scroll { white-space: nowrap; }
.filter-chip { display: inline-block; padding: 12rpx 34rpx; margin-right: 14rpx; border-radius: 36rpx; background: rgba(255,255,255,0.85); border: 2rpx solid #ddd7c9; font-size: 26rpx; color: #2c2c2c; }
.filter-chip.on { background: #9b5b3a; color: #f5f1e8; border-color: #9b5b3a; }
.list-scroll { padding: 0 30rpx; height: calc(100vh - 220rpx); }
.lib-item { display: flex; align-items: center; gap: 20rpx; }
.lib-cover { width: 110rpx; height: 140rpx; border-radius: 12rpx; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.cover-char { font-size: 64rpx; color: #9b5b3a; font-family: '霞鹭文楷', 'KaiTi', serif; }
.lib-main { flex: 1; display: flex; flex-direction: column; }
.lib-title-row { display: flex; align-items: center; gap: 12rpx; }
.lib-title { font-size: 34rpx; color: #2c2c2c; }
.lib-required { font-size: 20rpx; color: #f5f1e8; background: #9b5b3a; padding: 2rpx 14rpx; border-radius: 16rpx; }
.lib-author { font-size: 24rpx; color: #727272; margin-top: 10rpx; }
.lib-genre { font-size: 22rpx; color: #9b5b3a; margin-top: 6rpx; }
.lib-arrow { font-size: 40rpx; color: #ddd7c9; }
.empty, .load-more { text-align: center; color: #727272; font-size: 26rpx; padding: 30rpx; }
</style>