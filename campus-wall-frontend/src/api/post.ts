import request from '@/utils/request'

// 创建帖子
export function createPost(data: {
  title: string
  content: string
  category: string
  images?: string[]
  isAnonymous?: boolean
}) {
  return request({
    url: '/post',
    method: 'post',
    data
  })
}

// 查询帖子列表
export function queryPosts(data: {
  category?: string
  keyword?: string
  userId?: number
  sortBy?: string
  sortOrder?: string
  page?: number
  pageSize?: number
}) {
  return request({
    url: '/post/query',
    method: 'post',
    data
  })
}

// 获取帖子详情
export function getPostById(id: number) {
  return request({
    url: `/post/${id}`,
    method: 'get'
  })
}

// 更新帖子
export function updatePost(id: number, data: any) {
  return request({
    url: `/post/${id}`,
    method: 'put',
    data
  })
}

// 删除帖子
export function deletePost(id: number) {
  return request({
    url: `/post/${id}`,
    method: 'delete'
  })
}

// 点赞帖子
export function likePost(id: number) {
  return request({
    url: `/post/${id}/like`,
    method: 'post'
  })
}

// 取消点赞
export function unlikePost(id: number) {
  return request({
    url: `/post/${id}/like`,
    method: 'delete'
  })
}

// 收藏帖子
export function favoritePost(id: number) {
  return request({
    url: `/post/${id}/favorite`,
    method: 'post'
  })
}

// 取消收藏
export function unfavoritePost(id: number) {
  return request({
    url: `/post/${id}/favorite`,
    method: 'delete'
  })
}

// 获取收藏列表
export function getFavoritePosts(page = 1, pageSize = 10) {
  return request({
    url: '/post/favorites',
    method: 'get',
    params: { page, pageSize }
  })
}

// 获取用户发布的所有帖子
export function getUserPosts(userId: number, page = 1, pageSize = 10) {
  return request({
    url: `/post/user/${userId}`,
    method: 'get',
    params: { page, pageSize }
  })
}
