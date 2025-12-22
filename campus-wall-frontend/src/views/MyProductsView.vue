<template>
  <div class="my-products-container">
    <!-- 返回按钮 -->
    <el-button @click="$router.back()" class="back-btn" circle>
      <el-icon><ArrowLeft /></el-icon>
    </el-button>

    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">🛒 我的商品</h1>
        <div class="header-actions">
          <el-button @click="$router.push('/marketplace')" round>
            <el-icon><ShoppingCart /></el-icon>
            <span>查看市场</span>
          </el-button>
          <el-button type="primary" @click="$router.push('/create-product')" round>
            <el-icon><Plus /></el-icon>
            <span>发布商品</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 筛选和搜索 -->
      <el-card shadow="hover" class="filter-card">
        <div class="filter-bar">
          <div class="filter-left">
            <el-radio-group v-model="activeStatus" @change="handleStatusChange" size="small">
              <el-radio-button value="">全部</el-radio-button>
              <el-radio-button value="ON_SALE">在售</el-radio-button>
              <el-radio-button value="SOLD">已售出</el-radio-button>
            </el-radio-group>
          </div>
          <div class="filter-right">
            <span class="total-count">共 {{ total }} 件商品</span>
          </div>
        </div>
      </el-card>

      <!-- 商品列表 -->
      <div v-loading="loading" class="products-list">
        <el-empty v-if="!loading && productList.length === 0" description="暂无商品" />

        <div class="product-grid">
          <el-card
            v-for="product in productList"
            :key="product.id"
            class="product-card"
            shadow="hover"
          >
            <!-- 商品图片 -->
            <div class="product-image">
              <el-image
                :src="getImageUrl(product.images)"
                fit="cover"
                class="image"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                    <span>暂无图片</span>
                  </div>
                </template>
              </el-image>
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

              <!-- 操作按钮 -->
              <div class="product-actions">
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDelete(product.id)"
                  :loading="deletingId === product.id"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
                <el-button
                  type="primary"
                  size="small"
                  @click="$router.push(`/product/${product.id}`)"
                >
                  <el-icon><View /></el-icon>
                  查看
                </el-button>
                <el-button
                  size="small"
                  @click="$router.push(`/create-product?edit=${product.id}`)"
                >
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
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
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { queryProducts, deleteProduct } from '@/api/product'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const loading = ref(false)
const deletingId = ref<number | null>(null)
const productList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const activeStatus = ref('')
const API_BASE_URL = 'http://localhost:8080/api'

// 获取图片URL
const getImageUrl = (images: any) => {
  if (!images) return ''
  
  let firstImage = ''
  
  if (typeof images === 'string') {
    const trimmedImages = images.trim()
    if (trimmedImages) {
      firstImage = trimmedImages.split(',')[0]?.trim() || ''
    }
  } else if (Array.isArray(images) && images.length > 0) {
    firstImage = images[0]?.trim() || ''
  }
  
  if (!firstImage) return ''
  
  if (firstImage.startsWith('http')) {
    return firstImage
  }
  
  const baseUrl = API_BASE_URL.replace(/\/$/, '')
  const imagePath = firstImage.startsWith('/') ? firstImage : `/${firstImage}`
  return `${baseUrl}${imagePath}`
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const res = await queryProducts({
      status: activeStatus.value || undefined,
      sortBy: 'createTime',
      sortOrder: 'desc',
      page: currentPage.value,
      pageSize: pageSize.value,
      my: true // 只获取当前用户的商品
    })
    productList.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    console.error('加载商品失败:', error)
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

// 状态筛选改变
const handleStatusChange = () => {
  currentPage.value = 1
  loadProducts()
}

// 删除商品
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这件商品吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    deletingId.value = id
    await deleteProduct(id)
    ElMessage.success('删除成功')
    loadProducts() // 重新加载商品列表
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除商品失败:', error)
      ElMessage.error('删除失败')
    }
  } finally {
    deletingId.value = null
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.my-products-container {
  min-height: 100vh;
  background: linear-gradient(to bottom, #f5f7fa 0%, #e8eef5 100%);
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
  white-space: nowrap;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.main-content {
  max-width: 1400px;
  margin: 24px auto;
  padding: 0 24px;
}

.filter-card {
  margin-bottom: 16px;
  border-radius: 12px;
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

.products-list {
  min-height: 400px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.product-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
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

.image-slot {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  font-size: 14px;
  gap: 8px;
}

.image-slot .el-icon {
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

.product-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .main-content {
    padding: 0 12px;
  }

  .header-content {
    padding: 0 16px;
  }

  .logo {
    font-size: 22px;
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