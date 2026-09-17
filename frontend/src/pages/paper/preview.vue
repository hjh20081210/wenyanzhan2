<template>
  <view class="page">
    <NavBar title="试卷预览" />
    <view class="preview-toolbar">
      <view class="pt-btn" @tap="print">打印 (HTML)</view>
      <view class="pt-btn" @tap="exportPdf">导出 PDF (会员)</view>
    </view>
    <scroll-view scroll-y class="preview-body">
      <view class="paper-title" id="print-area">
        <text class="pp-name">{{ name || '文言斩·组卷' }}</text>
        <text class="pp-sub">姓名：______ 日期：______</text>
      </view>
      <view class="question" v-for="(q, i) in questions" :key="i">
        <text class="q-no">{{ i + 1 }}.</text>
        <text class="q-title">{{ q.title }}</text>
        <view class="q-options" v-if="q.options">
          <text class="q-opt" v-for="(op, j) in renderOptions(q.options)" :key="j">{{ op }}</text>
        </view>
      </view>
      <text class="print-hint">💡 H5可浏览器打印；PDF导出需会员+绑定手机号</text>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onLoad } from '@dcloudio/uni-app'
import { paperApi } from '@/api'
import NavBar from '@/components/NavBar.vue'

const name = ref('')
const questions = ref<any[]>([])

function renderOptions(options: any): string[] {
  if (!options) return []
  if (typeof options === 'string') {
    try {
      return JSON.parse(options)
    } catch {
      return [options]
    }
  }
  return Array.isArray(options) ? options : []
}
function print() {
  // #ifdef H5
  window.print && window.print()
  // #endif
  uni.showToast({ title: '请使用浏览器打印', icon: 'none' })
}
function exportPdf() {
  uni.showModal({
    title: 'PDF导出',
    content: 'PDF导出需要会员且已绑定手机号，是否前往开通？',
    confirmText: '去开通',
    success: (r) => { if (r.confirm) uni.navigateTo({ url: '/pages/member/buy' }) },
  })
}

onLoad((q: any) => {
  name.value = q?.name ? decodeURIComponent(q.name) : '文言斩·组卷'
  // 演示题目
  questions.value = [
    { title: '补写出下列句子中的空缺部分。（1）《出师表》中……', options: ['选项A', '选项B', '选项C', '选项D'] },
    { title: '下列加点词解释不正确的一项是（ ）', options: ['A. 属引凄异 引：连接', 'B. 潭中鱼可百许头 许：大约', 'C. 悉以咨之 悉：都', 'D. 能谤讥于市朝 谤：诽谤'] },
    { title: '翻译句子：温故而知新，可以为师矣。' },
  ]
})
</script>

<style lang="scss" scoped>
.page { background-color: #f5f1e8; min-height: 100vh; }
.preview-toolbar { display: flex; gap: 20rpx; padding: 20rpx 30rpx; }
.pt-btn { flex: 1; text-align: center; padding: 20rpx; border-radius: 40rpx; background: #9b5b3a; color: #f5f1e8; font-size: 28rpx; }
.preview-body { padding: 20rpx 30rpx; height: calc(100vh - 180rpx); }
.paper-title { text-align: center; padding: 40rpx; }
.pp-name { font-size: 44rpx; color: #2c2c2c; font-family: '霞鹭文楷', 'KaiTi', serif; display: block; }
.pp-sub { font-size: 24rpx; color: #727272; margin-top: 20rpx; display: block; }
.question { background: #fff; border-radius: 16rpx; padding: 26rpx; margin-bottom: 20rpx; }
.q-no { font-size: 30rpx; color: #9b5b3a; }
.q-title { font-size: 30rpx; color: #2c2c2c; line-height: 1.7; }
.q-options { margin-top: 14rpx; display: flex; flex-direction: column; gap: 10rpx; }
.q-opt { font-size: 28rpx; color: #555; }
.print-hint { display: block; text-align: center; font-size: 22rpx; color: #c0392b; margin-top: 20rpx; }
</style>