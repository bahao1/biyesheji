<template>
  <div class="my-comments-container">
    <!-- 返回按钮 -->
    <el-button @click="$router.back()" class="back-btn" circle>
      <el-icon><ArrowLeft /></el-icon>
    </el-button>

    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">💬 我的评论</h1>
        <div class="header-actions">
          <el-button @click="$router.push('/profile')" round>
            <el-icon><User /></el-icon>
            <span>返回个人中心</span>
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
            <el-radio-group v-model="sortBy" @change="handleSortChange" size="small">
              <el-radio-button value="createTime">最新</el-radio-button>
              <el-radio-button value="likeCount">点赞最多</el-radio-button>
            </el-radio-group>
          </div>
          <div class="filter-right">
            <span class="total-count">共 {{ total }} 条评论</span>
          </div>
        </div>
      </el-card>

      <!-- 评论列表 -->
      <div v-loading="loading" class="comments-list">
        <el-empty v-if="!loading && comments.length === 0" description="暂无评论" />

        <el-card
          v-for="comment in comments"
          :key="comment.id"
          class="comment-card"
          shadow="hover"
        >
          <!-- 评论头部 -->
          <div class="comment-header">
            <div class="comment-meta">
              <div class="comment-time">{{ formatTime(comment.createTime) }}</div>
              <el-tag size="small">
                {{ comment.status || '已发布' }}
              </el-tag>
            </div>
          </div>

          <!-- 评论内容 -->
          <div class="comment-content">
            {{ comment.content }}
          </div>

          <!-- 评论统计 -->
          <div class="comment-stats">
            <span class="stat-item">
              <el-icon><Star /></el-icon>
              <span>{{ comment.likeCount || 0 }} 点赞</span>
            </span>
            <span class="stat-item">
              <el-icon><View /></el-icon>
              <span>{{ comment.viewCount || 0 }} 浏览</span>
            </span>
          </div>

          <!-- 关联帖子 -->
          <div class="related-post">
            <el-divider content-position="left">关联帖子</el-divider>
            <div class="post-preview" @click="goToPost(comment.postId)">
              <h4 class="post-title">{{ comment.postTitle }}</h4>
              <p class="post-excerpt">{{ comment.postContent }}</p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-if="total > 0"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadComments"
          @size-change="loadComments"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCommentsByUserId } from '@/api/comment'

const router = useRouter()

const loading = ref(false)
const comments = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const sortBy = ref('createTime')

// 加载评论
const loadComments = async () => {
  loading.value = true
  try {
    // TODO: 后端 /comment/user 接口需要修复
    // 暂时返回空数据，避免报错
    comments.value = []
    total.value = 0
    
    // const res = await getCommentsByUserId({
    //   page: currentPage.value,
    //   pageSize: pageSize.value
    // })
    // comments.value = res.data?.content || []
    // total.value = res.data?.totalElements || 0
  } catch (error) {
    console.error('加载评论失败:', error)
    comments.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 排序改变
const handleSortChange = () => {
  currentPage.value = 1
  loadComments()
}

// 跳转到帖子详情
const goToPost = (postId: number) => {
  router.push(`/post/${postId}`)
}

// 格式化时间
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

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.my-comments-container {
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  cursor: pointer;
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
  margin-bottom: 24px;
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

.comments-list {
  min-height: 400px;
}

.comment-card {
  margin-bottom: 16px;
  border-radius: 12px;
  transition: all 0.3s;
}

.comment-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.comment-time {
  font-size: 14px;
  color: #909399;
}

.comment-content {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  margin-bottom: 16px;
  white-space: pre-wrap;
  word-break: break-word;
}

.comment-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.related-post {
  margin-top: 16px;
}

.post-preview {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.post-preview:hover {
  background: #e8eef5;
}

.post-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.post-excerpt {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
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
  .main-content {
    padding: 0 12px;
  }

  .header-content {
    padding: 0 16px;
  }

  .logo {
    font-size: 22px;
  }

  .back-btn {
    top: 60px;
    left: 12px;
  }
}
</style>