<template>
  <view class="page">
    <NavBar title="选择词包" />
    <scroll-view scroll-y class="pkg-scroll">
      <view class="pkg-card ink-card" v-for="p in packages" :key="p.name">
        <view class="pkg-main">
          <text class="pkg-name font-xu">{{ p.name }}</text>
          <text class="pkg-desc">{{ p.desc }}</text>
          <text class="pkg-count">{{ p.count }} 个词</text>
        </view>
        <view class="pkg-btn" :class="{ on: p.checked }" @tap="toggle(p)">{{ p.checked ? '已选' : '选择' }}</view>
      </view>
    </scroll-view>
    <view class="bottom-bar">
      <text class="total">已选 {{ checkedCount }} 个词包</text>
      <view class="start-btn" @tap="start">开始学习 →</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import NavBar from '@/components/NavBar.vue'

const packages = reactive([
  { name: '高中文言实词', desc: '120个常考实词精讲', count: 120, checked: false },
  { name: '高中文言虚词', desc: '18个核心虚词', count: 18, checked: false },
  { name: '初中必背篇目', desc: '初中教材必背文言文', count: 40, checked: false },
  { name: '通假字专项', desc: '高频通假字辨析', count: 36, checked: false },
  { name: '词类活用', desc: '名词作状/使动/意动', count: 30, checked: false },
  { name: '古今异义', desc: '易错古今异义词', count: 45, checked: false },
])
const checkedCount = computed(() => packages.filter((p) => p.checked).reduce((s, p) => s + p.count, 0))
function toggle(p: any) {
  p.checked = !p.checked
}
function start() {
  uni.navigateBack({ delta: 1 })
}
</script>

<style lang="scss" scoped>
.page { background-color: #f5f1e8; min-height: 100vh; }
.pkg-scroll { padding: 20rpx 30rpx; height: calc(100vh - 200rpx); }
.pkg-card { display: flex; align-items: center; justify-content: space-between; }
.pkg-main { display: flex; flex-direction: column; }
.pkg-name { font-size: 34rpx; color: #2c2c2c; }
.pkg-desc { font-size: 24rpx; color: #727272; margin-top: 8rpx; }
.pkg-count { font-size: 22rpx; color: #9b5b3a; margin-top: 6rpx; }
.pkg-btn { font-size: 26rpx; color: #9b5b3a; border: 2rpx solid #9b5b3a; padding: 10rpx 28rpx; border-radius: 30rpx; }
.pkg-btn.on { background: #9b5b3a; color: #f5f1e8; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; display: flex; align-items: center; justify-content: space-between; padding: 20rpx 30rpx; padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); background: #f5f1e8; }
.total { font-size: 26rpx; color: #727272; }
.start-btn { background: #9b5b3a; color: #f5f1e8; padding: 20rpx 50rpx; border-radius: 40rpx; font-size: 30rpx; }
</style>