import request from '@/utils/request'

/**
 * 私信消息相关API
 */

// 发送私信
export const sendMessage = (data: {
  receiverId: number
  content: string
}) => {
  return request({
    url: '/messages/send',
    method: 'post',
    data
  })
}

// 获取聊天记录
export const getChatHistory = (userId: number) => {
  return request({
    url: `/messages/chat/${userId}`,
    method: 'get'
  })
}

// 获取会话列表
export const getConversationList = () => {
  return request({
    url: '/messages/conversations',
    method: 'get'
  })
}

// 标记消息为已读
export const markAsRead = (userId: number) => {
  return request({
    url: `/messages/read/${userId}`,
    method: 'put'
  })
}

// 获取未读消息总数
export const getUnreadCount = () => {
  return request({
    url: '/messages/unread-count',
    method: 'get'
  })
}

// 获取与某用户的未读消息数
export const getUnreadCountWithUser = (userId: number) => {
  return request({
    url: `/messages/unread-count/${userId}`,
    method: 'get'
  })
}
