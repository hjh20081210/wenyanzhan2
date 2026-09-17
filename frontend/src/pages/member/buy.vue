<template>
  <view class="page">
    <NavBar title="会员开通" />
    <scroll-view scroll-y class="buy-scroll">
      <!-- 会员权益 -->
      <view class="vip-card">
        <text class="vip-title">文言斩会员</text>
        <text class="vip-price">¥19/月</text>
        <view class="vip-perks">
          <view class="perk" v-for="p in perks" :key="p">✓ {{ p }}</view>
        </view>
      </view>
      <!-- 额度购买 -->
      <view class="quota-section">
        <text class="qs-title">斩词额度购买</text>
        <text class="qs-sub">最低 300 起购 · 斩新词消耗额度，复习/默写/刷题免费</text>
        <view class="quota-options">
          <view class="quo" v-for="q in quotaOpts" :key="q" @tap="quota = q" :class="{ on: quota === q }">
            <text class="quo-num">{{ q }}</text>
            <text class="quo-unit">额度</text>
          </view>
        </view>
      </view>
      <view class="btn-primary buy-btn" @tap="buy">立即开通</view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { memberApi } from '@/api'
import NavBar from '@/components/NavBar.vue'

const perks = ['斩词无限量', '默写不限次数', '高考高阶真题解锁', 'PDF试卷导出', 'AI自定义词包', '学习报告导出']
const quotaOpts = [300, 500, 1000, 5000]
const quota = ref(300)

function buy() {
  uni.showActionSheet({
    itemList: ['开通会员', `购买 ${quota.value} 斩词额度`],
    success: (r) => {
      if (r.tapIndex === 0) {
        memberApi.subscribe(1).then(() => uni.showToast({ title: '已下单，请在支付页完成支付', icon: 'none' }))
      } else {
        memberApi.buyQuota(quota.value).then(() => uni.showToast({ title: '已下单', icon: 'none' }))
      }
    },
  })
}
</script>

<style lang="scss" scoped>
.page { background-color: #f5f1e8; min-height: 100vh; }
.buy-scroll { padding: 20rpx 30rpx; }
.vip-card { background: linear-gradient(135deg, #9b5b3a, #b5785a); border-radius: 30rpx; padding: 50rpx; color: #f5f1e8; }
.vip-title { font-size: 44rpx; font-weight: bold; display: block; font-family: '霞鹭文楷', 'KaiTi', serif; }
.vip-price { font-size: 60rpx; font-weight: bold; display: block; margin: 20rpx 0; }
.vip-perks { display: flex; flex-wrap: wrap; gap: 16rpx; }
.perk { font-size: 24rpx; background: rgba(255,255,255,0.15); padding: 10rpx 20rpx; border-radius: 24rpx; }
.quota-section { margin-top: 40rpx; background: rgba(255,255,255,0.85); border-radius: 24rpx; padding: 30rpx; }
.qs-title { font-size: 34rpx; color: #2c2c2c; font-weight: 600; }
.qs-sub { display: block; font-size: 24rpx; color: #727272; margin: 10rpx 0 24rpx; }
.quota-options { display: flex; gap: 20rpx; }
.quo { flex: 1; text-align: center; padding: 24rpx; border-radius: 16rpx; border: 2rpx solid #ddd7c9; }
.quo.on { border-color: #9b5b3a; background: rgba(155,91,58,0.05); }
.quo-num { font-size: 36rpx; color: #2c2c2c; font-weight: 600; }
.quo-unit { font-size: 22rpx; color: #727272; }
.buy-btn { margin-top: 50rpx; height: 100rpx; line-height: 100rpx; }
</style>