import request from '@/utils/request'

/**
 * 社团相关API
 */

// 查询社团列表
export const queryClubs = (params: any) => {
  return request({
    url: '/club/query',
    method: 'get',
    params
  })
}

// 查询招新列表
export const queryRecruitments = (params: any) => {
  return request({
    url: '/club/recruitment/query',
    method: 'get',
    params
  })
}

// 获取招新详情
export const getRecruitmentById = (id: number) => {
  return request({
    url: `/club/recruitment/${id}`,
    method: 'get'
  })
}

// 创建招新信息
export const createRecruitment = (data: any) => {
  return request({
    url: '/club/recruitment/create',
    method: 'post',
    data
  })
}

// 更新招新信息
export const updateRecruitment = (id: number, data: any) => {
  return request({
    url: `/club/recruitment/${id}`,
    method: 'put',
    data
  })
}

// 删除招新信息
export const deleteRecruitment = (id: number) => {
  return request({
    url: `/club/recruitment/${id}`,
    method: 'delete'
  })
}

// 获取我管理的社团
export const getMyClub = () => {
  return request({
    url: '/club/my-club',
    method: 'get'
  })
}
