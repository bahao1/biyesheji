<template>
  <div class="user-profile-container">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">👤 用户主页</h1>
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
      <!-- 用户信息卡片 -->
      <el-card shadow="hover" class="user-info-card" v-loading="userLoading">
        <div class="user-info" v-if="userInfo">
          <el-avatar :size="80" :src="userInfo.avatar || '/default-avatar.png'" class="user-avatar" />
          <div class="user-details">
            <h2 class="user-nickname">{{ userInfo.nickname }}</h2>
            <p class="user-username">@{{ userInfo.username }}</p>
            <p class="user-bio" v-if="userInfo.bio">{{ userInfo.bio }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-value">{{ postTotal }}</span>
                <span class="stat-label">帖子</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 帖子列表 -->
      <el-card shadow="hover" class="posts-card">
        <template #header>
          <div class="card-header">
            <span>TA的帖子</span>
          </div>
        </template>

        <div v-loading="loading" class="posts-list">
          <el-empty v-if="!loading && postList.length === 0" description="还没有发布任何帖子" />

          <div class="post-grid">
            <div
              v-for="post in postList"
              :key="post.id"
              class="post-card"
              @click="goToDetail(post.id)"
            >
              <div class="post-header">
                <h3 class="post-title">{{ post.title }}</h3>
                <el-tag size="small">{{ getCategoryName(post.category) }}</el-tag>
              </div>

              <div class="post-content">
                {{ post.content }}
              </div>

              <div class="post-images" v-if="post.images && post.images.length > 0">
                <el-image
                  v-for="(img, index) in post.images.slice(0, 3)"
                  :key="index"
                  :src="img"
                  fit="cover"
                  class="post-image"
                  :preview-src-list="post.images"
                  :initial-index="index"
                />
              </div>

              <div class="post-footer">
                <div class="post-meta">
                  <span class="meta-item">
                    <el-icon><View /></el-icon>
                    {{ post.viewCount }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Star /></el-icon>
                    {{ post.likeCount }}
                  </span>
                  <span class="meta-item">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ post.commentCount }}
                  </span>
                </div>
                <span class="post-time">{{ formatTime(post.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="pagination" v-if="postTotal > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="postTotal"
            layout="total, prev, pager, next"
            @current-change="loadUserPosts"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserPosts } from '@/api/post'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const userId = ref<number>(Number(route.params.id))
const userInfo = ref<any>(null)
const userLoading = ref(false)
const loading = ref(false)
const postList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const postTotal = ref(0)

// 加载用户信息
const loadUserInfo = async () => {
  userLoading.value = true
  try {
    const res = await request({
      url: `/user/${userId.value}`,
      method: 'get'
    })
    userInfo.value = res.data
  } catch (error) {
    console.error('加载用户信息失败:', error)
  } finally {
    userLoading.value = false
  }
}

// 加载用户帖子
const loadUserPosts = async () => {
  loading.value = true
  try {
    const res = await getUserPosts(userId.value, currentPage.value, pageSize.value)
    postList.value = res.data.content
    postTotal.value = res.data.totalElements
  } catch (error) {
    console.error('加载用户帖子失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到帖子详情
const goToDetail = (id: number) => {
  router.push(`/post/${id}`)
}

// 获取分类名称
const getCategoryName = (category: string) => {
  const map: any = {
    GENERAL: '闲聊',
    STUDY: '学习',
    LIFE: '生活',
    ACTIVITY: '活动',
    LOST_FOUND: '失物招领',
    HELP: '求助',
    CONFESSION: '表白墙',
    OTHER: '其他'
  }
  return map[category] || category
}

// 格式化时间
const formatTime = (timeStr: string) => {
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - time.getTime()

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`

  return `${time.getMonth() + 1}月${time.getDate()}日`
}

onMounted(() => {
  loadUserInfo()
  loadUserPosts()
})
</script>

<style scoped>
.user-profile-container {
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
  max-width: 1200px;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.main-content {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 24px;
}

.user-info-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.user-info {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.user-avatar {
  flex-shrink: 0;
}

.user-details {
  flex: 1;
}

.user-nickname {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.user-username {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #909399;
}

.user-bio {
  margin: 0 0 16px 0;
  font-size: 15px;
  color: #606266;
  line-height: 1.6;
}

.user-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

.posts-card {
  border-radius: 12px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

.posts-list {
  min-height: 400px;
}

.post-grid {
  display: grid;
  gap: 16px;
}

.post-card {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.post-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #c0c4cc;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.post-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  margin-right: 12px;
}

.post-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-images {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.post-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  cursor: pointer;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.post-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.post-time {
  font-size: 13px;
  color: #c0c4cc;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .user-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .user-stats {
    justify-content: center;
  }
}
</style>
