<template>
  <div class="marketplace-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">🛒 二手市场</h1>
        <div class="header-search">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品..."
            class="search-input"
            @keyup.enter="handleSearch"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="$router.push('/create-product')" round v-if="userStore.token">
            <el-icon><Plus /></el-icon>
            <span>发布商品</span>
          </el-button>
          <el-button @click="$router.push('/')" round>
            <el-icon><House /></el-icon>
            <span>返回首页</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-container">
      <!-- 左侧分类导航 -->
      <el-aside width="220px" class="sidebar">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Grid /></el-icon>
              <span>商品分类</span>
            </div>
          </template>
          <el-menu :default-active="activeCategory" @select="handleCategoryChange" class="category-menu">
            <el-menu-item index="">
              <el-icon><List /></el-icon>
              <span>全部商品</span>
            </el-menu-item>
            <el-menu-item index="BOOKS">
              <el-icon><Document /></el-icon>
              <span>图书教材</span>
            </el-menu-item>
            <el-menu-item index="ELECTRONICS">
              <el-icon><Cpu /></el-icon>
              <span>电子产品</span>
            </el-menu-item>
            <el-menu-item index="DAILY_GOODS">
              <el-icon><ShoppingBag /></el-icon>
              <span>生活用品</span>
            </el-menu-item>
            <el-menu-item index="SPORTS">
              <el-icon><Football /></el-icon>
              <span>运动器材</span>
            </el-menu-item>
            <el-menu-item index="STATIONERY">
              <el-icon><EditPen /></el-icon>
              <span>文具用品</span>
            </el-menu-item>
            <el-menu-item index="CLOTHES">
              <el-icon><Grid /></el-icon>
              <span>服装鞋帽</span>
            </el-menu-item>
            <el-menu-item index="OTHERS">
              <el-icon><MoreFilled /></el-icon>
              <span>其他</span>
            </el-menu-item>
          </el-menu>
        </el-card>

        <!-- 筛选条件 -->
        <el-card shadow="hover" class="filter-card" style="margin-top: 16px">
          <template #header>
            <div class="card-header">
              <el-icon><Filter /></el-icon>
              <span>筛选条件</span>
            </div>
          </template>
          <div class="filter-section">
            <div class="filter-label">商品状态</div>
            <el-radio-group v-model="activeStatus" @change="handleStatusChange" size="small">
              <el-radio-button value="">全部</el-radio-button>
              <el-radio-button value="ON_SALE">在售</el-radio-button>
            </el-radio-group>
          </div>
        </el-card>
      </el-aside>

      <!-- 主内容区 -->
      <div class="main-content">
        <!-- 排序栏 -->
        <el-card shadow="never" class="filter-bar-card">
          <div class="filter-bar">
            <div class="filter-left">
              <el-radio-group v-model="sortBy" @change="handleSortChange" size="small">
                <el-radio-button value="createTime">最新</el-radio-button>
                <el-radio-button value="price">价格</el-radio-button>
                <el-radio-button value="viewCount">浏览最多</el-radio-button>
              </el-radio-group>
            </div>
            <div class="filter-right">
              <span class="total-count">共 {{ total }} 件商品</span>
            </div>
          </div>
        </el-card>

        <!-- 商品列表 -->
        <div v-loading="loading" class="product-list">
          <el-empty v-if="!loading && productList.length === 0" description="暂无商品" />

          <div class="product-grid">
            <el-card
              v-for="product in productList"
              :key="product.id"
              class="product-card"
              shadow="hover"
              @click="goToDetail(product.id)"
            >
              <!-- 商品图片 -->
              <div class="product-image">
                <!-- 商品图片 -->
                <div class="product-image-wrapper">
                  <!-- 简化的图片显示逻辑，直接使用getImageUrl函数处理 -->
                  <el-image
                    :src="getImageUrl(product.images)"
                    fit="cover"
                    class="image"
                  >
                    <template #default>
                      <!-- 正在加载时显示的内容 -->
                      <div class="image-slot">
                        <el-icon><Loading /></el-icon>
                      </div>
                    </template>
                    <template #error>
                      <!-- 加载失败时显示的内容 -->
                      <div class="image-placeholder">
                        <el-icon><Picture /></el-icon>
                        <span>暂无图片</span>
                      </div>
                    </template>
                  </el-image>
                </div>
                <el-tag
                  v-if="product.status === 'SOLD'"
                  type="info"
                  class="status-tag"
                >
                  已售出
                </el-tag>
              </div>

              <!-- 商品信息 -->
              <div class="product-info">
                <h3 class="product-title">{{ product.title }}</h3>
                <p class="product-desc">{{ product.description }}</p>

                <div class="product-price-section">
                  <div class="price-main">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ product.price }}</span>
                  </div>
                  <div v-if="product.originalPrice" class="price-original">
                    ¥{{ product.originalPrice }}
                  </div>
                </div>

                <div class="product-footer">
                  <div class="seller-info">
                    <el-avatar :size="24">{{ product.sellerNickname?.charAt(0) }}</el-avatar>
                    <span class="seller-name">{{ product.sellerNickname }}</span>
                  </div>
                  <div class="product-stats">
                    <span class="stat-item">
                      <el-icon><View /></el-icon>
                      {{ product.viewCount }}
                    </span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-if="total > 0"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="loadProducts"
            @size-change="loadProducts"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { queryProducts } from '@/api/product'

