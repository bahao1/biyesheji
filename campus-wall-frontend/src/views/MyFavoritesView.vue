<template>
  <div class="my-favorites-container">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">⭐ 我的收藏</h1>
        <div class="header-actions">
          <el-button @click="$router.back()" round>
            <el-icon><ArrowLeft /></el-icon>
            <span>返回</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <el-card shadow="hover" class="content-card">
        <div v-loading="loading" class="posts-list">
          <el-empty v-if="!loading && postList.length === 0" description="还没有收藏任何帖子" />

          <div v-else class="post-items">
            <div
              v-for="post in postList"
              :key="post.id"
              class="post-item"
              @click="goToDetail(post.id)"
            >
              <div class="post-header">
                <h3 class="post-title">{{ post.title }}</h3>
                <el-tag :type="getCategoryColor(post.category)">
                  {{ getCategoryName(post.category) }}
                </el-tag>
              </div>

              <div class="post-author">
                <el-icon><User /></el-icon>
                <span>{{ post.authorNickname }}</span>
              </div>

              <div class="post-content">{{ post.content }}</div>

              <div class="post-footer">
                <div class="post-stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ post.viewCount }}
                  </span>
                  <span class="stat-item">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ post.commentCount }}
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ post.likeCount }}
                  </span>
                </div>
                <span class="post-time">{{ formatTime(post.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="loadFavorites"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFavoritePosts } from '@/api/post'
import { ElMessage } from 'element-plus'

const router = useRouter()

const loading = ref(false)
const postList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavoritePosts(currentPage.value, pageSize.value)
    postList.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    ElMessage.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

const goToDetail = (id: number) => {
  router.push(`/post/${id}`)
}

const getCategoryName = (category: string) => {
  const categoryMap: Record<string, string> = {
    GENERAL: '综合',
    STUDY: '学习',
    LIFE: '生活',
    ACTIVITY: '活动',
    CONFESSION: '表白墙',
    HELP: '求助',
    LOST_FOUND: '失物招领',
    OTHER: '其他'
  }
  return categoryMap[category] || category
}

const getCategoryColor = (category: string) => {
  const colorMap: Record<string, any> = {
    GENERAL: '',
    STUDY: 'success',
    LIFE: 'warning',
    ACTIVITY: 'danger',
    CONFESSION: 'danger',
    HELP: 'warning',
    LOST_FOUND: 'info',
    OTHER: 'info'
  }
  return colorMap[category] || ''
}

const formatTime = (timeStr: string) => {
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < minute) return '刚刚'
  if (diff < hour) return `${Math.floor(diff / minute)}分钟前`
  if (diff < day) return `${Math.floor(diff / hour)}小时前`
  if (diff < 7 * day) return `${Math.floor(diff / day)}天前`

  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.my-favorites-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 50%, #ffc371 100%);
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
  border-bottom: 2px solid rgba(240, 147, 251, 0.2);
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
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 50%, #ffc371 100%);
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

.content-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  min-height: 400px;
}

.posts-list {
  min-height: 300px;
}

.post-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-item {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.post-item:hover {
  background: #fff5f7;
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(240, 147, 251, 0.3);
  border-color: rgba(240, 147, 251, 0.4);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}

.post-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
}

.post-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
}

.post-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.post-time {
  font-size: 13px;
  color: #909399;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .post-header {
    flex-direction: column;
  }

  .post-stats {
    gap: 12px;
  }
}
</style>
