<template>
  <div class="profile-container">
    <!-- 返回按钮 -->
    <el-button @click="$router.back()" class="back-btn" circle>
      <el-icon><ArrowLeft /></el-icon>
    </el-button>

    <!-- 用户信息卡片 -->
    <el-card class="profile-card" shadow="hover">
      <div class="profile-header">
        <div class="avatar-section">
          <el-avatar :size="100" class="user-avatar">
            {{ userStore.user?.nickname?.charAt(0) }}
          </el-avatar>
          <div class="user-basic-info">
            <h2 class="user-nickname">{{ userStore.user?.nickname }}</h2>
            <p class="user-username">@{{ userStore.user?.username }}</p>
            <el-tag :type="getRoleType(userStore.user?.role)" size="large" class="role-tag">
              {{ getRoleName(userStore.user?.role) }}
            </el-tag>
          </div>
        </div>
      </div>

      <el-divider />

      <!-- 详细信息 -->
      <div class="profile-details">
        <div class="detail-section">
          <h3 class="section-title">
            <el-icon><User /></el-icon>
            基本信息
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <div class="info-label">
                <el-icon><User /></el-icon>
                <span>用户名</span>
              </div>
              <div class="info-value">{{ userStore.user?.username }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">
                <el-icon><Postcard /></el-icon>
                <span>昵称</span>
              </div>
              <div class="info-value">{{ userStore.user?.nickname }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">
                <el-icon><Message /></el-icon>
                <span>邮箱</span>
              </div>
              <div class="info-value">{{ userStore.user?.email || '未设置' }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">
                <el-icon><Phone /></el-icon>
                <span>手机号</span>
              </div>
              <div class="info-value">{{ userStore.user?.phone || '未设置' }}</div>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 账户信息 -->
        <div class="detail-section">
          <h3 class="section-title">
            <el-icon><Setting /></el-icon>
            账户信息
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <div class="info-label">
                <el-icon><Medal /></el-icon>
                <span>角色权限</span>
              </div>
              <div class="info-value">
                <el-tag :type="getRoleType(userStore.user?.role)">
                  {{ getRoleName(userStore.user?.role) }}
                </el-tag>
              </div>
            </div>
            <div class="info-item">
              <div class="info-label">
                <el-icon><Calendar /></el-icon>
                <span>注册时间</span>
              </div>
              <div class="info-value">{{ formatDate(userStore.user?.createTime) }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">
                <el-icon><CircleCheck /></el-icon>
                <span>账户状态</span>
              </div>
              <div class="info-value">
                <el-tag type="success">正常</el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-section">
        <el-button type="primary" size="large" round @click="showEditDialog = true">
          <el-icon><Edit /></el-icon>
          编辑资料
        </el-button>
        <el-button size="large" round @click="showPasswordDialog = true">
          <el-icon><Lock /></el-icon>
          修改密码
        </el-button>
      </div>
    </el-card>

    <!-- 编辑资料对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑资料"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateProfile" :loading="updating">
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="showPasswordDialog"
      title="修改密码"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="原密码">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码（至少6位）"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">
          确认修改
        </el-button>
      </template>
    </el-dialog>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card" shadow="hover" @click="$router.push('/my-posts')">
        <div class="stat-content">
          <el-icon class="stat-icon" color="#409eff"><Document /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.postCount }}</div>
            <div class="stat-label">我的帖子</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover" @click="$router.push('/my-products')">
        <div class="stat-content">
          <el-icon class="stat-icon" color="#f093fb"><ShoppingCart /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.productCount }}</div>
            <div class="stat-label">我的商品</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover" @click="$router.push('/my-favorites')">
        <div class="stat-content">
          <el-icon class="stat-icon" color="#67c23a"><Star /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.favoriteCount }}</div>
            <div class="stat-label">我的收藏</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover" @click="$router.push('/my-comments')">
        <div class="stat-content">
          <el-icon class="stat-icon" color="#e6a23c"><ChatDotRound /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.commentCount }}</div>
            <div class="stat-label">我的评论</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <el-icon class="stat-icon" color="#f56c6c"><StarFilled /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.likeCount }}</div>
            <div class="stat-label">获赞总数</div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateUser, changePassword } from '@/api/user'
import { getUserPosts, getFavoritePosts } from '@/api/post'
import { getUserProducts } from '@/api/product'
import { getCommentsByUserId } from '@/api/comment'

const router = useRouter()
const userStore = useUserStore()

// 对话框控制
const showEditDialog = ref(false)
const showPasswordDialog = ref(false)

// 统计数据
const stats = ref({
  postCount: 0,
  productCount: 0,
  favoriteCount: 0,
  commentCount: 0,
  likeCount: 0
})

