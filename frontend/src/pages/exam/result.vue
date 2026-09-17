<template>
  <view class="page">
    <NavBar title="批改结果" />
    <view class="result-hero">
      <view class="score-circle" :class="result.correct ? 'pass' : 'fail'">
        <text class="score">{{ result.correct ? 100 : 0 }}</text>
      </view>
      <text class="result-text">{{ result.correct ? '回答正确！' : '还需加油' }}</text>
    </view>
    <view class="analysis-card ink-card" v-if="result.analysis">
      <text class="a-label">解析</text>
      <text class="a-content">{{ result.analysis }}</text>
    </view>
    <view class="btn-row">
      <view class="again-btn" @tap="again">再练一组</view>
      <view class="detail-btn" @tap="goExam">继续刷题</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onLoad } from '@dcloudio/uni-app'
import NavBar from '@/components/NavBar.vue'

const result = ref<any>({ correct: false, analysis: '参考答案：略' })

function again() {
  uni.navigateBack({ delta: 1 })
}
function goExam() {
  uni.reLaunch({ url: '/pages/exam/write' })
}
onLoad((q: any) => {
  const correct = q?.correct === 'true'
  const analysis = q?.analysis || '参考答案'
  result.value = { correct, analysis }
})
</script>

<style lang="scss" scoped>
.page { background-color: #f5f1e8; min-height: 100vh; }
.result-hero { display: flex; flex-direction: column; align-items: center; padding: 80rpx 0 40rpx; }
.score-circle { width: 220rpx; height: 220rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.score-circle.pass { background: #9b5b3a; }
.score-circle.fail { background: #c0392b; }
.score { font-size: 80rpx; color: #f5f1e8; font-weight: bold; }
.result-text { font-size: 36rpx; color: #2c2c2c; margin-top: 30rpx; font-family: '霞鹭文楷', 'KaiTi', serif; }
.analysis-card { margin: 30rpx; }
.a-label { font-size: 28rpx; color: #9b5b3a; font-weight: 600; display: block; margin-bottom: 16rpx; }
.a-content { font-size: 28rpx; color: #555; line-height: 1.8; }
.btn-row { display: flex; gap: 20rpx; margin: 30rpx; }
.again-btn { flex: 1; text-align: center; height: 96rpx; line-height: 96rpx; background: rgba(255,255,255,0.9); border: 2rpx solid #9b5b3a; color: #9b5b3a; border-radius: 48rpx; font-size: 30rpx; }
.detail-btn { flex: 1; text-align: center; height: 96rpx; line-height: 96rpx; background: #9b5b3a; color: #f5f1e8; border-radius: 48rpx; font-size: 30rpx; }
</style>