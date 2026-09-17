<template>
  <view class="calendar-card ink-card">
    <view class="cal-header">
      <text class="cal-title">打卡日历</text>
      <view class="cal-stats">
        <view class="stat"><text class="num">{{ continueDays }}</text><text>连续天</text></view>
        <view class="stat"><text class="num">{{ monthDays }}</text><text>本月天</text></view>
      </view>
    </view>
    <view class="cal-grid">
      <view class="weekday" v-for="w in ['一','二','三','四','五','六','日']" :key="w">{{ w }}</view>
      <view class="cal-cell empty" v-for="i in offset" :key="'e'+i"></view>
      <view
        v-for="(d, idx) in dayList"
        :key="idx"
        class="cal-cell"
        :class="{ checked: checkedSet.has(d), today: d === todayStr }"
      >
        {{ d }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { userApi } from '@/api'

const checkDates = ref<string[]>([])
const continueDays = ref(0)
const monthDays = ref(0)

const now = new Date()
const year = now.getFullYear()
const month = now.getMonth() + 1
const monthStr = `${year}-${String(month).padStart(2, '0')}`
const todayStr = `${monthStr}-${String(now.getDate()).padStart(2, '0')}`
const daysInMonth = new Date(year, month, 0).getDate()
const offset = (new Date(year, month - 1, 1).getDay() + 6) % 7 // 周一开头

const dayList = computed(() => Array.from({ length: daysInMonth }, (_, i) => String(i + 1).padStart(2, '0')))
const checkedSet = computed(() => new Set(checkDates.value))

async function load() {
  try {
    const res = await userApi.calendar(monthStr)
    checkDates.value = res.checkDates
    continueDays.value = res.continueDays
    monthDays.value = res.monthDays
  } catch (e) {
    // 未登录或失败，使用本地示例
    checkDates.value = []
  }
}
load()
</script>

<style lang="scss" scoped>
.cal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}
.cal-title {
  font-size: 34rpx;
  color: #2c2c2c;
  font-weight: 600;
}
.cal-stats {
  display: flex;
  gap: 24rpx;
}
.stat {
  display: flex;
  align-items: baseline;
  gap: 6rpx;
  font-size: 22rpx;
  color: #727272;
}
.stat .num {
  font-size: 40rpx;
  color: #9b5b3a;
  font-weight: bold;
}
.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8rpx;
}
.weekday,
.cal-cell {
  text-align: center;
  font-size: 24rpx;
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 12rpx;
}
.weekday {
  color: #9b5b3a;
  font-weight: 600;
}
.cal-cell.empty {
  visibility: hidden;
}
.cal-cell.checked {
  background-color: #9b5b3a;
  color: #f5f1e8;
  font-weight: 600;
  box-shadow: 0 4rpx 12rpx rgba(155, 91, 58, 0.3);
}
.cal-cell.today {
  border: 2rpx solid #9b5b3a;
  color: #9b5b3a;
  font-weight: bold;
}
</style>