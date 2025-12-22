<template>
  <div class="carpool-detail-container">
    <div class="header">
      <div class="header-content">
        <h1 class="logo">🚗 拼车详情</h1>
        <el-button @click="$router.back()" round>
          <el-icon><ArrowLeft /></el-icon>
          <span>返回</span>
        </el-button>
      </div>
    </div>

    <div class="main-content" v-loading="loading">
      <el-card v-if="carpool" shadow="hover" class="detail-card">
        <div class="card-header">
          <el-tag :type="carpool.type === 'DRIVER' ? 'success' : 'warning'" size="large">
            {{ carpool.type === 'DRIVER' ? '车主找乘客' : '乘客找车主' }}
          </el-tag>
          <el-tag v-if="carpool.status === 'OPEN'" type="success">开放</el-tag>
          <el-tag v-else-if="carpool.status === 'FULL'" type="info">已满</el-tag>
          <el-tag v-else type="danger">已关闭</el-tag>
        </div>

        <div class="route-section">
          <h3>行程信息</h3>
          <div class="route-info">
            <div class="route-item">
              <label>出发地:</label>
              <span>{{ carpool.departurePlace }}</span>
            </div>
            <div class="route-arrow">
              <el-icon><Right /></el-icon>
            </div>
            <div class="route-item">
              <label>目的地:</label>
              <span>{{ carpool.destination }}</span>
            </div>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(carpool.departureTime) }}</span>
            </div>
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>{{ carpool.remainingSeats }}/{{ carpool.seats }}人</span>
            </div>
            <div class="info-item" v-if="carpool.pricePerPerson">
              <el-icon><Money /></el-icon>
              <span class="price">¥{{ carpool.pricePerPerson }}/人</span>
            </div>
            <div class="info-item">
              <el-icon><View /></el-icon>
              <span>浏览 {{ carpool.viewCount }} 次</span>
            </div>
          </div>
        </div>

        <div class="contact-section" v-if="carpool.contact">
          <h3>联系方式</h3>
          <p class="contact-info">{{ carpool.contact }}</p>
        </div>

        <div class="remarks-section" v-if="carpool.remarks">
          <h3>备注信息</h3>
          <p class="remarks-text">{{ carpool.remarks }}</p>
        </div>

        <!-- 参与按钮区域 -->
        <div class="action-section">
          <el-button
            type="primary"
            size="large"
            @click="handleJoin"
            :disabled="carpool.status !== 'OPEN' || joining"
            :loading="joining"
          >
            <el-icon><Check /></el-icon>
            <span>{{ carpool.status === 'FULL' ? '已满员' : carpool.status === 'CLOSED' ? '已关闭' : '我要参与' }}</span>
          </el-button>
        </div>

        <div class="time-section">
          <p class="time-info">发布时间: {{ formatDateTime(carpool.createTime) }}</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCarpoolById, joinCarpool } from '@/api/carpool'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const joining = ref(false)
const carpool = ref<any>(null)

const loadCarpool = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const res = await getCarpoolById(id)
    carpool.value = res.data
  } catch (error) {
    console.error('加载拼车详情失败:', error)
    ElMessage.error('加载拼车详情失败')
  } finally {
    loading.value = false
  }
}

const handleJoin = async () => {
  if (!carpool.value) return

  joining.value = true
  try {
    const res = await joinCarpool(carpool.value.id)
    carpool.value = res.data
    ElMessage.success('参与成功！')
  } catch (error: any) {
    console.error('参与拼车失败:', error)
    ElMessage.error(error.response?.data?.message || '参与失败')
  } finally {
    joining.value = false
  }
}

const formatTime = (timeStr: string) => {
  const time = new Date(timeStr)
  return `${time.getFullYear()}年${time.getMonth() + 1}月${time.getDate()}日 ${time.getHours()}:${String(time.getMinutes()).padStart(2, '0')}`
}

const formatDateTime = (timeStr: string) => {
  const time = new Date(timeStr)
  return `${time.getFullYear()}-${String(time.getMonth() + 1).padStart(2, '0')}-${String(time.getDate()).padStart(2, '0')} ${String(time.getHours()).padStart(2, '0')}:${String(time.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  loadCarpool()
})
</script>

<style scoped>
.carpool-detail-container {
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
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.logo {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.main-content {
  max-width: 1000px;
  margin: 24px auto;
  padding: 0 24px;
}

.detail-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.route-section,
.contact-section,
.remarks-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.route-section h3,
.contact-section h3,
.remarks-section h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #303133;
}

.route-info {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.route-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.route-item label {
  font-size: 14px;
  color: #909399;
}

.route-item span {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.route-arrow {
  font-size: 24px;
  color: #4facfe;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #606266;
}

.price {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}

.contact-info {
  margin: 0;
  padding: 16px;
  background: #fff9e6;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.remarks-text {
  margin: 0;
  font-size: 15px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}

.time-section {
  margin-top: 16px;
}

.time-info {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.action-section {
  display: flex;
  justify-content: center;
  padding: 24px 0;
  border-top: 2px solid #f0f0f0;
  border-bottom: 2px solid #f0f0f0;
  margin-bottom: 16px;
}

.action-section .el-button {
  min-width: 200px;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  transition: all 0.3s ease;
}

.action-section .el-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(79, 172, 254, 0.4);
}

.action-section .el-button:disabled {
  background: #e4e7ed;
  color: #909399;
}

@media (max-width: 768px) {
  .route-info {
    flex-direction: column;
  }

  .route-arrow {
    transform: rotate(90deg);
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
