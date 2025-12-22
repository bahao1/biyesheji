<template>
  <div class="club-manage-container">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">🎭 社团管理后台</h1>
        <div class="header-actions">
          <el-button @click="$router.push('/clubs')" round>
            <el-icon><ArrowLeft /></el-icon>
            <span>返回招新</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 社团信息卡片 -->
      <el-card shadow="hover" class="club-info-card">
        <template #header>
          <div class="card-header">
            <span>我的社团</span>
          </div>
        </template>
        <div v-if="myClub" class="club-info">
          <div class="info-row">
            <span class="label">社团名称:</span>
            <span class="value">{{ myClub.name }}</span>
          </div>
          <div class="info-row">
            <span class="label">社团类型:</span>
            <span class="value">{{ myClub.type }}</span>
          </div>
          <div class="info-row">
            <span class="label">成员数量:</span>
            <span class="value">{{ myClub.memberCount }}人</span>
          </div>
          <div class="info-row">
            <span class="label">社团简介:</span>
            <span class="value">{{ myClub.description }}</span>
          </div>
        </div>
        <el-empty v-else description="您还未管理任何社团" />
      </el-card>

      <!-- 招新管理 -->
      <el-card shadow="hover" class="manage-card">
        <template #header>
          <div class="card-header">
            <span>招新管理</span>
            <el-button type="primary" @click="showCreateDialog = true" size="small">
              <el-icon><Plus /></el-icon>
              发布招新
            </el-button>
          </div>
        </template>

        <el-table :data="recruitmentList" v-loading="loading" stripe>
          <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
          <el-table-column prop="quota" label="招新人数" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'OPEN' ? 'success' : 'info'">
                {{ row.status === 'OPEN' ? '招新中' : '已结束' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览量" width="100" />
          <el-table-column prop="createTime" label="发布时间" width="180" />
          <el-table-column label="操作" fixed="right" width="180">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-if="total > 0"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="loadRecruitments"
          />
        </div>
      </el-card>
    </div>

    <!-- 创建/编辑招新对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingRecruitment ? '编辑招新' : '发布招新'"
      width="700px"
    >
      <el-form :model="recruitmentForm" label-width="100px">
        <el-form-item label="招新标题" required>
          <el-input v-model="recruitmentForm.title" placeholder="请输入招新标题" />
        </el-form-item>
        <el-form-item label="招新内容" required>
          <el-input
            v-model="recruitmentForm.content"
            type="textarea"
            :rows="6"
            placeholder="详细介绍社团和招新要求"
          />
        </el-form-item>
        <el-form-item label="招新要求">
          <el-input
            v-model="recruitmentForm.requirements"
            type="textarea"
            :rows="4"
            placeholder="加入社团的要求"
          />
        </el-form-item>
        <el-form-item label="招新人数">
          <el-input-number v-model="recruitmentForm.quota" :min="1" />
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker
            v-model="recruitmentForm.deadline"
            type="datetime"
            placeholder="选择截止时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="recruitmentForm.contact" placeholder="微信、QQ等联系方式" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="recruitmentForm.status">
            <el-radio value="OPEN">招新中</el-radio>
            <el-radio value="CLOSED">已结束</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ editingRecruitment ? '保存' : '发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as clubApi from '@/api/club'

const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const myClub = ref<any>(null)
const recruitmentList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showCreateDialog = ref(false)
const editingRecruitment = ref<any>(null)

const recruitmentForm = reactive({
  title: '',
  content: '',
  requirements: '',
  quota: 10,
  deadline: null,
  contact: '',
  status: 'OPEN'
})

// 加载社团信息
const loadMyClub = async () => {
  try {
    const res = await clubApi.getMyClub()
    myClub.value = res.data
  } catch (error) {
    console.error('加载社团信息失败:', error)
  }
}

// 加载招新列表
const loadRecruitments = async () => {
  if (!myClub.value) return

  loading.value = true
  try {
    const res = await clubApi.queryRecruitments({
      clubId: myClub.value.id,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    recruitmentList.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    console.error('加载招新列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 编辑招新
const handleEdit = (recruitment: any) => {
  editingRecruitment.value = recruitment
  Object.assign(recruitmentForm, {
    title: recruitment.title,
    content: recruitment.content,
    requirements: recruitment.requirements,
    quota: recruitment.quota,
    deadline: recruitment.deadline ? new Date(recruitment.deadline) : null,
    contact: recruitment.contact,
    status: recruitment.status
  })
  showCreateDialog.value = true
}

// 提交表单
const handleSubmit = async () => {
  submitting.value = true
  try {
    if (editingRecruitment.value) {
      await clubApi.updateRecruitment(editingRecruitment.value.id, recruitmentForm)
      ElMessage.success('更新成功')
    } else {
      await clubApi.createRecruitment(recruitmentForm)
      ElMessage.success('发布成功')
    }
    showCreateDialog.value = false
    resetForm()
    loadRecruitments()
  } catch (error) {
    console.error('操作失败:', error)
  } finally {
    submitting.value = false
  }
}

// 删除招新
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条招新信息吗?', '提示', { type: 'warning' })
    await clubApi.deleteRecruitment(id)
    ElMessage.success('删除成功')
    loadRecruitments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 重置表单
const resetForm = () => {
  editingRecruitment.value = null
  Object.assign(recruitmentForm, {
    title: '',
    content: '',
    requirements: '',
    quota: 10,
    deadline: null,
    contact: '',
    status: 'OPEN'
  })
}

onMounted(async () => {
  await loadMyClub()
  if (myClub.value) {
    loadRecruitments()
  }
})
</script>

<style scoped>
.club-manage-container {
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

.main-content {
  max-width: 1400px;
  margin: 24px auto;
  padding: 0 24px;
}

.club-info-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.club-info {
  padding: 12px 0;
}

.info-row {
  display: flex;
  margin-bottom: 16px;
  font-size: 15px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  width: 100px;
  color: #909399;
}

.value {
  flex: 1;
  color: #303133;
  font-weight: 500;
}

.manage-card {
  border-radius: 12px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
