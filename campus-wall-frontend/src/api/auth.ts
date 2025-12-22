import request from '@/utils/request'

// 用户注册
export function register(data: {
  username: string
  password: string
  nickname: string
  phone?: string
  email?: string
}) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 用户登录
export function login(data: { username: string; password: string }) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 获取当前用户信息
export function getCurrentUser() {
  return request({
    url: '/user/current',
    method: 'get'
  })
}
