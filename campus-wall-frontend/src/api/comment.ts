import request from '@/utils/request'

// 创建评论
export function createComment(data: {
  postId: number
  content: string
  parentId?: number
  replyToUserId?: number
}) {
  return request({
    url: '/comment',
    method: 'post',
    data
  })
}

// 删除评论
export function deleteComment(id: number) {
  return request({
    url: `/comment/${id}`,
    method: 'delete'
  })
}

// 获取帖子的评论列表（分页）
export function getCommentsByPostId(postId: number, page = 1, pageSize = 10) {
  return request({
    url: `/comment/post/${postId}`,
    method: 'get',
    params: { page, pageSize }
  })
}

// 获取帖子的所有评论（树形结构）
export function getCommentsTree(postId: number) {
  return request({
    url: `/comment/post/${postId}/tree`,
    method: 'get'
  })
}

// 获取当前用户的评论列表
export function getCommentsByUserId(params: {
  sortBy?: string
  sortOrder?: string
  page?: number
  pageSize?: number
}) {
  return request({
    url: '/comment/user',
    method: 'get',
    params
  })
}
