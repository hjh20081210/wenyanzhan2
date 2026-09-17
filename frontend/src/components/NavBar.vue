<template>
  <view class="nav-bar">
    <view class="nav-left" @tap="back">
      <text class="arrow">‹</text>
    </view>
    <text class="nav-title">{{ title }}</text>
    <view class="nav-right" @tap="more">
      <text class="dots">•••</text>
    </view>
  </view>
</template>

<script setup lang="ts">
const props = defineProps<{ title: string; back?: boolean }>()

function back() {
  if (props.back !== false) {
    uni.navigateBack({ delta: 1 })
  }
}
function more() {
  uni.showActionSheet({ itemList: ['分享', '收藏'], success: () => {} })
}
</script>

<style lang="scss" scoped>
.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx 30rpx;
  padding-top: calc(30rpx + env(safe-area-inset-top));
  background: #f5f1e8;
}
.nav-left { width: 60rpx; }
.arrow { font-size: 56rpx; color: #2c2c2c; }
.nav-title { font-size: 34rpx; color: #2c2c2c; font-weight: 600; font-family: '霞鹭文楷', 'KaiTi', serif; }
.nav-right { width: 60rpx; text-align: right; }
.dots { font-size: 34rpx; color: #2c2c2c; }
</style>