<template>
  <view class="login-page">
    <InkBackground seedText="文">
      <view class="content">
        <view class="brand">
          <text class="brand-name font-xu">文言斩</text>
          <text class="brand-slogan">朝吟暮诵 · 墨染书香</text>
        </view>
        <view class="login-btns">
          <view class="lg-btn wechat" @tap="login('wechat')">微信一键登录</view>
          <view class="lg-btn qq" @tap="login('qq')">QQ 登录</view>
        </view>
        <text class="agree">登录即代表同意《用户协议》与《隐私政策》</text>
      </view>
    </InkBackground>
  </view>
</template>

<script setup lang="ts">
import { useUserStore } from '@/store/user'
import { authApi } from '@/api'
import InkBackground from '@/components/InkBackground/InkBackground.vue'

const userStore = useUserStore()

async function login(platform: 'wechat' | 'qq') {
  uni.showLoading({ title: '登录中' })
  try {
    const res: any = platform === 'wechat' ? await authApi.wechat() : await authApi.qq()
    userStore.setLogin({
      token: res.token,
      userId: res.userId,
      nickname: res.nickname,
      avatar: res.avatar,
      isFirstInit: res.isFirstInit,
    })
    uni.hideLoading()
    // 新用户走年级选择
    if (res.isFirstInit === 1) {
      uni.reLaunch({ url: '/pages/user/grade' })
    } else {
      uni.reLaunch({ url: '/pages/index/index' })
    }
  } catch (e) {
    uni.hideLoading()
    // 演示兜底：无后端时本地模拟登录
    userStore.setLogin({ token: 'mock-token', userId: 1001, nickname: '文言学子', isFirstInit: 1 })
    uni.reLaunch({ url: '/pages/user/grade' })
  }
}
</script>

<style lang="scss" scoped>
.login-page { height: 100vh; }
.content { position: relative; height: 100vh; display: flex; flex-direction: column; justify-content: space-between; padding: 0 60rpx 100rpx; }
.brand { margin-top: 240rpx; text-align: center; }
.brand-name { font-size: 80rpx; color: #2c2c2c; font-weight: bold; }
.brand-slogan { display: block; font-size: 26rpx; color: #9b5b3a; margin-top: 20rpx; letter-spacing: 6rpx; }
.login-btns { display: flex; flex-direction: column; gap: 24rpx; }
.lg-btn { height: 100rpx; line-height: 100rpx; text-align: center; border-radius: 50rpx; font-size: 32rpx; font-weight: 600; color: #f5f1e8; }
.wechat { background: #07c160; }
.qq { background: #12b7f5; }
.agree { text-align: center; font-size: 22rpx; color: #727272; margin-top: 20rpx; }
</style>