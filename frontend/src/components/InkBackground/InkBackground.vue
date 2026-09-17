<template>
  <view class="ink-bg" :class="{ night: theme === 'night' }">
    <!-- 水墨晕染圆 -->
    <view class="blot blot-1"></view>
    <view class="blot blot-2"></view>
    <view class="blot blot-3"></view>
    <view class="ink-text">{{ seedText }}</view>
    <slot />
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/store/user'

const props = withDefaults(defineProps<{ seedText?: string }>(), { seedText: '文' })
const userStore = useUserStore()
const theme = computed(() => userStore.theme)
</script>

<style lang="scss" scoped>
.ink-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  background-color: #f5f1e8;
  pointer-events: none;
  &.night {
    background-color: #1e1d1a;
  }
}
.blot {
  position: absolute;
  border-radius: 50%;
  filter: blur(40rpx);
  opacity: 0.35;
}
.blot-1 {
  width: 320rpx;
  height: 320rpx;
  background: radial-gradient(circle, rgba(44, 44, 44, 0.25), transparent 70%);
  top: -80rpx;
  right: -60rpx;
}
.blot-2 {
  width: 240rpx;
  height: 240rpx;
  background: radial-gradient(circle, rgba(155, 91, 58, 0.22), transparent 70%);
  top: 40%;
  left: -80rpx;
}
.blot-3 {
  width: 200rpx;
  height: 200rpx;
  background: radial-gradient(circle, rgba(44, 44, 44, 0.15), transparent 70%);
  bottom: 10%;
  right: 10%;
}
.ink-text {
  position: absolute;
  bottom: 8%;
  right: 8%;
  font-size: 200rpx;
  color: rgba(44, 44, 44, 0.06);
  font-family: '霞鹭文楷', 'KaiTi', serif;
}
</style>