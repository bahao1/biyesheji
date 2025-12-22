<template>
  <div class="clubs-container">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">🎭 社团招新</h1>
        <div class="header-actions">
          <el-button @click="$router.push('/')" round>
            <el-icon><House /></el-icon>
            <span>返回首页</span>
          </el-button>
          <el-button
            v-if="userStore.user?.role === 'CLUB_ADMIN'"
            type="primary"
            @click="$router.push('/club-manage')"
            round
          >
            <el-icon><Setting /></el-icon>
            <span>社团管理</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 搜索栏 -->
      <el-card shadow="hover" class="search-card">
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索社团招新..."
            style="width: 300px; margin-right: 12px"
            clearable
            @keyup.enter="loadRecruitments"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 150px; margin-right: 12px" clearable>
            <el-option label="招新中" value="OPEN" />
            <el-option label="已结束" value="CLOSED" />
          </el-select>
          <el-button type="primary" @click="loadRecruitments">搜索</el-button>
        </div>
      </el-card>

      <!-- 招新列表 -->
      <div v-loading="loading" class="recruitment-list">
        <el-empty v-if="!loading && recruitmentList.length === 0" description="暂无招新信息" />

        <div class="recruitment-grid">
          <el-card
            v-for="recruitment in recruitmentList"
            :key="recruitment.id"
            class="recruitment-card"
            shadow="hover"
            @click="goToDetail(recruitment.id)"
          >
            <div class="card-header">
              <h3 class="recruitment-title">{{ recruitment.title }}</h3>
              <el-tag :type="recruitment.status === 'OPEN' ? 'success' : 'info'">
                {{ recruitment.status === 'OPEN' ? '招新中' : '已结束' }}
              </el-tag>
            </div>

            <div class="club-info">
              <el-icon><School /></el-icon>
              <span class="club-name">{{ recruitment.clubName }}</span>
            </div>

            <div class="recruitment-content">
              {{ recruitment.content }}
            </div>

            <div class="recruitment-footer">
              <div class="info-item">
                <el-icon><Users /></el-icon>
                <span>招新{{ recruitment.quota }}人</span>
              </div>
              <div class="info-item">
                <el-icon><View /></el-icon>
                <span>{{ recruitment.viewCount }}</span>
              </div>
              <div class="info-item" v-if="recruitment.deadline">
                <el-icon><Clock /></el-icon>
                <span>{{ formatDate(recruitment.deadline) }}截止</span>
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
          layout="total, prev, pager, next"
          @current-change="loadRecruitments"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { queryRecruitments } from '@/api/club'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const recruitmentList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const searchKeyword = ref('')
const statusFilter = ref('')

const loadRecruitments = async () => {
  loading.value = true
  try {
    const res = await queryRecruitments({
      keyword: searchKeyword.value || undefined,
      status: statusFilter.value || undefined,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    recruitmentList.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    console.error('加载招新信息失败:', error)
  } finally {
    loading.value = false
  }
}

const goToDetail = (id: number) => {
  router.push(`/club-recruitment/${id}`)
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

onMounted(() => {
  loadRecruitments()
})
</script>

<style scoped>
.clubs-container {
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
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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

.recruitment-list {
  min-height: 400px;
}

.recruitment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.recruitment-card {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 12px;
}

.recruitment-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.recruitment-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  margin-right: 12px;
}

.club-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: #f093fb;
  font-weight: 500;
}

.club-name {
  font-size: 15px;
}

.recruitment-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.recruitment-footer {
  display: flex;
  gap: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.info-item {
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
  .recruitment-grid {
    grid-template-columns: 1fr;
  }
}
</style>
