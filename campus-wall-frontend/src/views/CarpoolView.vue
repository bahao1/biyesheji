<template>
  <div class="carpool-container">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">🚗 校园拼车</h1>
        <div class="header-actions">
          <el-button @click="$router.push('/')" round>
            <el-icon><House /></el-icon>
            <span>返回首页</span>
          </el-button>
          <el-button type="primary" @click="$router.push('/create-carpool')" round>
            <el-icon><Plus /></el-icon>
            <span>发布拼车</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 搜索栏 -->
      <el-card shadow="hover" class="search-card">
        <div class="search-bar">
          <el-select v-model="typeFilter" placeholder="拼车类型" style="width: 150px; margin-right: 12px" clearable>
            <el-option label="车主找乘客" value="DRIVER" />
            <el-option label="乘客找车主" value="PASSENGER" />
          </el-select>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索出发地或目的地..."
            style="width: 300px; margin-right: 12px"
            clearable
            @keyup.enter="loadCarpools"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="loadCarpools">搜索</el-button>
        </div>
      </el-card>

      <!-- 拼车列表 -->
      <div v-loading="loading" class="carpool-list">
        <el-empty v-if="!loading && carpoolList.length === 0" description="暂无拼车信息" />

        <div class="carpool-grid">
          <el-card
            v-for="carpool in carpoolList"
            :key="carpool.id"
            class="carpool-card"
            shadow="hover"
            @click="goToDetail(carpool.id)"
          >
            <div class="card-header">
              <el-tag :type="carpool.type === 'DRIVER' ? 'success' : 'warning'" size="large">
                {{ carpool.type === 'DRIVER' ? '车主找乘客' : '乘客找车主' }}
              </el-tag>
              <el-tag v-if="carpool.status === 'OPEN'" type="success">开放</el-tag>
              <el-tag v-else-if="carpool.status === 'FULL'" type="info">已满</el-tag>
              <el-tag v-else type="danger">已关闭</el-tag>
            </div>

            <div class="route-info">
              <div class="route-item">
                <span class="route-label">出发:</span>
                <span class="route-value">{{ carpool.departurePlace }}</span>
              </div>
              <el-icon class="arrow-icon"><Right /></el-icon>
              <div class="route-item">
                <span class="route-label">到达:</span>
                <span class="route-value">{{ carpool.destination }}</span>
              </div>
            </div>

            <div class="carpool-info">
              <div class="info-item">
                <el-icon><Clock /></el-icon>
                <span>{{ formatTime(carpool.departureTime) }}</span>
              </div>
              <div class="info-item">
                <el-icon><User /></el-icon>
                <span>{{ carpool.remainingSeats }}/{{ carpool.seats }}座</span>
              </div>
              <div class="info-item" v-if="carpool.pricePerPerson">
                <el-icon><Money /></el-icon>
                <span class="price">¥{{ carpool.pricePerPerson }}/人</span>
              </div>
            </div>

            <div class="carpool-footer" v-if="carpool.remarks">
              <p class="remarks">{{ carpool.remarks }}</p>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadCarpools"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { queryCarpools } from '@/api/carpool'

const router = useRouter()

const loading = ref(false)
const carpoolList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const searchKeyword = ref('')
const typeFilter = ref('')

const loadCarpools = async () => {
  loading.value = true
  try {
    const res = await queryCarpools({
      type: typeFilter.value || undefined,
      keyword: searchKeyword.value || undefined,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    carpoolList.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    console.error('加载拼车信息失败:', error)
  } finally {
    loading.value = false
  }
}

const goToDetail = (id: number) => {
  router.push(`/carpool/${id}`)
}

const formatTime = (timeStr: string) => {
  const time = new Date(timeStr)
  return `${time.getMonth() + 1}月${time.getDate()}日 ${time.getHours()}:${String(time.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  loadCarpools()
})
</script>

<style scoped>
.carpool-container {
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
}

.logo {
  margin: 0;
  font-size: 26px;
  font-weight: 600;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.main-content {
  max-width: 1400px;
  margin: 24px auto;
  padding: 0 24px;
}

.search-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.search-bar {
  display: flex;
  align-items: center;
}

.carpool-list {
  min-height: 400px;
}

.carpool-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.carpool-card {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 12px;
}

.carpool-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.route-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.route-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.route-label {
  font-size: 12px;
  color: #909399;
}

.route-value {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.arrow-icon {
  font-size: 20px;
  color: #4facfe;
}

.carpool-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #606266;
}

.price {
  color: #f56c6c;
  font-weight: 600;
}

.carpool-footer {
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.remarks {
  margin: 0;
  font-size: 13px;
  color: #909399;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .carpool-grid {
    grid-template-columns: 1fr;
  }
}
</style>
