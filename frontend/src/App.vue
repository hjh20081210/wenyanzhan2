<script setup lang="ts">
import { onLaunch } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'

onLaunch(() => {
  const userStore = useUserStore()
  // 从本地读取token与UI偏好
  userStore.init()
  console.log('文言斩启动')
})
</script>

<style lang="scss">
/* 页面根容器：宣纸或夜间底色，交给组件按主题设置 */
page {
  background-color: #f5f1e8;
  font-family: 'system', '霞鹭文楷', 'KaiTi', serif;
}
</style>