<template>
  <view class="page">
    <NavBar title="学习设置" />
    <scroll-view scroll-y class="setting-scroll">
      <!-- 阅读字体 -->
      <view class="settings-group">
        <text class="group-label">阅读字体</text>
        <view class="opt-row" v-for="f in fonts" :key="f.value" @tap="setFont(f.value)">
          <text :class="['opt-name', f.value]">{{ f.label }}</text>
          <text class="check">{{ userStore.fontType === f.value ? '✓' : '' }}</text>
        </view>
      </view>
      <!-- 阅读背景 -->
      <view class="settings-group">
        <text class="group-label">阅读背景 & 夜间模式</text>
        <view class="opt-row" v-for="b in bgs" :key="b.value" @tap="setBg(b.value)">
          <view class="bg-swatch" :style="{ backgroundColor: b.color }"></view>
          <text class="opt-name">{{ b.label }}</text>
          <text class="check">{{ (userStore.theme === b.value) ? '✓' : '' }}</text>
        </view>
      </view>
      <!-- 打卡提醒 -->
      <view class="settings-group">
        <text class="group-label">打卡提醒</text>
        <view class="opt-row">
          <text class="opt-name">开启定时提醒</text>
          <switch :checked="remindSwitch" @change="toggleRemind" color="#9b5b3a" style="transform: scale(0.8)" />
        </view>
        <view class="opt-row" v-if="remindSwitch">
          <text class="opt-name">提醒时间</text>
          <picker mode="time" :value="remindTime" @change="onTimeChange">
            <text class="time-val">{{ remindTime }}</text>
          </picker>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api'
import NavBar from '@/components/NavBar.vue'

const userStore = useUserStore()
const fonts = [
  { label: '系统字体', value: 'system' },
  { label: '霞鹭文楷', value: 'font-xu' },
  { label: '马叙伦楷', value: 'font-ma' },
]
const bgs = [
  { label: '宣纸米白', value: 'paper', color: '#f5f1e8' },
  { label: '夜间模式', value: 'night', color: '#1e1d1a' },
]
const remindSwitch = ref(false)
const remindTime = ref('21:00')

function setFont(f: string) {
  userStore.setFont(f)
}
function setBg(b: string) {
  userStore.setTheme(b)
}
function toggleRemind(e: any) {
  remindSwitch.value = e.detail.value
  userApi.saveRemind({ remindSwitch: e.detail.value ? 1 : 0, remindTime: remindTime.value }).catch(() => {})
}
function onTimeChange(e: any) {
  remindTime.value = e.detail.value
  userApi.saveRemind({ remindSwitch: 1, remindTime: remindTime.value }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.page { background-color: #f5f1e8; min-height: 100vh; }
.setting-scroll { padding: 20rpx 30rpx; height: calc(100vh - 120rpx); }
.settings-group { margin-bottom: 30rpx; background: rgba(255,255,255,0.85); border-radius: 20rpx; padding: 20rpx 26rpx; }
.group-label { font-size: 26rpx; color: #9b5b3a; font-weight: 600; display: block; margin-bottom: 10rpx; }
.opt-row { display: flex; align-items: center; justify-content: space-between; padding: 22rpx 0; border-bottom: 2rpx solid #f0ebe0; }
.opt-name { font-size: 30rpx; color: #2c2c2c; }
.bg-swatch { width: 48rpx; height: 48rpx; border-radius: 12rpx; margin-right: 20rpx; border: 2rpx solid #ddd7c9; }
.check { font-size: 34rpx; color: #9b5b3a; font-weight: bold; }
.time-val { font-size: 30rpx; color: #9b5b3a; }
</style>