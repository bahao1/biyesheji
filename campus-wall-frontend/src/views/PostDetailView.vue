<template>
  <div class="post-detail-container">
    <!-- 返回按钮 -->
    <el-button @click="$router.back()" class="back-btn" circle>
      <el-icon><ArrowLeft /></el-icon>
    </el-button>

    <!-- 帖子内容 -->
    <el-card v-loading="loading" class="post-card" shadow="hover">
      <template v-if="post">
        <!-- 帖子头部 -->
        <div class="post-header">
          <div class="author-section">
            <el-avatar :size="50">{{ post.nickname?.charAt(0) }}</el-avatar>
            <div class="author-info">
              <div class="author-name">{{ post.nickname }}</div>
              <div class="post-time">{{ formatTime(post.createTime) }}</div>
            </div>
          </div>
          <el-tag :type="getCategoryType(post.category)" size="large">
            {{ getCategoryName(post.category) }}
          </el-tag>
        </div>

        <!-- 帖子标题 -->
        <h1 class="post-title">{{ post.title }}</h1>

        <!-- 帖子内容 -->
        <div class="post-content">{{ post.content }}</div>

        <!-- 帖子图片 -->
        <div v-if="post.images && post.images.length > 0" class="post-images">
          <div 
            v-for="(image, index) in post.images" 
            :key="index" 
            class="image-item"
            @click="previewImage(index)"
          >
            <el-image
              :src="getImageUrl(image)"
              :preview-src-list="post.images.map((img: string) => getImageUrl(img))"
              fit="cover"
            />
          </div>
        </div>

        <!-- 帖子统计 -->
        <div class="post-stats">
          <span class="stat-item">
            <el-icon><View /></el-icon>
            {{ post.viewCount }} 浏览
          </span>
          <span class="stat-item">
            <el-icon><ChatDotRound /></el-icon>
            {{ post.commentCount }} 评论
          </span>
          <span class="stat-item">
            <el-icon><Star /></el-icon>
            {{ post.likeCount }} 点赞
          </span>
        </div>

        <!-- 互动按钮 -->
        <div class="post-actions">
          <el-button
            @click="handleLike"
            :type="post.isLiked ? 'primary' : 'default'"
            :icon="post.isLiked ? 'StarFilled' : 'Star'"
            size="large"
            round
          >
            {{ post.isLiked ? '已点赞' : '点赞' }} ({{ post.likeCount }})
          </el-button>
          <el-button
            @click="handleFavorite"
            :type="post.isFavorited ? 'warning' : 'default'"
            size="large"
            round
          >
            <el-icon><Collection /></el-icon>
            {{ post.isFavorited ? '已收藏' : '收藏' }}
          </el-button>
          <el-button size="large" round>
            <el-icon><Share /></el-icon>
            分享
          </el-button>
        </div>
      </template>
    </el-card>

    <!-- 评论区 -->
    <el-card class="comment-card" shadow="hover">
      <template #header>
        <div class="comment-header">
          <h3>
            <el-icon><ChatDotRound /></el-icon>
            评论 ({{ post?.commentCount || 0 }})
          </h3>
        </div>
      </template>

      <!-- 发表评论 -->
      <div class="comment-input-section">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="4"
          placeholder="写下你的评论..."
          class="comment-textarea"
        />
        <div class="comment-actions">
          <el-button type="primary" @click="handleComment" round>
            <el-icon><Promotion /></el-icon>
            发表评论
          </el-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list">
        <el-empty v-if="comments.length === 0" description="暂无评论，快来抢沙发吧！" />

        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-main">
            <el-avatar :size="40">{{ comment.nickname?.charAt(0) }}</el-avatar>
            <div class="comment-body">
              <div class="comment-author">{{ comment.nickname }}</div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-footer">
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                <div class="comment-actions-btn">
                  <el-button text size="small">
                    <el-icon><ChatLineRound /></el-icon>
                    回复
                  </el-button>
                </div>
              </div>

              <!-- 子评论 -->
              <div v-if="comment.replies && comment.replies.length > 0" class="replies">
                <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                  <el-avatar :size="32">{{ reply.nickname?.charAt(0) }}</el-avatar>
                  <div class="reply-body">
                    <div class="reply-author">
                      {{ reply.nickname }}
                      <span v-if="reply.replyToNickname" class="reply-to">
                        回复 @{{ reply.replyToNickname }}
                      </span>
                    </div>
                    <div class="reply-content">{{ reply.content }}</div>
                    <div class="reply-time">{{ formatTime(reply.createTime) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getPostById, likePost, unlikePost, favoritePost, unfavoritePost } from '@/api/post'
import { getCommentsTree, createComment } from '@/api/comment'
import { ElMessage } from 'element-plus'

const route = useRoute()
const loading = ref(false)
const post = ref<any>(null)
const comments = ref<any[]>([])
const commentContent = ref('')

const loadPost = async () => {
  loading.value = true
  try {
    const res = await getPostById(Number(route.params.id))
    post.value = res.data
  } catch (error) {
    console.error('加载帖子失败:', error)
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    const res = await getCommentsTree(Number(route.params.id))
    comments.value = res.data
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

const handleLike = async () => {
  try {
    if (post.value.isLiked) {
      await unlikePost(post.value.id)
      post.value.isLiked = false
      post.value.likeCount--
    } else {
      await likePost(post.value.id)
      post.value.isLiked = true
      post.value.likeCount++
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const handleFavorite = async () => {
  try {
    if (post.value.isFavorited) {
      await unfavoritePost(post.value.id)
      post.value.isFavorited = false
      ElMessage.success('已取消收藏')
    } else {
      await favoritePost(post.value.id)
      post.value.isFavorited = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏失败:', error)
  }
}

const handleComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    await createComment({
      postId: Number(route.params.id),
      content: commentContent.value
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    loadComments()
    post.value.commentCount++
  } catch (error) {
    console.error('评论失败:', error)
  }
}

const getCategoryName = (category: string) => {
  const map: Record<string, string> = {
    GENERAL: '普通帖子',
    CONFESSION: '表白墙',
    LOST_FOUND: '失物招领',
    HELP: '求助',
    ACTIVITY: '活动',
    STUDY: '学习交流',
    LIFE: '生活分享',
    OTHER: '其他'
  }
  return map[category] || category
}

const getCategoryType = (category: string) => {
  const map: Record<string, any> = {
    GENERAL: '',
    CONFESSION: 'danger',
    LOST_FOUND: 'warning',
    HELP: 'info',
    ACTIVITY: 'success',
    STUDY: 'primary',
    LIFE: '',
    OTHER: 'info'
  }
  return map[category] || ''
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

// 图片处理
const API_BASE_URL = 'http://localhost:8080/api'

const getImageUrl = (image: string) => {
  if (image.startsWith('http')) {
    return image
  }
  return `${API_BASE_URL}${image}`
}

const previewImage = (index: number) => {
  // Element Plus的el-image组件自带预览功能，这里可以添加自定义逻辑
  console.log('Preview image:', index)
}

onMounted(() => {
  loadPost()
  loadComments()
})
</script>

<style scoped>
.post-detail-container {
  max-width: 900px;
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

.post-card {
  margin-bottom: 24px;
  border-radius: 16px;
  overflow: hidden;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.author-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-info {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.post-time {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.post-title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.4;
  margin: 0 0 20px 0;
}

.post-content {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
  margin: 24px 0;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 帖子图片样式 */
.post-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 24px 0;
}

.image-item {
  width: calc(33.33% - 8px);
  height: 0;
  padding-bottom: calc(33.33% - 8px);
  position: relative;
  cursor: pointer;
  overflow: hidden;
  border-radius: 8px;
  transition: transform 0.3s;
}

.image-item:hover {
  transform: scale(1.02);
}

.image-item :deep(.el-image) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

/* 响应式图片布局 */
@media (max-width: 1024px) {
  .image-item {
    width: calc(50% - 6px);
    padding-bottom: calc(50% - 6px);
  }
}

@media (max-width: 768px) {
  .image-item {
    width: calc(100% - 0px);
    padding-bottom: calc(100% - 0px);
  }
}

.post-stats {
  display: flex;
  gap: 32px;
  padding: 20px 0;
  margin: 20px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #606266;
}

.stat-item .el-icon {
  font-size: 18px;
  color: #909399;
}

.post-actions {
  display: flex;
  gap: 16px;
  margin: 24px 0;
}

.post-actions .el-button {
  flex: 1;
  height: 44px;
  font-size: 15px;
  font-weight: 500;
}

.comment-card {
  border-radius: 16px;
  overflow: hidden;
}

.comment-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-input-section {
  margin-bottom: 24px;
}

.comment-textarea {
  margin-bottom: 12px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.comment-list {
  margin-top: 24px;
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-main {
  display: flex;
  gap: 12px;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-author {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 12px;
  white-space: pre-wrap;
  word-break: break-word;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-time {
  font-size: 13px;
  color: #909399;
}

.comment-actions-btn {
  display: flex;
  gap: 8px;
}

.replies {
  margin-top: 16px;
  padding-left: 20px;
  border-left: 2px solid #f0f0f0;
}

.reply-item {
  display: flex;
  gap: 10px;
  padding: 12px 0;
}

.reply-item:first-child {
  padding-top: 0;
}

.reply-body {
  flex: 1;
  min-width: 0;
}

.reply-author {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.reply-to {
  font-weight: 400;
  color: #409eff;
  margin-left: 4px;
}

.reply-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 6px;
  white-space: pre-wrap;
  word-break: break-word;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .post-detail-container {
    padding: 0 12px;
  }

  .back-btn {
    top: 60px;
    left: 12px;
  }

  .post-title {
    font-size: 22px;
  }

  .post-content {
    font-size: 15px;
  }

  .post-actions .el-button {
    font-size: 14px;
  }

  .replies {
    padding-left: 12px;
  }
}
</style>
