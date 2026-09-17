<template>
  <view class="card-swiper">
    <!-- 当前卡片 -->
    <view
      v-if="current"
      class="word-card"
      :class="[flipped ? 'flipped' : '', depthClass]"
      @tap="flip"
    >
      <view class="card-face card-front">
        <text class="word">{{ current.word }}</text>
        <text class="pos">{{ current.pos || '' }}</text>
      </view>
      <view class="card-face card-back">
        <scroll-view scroll-y class="back-scroll">
          <text class="explain">{{ current.explain }}</text>
          <view v-if="current.example" class="example-block">
            <text class="example-label">例句</text>
            <text class="example">{{ current.example }}</text>
          </view>
        </scroll-view>
      </view>
    </view>
    <!-- 操作按钮 -->
    <view v-if="current" class="action-row">
      <view class="action-btn forget" @tap="emitFeedback(0)">不认识</view>
      <view class="action-btn blur" @tap="emitFeedback(1)">模糊</view>
      <view class="action-btn know" @tap="emitFeedback(2)">认识</view>
    </view>
    <!-- 完成态 -->
    <view v-else class="done-box">
      <text class="done">今日成就达成</text>
      <text class="done-sub">🎉 卡片全部复习完成</text>
    </view>
    <!-- 进度 -->
    <view v-if="list.length" class="progress-row">
      <view class="progress-bar"><view class="progress-fill" :style="{ width: percent + '%' }"></view></view>
      <text class="progress-text">{{ list.length - index }} / {{ list.length }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

export interface WordCard {
  wordId: number
  word: string
  wordType?: number
  pos?: string
  explain: string
  example?: string
}

const props = withDefaults(defineProps<{ list: WordCard[] }>(), { list: () => [] })
const emit = defineEmits<{ (e: 'tap', d: any): void; (e: 'feedback', data: any): void }>()

const index = ref(0)
const flipped = ref(false)

const current = computed(() => props.list[index.value])
const percent = computed(() => (props.list.length ? Math.round(((index.value) / props.list.length) * 100) : 0))
const depthClass = computed(() => index.value)

function flip() {
  flipped.value = !flipped.value
}
function emitFeedback(f: number) {
  if (!current.value) return
  emit('feedback', { wordId: current.value.wordId, feedback: f })
  flipped.value = false
  if (index.value < props.list.length - 1) {
    index.value++
  } else {
    index.value++ // 触发完成态
  }
}
</script>

<style lang="scss" scoped>
.card-swiper {
  padding: 30rpx;
  min-height: 700rpx;
}
.word-card {
  position: relative;
  height: 560rpx;
  border-radius: 28rpx;
  box-shadow: 0 20rpx 50rpx rgba(44, 44, 44, 0.15);
  background: linear-gradient(160deg, #fffdf8, #f5efe2);
  overflow: hidden;
  transition: transform 0.4s, opacity 0.4s;
  transform-style: preserve-3d;
}
.word-card.flipped {
  transform: rotateY(180deg);
}
.card-face {
  position: absolute;
  inset: 0;
  backface-visibility: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx;
}
.card-front {
  background: radial-gradient(circle at 80% 20%, rgba(155, 91, 58, 0.08), transparent 50%);
}
.word {
  font-size: 120rpx;
  color: #2c2c2c;
  font-family: '霞鹭文楷', 'KaiTi', serif;
  margin-bottom: 20rpx;
}
.pos {
  font-size: 30rpx;
  color: #9b5b3a;
}
.card-back {
  transform: rotateY(180deg);
}
.back-scroll {
  height: 100%;
  width: 100%;
}
.explain {
  font-size: 38rpx;
  color: #2c2c2c;
  line-height: 1.6;
  font-family: '马叙伦楷', 'KaiTi', serif;
}
.example-block {
  margin-top: 30rpx;
  padding: 20rpx;
  border-left: 4rpx solid #9b5b3a;
  background: rgba(155, 91, 58, 0.06);
}
.example-label {
  font-size: 24rpx;
  color: #9b5b3a;
  margin-right: 12rpx;
}
.example {
  font-size: 28rpx;
  color: #727272;
}
.action-row {
  display: flex;
  gap: 20rpx;
  margin-top: 40rpx;
}
.action-btn {
  flex: 1;
  height: 96rpx;
  line-height: 96rpx;
  text-align: center;
  border-radius: 48rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: #f5f1e8;
}
.forget {
  background-color: #727272;
}
.blur {
  background-color: #c9a86a;
}
.know {
  background-color: #9b5b3a;
}
.done-box {
  height: 560rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 28rpx;
  background: linear-gradient(160deg, #f5efe2, #e9dfcd);
}
.done {
  font-size: 46rpx;
  color: #9b5b3a;
  font-family: '霞鹭文楷', 'KaiTi', serif;
  margin-bottom: 20rpx;
}
.done-sub {
  font-size: 30rpx;
  color: #727272;
}
.progress-row {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-top: 30rpx;
}
.progress-bar {
  flex: 1;
  height: 12rpx;
  border-radius: 6rpx;
  background: #e5ddcd;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  border-radius: 6rpx;
  background: linear-gradient(90deg, #c9a86a, #9b5b3a);
  transition: width 0.3s;
}
.progress-text {
  font-size: 24rpx;
  color: #727272;
}
</style>