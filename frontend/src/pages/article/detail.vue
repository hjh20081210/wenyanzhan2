<template>
  <view class="page">
    <NavBar :title="'篇目详情'" :back="true" />
    <scroll-view scroll-y class="detail-scroll">
      <view class="detail-header" :style="{ backgroundColor: '#f5f1e8' }">
        <text class="d-title font-xu">{{ article?.title }}</text>
        <text class="d-meta">{{ article?.dynasty }} · {{ article?.author }}</text>
        <text class="d-genre" v-if="article?.genre">{{ article.genre }}</text>
      </view>

      <view class="section">
        <text class="section-label">原文</text>
        <view class="original font-xu">
          {{ article?.content || '（原文数据）' }}
        </view>
      </view>

      <view class="section" v-if="article?.translate">
        <text class="section-label">译文</text>
        <view class="translate">
          {{ article.translate }}
        </view>
      </view>

      <view class="section" v-if="article?.appreciate">
        <text class="section-label">赏析</text>
        <view class="appreciate">
          {{ article.appreciate }}
        </view>
      </view>

      <view class="btn-row">
        <view class="start-btn" @tap="readArticle">进入阅读</view>
        <view class="fav-btn" @tap="toggleFav">{{ favorite ? '已收藏' : '收藏' }}</view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onLoad } from '@dcloudio/uni-app'
import { articleApi } from '@/api'
import NavBar from '@/components/NavBar.vue'

const article = ref<any>(null)
const favorite = ref(false)
const articleId = ref<number>(0)

function readArticle() {
  uni.navigateTo({ url: `/pages/article/read?articleId=${articleId.value}` })
}
function toggleFav() {
  if (!favorite.value) {
    articleApi.favorite(articleId.value).then(() => { favorite.value = true; uni.showToast({ title: '已收藏', icon: 'none' }) })
  } else {
    articleApi.unFavorite(articleId.value).then(() => { favorite.value = false; uni.showToast({ title: '已取消收藏', icon: 'none' }) })
  }
}

onLoad((q: any) => {
  articleId.value = Number(q?.articleId || 1)
  articleApi.detail(articleId.value).then((a) => { article.value = a }).catch(() => {
    article.value = {
      title: '爱莲说', dynasty: '宋', author: '周敦颐', genre: '散文',
      content: '水陆草木之花，可爱者甚蕃。晋陶渊明独爱菊。自李唐来，世人甚爱牡丹。予独爱莲之出淤泥而不染……',
      translate: '水上陆上各种草木的花……（译文）',
      appreciate: '本文托物言志，借莲之形象展现作者洁身自好、不慕名利的高尚品格……',
    }
  })
})
</script>

<style lang="scss" scoped>
.page { background-color: #f5f1e8; min-height: 100vh; }
.detail-scroll { padding: 0 30rpx 40rpx; height: calc(100vh - 120rpx); }
.detail-header { text-align: center; padding: 40rpx 0 30rpx; }
.d-title { font-size: 52rpx; color: #2c2c2c; }
.d-meta { display: block; font-size: 26rpx; color: #9b5b3a; margin-top: 12rpx; }
.d-genre { display: inline-block; margin-top: 12rpx; font-size: 22rpx; color: #9b5b3a; background: rgba(155,91,58,0.08); padding: 6rpx 20rpx; border-radius: 24rpx; }
.section { margin-top: 26rpx; }
.section-label { font-size: 26rpx; color: #9b5b3a; font-weight: 600; display: block; margin-bottom: 14rpx; padding-left: 10rpx; border-left: 4rpx solid #9b5b3a; }
.original { font-size: 34rpx; color: #2c2c2c; line-height: 2; padding: 26rpx; background: rgba(255,255,255,0.8); border-radius: 16rpx; }
.translate, .appreciate { font-size: 30rpx; color: #555; line-height: 1.9; padding: 24rpx; background: rgba(255,255,255,0.6); border-radius: 16rpx; }
.btn-row { display: flex; gap: 20rpx; margin-top: 40rpx; }
.start-btn { flex: 3; text-align: center; height: 96rpx; line-height: 96rpx; background: #9b5b3a; color: #f5f1e8; border-radius: 48rpx; font-size: 32rpx; font-weight: 600; }
.fav-btn { flex: 1; text-align: center; height: 96rpx; line-height: 96rpx; background: rgba(255,255,255,0.9); color: #9b5b3a; border-radius: 48rpx; font-size: 30rpx; border: 2rpx solid #9b5b3a; }
</style>