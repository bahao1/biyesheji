<template>
  <div class="product-detail-container">
    <!-- 返回按钮 -->
    <el-button @click="$router.back()" class="back-btn" circle>
      <el-icon><ArrowLeft /></el-icon>
    </el-button>

    <!-- 商品详情 -->
    <el-card v-loading="loading" class="product-card" shadow="hover">
      <template v-if="product">
        <div class="product-main">
          <!-- 左侧图片区域 -->
          <div class="product-images">
            <div class="main-image">
              <el-image
                :src="getImageUrl(product.images)"
                fit="contain"
                class="image"
                :preview-src-list="getPreviewImages(product.images)"
              >
                <template #default>
                  <div class="image-loading">
                    <el-icon><Loading /></el-icon>
                    <span>加载中...</span>
                  </div>
                </template>
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                    <span>暂无图片</span>
                  </div>
                </template>
              </el-image>
            </div>
          </div>

          <!-- 右侧商品信息 -->
          <div class="product-info-section">
            <!-- 标题和状态 -->
            <div class="title-section">
              <h1 class="product-title">{{ product.title }}</h1>
              <el-tag
                v-if="product.status === 'SOLD'"
                type="info"
                size="large"
              >
                已售出
              </el-tag>
              <el-tag
                v-else-if="product.status === 'ON_SALE'"
                type="success"
                size="large"
              >
                在售
              </el-tag>
            </div>

            <!-- 价格区域 -->
            <div class="price-section">
              <div class="current-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ product.price }}</span>
              </div>
              <div v-if="product.originalPrice" class="original-price">
                原价 ¥{{ product.originalPrice }}
              </div>
            </div>

            <!-- 商品信息列表 -->
            <div class="info-list">
              <div class="info-item" v-if="product.condition">
                <div class="info-label">
                  <el-icon><Star /></el-icon>
                  <span>成色</span>
                </div>
                <div class="info-value">{{ product.condition }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">
                  <el-icon><Grid /></el-icon>
                  <span>分类</span>
                </div>
                <div class="info-value">{{ getCategoryName(product.category) }}</div>
              </div>
              <div class="info-item" v-if="product.location">
                <div class="info-label">
                  <el-icon><Location /></el-icon>
                  <span>交易地点</span>
                </div>
                <div class="info-value">{{ product.location }}</div>
              </div>
              <div class="info-item" v-if="product.contact">
                <div class="info-label">
                  <el-icon><Phone /></el-icon>
                  <span>联系方式</span>
                </div>
                <div class="info-value">{{ product.contact }}</div>
              </div>
            </div>

            <!-- 卖家信息 -->
            <div class="seller-section">
              <div class="seller-header">
                <el-icon><User /></el-icon>
                <span>卖家信息</span>
              </div>
              <div class="seller-info">
                <el-avatar :size="48">{{ product.sellerNickname?.charAt(0) }}</el-avatar>
                <div class="seller-details">
                  <div class="seller-name">{{ product.sellerNickname }}</div>
                  <div class="seller-time">发布于 {{ formatTime(product.createTime) }}</div>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="action-buttons">
              <el-button
                type="primary"
                size="large"
                round
                :disabled="product.status === 'SOLD'"
                @click="contactSeller"
              >
                <el-icon><ChatDotRound /></el-icon>
                联系卖家
              </el-button>
              <el-button size="large" round>
                <el-icon><Star /></el-icon>
                收藏
              </el-button>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 商品描述 -->
        <div class="description-section">
          <h3 class="section-title">
            <el-icon><Document /></el-icon>
            商品描述
          </h3>
          <div class="description-content">
            {{ product.description || '暂无描述' }}
          </div>
        </div>

        <!-- 浏览统计 -->
        <div class="stats-section">
          <div class="stat-item">
            <el-icon><View /></el-icon>
            <span>{{ product.viewCount }} 次浏览</span>
          </div>
          <div class="stat-item">
            <el-icon><Clock /></el-icon>
            <span>发布于 {{ formatDate(product.createTime) }}</span>
          </div>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductById } from '@/api/product'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const product = ref<any>(null)

const API_BASE_URL = 'http://localhost:8080/api'

