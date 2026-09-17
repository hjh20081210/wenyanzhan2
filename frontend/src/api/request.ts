import { useUserStore } from '@/store/user'

interface Result<T = any> {
  code: number
  message: string
  data: T
}

/** 通用请求封装 */
export function request<T = any>(options: UniApp.RequestOptions): Promise<T> {
  return new Promise((resolve, reject) => {
    const userStore = useUserStore()
    uni.request({
      url: options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...(userStore.token ? { Authorization: 'Bearer ' + userStore.token } : {}),
        ...(options.header || {}),
      },
      success: (res) => {
        const body = res.data as Result<T>
        if (body && body.code === 200) {
          resolve(body.data)
        } else if (body && body.code === 401) {
          userStore.logout()
          uni.navigateTo({ url: '/pages/login/login' })
          reject(body)
        } else {
          uni.showToast({ title: body?.message || '请求失败', icon: 'none' })
          reject(body)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络异常', icon: 'none' })
        reject(err)
      },
    })
  })
}

export const get = <T = any>(url: string, data?: any) => request<T>({ url, method: 'GET', data })
export const post = <T = any>(url: string, data?: any) => request<T>({ url, method: 'POST', data })
export const del = <T = any>(url: string, data?: any) => request<T>({ url, method: 'DELETE', data })