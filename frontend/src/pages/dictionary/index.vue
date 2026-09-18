<template>
  <view class="page">
    <NavBar title="汉字字典" />
    <view class="dict-search">
      <input
        class="dict-input"
        v-model="entry"
        placeholder="输入汉字 / 词语，如：之、闻、谓"
        confirm-type="search"
        @confirm="lookup"
      />
      <text class="dict-go" @tap="lookup">查</text>
    </view>

    <!-- 查无结果/未查询 -->
    <scroll-view scroll-y class="dict-scroll">
      <!-- 命中卡片 -->
      <view v-if="word" class="dict-card">
        <view class="dc-head">
          <text class="dc-char font-xu">{{ word.entry }}</text>
          <view class="dc-info">
            <text class="dc-pinyin">{{ word.pinyin || '—' }}</text>
            <text class="dc-radical" v-if="word.radical">部首：{{ word.radical }} · {{ word.stroke || '?' }}画</text>
          </view>
        </view>
        <!-- 释义列表 -->
        <view class="dc-explain" v-if="meanings.length">
          <view class="dce-item" v-for="(m, i) in meanings" :key="i">
            <text class="dce-num">{{ i + 1 }}</text>
            <text class="dce-text">{{ m.explain }}</text>
            <view class="dce-foot">
              <text class="dce-ex" v-if="m.example">{{ m.example }}</text>
              <text class="dce-src" v-if="m.source">——《{{ m.source }}》</text>
            </view>
          </view>
        </view>
        <view class="dc-source" v-if="word.source">资料来源：{{ word.source }}</view>
      </view>

      <!-- 热门/常用字 -->
      <template v-else>
        <text class="hot-title">常用字 / 热词</text>
        <view class="hot-grid">
          <view class="hot-item ink-card" v-for="w in hotList" :key="w.dictId" @tap="pick(w.entry)">
            <text class="hot-char font-xu">{{ w.entry }}</text>
            <text class="hot-pinyin">{{ w.pinyin }}</text>
          </view>
        </view>
      </template>

      <!-- 提示 -->
      <text class="dict-tip" v-if="hint">{{ hint }}</text>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { dictApi } from '@/api'
import NavBar from '@/components/NavBar.vue'

const entry = ref('')
const word = ref<any>(null)
const meanings = ref<any[]>([])
const hotList = ref<any[]>([])
const hint = ref('')

function parseMeanings(w: any) {
  if (!w || !w.explain) { meanings.value = []; return }
  try {
    const arr = JSON.parse(w.explain)
    meanings.value = Array.isArray(arr) ? arr : [{ explain: w.explain }]
  } catch {
    meanings.value = [{ explain: w.explain }]
  }
}
function renderWord(w: any) {
  word.value = w
  parseMeanings(w)
  hint.value = ''
}
function lookup() {
  const k = (entry.value || '').trim()
  if (!k) { hint.value = '请输入要查询的汉字或词语'; return }
  dictApi.lookup(k).then((res: any) => {
    if (res) renderWord(res)
    else {
      word.value = null; meanings.value = []
      hint.value = `未收录「${k}」，可尝试搜索相似词`
    }
  }).catch(() => {
    // 演示兜底
    renderWord({ entry: k, pinyin: '—', radical: '—', stroke: 0, explain: JSON.stringify([{ explain: '待补全释义（生产由字典数据源导入）', example: '示例' }]), source: '字典演示数据' })
  })
}
function pick(k: string) {
  entry.value = k
  lookup()
}
onMounted(() => {
  dictApi.hot(12).then((res: any) => { hotList.value = res || [] }).catch(() => {
    hotList.value = [
      { dictId: 1, entry: '之', pinyin: 'zhī' },
      { dictId: 2, entry: '而', pinyin: 'ér' },
      { dictId: 3, entry: '其', pinyin: 'qí' },
      { dictId: 4, entry: '于', pinyin: 'yú' },
    ]
  })
})
onLoad((q: any) => {
  if (q?.entry) {
    entry.value = decodeURIComponent(q.entry)
    lookup()
  }
})
</script>

<style lang="scss" scoped>
.page { background-color: #f5f1e8; min-height: 100vh; }
.dict-search { display: flex; align-items: center; gap: 20rpx; padding: 20rpx 30rpx; }
.dict-input { flex: 1; height: 88rpx; background: rgba(255,255,255,0.9); border-radius: 44rpx; padding: 0 32rpx; font-size: 30rpx; }
.dict-go { width: 120rpx; height: 88rpx; line-height: 88rpx; text-align: center; background: #9b5b3a; color: #f5f1e8; border-radius: 44rpx; font-size: 30rpx; }
.dict-scroll { padding: 10rpx 30rpx; height: calc(100vh - 130rpx); }
.dict-card { background: rgba(255,255,255,0.92); border-radius: 24rpx; padding: 34rpx; box-shadow: 0 10rpx 30rpx rgba(44,44,44,0.08); }
.dc-head { display: flex; align-items: center; gap: 30rpx; padding-bottom: 24rpx; border-bottom: 1rpx solid #efe7d8; }
.dc-char { font-size: 140rpx; color: #2c2c2c; line-height: 1; }
.dc-info { display: flex; flex-direction: column; }
.dc-pinyin { font-size: 34rpx; color: #9b5b3a; }
.dc-radical { font-size: 26rpx; color: #727272; margin-top: 10rpx; }
.dc-explain { margin-top: 24rpx; }
.dce-item { display: flex; flex-wrap: wrap; padding: 14rpx 0; }
.dce-num { width: 44rpx; height: 44rpx; line-height: 44rpx; text-align: center; border-radius: 50%; background: rgba(155,91,58,0.12); color: #9b5b3a; font-size: 26rpx; margin-right: 16rpx; }
.dce-text { flex: 1; font-size: 32rpx; color: #2c2c2c; line-height: 1.7; min-width: 200rpx; }
.dce-foot { width: 100%; padding-left: 60rpx; }
.dce-ex { font-size: 26rpx; color: #727272; }
.dce-src { font-size: 24rpx; color: #9b5b3a; }
.dc-source { margin-top: 20rpx; font-size: 22rpx; color: #b0a894; text-align: right; }
.hot-title { display: block; font-size: 30rpx; color: #2c2c2c; font-weight: 600; margin: 20rpx 0; }
.hot-grid { display: flex; flex-wrap: wrap; gap: 20rpx; }
.hot-item { width: 210rpx; display: flex; flex-direction: column; align-items: center; }
.hot-char { font-size: 64rpx; color: #2c2c2c; }
.hot-pinyin { font-size: 24rpx; color: #727272; margin-top: 8rpx; }
.dict-tip { display: block; text-align: center; color: #727272; padding: 60rpx 0; font-size: 26rpx; }
</style>