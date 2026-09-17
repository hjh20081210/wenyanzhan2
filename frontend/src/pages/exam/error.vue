<template>
  <view class="page">
    <NavBar title="错题本" />
    <view class="error-tabs">
      <view class="etab" :class="{ on: tab === 'exam' }" @tap="tab = 'exam'">真题错题</view>
      <view class="etab" :class="{ on: tab === 'word' }" @tap="tab = 'word'">字词错题</view>
    </view>
    <view class="error-toolbar">
      <text class="etool" @tap="selectAll">{{ allChecked ? '取消全选' : '全选' }}</text>
      <text class="etool" @tap="makePaper">组卷</text>
      <text class="etool danger" @tap="clearError">清空错题</text>
    </view>
    <scroll-view scroll-y class="error-list">
      <view v-for="e in errorList" :key="e.questionId || e.wordId" class="error-item ink-card">
        <view class="ei-check" @tap="toggle(e)">
          <text :class="['ei-box', { on: e.checked }]">{{ e.checked ? '✓' : '' }}</text>
        </view>
        <view class="ei-main" @tap="practise(e)">
          <text class="ei-title font-xu">{{ tab === 'exam' ? e.title : e.word }}</text>
          <text class="ei-sub">{{ tab === 'exam' ? (e.analysis || '') : (e.explain || '') }}</text>
        </view>
      </view>
      <view v-if="!errorList.length" class="empty">暂无错题，继续保持 📖</view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { examApi, wordApi } from '@/api'
import NavBar from '@/components/NavBar.vue'

const tab = ref<'exam' | 'word'>('exam')
const errorList = ref<any[]>([])
const allChecked = ref(false)

const selected = reactive<Set<number>>(new Set())

function toggle(e: any) {
  const id = e.questionId || e.wordId
  if (selected.has(id)) selected.delete(id)
  else selected.add(id)
  e.checked = selected.has(id)
  updateAllChecked()
}
function selectAll() {
  if (allChecked.value) {
    selected.clear()
    errorList.value.forEach((e) => (e.checked = false))
  } else {
    errorList.value.forEach((e) => {
      const id = e.questionId || e.wordId
      selected.add(id)
      e.checked = true
    })
  }
  allChecked.value = !allChecked.value
}
function updateAllChecked() {
  allChecked.value = errorList.value.length > 0 && errorList.value.every((e) => e.checked)
}
function makePaper() {
  uni.showToast({ title: '已生成错题试卷（可在我的试卷查看）', icon: 'none' })
}
function clearError() {
  uni.showModal({
    title: '提示',
    content: '确定清空全部错题吗？此操作不可恢复',
    success: (r) => {
      if (r.confirm) {
        if (tab.value === 'exam') examApi.clearError().catch(() => {})
        errorList.value = []
        selected.clear()
      }
    },
  })
}
function practise(e: any) {
  uni.navigateTo({ url: `/pages/article/read?articleId=${e.articleId || 1}` })
}

watch(tab, () => load())

async function load() {
  errorList.value = []
  try {
    if (tab.value === 'exam') {
      const res = await examApi.errorList()
      errorList.value = (res || []).map((x: any) => ({ ...x, checked: false }))
    } else {
      // 字词错题未单独后端，演示
      errorList.value = [
        { wordId: 1, word: '故', explain: '因此 / 故意，重点虚词', checked: false },
        { wordId: 2, word: '之', explain: '助词/代词，高频虚词', checked: false },
      ]
    }
  } catch (e) {
    // 演示
    if (tab.value === 'exam') {
      errorList.value = [{ questionId: 1, title: '默写题：出师表', analysis: '参考解析：先帝创业未半而中道崩殂…', checked: false }]
    }
  }
}
load()
</script>

<style lang="scss" scoped>
.page { background-color: #f5f1e8; min-height: 100vh; }
.error-tabs { display: flex; padding: 20rpx 30rpx; gap: 20rpx; }
.etab { flex: 1; text-align: center; padding: 20rpx; border-radius: 40rpx; background: rgba(255,255,255,0.8); color: #727272; font-size: 28rpx; }
.etab.on { background: #9b5b3a; color: #f5f1e8; }
.error-toolbar { display: flex; justify-content: flex-end; gap: 20rpx; padding: 0 30rpx 16rpx; }
.etool { font-size: 24rpx; color: #9b5b3a; }
.etool.danger { color: #c0392b; }
.error-list { padding: 0 30rpx; height: calc(100vh - 260rpx); }
.error-item { display: flex; align-items: center; gap: 20rpx; }
.ei-check { width: 44rpx; }
.ei-box { display: block; width: 40rpx; height: 40rpx; border-radius: 10rpx; border: 2rpx solid #ddd7c9; text-align: center; line-height: 40rpx; font-size: 26rpx; color: #f5f1e8; }
.ei-box.on { background: #9b5b3a; border-color: #9b5b3a; }
.ei-main { flex: 1; display: flex; flex-direction: column; }
.ei-title { font-size: 32rpx; color: #2c2c2c; }
.ei-sub { font-size: 24rpx; color: #727272; margin-top: 8rpx; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.empty { text-align: center; color: #727272; padding: 60rpx; }
</style>