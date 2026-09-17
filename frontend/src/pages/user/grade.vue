<template>
  <view class="grade-page">
    <view class="grade-title">
      <text class="gt font-xu">选择你的学段</text>
      <text class="gs">我们将据此为你推荐合适的篇目与题库</text>
    </view>
    <view class="grade-cards">
      <view class="grade-card" :class="{ on: sel === 1 }" @tap="sel = 1">
        <text class="gc-icon">📖</text>
        <text class="gc-name">初中</text>
        <text class="gc-desc">七年级 ～ 九年级</text>
      </view>
      <view class="grade-card" :class="{ on: sel === 2 }" @tap="sel = 2">
        <text class="gc-icon">🏫</text>
        <text class="gc-name">高中</text>
        <text class="gc-desc">高一 ～ 高三</text>
      </view>
    </view>
    <view class="confirm-btn" @tap="confirm">确认并开始</view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { userApi } from '@/api'

const sel = ref(1)
function confirm() {
  userApi.saveGrade(sel.value).then(() => { finish() }).catch(() => finish())
}
function finish() {
  uni.setStorageSync('guideDone', true)
  uni.reLaunch({ url: '/pages/index/index' })
}
</script>

<style lang="scss" scoped>
.grade-page { background: linear-gradient(160deg, #f5f1e8, #e9dfcd); min-height: 100vh; padding: 120rpx 50rpx; }
.grade-title { text-align: center; margin-bottom: 80rpx; }
.gt { font-size: 56rpx; color: #2c2c2c; font-weight: bold; display: block; }
.gs { font-size: 26rpx; color: #727272; margin-top: 20rpx; display: block; }
.grade-cards { display: flex; flex-direction: column; gap: 40rpx; }
.grade-card { background: rgba(255,255,255,0.85); border-radius: 30rpx; padding: 50rpx; text-align: center; border: 4rpx solid transparent; box-shadow: 0 12rpx 30rpx rgba(155,91,58,0.08); }
.grade-card.on { border-color: #9b5b3a; }
.gc-icon { font-size: 72rpx; display: block; }
.gc-name { font-size: 44rpx; color: #2c2c2c; font-weight: 600; display: block; margin: 20rpx 0 8rpx; }
.gc-desc { font-size: 26rpx; color: #727272; }
.confirm-btn { margin-top: 80rpx; height: 100rpx; line-height: 100rpx; text-align: center; background: #9b5b3a; color: #f5f1e8; border-radius: 50rpx; font-size: 34rpx; font-weight: 600; }
</style>