// 获取图片URL
const getImageUrl = (images: any) => {
  // 记录原始images数据
  console.log('ProductDetail - Raw images:', images, 'Type:', typeof images)
  
  // 处理空值情况
  if (!images) {
    console.log('ProductDetail - No images provided')
    return ''
  }
  
  let firstImage = ''
  
  // 处理字符串格式的images
  if (typeof images === 'string') {
    // 直接获取第一个图片路径
    const imageArray = images.split(',').map((img: string) => img.trim()).filter(Boolean)
    console.log('ProductDetail - Split images:', imageArray)
    firstImage = imageArray[0] || ''
  } 
  // 处理数组格式的images
  else if (Array.isArray(images)) {
    const trimmedImages = images.map((img: any) => String(img).trim()).filter(Boolean)
    console.log('ProductDetail - Array images after trimming:', trimmedImages)
    firstImage = trimmedImages[0] || ''
  } 
  // 处理其他格式
  else {
    try {
      // 尝试转换为字符串
      const strImages = String(images).trim()
      console.log('ProductDetail - Converted to string:', strImages)
      if (strImages) {
        const imageArray = strImages.split(',').map((img: string) => img.trim()).filter(Boolean)
        console.log('ProductDetail - Split converted images:', imageArray)
        firstImage = imageArray[0] || ''
      }
    } catch (e) {
      console.error('ProductDetail - Error processing images:', e)
      firstImage = ''
    }
  }
  
  // 处理空的firstImage
  if (!firstImage) {
    console.log('ProductDetail - No first image found')
    return ''
  }
  
  console.log('ProductDetail - First image:', firstImage)
  
  // 直接使用完整的http URL，不添加任何前缀
  let imageUrl = ''
  
  // 如果已经是完整的URL，直接返回
  if (firstImage.startsWith('http')) {
    imageUrl = firstImage
  } 
  // 否则，使用http://localhost:8080/api + 路径
  else {
    // 确保路径以/开头
    const normalizedPath = firstImage.startsWith('/') ? firstImage : `/${firstImage}`
    imageUrl = `http://localhost:8080/api${normalizedPath}`
  }
  
  console.log('ProductDetail - Generated image URL:', imageUrl)
  return imageUrl
}

// 获取图片预览列表
const getPreviewImages = (images: any) => {
  if (!images) return []
  
  let imagesArray: string[] = []
  
  // 处理字符串格式的images（逗号分隔）
  if (typeof images === 'string') {
    imagesArray = images.split(',').map((img: string) => img.trim()).filter(Boolean)
  } 
  // 处理数组格式的images
  else if (Array.isArray(images)) {
    imagesArray = images.map((img: any) => String(img).trim()).filter(Boolean)
  } else {
    imagesArray = [String(images).trim()]
  }

  // 生成完整的URL
  return imagesArray.map(img => {
    if (img.startsWith('http')) {
      return img
    }
    const normalizedPath = img.startsWith('/') ? img : `/${img}`
    return `http://localhost:8080/api${normalizedPath}`
  })
}

const loadProduct = async () => {
  loading.value = true
  try {
    const res = await getProductById(Number(route.params.id))
    product.value = res.data
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

// 联系卖家
const contactSeller = () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!product.value?.userId) {
    ElMessage.error('无法获取卖家信息')
    return
  }

  // 跳转到私信页面,并传递卖家ID
  router.push({
    path: '/messages',
    query: { userId: product.value.userId }
  })
}

const getCategoryName = (category: string) => {
  const map: Record<string, string> = {
    BOOKS: '图书教材',
    ELECTRONICS: '电子产品',
    DAILY_GOODS: '生活用品',
    SPORTS: '运动器材',
    STATIONERY: '文具用品',
    CLOTHES: '服装鞋帽',
    OTHERS: '其他'
  }
  return map[category] || category
}

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`

  return date.toLocaleDateString()
}

const formatDate = (time: string) => {
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
  position: relative;
}

.back-btn {
  position: fixed;
  top: 80px;
  left: 20px;
  z-index: 100;
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.back-btn:hover {
  background: #f5f7fa;
}

.product-card {
  border-radius: 16px;
  overflow: hidden;
}

.product-main {
  display: grid;
  grid-template-columns: 500px 1fr;
  gap: 40px;
  margin-bottom: 32px;
}

.product-images {
  position: sticky;
  top: 100px;
  height: fit-content;
}

.main-image {
  width: 100%;
  height: 500px;
  background: #f5f7fa;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image {
  width: 100%;
  height: 100%;
}

.image-placeholder,
.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #909399;
  font-size: 16px;
}

.image-placeholder .el-icon,
.image-error .el-icon {
  font-size: 64px;
}

.product-info-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.title-section {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.product-title {
  flex: 1;
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.4;
}

.price-section {
  padding: 20px;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe8e8 100%);
  border-radius: 12px;
  border: 2px solid #ffa5a5;
}

.current-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.price-symbol {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
  margin-right: 4px;
}

.price-value {
  font-size: 42px;
  font-weight: 700;
  color: #f56c6c;
}

.original-price {
  font-size: 16px;
  color: #909399;
  text-decoration: line-through;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #606266;
  font-weight: 500;
}

.info-value {
  font-size: 15px;
  color: #303133;
  font-weight: 600;
}

.seller-section {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;
}

.seller-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.seller-details {
  flex: 1;
}

.seller-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.seller-time {
  font-size: 13px;
  color: #909399;
}

.action-buttons {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 12px;
}

.action-buttons .el-button {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 16px 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.description-content {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

.stats-section {
  display: flex;
  gap: 24px;
  padding-top: 16px;
  margin-top: 16px;
  border-top: 1px solid #ebeef5;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #909399;
}

@media (max-width: 968px) {
  .product-main {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .main-image {
    height: 400px;
  }

  .product-images {
    position: static;
  }

  .action-buttons {
    grid-template-columns: 1fr;
  }
}
</style>
