<template>
  <div class="admin-container">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">🛡️ 管理员后台</h1>
        <div class="header-actions">
          <el-button @click="$router.push('/')" round>
            <el-icon><House /></el-icon>
            <span>返回首页</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 统计卡片 -->
      <div class="statistics-cards">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
              <div class="stat-sub">活跃: {{ statistics.activeUsers }}</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon post-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalPosts }}</div>
              <div class="stat-label">总帖子数</div>
              <div class="stat-sub">未删除: {{ statistics.activePosts }}</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon product-icon">
              <el-icon><ShoppingBag /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalProducts }}</div>
              <div class="stat-label">总商品数</div>
              <div class="stat-sub">未删除: {{ statistics.activeProducts }}</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon comment-icon">
              <el-icon><ChatLineSquare /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalComments }}</div>
              <div class="stat-label">总评论数</div>
              <div class="stat-sub">未删除: {{ statistics.activeComments }}</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 管理标签页 -->
      <el-card class="manage-card" shadow="hover">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <!-- 用户管理 -->
          <el-tab-pane label="用户管理" name="users">
            <div class="search-bar">
              <el-input
                v-model="userSearch"
                placeholder="搜索用户名或昵称..."
                style="width: 300px; margin-right: 12px"
                clearable
                @keyup.enter="loadUsers"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-select v-model="roleFilter" placeholder="角色筛选" style="width: 150px; margin-right: 12px" clearable>
                <el-option label="普通用户" value="USER" />
                <el-option label="社团管理员" value="CLUB_ADMIN" />
                <el-option label="超级管理员" value="SUPER_ADMIN" />
              </el-select>
              <el-button type="primary" @click="loadUsers">搜索</el-button>
            </div>

            <el-table :data="userList" v-loading="loading" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="username" label="用户名" width="150" />
              <el-table-column prop="nickname" label="昵称" width="150" />
              <el-table-column prop="email" label="邮箱" width="200" />
              <el-table-column prop="phone" label="手机号" width="130" />
              <el-table-column label="角色" width="120">
                <template #default="{ row }">
                  <el-tag v-if="row.role === 'SUPER_ADMIN'" type="danger">超级管理员</el-tag>
                  <el-tag v-else-if="row.role === 'CLUB_ADMIN'" type="warning">社团管理员</el-tag>
                  <el-tag v-else type="info">普通用户</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.disabled ? 'danger' : 'success'">
                    {{ row.disabled ? '已禁用' : '正常' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="注册时间" width="180" />
              <el-table-column label="操作" fixed="right" width="250">
                <template #default="{ row }">
                  <el-button
                    v-if="!row.disabled"
                    type="danger"
                    size="small"
                    @click="handleUserStatus(row.id, true)"
                  >
                    禁用
                  </el-button>
                  <el-button
                    v-else
                    type="success"
                    size="small"
                    @click="handleUserStatus(row.id, false)"
                  >
                    启用
                  </el-button>
                  <el-button
                    v-if="row.role !== 'CLUB_ADMIN'"
                    type="warning"
                    size="small"
                    @click="openAssignDialog(row.id)"
                  >
                    分配社团
                  </el-button>
                  <el-button
                    v-else
                    type="info"
                    size="small"
                    @click="handleRemoveClubAdmin(row.id)"
                  >
                    取消管理员
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination">
              <el-pagination
                v-model:current-page="userPage"
                v-model:page-size="userPageSize"
                :total="userTotal"
                layout="total, sizes, prev, pager, next"
                @current-change="loadUsers"
                @size-change="loadUsers"
              />
            </div>
          </el-tab-pane>

          <!-- 帖子管理 -->
          <el-tab-pane label="帖子管理" name="posts">
            <div class="search-bar">
              <el-input
                v-model="postSearch"
                placeholder="搜索帖子标题或内容..."
                style="width: 300px; margin-right: 12px"
                clearable
                @keyup.enter="loadPosts"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="loadPosts">搜索</el-button>
            </div>

            <el-table :data="postList" v-loading="loading" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
              <el-table-column prop="authorNickname" label="作者" width="120" />
              <el-table-column label="分类" width="100">
                <template #default="{ row }">
                  <el-tag>{{ getCategoryName(row.category) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag v-if="row.deleted" type="danger">已删除</el-tag>
                  <el-tag v-else type="success">正常</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="viewCount" label="浏览" width="80" />
              <el-table-column prop="likeCount" label="点赞" width="80" />
              <el-table-column prop="commentCount" label="评论" width="80" />
              <el-table-column prop="createTime" label="发布时间" width="180" />
              <el-table-column label="操作" fixed="right" width="100">
                <template #default="{ row }">
                  <el-button
                    v-if="!row.deleted"
                    type="danger"
                    size="small"
                    @click="handleDeletePost(row.id)"
                  >
                    删除
                  </el-button>
                  <el-tag v-else type="info" size="small">已删除</el-tag>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination">
              <el-pagination
                v-model:current-page="postPage"
                v-model:page-size="postPageSize"
                :total="postTotal"
                layout="total, sizes, prev, pager, next"
                @current-change="loadPosts"
                @size-change="loadPosts"
              />
            </div>
          </el-tab-pane>

          <!-- 商品管理 -->
          <el-tab-pane label="商品管理" name="products">
            <div class="search-bar">
              <el-input
                v-model="productSearch"
                placeholder="搜索商品标题或描述..."
                style="width: 300px; margin-right: 12px"
                clearable
                @keyup.enter="loadProducts"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="loadProducts">搜索</el-button>
            </div>

            <el-table :data="productList" v-loading="loading" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
              <el-table-column prop="sellerNickname" label="卖家" width="120" />
              <el-table-column prop="price" label="价格" width="100">
                <template #default="{ row }">
                  <span class="price">¥{{ row.price }}</span>
                </template>
              </el-table-column>
              <el-table-column label="商品状态" width="100">
                <template #default="{ row }">
                  <el-tag v-if="row.status === 'ON_SALE'" type="success">在售</el-tag>
                  <el-tag v-else-if="row.status === 'SOLD'" type="info">已售出</el-tag>
                  <el-tag v-else type="warning">已下架</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="删除状态" width="100">
                <template #default="{ row }">
                  <el-tag v-if="row.deleted" type="danger">已删除</el-tag>
                  <el-tag v-else type="success">正常</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="viewCount" label="浏览" width="80" />
              <el-table-column prop="createTime" label="发布时间" width="180" />
              <el-table-column label="操作" fixed="right" width="100">
                <template #default="{ row }">
                  <el-button
                    v-if="!row.deleted"
                    type="danger"
                    size="small"
                    @click="handleDeleteProduct(row.id)"
                  >
                    删除
                  </el-button>
                  <el-tag v-else type="info" size="small">已删除</el-tag>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination">
              <el-pagination
                v-model:current-page="productPage"
                v-model:page-size="productPageSize"
                :total="productTotal"
                layout="total, sizes, prev, pager, next"
                @current-change="loadProducts"
                @size-change="loadProducts"
              />
            </div>
          </el-tab-pane>

          <!-- 评论管理 -->
          <el-tab-pane label="评论管理" name="comments">
            <div class="search-bar">
              <el-input
                v-model="commentSearch"
                placeholder="搜索评论内容..."
                style="width: 300px; margin-right: 12px"
                clearable
                @keyup.enter="loadComments"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="loadComments">搜索</el-button>
            </div>

            <el-table :data="commentList" v-loading="loading" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="content" label="内容" width="300" show-overflow-tooltip />
              <el-table-column prop="authorNickname" label="作者" width="120" />
              <el-table-column prop="postId" label="帖子ID" width="100" />
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag v-if="row.deleted" type="danger">已删除</el-tag>
                  <el-tag v-else type="success">正常</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="likeCount" label="点赞" width="80" />
              <el-table-column prop="createTime" label="评论时间" width="180" />
              <el-table-column label="操作" fixed="right" width="100">
                <template #default="{ row }">
                  <el-button
                    v-if="!row.deleted"
                    type="danger"
                    size="small"
                    @click="handleDeleteComment(row.id)"
                  >
                    删除
                  </el-button>
                  <el-tag v-else type="info" size="small">已删除</el-tag>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination">
              <el-pagination
                v-model:current-page="commentPage"
                v-model:page-size="commentPageSize"
                :total="commentTotal"
                layout="total, sizes, prev, pager, next"
                @current-change="loadComments"
                @size-change="loadComments"
              />
            </div>
          </el-tab-pane>

          <!-- 社团管理 -->
          <el-tab-pane label="社团管理" name="clubs">
            <div class="search-bar">
              <el-input
                v-model="clubSearch"
                placeholder="搜索社团名称..."
                style="width: 300px; margin-right: 12px"
                clearable
                @keyup.enter="loadClubs"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="loadClubs">搜索</el-button>
              <el-button type="success" @click="showCreateClubDialog = true" style="margin-left: 12px">
                <el-icon><Plus /></el-icon>
                <span>创建社团</span>
              </el-button>
            </div>

            <el-table :data="clubList" v-loading="loading" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="name" label="社团名称" width="180" show-overflow-tooltip />
              <el-table-column prop="type" label="类型" width="120" />
              <el-table-column prop="presidentId" label="负责人ID" width="100" />
              <el-table-column prop="memberCount" label="成员数" width="100" />
              <el-table-column prop="description" label="简介" min-width="200" show-overflow-tooltip />
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
                    {{ row.status === 'ACTIVE' ? '活跃' : '停用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column label="操作" fixed="right" width="100">
                <template #default="{ row }">
                  <el-button type="danger" size="small" @click="handleDeleteClub(row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination">
              <el-pagination
                v-model:current-page="clubPage"
                v-model:page-size="clubPageSize"
                :total="clubTotal"
                layout="total, sizes, prev, pager, next"
                @current-change="loadClubs"
                @size-change="loadClubs"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 创建社团对话框 -->
    <el-dialog v-model="showCreateClubDialog" title="创建社团" width="600px">
      <el-form :model="clubForm" label-width="100px">
        <el-form-item label="社团名称" required>
          <el-input v-model="clubForm.name" placeholder="请输入社团名称" />
        </el-form-item>
        <el-form-item label="社团类型" required>
          <el-select v-model="clubForm.type" placeholder="请选择社团类型" style="width: 100%">
            <el-option label="文艺类" value="文艺类" />
            <el-option label="体育类" value="体育类" />
            <el-option label="学术类" value="学术类" />
            <el-option label="公益类" value="公益类" />
            <el-option label="实践类" value="实践类" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人ID">
          <el-input-number v-model="clubForm.presidentId" :min="1" placeholder="请输入用户ID（选填）" style="width: 100%" />
        </el-form-item>
        <el-form-item label="社团简介" required>
          <el-input
            v-model="clubForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入社团简介"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateClubDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateClub" :loading="submitting">创建</el-button>
      </template>
    </el-dialog>

    <!-- 分配社团管理员对话框 -->
    <el-dialog v-model="showAssignAdminDialog" title="分配社团管理员" width="500px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="用户ID" required>
          <el-input-number v-model="assignForm.userId" :min="1" placeholder="请输入用户ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="社团ID" required>
          <el-input-number v-model="assignForm.clubId" :min="1" placeholder="请输入社团ID" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAssignAdminDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAssignClubAdmin" :loading="submitting">分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as adminApi from '@/api/admin'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const activeTab = ref('users')

// 统计数据
const statistics = reactive({
  totalUsers: 0,
  activeUsers: 0,
  totalPosts: 0,
  activePosts: 0,
  totalProducts: 0,
  activeProducts: 0,
  totalComments: 0,
  activeComments: 0
})

// 用户管理
const userList = ref<any[]>([])
const userSearch = ref('')
const roleFilter = ref('')
const userPage = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)

// 帖子管理
const postList = ref<any[]>([])
const postSearch = ref('')
const postPage = ref(1)
const postPageSize = ref(10)
const postTotal = ref(0)

// 商品管理
const productList = ref<any[]>([])
const productSearch = ref('')
const productPage = ref(1)
const productPageSize = ref(10)
const productTotal = ref(0)

// 评论管理
const commentList = ref<any[]>([])
const commentSearch = ref('')
const commentPage = ref(1)
const commentPageSize = ref(10)
const commentTotal = ref(0)

// 社团管理
const clubList = ref<any[]>([])
const clubSearch = ref('')
const clubPage = ref(1)
const clubPageSize = ref(10)
const clubTotal = ref(0)
const showCreateClubDialog = ref(false)
const showAssignAdminDialog = ref(false)

const clubForm = reactive({
  name: '',
  type: '',
  presidentId: null,
  description: ''
})

const assignForm = reactive({
  userId: null as number | null,
  clubId: null as number | null
})

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await adminApi.getStatistics()
    Object.assign(statistics, res.data)
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const res = await adminApi.getUsers({
      keyword: userSearch.value || undefined,
      role: roleFilter.value || undefined,
      page: userPage.value,
      pageSize: userPageSize.value
    })
    userList.value = res.data.content
    userTotal.value = res.data.totalElements
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 更新用户状态
const handleUserStatus = async (userId: number, disabled: boolean) => {
  try {
    await ElMessageBox.confirm(
      `确定要${disabled ? '禁用' : '启用'}该用户吗?`,
      '提示',
      { type: 'warning' }
    )
    await adminApi.updateUserStatus(userId, disabled)
    ElMessage.success('操作成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新用户状态失败:', error)
    }
  }
}

// 加载帖子列表
const loadPosts = async () => {
  loading.value = true
  try {
    const res = await adminApi.getPosts({
      keyword: postSearch.value || undefined,
      page: postPage.value,
      pageSize: postPageSize.value
    })
    postList.value = res.data.content
    postTotal.value = res.data.totalElements
  } catch (error) {
    console.error('加载帖子列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 删除帖子
const handleDeletePost = async (postId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该帖子吗?', '提示', { type: 'warning' })
    await adminApi.deletePost(postId)
    ElMessage.success('删除成功')
    loadPosts()
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除帖子失败:', error)
    }
  }
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const res = await adminApi.getProducts({
      keyword: productSearch.value || undefined,
      page: productPage.value,
      pageSize: productPageSize.value
    })
    productList.value = res.data.content
    productTotal.value = res.data.totalElements
  } catch (error) {
    console.error('加载商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 删除商品
const handleDeleteProduct = async (productId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗?', '提示', { type: 'warning' })
    await adminApi.deleteProduct(productId)
    ElMessage.success('删除成功')
    loadProducts()
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除商品失败:', error)
    }
  }
}

// 加载评论列表
const loadComments = async () => {
  loading.value = true
  try {
    const res = await adminApi.getComments({
      keyword: commentSearch.value || undefined,
      page: commentPage.value,
      pageSize: commentPageSize.value
    })
    commentList.value = res.data.content
    commentTotal.value = res.data.totalElements
  } catch (error) {
    console.error('加载评论列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 删除评论
const handleDeleteComment = async (commentId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该评论吗?', '提示', { type: 'warning' })
    await adminApi.deleteComment(commentId)
    ElMessage.success('删除成功')
    loadComments()
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
    }
  }
}

// 标签页切换
const handleTabChange = (tabName: string) => {
  switch (tabName) {
    case 'users':
      loadUsers()
      break
    case 'posts':
      loadPosts()
      break
    case 'products':
      loadProducts()
      break
    case 'comments':
      loadComments()
      break
    case 'clubs':
      loadClubs()
      break
  }
}

// 加载社团列表
const loadClubs = async () => {
  loading.value = true
  try {
    const res = await adminApi.getClubs({
      keyword: clubSearch.value || undefined,
      page: clubPage.value,
      pageSize: clubPageSize.value
    })
    clubList.value = res.data.content
    clubTotal.value = res.data.totalElements
  } catch (error) {
    console.error('加载社团列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 创建社团
const handleCreateClub = async () => {
  submitting.value = true
  try {
    await adminApi.createClub(clubForm)
    ElMessage.success('创建成功')
    showCreateClubDialog.value = false
    Object.assign(clubForm, { name: '', type: '', presidentId: null, description: '' })
    loadClubs()
  } catch (error) {
    console.error('创建社团失败:', error)
  } finally {
    submitting.value = false
  }
}

// 删除社团
const handleDeleteClub = async (clubId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该社团吗?', '提示', { type: 'warning' })
    await adminApi.deleteClub(clubId)
    ElMessage.success('删除成功')
    loadClubs()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除社团失败:', error)
    }
  }
}

// 分配社团管理员
const openAssignDialog = (userId: number) => {
  assignForm.userId = userId
  assignForm.clubId = null
  showAssignAdminDialog.value = true
}

const handleAssignClubAdmin = async () => {
  submitting.value = true
  try {
    await adminApi.assignClubAdmin(assignForm.userId!, assignForm.clubId!)
    ElMessage.success('分配成功')
    showAssignAdminDialog.value = false
    Object.assign(assignForm, { userId: null, clubId: null })
    loadUsers()
  } catch (error) {
    console.error('分配社团管理员失败:', error)
  } finally {
    submitting.value = false
  }
}

// 取消社团管理员
const handleRemoveClubAdmin = async (userId: number) => {
  try {
    await ElMessageBox.confirm('确定要取消该用户的社团管理员权限吗?', '提示', { type: 'warning' })
    await adminApi.removeClubAdmin(userId)
    ElMessage.success('取消成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消社团管理员失败:', error)
    }
  }
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

onMounted(() => {
  loadStatistics()
  loadUsers()
})
</script>

<style scoped>
.admin-container {
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
  max-width: 1400px;
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
  text-shadow: 0 2px 10px rgba(102, 126, 234, 0.3);
  animation: logoFloat 3s ease-in-out infinite;
}

@keyframes logoFloat {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-5px); }
}

.main-content {
  max-width: 1400px;
  margin: 24px auto;
  padding: 0 24px;
}

.statistics-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.3);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 4px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.stat-card:hover .stat-icon {
  transform: rotate(360deg) scale(1.1);
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.post-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.product-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.comment-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  line-height: 1;
  margin-bottom: 8px;
  animation: countUp 0.8s ease-out;
}

@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  margin-bottom: 4px;
}

.stat-sub {
  font-size: 12px;
  color: #67c23a;
  font-weight: 600;
}

.manage-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  overflow: hidden;
}

.search-bar {
  margin-bottom: 20px;
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.price {
  color: #f56c6c;
  font-weight: 700;
  font-size: 16px;
}

/* Element Plus styling overrides */
:deep(.el-table) {
  background: transparent;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: white !important;
  font-weight: 600;
  border: none !important;
}

:deep(.el-table tr:hover) {
  background: rgba(102, 126, 234, 0.08) !important;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
}

:deep(.el-tabs__item.is-active) {
  color: #667eea;
  font-weight: 700;
}

:deep(.el-tabs__item:hover) {
  color: #764ba2;
}

:deep(.el-tabs__active-bar) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  height: 3px;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

:deep(.el-card__body) {
  padding: 24px;
}

:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  margin: 0;
}

:deep(.el-dialog__title) {
  color: white;
  font-weight: 700;
  font-size: 20px;
}

:deep(.el-dialog__close) {
  color: white;
  font-size: 20px;
}

:deep(.el-input__inner), :deep(.el-textarea__inner) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__inner:focus), :deep(.el-textarea__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 10px rgba(102, 126, 234, 0.3);
}

@media (max-width: 1200px) {
  .statistics-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .statistics-cards {
    grid-template-columns: 1fr;
  }

  .logo {
    font-size: 22px;
  }

  .stat-value {
    font-size: 28px;
  }
}
</style>
