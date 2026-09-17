<template>
  <view :class="[theme === 'night' ? 'page-night' : 'page-paper']">
    <view class="content">
      <!-- 用户信息 -->
      <view class="user-card" @tap="ensureLogin">
        <view class="avatar" v-if="userStore.avatar">
          <image :src="userStore.avatar" class="avatar-img" />
        </view>
        <view class="avatar avatar-text" v-else>{{ (userStore.nickname || '文').charAt(0) }}</view>
        <view class="user-info">
          <text class="nickname">{{ userStore.nickname || '文言学子' }}</text>
          <text class="vip-tag" :class="{ on: isVip }">{{ isVip ? '会员' : '普通用户' }}</text>
        </view>
        <text class="quota" @tap.stop="go('/pages/member/buy')">斩词额度 {{ quotaRemain || 150 }}</text>
      </view>

      <!-- 打卡日历 -->
      <view class="section-title">学习打卡</view>
      <CalendarCheck />

      <!-- 数据统计 -->
      <view class="stats-row">
        <view class="stat-card" @tap="go('/pages/exam/error')">
          <text class="stat-num">0</text>
          <text class="stat-label">错题</text>
        </view>
        <view class="stat-card" @tap="go('/pages/article/library')">
          <text class="stat-num">0</text>
          <text class="stat-label">已读</text>
        </view>
        <view class="stat-card" @tap="go('/pages/paper/list')">
          <text class="stat-num">0</text>
          <text class="stat-label">试卷</text>
        </view>
      </view>

      <!-- 功能菜单 -->
      <view class="menu">
        <view class="menu-item" v-for="m in menus" :key="m.url" @tap="go(m.url)">
          <text class="menu-icon">{{ m.icon }}</text>
          <text class="menu-text">{{ m.label }}</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>
    <TabBar current="/pages/user/me" />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { memberApi } from '@/api'
import TabBar from '@/components/TabBar.vue'
import CalendarCheck from '@/components/CalendarCheck/CalendarCheck.vue'

const userStore = useUserStore()
const theme = computed(() => userStore.theme)
const isVip = ref(false)
const quotaRemain = ref(0)

const menus = [
  { icon: '🖊', label: '默写刷题', url: '/pages/exam/write' },
  { icon: '📕', label: '错题本', url: '/pages/exam/error' },
  { icon: '🧾', label: '我的试卷', url: '/pages/paper/list' },
  { icon: '📄', label: '组卷打印', url: '/pages/paper/preview' },
  { icon: '🍃', label: '学习设置', url: '/pages/user/setting' },
  { icon: '💎', label: '开通会员', url: '/pages/member/buy' },
]

function go(url: string) {
  uni.navigateTo({ url })
}
function ensureLogin() {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
  }
}
onMounted(async () => {
  if (userStore.isLoggedIn) {
    try {
      const q = await memberApi.quota()
      quotaRemain.value = q.remaining ?? 150
      isVip.value = !!q.isVip
    } catch (e) {}
  }
})
</script>

<style lang="scss" scoped>
.page-paper, .page-night { padding-bottom: 140rpx; }
.page-paper { background-color: #f5f1e8; min-height: 100vh; }
.page-night { background-color: #1e1d1a; min-height: 100vh; }
.content { padding: 30rpx; }
.user-card { display: flex; align-items: center; gap: 20rpx; padding: 40rpx 20rpx; }
.avatar { width: 110rpx; height: 110rpx; border-radius: 50%; overflow: hidden; display: flex; align-items: center; justify-content: center; }
.avatar-text { background: #9b5b3a; color: #f5f1e8; font-size: 52rpx; font-family: '霞鹭文楷', 'KaiTi', serif; }
.avatar-img { width: 100%; height: 100%; }
.user-info { flex: 1; display: flex; align-items: center; gap: 12rpx; }
.nickname { font-size: 38rpx; color: #2c2c2c; font-weight: 600; }
.vip-tag { font-size: 20rpx; color: #727272; background: #eee; padding: 4rpx 14rpx; border-radius: 16rpx; }
.vip-tag.on { background: #9b5b3a; color: #f5f1e8; }
.quota { font-size: 26rpx; color: #9b5b3a; }
.section-title { font-size: 30rpx; color: #9b5b3a; font-weight: 600; margin: 20rpx 0 16rpx; }
.stats-row { display: flex; gap: 20rpx; }
.stat-card { flex: 1; background: rgba(255,255,255,0.85); border-radius: 20rpx; padding: 30rpx; text-align: center; box-shadow: 0 8rpx 24rpx rgba(155,91,58,0.08); }
.stat-num { display: block; font-size: 48rpx; color: #9b5b3a; font-weight: bold; }
.stat-label { font-size: 24rpx; color: #727272; }
.menu { margin-top: 30rpx; background: rgba(255,255,255,0.85); border-radius: 20rpx; overflow: hidden; }
.menu-item { display: flex; align-items: center; padding: 28rpx 24rpx; border-bottom: 2rpx solid #f0ebe0; }
.menu-icon { margin-right: 20rpx; }
.menu-text { flex: 1; font-size: 30rpx; color: #2c2c2c; }
.menu-arrow { font-size: 40rpx; color: #ddd7c9; }
</style>