// 编辑资料表单
const editForm = ref({
  nickname: '',
  email: '',
  phone: ''
})

// 修改密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 加载状态
const updating = ref(false)
const changingPassword = ref(false)

// 打开编辑对话框时初始化表单
const openEditDialog = () => {
  editForm.value = {
    nickname: userStore.user?.nickname || '',
    email: userStore.user?.email || '',
    phone: userStore.user?.phone || ''
  }
  showEditDialog.value = true
}

// 更新用户资料
const handleUpdateProfile = async () => {
  if (!editForm.value.nickname) {
    ElMessage.warning('昵称不能为空')
    return
  }

  updating.value = true
  try {
    const updatedUser = await updateUser(userStore.user?.id!, editForm.value)
    ElMessage.success('资料更新成功')
    userStore.user = updatedUser.data
    showEditDialog.value = false
  } catch (error: any) {
    console.error('更新资料失败:', error)
    ElMessage.error(error.response?.data?.message || '更新失败')
  } finally {
    updating.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordForm.value.oldPassword) {
    ElMessage.warning('请输入原密码')
    return
  }

  if (!passwordForm.value.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }

  if (passwordForm.value.newPassword.length < 6) {
    ElMessage.warning('新密码长度不能少于6位')
    return
  }

  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  changingPassword.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    showPasswordDialog.value = false
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
    // 清除登录状态，跳转到登录页
    setTimeout(() => {
      userStore.logout()
      router.push('/login')
    }, 1500)
  } catch (error: any) {
    console.error('修改密码失败:', error)
    ElMessage.error(error.response?.data?.message || '修改失败')
  } finally {
    changingPassword.value = false
  }
}

const getRoleName = (role: string) => {
  const map: Record<string, string> = {
    USER: '普通用户',
    CLUB_ADMIN: '社团管理员',
    SUPER_ADMIN: '超级管理员'
  }
  return map[role] || role
}

const getRoleType = (role: string) => {
  const map: Record<string, any> = {
    USER: '',
    CLUB_ADMIN: 'warning',
    SUPER_ADMIN: 'danger'
  }
  return map[role] || ''
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '未知'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 加载统计数据
const loadStats = async () => {
  if (!userStore.user?.id) return

  try {
    // 获取帖子数量 (后端API的page参数从1开始)
    const postsRes = await getUserPosts(userStore.user.id, 1, 1)
    stats.value.postCount = postsRes.data?.totalElements || 0

    // 获取商品数量 (后端API的page参数从1开始)
    const productsRes = await getUserProducts(userStore.user.id, { page: 1, pageSize: 1 })
    stats.value.productCount = productsRes.data?.totalElements || 0

    // 获取收藏数量
    try {
      const favoritesRes = await getFavoritePosts(1, 1)
      stats.value.favoriteCount = favoritesRes.data?.totalElements || 0
    } catch (error) {
      console.error('获取收藏数量失败:', error)
      stats.value.favoriteCount = 0
    }

    // 获取评论数量（暂时禁用，等待后端修复）
    // TODO: 后端 /comment/user 接口需要修复
    stats.value.commentCount = 0
    
    // try {
    //   const commentsRes = await getCommentsByUserId({
    //     page: 1,
    //     pageSize: 1
    //   })
    //   console.log('评论API响应:', commentsRes)
    //   stats.value.commentCount = commentsRes.data?.totalElements || 0
    // } catch (error) {
    //   console.error('获取评论数量失败:', error)
    //   stats.value.commentCount = 0
    // }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 页面加载时获取统计数据
onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.profile-container {
  max-width: 1000px;
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

.profile-card {
  margin-bottom: 24px;
  border-radius: 16px;
  overflow: hidden;
}

.profile-header {
  padding: 20px 0;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.user-avatar {
  font-size: 40px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 4px solid #f5f7fa;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-basic-info {
  flex: 1;
}

.user-nickname {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.user-username {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #909399;
}

.role-tag {
  font-size: 14px;
  padding: 8px 16px;
}

.profile-details {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.info-item {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.info-item:hover {
  background: #e8eef5;
  transform: translateY(-2px);
}

.info-label {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.info-value {
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.action-section {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.action-section .el-button {
  min-width: 140px;
  height: 44px;
  font-size: 15px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.stat-card {
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px;
}

.stat-icon {
  font-size: 48px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

@media (max-width: 768px) {
  .profile-container {
    padding: 0 12px;
  }

  .back-btn {
    top: 60px;
    left: 12px;
  }

  .avatar-section {
    flex-direction: column;
    text-align: center;
  }

  .user-nickname {
    font-size: 24px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .action-section {
    flex-direction: column;
  }

  .action-section .el-button {
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
