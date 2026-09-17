<template>
  <view class="tabbar" :class="{ night: theme === 'night' }">
    <view
      v-for="item in tabs"
      :key="item.pagePath"
      class="tab-item"
      :class="[{ active: current === item.pagePath }, getFontClass()]"
      @tap="switchTab(item.pagePath)"
    >
      <view class="tab-icon">
        <text class="icon-char">{{ item.icon }}</text>
      </view>
      <text class="tab-text">{{ item.text }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { onShow, onHide } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'

const props = defineProps<{ current: string }>()
const tabs = [
  { pagePath: '/pages/index/index', text: '首页', icon: '首' },
  { pagePath: '/pages/article/library', text: '篇目库', icon: '库' },
  { pagePath: '/pages/word/card', text: '背诵', icon: '诵' },
  { pagePath: '/pages/user/me', text: '我的', icon: '我' },
]
const userStore = useUserStore()
const current = ref(props.current)
const theme = computed(() => (userStore.theme === 'night' ? 'night' : 'light'))

function switchTab(url: string) {
  if (current.value === url) return
  uni.reLaunch({ url })
}
function getFontClass() {
  return ''
}
</script>

<style lang="scss" scoped>
.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  padding-bottom: env(safe-area-inset-bottom);
  display: flex;
  background-color: rgba(245, 241, 232, 0.96);
  box-shadow: 0 -4rpx 20rpx rgba(155, 91, 58, 0.08);
  z-index: 100;
  &.night {
    background-color: #1e1d1a;
  }
}
.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #727272;
  &.active {
    color: #9b5b3a;
  }
}
.tab-icon {
  width: 52rpx;
  height: 52rpx;
  border-radius: 50%;
  border: 2rpx solid #ddd7c9;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4rpx;
  font-size: 28rpx;
}
.active .tab-icon {
  background-color: #9b5b3a;
  border-color: #9b5b3a;
  color: #f5f1e8;
}
.tab-text {
  font-size: 22rpx;
}
</style>