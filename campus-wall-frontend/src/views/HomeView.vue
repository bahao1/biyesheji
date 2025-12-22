<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">🎓 校园墙</h1>
        <div class="header-search">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索帖子..."
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
          <el-button @click="$router.push('/marketplace')" round>
            <el-icon><ShoppingCart /></el-icon>
            <span>二手市场</span>
          </el-button>
          <el-button @click="$router.push('/clubs')" round>
            <el-icon><School /></el-icon>
            <span>社团招新</span>
          </el-button>
          <el-button @click="$router.push('/carpool')" round>
            <el-icon><Car /></el-icon>
            <span>校园拼车</span>
          </el-button>
          <el-button @click="$router.push('/love-letter')" round v-if="userStore.token">
            <el-icon><ChatDotRound /></el-icon>
            <span>AI情书</span>
          </el-button>
          <el-button @click="$router.push('/chat-assistant')" round v-if="userStore.token">
            <el-icon><ChatLineRound /></el-icon>
            <span>AI助手</span>
          </el-button>
          <el-button @click="$router.push('/messages')" round v-if="userStore.token">
            <el-icon><ChatDotRound /></el-icon>
            <span>消息中心</span>
          </el-button>
          <template v-if="userStore.token">
            <el-button type="primary" @click="$router.push('/create-post')" round>
              <el-icon><Edit /></el-icon>
              <span>发布帖子</span>
            </el-button>
            <el-dropdown @command="handleCommand">
              <div class="user-avatar">
                <el-avatar :size="36">{{ userStore.user?.nickname?.charAt(0) }}</el-avatar>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="my-posts">
                    <el-icon><Document /></el-icon>
                    我的帖子
                  </el-dropdown-item>
                  <el-dropdown-item command="favorites">
                    <el-icon><Star /></el-icon>
                    我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item v-if="userStore.user?.role === 'SUPER_ADMIN'" command="admin" divided>
                    <el-icon><Setting /></el-icon>
                    管理员后台
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button @click="$router.push('/login')" round>登录</el-button>
            <el-button type="primary" @click="$router.push('/register')" round>注册</el-button>
          </template>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-container">
      <!-- 左侧分类导航 -->
        <el-aside :width="isMobile ? '100%' : '220px'" class="sidebar">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Grid /></el-icon>
              <span>帖子分类</span>
            </div>
          </template>
          <el-menu :default-active="activeCategory" @select="handleCategoryChange" class="category-menu">
            <el-menu-item index="">
              <el-icon><List /></el-icon>
              <span>全部帖子</span>
            </el-menu-item>
            <el-menu-item index="GENERAL">
              <el-icon><Document /></el-icon>
              <span>普通帖子</span>
            </el-menu-item>
            <el-menu-item index="CONFESSION">
              <el-icon><ChatDotRound /></el-icon>
              <span>表白墙</span>
            </el-menu-item>
            <el-menu-item index="LOST_FOUND">
              <el-icon><Search /></el-icon>
              <span>失物招领</span>
            </el-menu-item>
            <el-menu-item index="HELP">
              <el-icon><QuestionFilled /></el-icon>
              <span>求助</span>
            </el-menu-item>
            <el-menu-item index="ACTIVITY">
              <el-icon><Calendar /></el-icon>
              <span>活动</span>
            </el-menu-item>
            <el-menu-item index="STUDY">
              <el-icon><Reading /></el-icon>
              <span>学习交流</span>
            </el-menu-item>
            <el-menu-item index="LIFE">
              <el-icon><Coffee /></el-icon>
              <span>生活分享</span>
            </el-menu-item>
          </el-menu>
        </el-card>

        <!-- 热门标签 -->
        <el-card shadow="hover" class="hot-tags" style="margin-top: 16px">
          <template #header>
            <div class="card-header">
              <el-icon><TrendCharts /></el-icon>
              <span>热门话题</span>
            </div>
          </template>
          <div class="tags">
            <el-tag type="danger" effect="plain" size="small">#校园生活</el-tag>
            <el-tag type="warning" effect="plain" size="small">#考试周</el-tag>
            <el-tag type="success" effect="plain" size="small">#社团招新</el-tag>
            <el-tag type="info" effect="plain" size="small">#二手交易</el-tag>
          </div>
        </el-card>
      </el-aside>

      <!-- 主内容区 -->
      <div class="main-content">
        <!-- 筛选和排序 -->
        <el-card shadow="never" class="filter-card">
          <div class="filter-bar">
            <div class="filter-left">
              <el-radio-group v-model="sortBy" @change="handleSortChange" size="small">
                <el-radio-button value="createTime">最新</el-radio-button>
                <el-radio-button value="viewCount">最热</el-radio-button>
                <el-radio-button value="likeCount">点赞最多</el-radio-button>
              </el-radio-group>
            </div>
            <div class="filter-right">
              <span class="total-count">共 {{ total }} 个帖子</span>
            </div>
          </div>
        </el-card>

        <!-- 帖子列表 -->
        <div v-loading="loading" class="post-list">
          <el-empty v-if="!loading && postList.length === 0" description="暂无帖子" />

          <el-card
            v-for="post in postList"
            :key="post.id"
            class="post-card"
            shadow="hover"
            @click="goToDetail(post.id)"
          >
            <div class="post-header">
              <div class="post-author">
                <el-avatar :size="40">{{ post.nickname?.charAt(0) }}</el-avatar>
                <div class="author-info">
                  <div class="author-name">{{ post.nickname }}</div>
                  <div class="post-time">{{ formatTime(post.createTime) }}</div>
                </div>
              </div>
              <el-tag :type="getCategoryType(post.category)" size="small">
                {{ getCategoryName(post.category) }}
              </el-tag>
            </div>

            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-content">{{ post.content }}</p>

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
            @current-change="loadPosts"
            @size-change="loadPosts"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { queryPosts } from '@/api/post'

