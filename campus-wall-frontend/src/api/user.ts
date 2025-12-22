import request from '@/utils/request'

/**
 * 用户相关API
 */

// 更新用户信息
export const updateUser = (id: number, data: any) => {
  return request({
    url: `/user/${id}`,
    method: 'put',
    data
  })
}

// 修改密码
export const changePassword = (data: { oldPassword: string; newPassword: string }) => {
  return request({
    url: '/user/change-password',
    method: 'post',
    data
  })
}

// 获取用户信息
export const getUserById = (id: number) => {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}
