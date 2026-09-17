<template>
  <view class="page">
    <NavBar title="默写刷题" />
    <scroll-view scroll-y class="write-scroll">
      <!-- 题目卡片 -->
      <view class="question-card ink-card">
        <text class="q-hint">{{ hint || '默写下句' }}</text>
        <view class="q-content font-xu">{{ displayText }}</view>
        <textarea
          class="answer-area"
          v-model="answer"
          :placeholder="placeholderText"
          auto-height
          maxlength="500"
        />
        <view class="asr-row" @tap="openAsr">
          <text class="asr-btn">🎤 语音转写</text>
          <text class="asr-tip">识别后可编辑再提交</text>
        </view>
      </view>
      <view class="btn-primary submit-btn" @tap="submit">提交批改</view>
      <!-- 解析 -->
      <view class="analysis ink-card" v-if="result">
        <text class="a-title">批改结果：{{ result.correct ? '✓ 正确' : '✗ 有误' }}</text>
        <text class="a-content">{{ result.analysis }}</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onLoad } from '@dcloudio/uni-app'
import { writeApi } from '@/api'
import NavBar from '@/components/NavBar.vue'

const answer = ref('')
const hint = ref('根据提示写出原句')
const displayText = ref('（默写正文）')
const placeholderText = '请输入默写内容…'
const result = ref<any>(null)
const exerciseId = ref(0)

function openAsr() {
  const recorder = uni.getRecorderManager()
  recorder.onStart(() => uni.showLoading({ title: '录音中' }))
  recorder.onStop((res: any) => {
    uni.hideLoading()
    // 演示：模拟ASR识别结果（生产接入讯飞，识别结果必须先编辑再提交）
    answer.value = '模拟语音识别结果，可手动修改后提交'
    uni.showToast({ title: '识别完成，请核对修改', icon: 'none' })
  })
  recorder.start({ format: 'mp3' })
  setTimeout(() => recorder.stop(), 3000)
}

function submit() {
  writeApi.submit({ exerciseId: exerciseId.value || 1, userAnswer: answer.value }).then((res: any) => {
    result.value = res
    // 首次作答返回 showRemindPopup
    if (res.showRemindPopup) {
      showRemindPopup(res)
    }
  }).catch(() => {
    // 演示结果
    result.value = { correct: false, analysis: '参考答案：予独爱莲之出淤泥而不染。' }
  })
}

function showRemindPopup(res: any) {
  uni.showModal({
    title: '打卡邀约',
    content: '我们要不要约定一个时间，定时打卡训练文言文啦？',
    confirmText: '确定开启提醒',
    cancelText: '暂时跳过',
    success: (r) => {
      if (r.confirm) {
        const now = new Date()
        const hm = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
        // 使用设置页保存提醒
        uni.setStorageSync('pendingRemind', hm)
        uni.showToast({ title: '已开启每日打卡提醒', icon: 'success' })
      }
    },
  })
}

onLoad((q: any) => {
  if (q?.articleId) exerciseId.value = Number(q.articleId)
})
</script>

<style lang="scss" scoped>
.page { background-color: #f5f1e8; min-height: 100vh; }
.write-scroll { padding: 20rpx 30rpx; height: calc(100vh - 120rpx); }
.q-hint { font-size: 28rpx; color: #9b5b3a; }
.q-content { font-size: 36rpx; color: #2c2c2c; line-height: 1.9; margin: 20rpx 0; }
.answer-area { width: 100%; min-height: 160rpx; background: #f8f4ea; border-radius: 16rpx; padding: 20rpx; font-size: 32rpx; margin-top: 10rpx; }
.asr-row { display: flex; align-items: center; gap: 16rpx; margin-top: 30rpx; }
.asr-btn { font-size: 28rpx; color: #f5f1e8; background: #9b5b3a; padding: 14rpx 28rpx; border-radius: 34rpx; }
.asr-tip { font-size: 22rpx; color: #727272; }
.submit-btn { margin-top: 30rpx; height: 100rpx; line-height: 100rpx; }
.analysis { margin-top: 30rpx; }
.a-title { font-size: 30rpx; color: #2c2c2c; font-weight: 600; display: block; margin-bottom: 16rpx; }
.a-content { font-size: 28rpx; color: #555; line-height: 1.8; }
</style>