// 响应式布局
const isMobile = ref(window.innerWidth < 768)

const handleResize = () => {
  isMobile.value = window.innerWidth < 768
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  loadPosts()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const postList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activeCategory = ref('')
const searchKeyword = ref('')
const sortBy = ref('createTime')

const loadPosts = async () => {
  loading.value = true
  try {
    const res = await queryPosts({
      category: activeCategory.value || undefined,
      keyword: searchKeyword.value || undefined,
      sortBy: sortBy.value,
      sortOrder: 'desc',
      page: currentPage.value,
      pageSize: pageSize.value
    })
    postList.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    console.error('加载帖子失败:', error)
  } finally {
    loading.value = false
  }
}

const handleCategoryChange = (index: string) => {
  activeCategory.value = index
  currentPage.value = 1
  loadPosts()
}

const handleSearch = () => {
  currentPage.value = 1
  loadPosts()
}

const handleSortChange = () => {
  currentPage.value = 1
  loadPosts()
}

const goToDetail = (id: number) => {
  router.push(`/post/${id}`)
}

const handleCommand = (command: string) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'my-posts') {
    activeCategory.value = ''
    searchKeyword.value = ''
    // TODO: 筛选当前用户的帖子
  } else if (command === 'favorites') {
    // TODO: 跳转到收藏页面
  } else if (command === 'admin') {
    router.push('/admin')
  } else if (command === 'logout') {
    userStore.logout()
  }
}

const getCategoryName = (category: string) => {
  const map: Record<string, string> = {
    GENERAL: '普通',
    CONFESSION: '表白',
    LOST_FOUND: '失物',
    HELP: '求助',
    ACTIVITY: '活动',
    STUDY: '学习',
    LIFE: '生活'
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
    LIFE: ''
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

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.home-container {
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
  width: 100%;
  max-width: 100%;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
  gap: 16px;
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

.user-avatar {
  cursor: pointer;
  transition: transform 0.2s;
}

.user-avatar:hover {
  transform: scale(1.05);
}

.main-container {
  width: 100%;
  max-width: 100%;
  margin: 24px auto;
  padding: 0 16px;
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
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

.hot-tags .tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.filter-card {
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

.post-list {
  min-height: 400px;
}

.post-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 12px;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-info {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 500;
  color: #303133;
  font-size: 14px;
}

.post-time {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.post-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  line-height: 1.5;
}

.post-content {
  margin: 0 0 16px 0;
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-footer {
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.post-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #909399;
  font-size: 14px;
  transition: color 0.3s;
}

.stat-item:hover {
  color: #409eff;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
