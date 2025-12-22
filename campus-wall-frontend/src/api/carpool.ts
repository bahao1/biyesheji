import request from '@/utils/request'

/**
 * 拼车相关API
 */

// 创建拼车信息
export const createCarpool = (data: any) => {
  return request({
    url: '/carpool',
    method: 'post',
    data
  })
}

// 更新拼车信息
export const updateCarpool = (id: number, data: any) => {
  return request({
    url: `/carpool/${id}`,
    method: 'put',
    data
  })
}

// 删除拼车信息
export const deleteCarpool = (id: number) => {
  return request({
    url: `/carpool/${id}`,
    method: 'delete'
  })
}

// 获取拼车详情
export const getCarpoolById = (id: number) => {
  return request({
    url: `/carpool/${id}`,
    method: 'get'
  })
}

// 查询拼车列表
export const queryCarpools = (params: any) => {
  return request({
    url: '/carpool/query',
    method: 'get',
    params
  })
}

// 获取我的拼车列表
export const getMyCarpools = (params: any) => {
  return request({
    url: '/carpool/my',
    method: 'get',
    params
  })
}

// 参与拼车
export const joinCarpool = (id: number) => {
  return request({
    url: `/carpool/${id}/join`,
    method: 'post'
  })
}

// 取消参与拼车
export const cancelJoinCarpool = (id: number) => {
  return request({
    url: `/carpool/${id}/cancel`,
    method: 'post'
  })
}
