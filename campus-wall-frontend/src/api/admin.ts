import request from '@/utils/request'

/**
 * 管理员相关API
 */

// 获取系统统计数据
export const getStatistics = () => {
  return request({
    url: '/admin/statistics',
    method: 'get'
  })
}

// 查询用户列表
export const getUsers = (params: any) => {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

// 更新用户状态
export const updateUserStatus = (userId: number, disabled: boolean) => {
  return request({
    url: `/admin/users/${userId}/status`,
    method: 'put',
    params: { disabled }
  })
}

// 查询所有帖子
export const getPosts = (params: any) => {
  return request({
    url: '/admin/posts',
    method: 'get',
    params
  })
}

// 删除帖子
export const deletePost = (postId: number) => {
  return request({
    url: `/admin/posts/${postId}`,
    method: 'delete'
  })
}

// 查询所有商品
export const getProducts = (params: any) => {
  return request({
    url: '/admin/products',
    method: 'get',
    params
  })
}

// 删除商品
export const deleteProduct = (productId: number) => {
  return request({
    url: `/admin/products/${productId}`,
    method: 'delete'
  })
}

// 查询所有评论
export const getComments = (params: any) => {
  return request({
    url: '/admin/comments',
    method: 'get',
    params
  })
}

// 删除评论
export const deleteComment = (commentId: number) => {
  return request({
    url: `/admin/comments/${commentId}`,
    method: 'delete'
  })
}

// 查询所有社团
export const getClubs = (params: any) => {
  return request({
    url: '/admin/clubs',
    method: 'get',
    params
  })
}

// 创建社团
export const createClub = (data: any) => {
  return request({
    url: '/admin/clubs',
    method: 'post',
    data
  })
}

// 删除社团
export const deleteClub = (clubId: number) => {
  return request({
    url: `/admin/clubs/${clubId}`,
    method: 'delete'
  })
}

// 分配社团管理员
export const assignClubAdmin = (userId: number, clubId: number) => {
  return request({
    url: `/admin/users/${userId}/assign-club-admin`,
    method: 'put',
    params: { clubId }
  })
}

// 取消社团管理员
export const removeClubAdmin = (userId: number) => {
  return request({
    url: `/admin/users/${userId}/remove-club-admin`,
    method: 'put'
  })
}
