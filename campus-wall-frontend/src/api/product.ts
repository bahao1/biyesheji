import request from '@/utils/request'

/**
 * 商品相关API
 */

// 创建商品
export const createProduct = (data: any) => {
  return request({
    url: '/product/create',
    method: 'post',
    data
  })
}

// 查询商品列表
export const queryProducts = (params: any) => {
  return request({
    url: '/product/query',
    method: 'get',
    params
  })
}

// 获取商品详情
export const getProductById = (id: number) => {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

// 更新商品
export const updateProduct = (id: number, data: any) => {
  return request({
    url: `/product/${id}`,
    method: 'put',
    data
  })
}

// 删除商品
export const deleteProduct = (id: number) => {
  return request({
    url: `/product/${id}`,
    method: 'delete'
  })
}

// 更新商品状态
export const updateProductStatus = (id: number, status: string) => {
  return request({
    url: `/product/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取用户的商品列表
export const getUserProducts = (userId: number, params: any) => {
  return request({
    url: `/product/user/${userId}`,
    method: 'get',
    params
  })
}
