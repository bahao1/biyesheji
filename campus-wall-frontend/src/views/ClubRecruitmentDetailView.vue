<template>
  <div class="recruitment-detail-container">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">🎭 社团招新详情</h1>
        <div class="header-actions">
          <el-button @click="$router.push('/clubs')" round>
            <el-icon><ArrowLeft /></el-icon>
            <span>返回列表</span>
          </el-button>
          <el-button @click="$router.push('/')" round>
            <el-icon><House /></el-icon>
            <span>返回首页</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <el-card v-loading="loading" shadow="hover" class="detail-card">
        <template v-if="recruitment">
          <!-- 标题区域 -->
          <div class="title-section">
            <h1 class="recruitment-title">{{ recruitment.title }}</h1>
            <el-tag :type="recruitment.status === 'OPEN' ? 'success' : 'info'" size="large">
              {{ recruitment.status === 'OPEN' ? '招新中' : '已结束' }}
            </el-tag>
          </div>

          <!-- 社团信息 -->
          <div class="club-info-section">
            <div class="club-header">
              <el-icon class="club-icon"><School /></el-icon>
              <h2 class="club-name">{{ recruitment.clubName }}</h2>
            </div>
          </div>

          <!-- 招新信息 -->
          <div class="info-grid">
            <div class="info-item">
              <el-icon class="info-icon"><Users /></el-icon>
              <div class="info-content">
                <div class="info-label">招新人数</div>
                <div class="info-value">{{ recruitment.quota }}人</div>
              </div>
            </div>

            <div class="info-item">
              <el-icon class="info-icon"><View /></el-icon>
              <div class="info-content">
                <div class="info-label">浏览量</div>
                <div class="info-value">{{ recruitment.viewCount }}</div>
              </div>
            </div>

            <div class="info-item" v-if="recruitment.deadline">
              <el-icon class="info-icon"><Clock /></el-icon>
              <div class="info-content">
                <div class="info-label">截止时间</div>
                <div class="info-value">{{ formatDateTime(recruitment.deadline) }}</div>
              </div>
            </div>

            <div class="info-item" v-if="recruitment.contact">
              <el-icon class="info-icon"><Phone /></el-icon>
              <div class="info-content">
                <div class="info-label">联系方式</div>
                <div class="info-value">{{ recruitment.contact }}</div>
              </div>
            </div>
          </div>

          <!-- 招新内容 -->
          <div class="content-section">
            <h3 class="section-title">
              <el-icon><Document /></el-icon>
              <span>招新简介</span>
            </h3>
            <div class="content-text">{{ recruitment.content }}</div>
          </div>

          <!-- 招新要求 -->
          <div class="content-section" v-if="recruitment.requirements">
            <h3 class="section-title">
              <el-icon><List /></el-icon>
              <span>招新要求</span>
            </h3>
            <div class="content-text">{{ recruitment.requirements }}</div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-section">
            <el-button type="primary" size="large" :disabled="recruitment.status !== 'OPEN'" @click="handleContact">
              <el-icon><ChatLineSquare /></el-icon>
              <span>联系我们</span>
            </el-button>
          </div>
        </template>

        <el-empty v-else-if="!loading" description="招新信息不存在" />
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRecruitmentById } from '@/api/club'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const recruitment = ref<any>(null)

const loadRecruitment = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const res = await getRecruitmentById(id)
    recruitment.value = res.data
    console.log('Recruitment data:', recruitment.value)
  } catch (error) {
    console.error('加载招新信息失败:', error)
    ElMessage.error('加载招新信息失败')
  } finally {
    loading.value = false
  }
}

const formatDateTime = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 联系我们点击事件
const handleContact = () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  // 检查recruitment数据是否加载完成
  if (!recruitment.value) {
    ElMessage.warning('数据加载中，请稍后重试')
    return
  }

  // 打印完整的recruitment数据，查看所有可用字段
  console.log('Recruitment data for contact:', recruitment.value)

  // 跳转到消息页面，联系帖子的发布者
  // 尝试获取发布者ID，检查所有可能的字段名
  const publisherId =
    recruitment.value.presidentId || // 社长ID (主要字段)
    recruitment.value.creatorId ||
    recruitment.value.publisherId ||
    recruitment.value.userId ||
    recruitment.value.authorId ||
    recruitment.value.clubAdminId || // 保留对clubAdminId的支持，作为备选
    recruitment.value.adminId ||
    recruitment.value.ownerId

  console.log('Publisher ID found:', publisherId)

  if (publisherId) {
    ElMessage.success('正在跳转到聊天界面...')
    router.push(`/messages?userId=${publisherId}`)
  } else {
    // 检查是否有其他可能的ID字段
    const allKeys = Object.keys(recruitment.value)
    const idKeys = allKeys.filter(key => key.toLowerCase().includes('id'))
    console.log('All ID fields:', idKeys)
    
    ElMessage.warning('暂无法获取帖子发布者信息，跳转到消息列表')
    router.push('/messages')
  }
}

onMounted(() => {
  loadRecruitment()
})
</script>

<style scoped>
.recruitment-detail-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 16px 0;
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 2px solid rgba(102, 126, 234, 0.2);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.logo {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.main-content {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 24px;
}

.detail-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.title-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  padding-bottom: 24px;
  border-bottom: 2px solid #f0f0f0;
  margin-bottom: 24px;
}

.recruitment-title {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1.4;
  flex: 1;
}

.club-info-section {
  margin-bottom: 32px;
}

.club-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 12px;
  color: white;
}

.club-icon {
  font-size: 32px;
}

.club-name {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.info-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.2);
}

.info-icon {
  font-size: 32px;
  color: #667eea;
}

.info-content {
  flex: 1;
}

.info-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.info-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.content-section {
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.section-title .el-icon {
  font-size: 24px;
  color: #667eea;
}

.content-text {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

.action-section {
  display: flex;
  justify-content: center;
  padding-top: 32px;
  border-top: 2px solid #f0f0f0;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  padding: 16px 48px;
  font-size: 16px;
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
  .recruitment-title {
    font-size: 24px;
  }

  .club-name {
    font-size: 20px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
