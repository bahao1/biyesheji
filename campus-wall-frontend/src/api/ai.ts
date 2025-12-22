import request from '@/utils/request'

/**
 * AI功能相关API
 */

// 生成表白情书
export const generateLoveLetter = (data: {
  targetName: string
  keywords: string[]
  style: string
}) => {
  return request({
    url: '/ai/love-letter',
    method: 'post',
    data
  })
}

// 生成藏头诗
export const generateAcrosticPoem = (data: { text: string }) => {
  return request({
    url: '/ai/acrostic-poem',
    method: 'post',
    data
  })
}

// AI聊天助手
export const chatAssistant = (data: {
  question: string
  history: Array<{ role: string; content: string }>
}) => {
  return request({
    url: '/ai/chat',
    method: 'post',
    data
  })
}
