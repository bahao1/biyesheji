<template>
  <div class="image-upload-container">
    <el-upload
      :action="uploadUrl"
      :headers="uploadHeaders"
      :file-list="fileList"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-remove="handleRemove"
      :before-upload="beforeUpload"
      :limit="limit"
      :on-exceed="handleExceed"
      list-type="picture-card"
      :disabled="disabled"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

interface Props {
  modelValue?: string[]
  limit?: number
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  limit: 9,
  disabled: false
})

const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()
const API_BASE_URL = 'http://localhost:8080/api'
const uploadUrl = API_BASE_URL + '/file/upload'

const uploadHeaders = {
  Authorization: `Bearer ${userStore.token}`
}

const fileList = ref<any[]>([])

// 监听props变化
watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal.length > 0) {
    fileList.value = newVal.map((url, index) => ({
      uid: index,
      name: `image-${index}`,
      url: url.startsWith('http') ? url : API_BASE_URL + url
    }))
  } else {
    fileList.value = []
  }
}, { immediate: true })

// 上传前校验
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

// 上传成功
const handleSuccess = (response: any, file: any, files: any[]) => {
  if (response.code === 200) {
    const urls = files.map(f => {
      if (f.response) {
        return f.response.data
      }
      // 处理已有的URL
      return f.url.replace(API_BASE_URL, '')
    })
    emit('update:modelValue', urls)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败
const handleError = (error: any) => {
  console.error('上传失败:', error)
  ElMessage.error('上传失败，请重试')
}

// 移除图片
const handleRemove = (file: any, files: any[]) => {
  const urls = files.map(f => {
    if (f.response) {
      return f.response.data
    }
    return f.url.replace(API_BASE_URL, '')
  })
  emit('update:modelValue', urls)
}

// 超出限制
const handleExceed = () => {
  ElMessage.warning(`最多只能上传 ${props.limit} 张图片`)
}
</script>

<style scoped>
.image-upload-container {
  width: 100%;
}

:deep(.el-upload-list--picture-card) {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.el-upload--picture-card) {
  width: 148px;
  height: 148px;
  border-radius: 8px;
  transition: all 0.3s;
}

:deep(.el-upload--picture-card:hover) {
  border-color: #409eff;
}

:deep(.el-upload-list__item) {
  width: 148px;
  height: 148px;
  border-radius: 8px;
}
</style>