const router = useRouter()
const userStore = useUserStore()

const API_BASE_URL = 'http://localhost:8080/api'

// 获取图片URL
const getImageUrl = (images: any) => {
  // 记录原始images数据
  console.log('Marketplace - Raw images:', images, 'Type:', typeof images)
  
  // 处理空值情况
  if (!images) {
    console.log('Marketplace - No images provided')
    return ''
  }
  
  let firstImage = ''
  
  // 处理字符串格式的images
  if (typeof images === 'string') {
    // 去除首尾空格
    const trimmedImages = images.trim()
    if (!trimmedImages) {
      console.log('Marketplace - Images string is empty after trimming')
      return ''
    }
    
    // 直接获取第一个图片路径，不处理JSON字符串
    const imageArray = trimmedImages.split(',').map((img: string) => img.trim()).filter(Boolean)
    console.log('Marketplace - Split images:', imageArray)
    firstImage = imageArray[0] || ''
  } 
  // 处理数组格式的images
  else if (Array.isArray(images)) {
    const trimmedImages = images.map((img: any) => String(img).trim()).filter(Boolean)
    console.log('Marketplace - Array images after trimming:', trimmedImages)
    firstImage = trimmedImages[0] || ''
  } 
  // 处理其他格式
  else {
    try {
      // 尝试转换为字符串
      const strImages = String(images).trim()
      console.log('Marketplace - Converted to string:', strImages)
      if (strImages) {
        const imageArray = strImages.split(',').map((img: string) => img.trim()).filter(Boolean)
        console.log('Marketplace - Split converted images:', imageArray)
        firstImage = imageArray[0] || ''
      }
    } catch (e) {
      console.error('Marketplace - Error processing images:', e)
      firstImage = ''
    }
  }
  
  // 处理空的firstImage
  if (!firstImage) {
    console.log('Marketplace - No first image found')
    return ''
  }
  
  console.log('Marketplace - First image:', firstImage)
  
  // 直接使用完整的http URL，不添加任何前缀
  // 这里假设firstImage已经是完整的URL，或者是uploads/开头的路径
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
  
  console.log('Marketplace - Generated image URL:', imageUrl)
  return imageUrl
}

const loading = ref(false)
const productList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const activeCategory = ref('')
const activeStatus = ref('')
const searchKeyword = ref('')
const sortBy = ref('createTime')

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await queryProducts({
      category: activeCategory.value || undefined,
      status: activeStatus.value || undefined,
      keyword: searchKeyword.value || undefined,
      sortBy: sortBy.value,
      sortOrder: 'desc',
      page: currentPage.value,
      pageSize: pageSize.value
    })
    productList.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

const handleCategoryChange = (index: string) => {
  activeCategory.value = index
  currentPage.value = 1
  loadProducts()
}

const handleStatusChange = () => {
  currentPage.value = 1
  loadProducts()
}

const handleSearch = () => {
  currentPage.value = 1
  loadProducts()
}

const handleSortChange = () => {
  currentPage.value = 1
  loadProducts()
}

const goToDetail = (id: number) => {
  router.push(`/product/${id}`)
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.marketplace-container {
  min-height: 100vh;
  background: linear-gradient(to bottom, #f5f7fa 0%, #e8eef5 100%);
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 16px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  gap: 24px;
}

.logo {
  margin: 0;
  font-size: 26px;
  font-weight: 600;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  cursor: pointer;
  white-space: nowrap;
}

.header-search {
  flex: 1;
  max-width: 500px;
}

.search-input {
  width: 100%;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.main-container {
  max-width: 1400px;
  margin: 24px auto;
  padding: 0 24px;
  display: flex;
  gap: 24px;
}

.sidebar {
  flex-shrink: 0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.category-menu {
  border: none;
}

.filter-section {
  margin-bottom: 16px;
}

.filter-section:last-child {
  margin-bottom: 0;
}

.filter-label {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.filter-bar-card {
  margin-bottom: 16px;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-count {
  color: #909399;
  font-size: 14px;
}

.product-list {
  min-height: 400px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.product-card {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 12px;
  overflow: hidden;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.product-image {
  position: relative;
  width: 100%;
  height: 200px;
  background: #f5f7fa;
  overflow: hidden;
}

.image {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
  gap: 8px;
}

.image-placeholder .el-icon {
  font-size: 48px;
}

.image-slot {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  font-size: 48px;
}

.status-tag {
  position: absolute;
  top: 12px;
  right: 12px;
}

.product-info {
  padding: 16px;
}

.product-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.product-desc {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #909399;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

.product-price-section {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 12px;
}

.price-main {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 16px;
  font-weight: 700;
  color: #f56c6c;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}

.price-original {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.seller-name {
  font-size: 13px;
  color: #606266;
}

.product-stats {
  display: flex;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
  }

  .sidebar {
    width: 100% !important;
  }

  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }

  .product-image {
    height: 160px;
  }
}
</style>
