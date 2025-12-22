import request from '@/utils/request'

/**
 * 文件上传相关API
 */

// 上传文件
export const uploadFile = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: '/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除文件
export const deleteFile = (url: string) => {
  return request({
    url: '/file',
    method: 'delete',
    params: { url }
  })
}
