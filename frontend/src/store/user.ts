import { defineStore } from 'pinia'

interface UserState {
  token: string
  userId: number
  nickname: string
  avatar: string
  isFirstInit: number
  // UI偏好（本地缓存）
  fontType: string
  bgType: string
  theme: 'paper' | 'night'
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: '',
    userId: 0,
    nickname: '',
    avatar: '',
    isFirstInit: 1,
    fontType: 'system',
    bgType: 'paper',
    theme: 'paper',
  }),
  actions: {
    /** 从存储恢复状态 */
    init() {
      this.token = uni.getStorageSync('token') || ''
      this.userId = uni.getStorageSync('userId') || 0
      this.nickname = uni.getStorageSync('nickname') || ''
      this.avatar = uni.getStorageSync('avatar') || ''
      this.isFirstInit = uni.getStorageSync('isFirstInit') ?? 1
      // UI偏好
      const ui = uni.getStorageSync('uiPrefs')
      if (ui) {
        this.fontType = ui.fontType || 'system'
        this.bgType = ui.bgType || 'paper'
        this.theme = ui.theme || 'paper'
      }
    },
    setLogin(data: { token: string; userId: number; nickname?: string; avatar?: string; isFirstInit?: number }) {
      this.token = data.token
      this.userId = data.userId
      this.nickname = data.nickname || ''
      this.avatar = data.avatar || ''
      if (data.isFirstInit !== undefined) this.isFirstInit = data.isFirstInit
      uni.setStorageSync('token', this.token)
      uni.setStorageSync('userId', this.userId)
      uni.setStorageSync('nickname', this.nickname)
      uni.setStorageSync('avatar', this.avatar)
      uni.setStorageSync('isFirstInit', this.isFirstInit)
    },
    logout() {
      this.token = ''
      this.userId = 0
      uni.removeStorageSync('token')
      uni.removeStorageSync('userId')
    },
    setTheme(theme: 'paper' | 'night') {
      this.theme = theme
      this.bgType = theme === 'night' ? 'night' : 'paper'
      this.saveUiPrefs()
    },
    setFont(fontType: string) {
      this.fontType = fontType
      this.saveUiPrefs()
    },
    saveUiPrefs() {
      uni.setStorageSync('uiPrefs', { fontType: this.fontType, bgType: this.bgType, theme: this.theme })
    },
    get isLoggedIn() {
      return !!this.token
    },
  },
})