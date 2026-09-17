<template>
  <view class="page">
    <NavBar title="我的试卷" />
    <view class="create-row">
      <view class="create-btn" @tap="createPaper">+ 新建组卷</view>
    </view>
    <scroll-view scroll-y class="paper-list">
      <view class="paper-card ink-card" v-for="p in papers" :key="p.paperId">
        <view class="p-main" @tap="preview(p)">
          <text class="p-name font-xu">{{ p.paperName }}</text>
          <text class="p-meta">{{ p.questionCount || 0 }} 道题 · {{ p.paperType === 1 ? '错题组卷' : '自定义组卷' }}</text>
        </view>
        <view class="p-actions">
          <text class="p-act" @tap="preview(p)">预览打印</text>
          <text class="p-act danger" @tap="remove(p)">删除</text>
        </view>
      </view>
      <view v-if="!papers.length" class="empty">暂无试卷，点击左上角新建</view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { paperApi } from '@/api'
import NavBar from '@/components/NavBar.vue'

const papers = ref<any[]>([])

function createPaper() {
  uni.showModal({
    title: '新建组卷',
    editable: true,
    placeholderText: '请输入试卷名称',
    success: (r) => {
      if (r.confirm && r.content) {
        paperApi.create({ paperName: r.content, paperType: 2 }).then((id) => {
          load()
          uni.showToast({ title: '创建成功', icon: 'success' })
        }).catch(() => {
          papers.value.unshift({ paperId: Date.now(), paperName: r.content, questionCount: 0, paperType: 2 })
        })
      }
    },
  })
}
function preview(p: any) {
  uni.navigateTo({ url: `/pages/paper/preview?paperId=${p.paperId}&name=${encodeURIComponent(p.paperName)}` })
}
function remove(p: any) {
  uni.showModal({
    title: '删除试卷',
    content: `确定删除「${p.paperName}」吗？`,
    success: (r) => {
      if (r.confirm) {
        paperApi.delete(p.paperId).then(load).catch(() => {
          papers.value = papers.value.filter((x) => x.paperId !== p.paperId)
        })
      }
    },
  })
}
function load() {
  paperApi.list().then((res: any) => { papers.value = res || [] }).catch(() => {
    papers.value = [{ paperId: 1, paperName: '错题组卷-文言文', questionCount: 5, paperType: 1 }]
  })
}
onMounted(load)
</script>

<style lang="scss" scoped>
.page { background-color: #f5f1e8; min-height: 100vh; }
.create-row { padding: 20rpx 30rpx; }
.create-btn { display: inline-block; background: #9b5b3a; color: #f5f1e8; padding: 16rpx 40rpx; border-radius: 36rpx; font-size: 28rpx; }
.paper-list { padding: 0 30rpx; height: calc(100vh - 200rpx); }
.paper-card { display: flex; justify-content: space-between; align-items: center; }
.p-main { display: flex; flex-direction: column; }
.p-name { font-size: 32rpx; color: #2c2c2c; }
.p-meta { font-size: 24rpx; color: #727272; margin-top: 8rpx; }
.p-actions { display: flex; gap: 16rpx; }
.p-act { font-size: 24rpx; color: #9b5b3a; padding: 8rpx 20rpx; border: 2rpx solid #9b5b3a; border-radius: 26rpx; }
.p-act.danger { color: #c0392b; border-color: #c0392b; }
.empty { text-align: center; color: #727272; padding: 60rpx; }
</style>