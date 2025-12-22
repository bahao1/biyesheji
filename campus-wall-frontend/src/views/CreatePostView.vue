<template>
  <div class="create-post-container">
    <!-- 返回按钮 -->
    <el-button @click="$router.back()" class="back-btn" circle>
      <el-icon><ArrowLeft /></el-icon>
    </el-button>

    <el-card class="create-post-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Edit /></el-icon>
            <h2>发布帖子</h2>
          </div>
          <div class="header-tip">分享你的想法和故事</div>
        </div>
      </template>

      <el-form :model="postForm" :rules="rules" ref="formRef" label-width="100px" class="post-form">
        <el-form-item label="帖子标题" prop="title">
          <el-input
            v-model="postForm.title"
            placeholder="给你的帖子起个标题吧..."
            size="large"
            maxlength="100"
            show-word-limit
          >
            <template #prefix>
              <el-icon><Document /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="帖子分类" prop="category">
          <el-select
            v-model="postForm.category"
            placeholder="选择一个合适的分类"
            size="large"
            style="width: 100%"
          >
            <el-option label="📝 普通帖子" value="GENERAL">
              <span class="option-content">
                <el-icon><Document /></el-icon>
                <span>普通帖子</span>
              </span>
            </el-option>
            <el-option label="❤️ 表白墙" value="CONFESSION">
              <span class="option-content">
                <el-icon><ChatDotRound /></el-icon>
                <span>表白墙</span>
              </span>
            </el-option>
            <el-option label="🔍 失物招领" value="LOST_FOUND">
              <span class="option-content">
                <el-icon><Search /></el-icon>
                <span>失物招领</span>
              </span>
            </el-option>
            <el-option label="❓ 求助" value="HELP">
              <span class="option-content">
                <el-icon><QuestionFilled /></el-icon>
                <span>求助</span>
              </span>
            </el-option>
            <el-option label="📅 活动" value="ACTIVITY">
              <span class="option-content">
                <el-icon><Calendar /></el-icon>
                <span>活动</span>
              </span>
            </el-option>
            <el-option label="📚 学习交流" value="STUDY">
              <span class="option-content">
                <el-icon><Reading /></el-icon>
                <span>学习交流</span>
              </span>
            </el-option>
            <el-option label="☕ 生活分享" value="LIFE">
              <span class="option-content">
                <el-icon><Coffee /></el-icon>
                <span>生活分享</span>
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="帖子内容" prop="content">
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="12"
            placeholder="写下你想分享的内容...&#10;&#10;支持换行和长文本"
            maxlength="5000"
            show-word-limit
            class="content-textarea"
          />
        </el-form-item>

        <el-form-item label="图片">
          <ImageUpload v-model="postForm.images" :limit="9" />
          <div class="upload-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>最多可上传9张图片，每张图片不超过10MB</span>
          </div>
        </el-form-item>

        <el-form-item label="发布设置">
          <div class="settings-section">
            <div class="setting-item">
              <div class="setting-label">
                <el-icon><Hide /></el-icon>
                <span>匿名发布</span>
              </div>
              <el-switch
                v-model="postForm.isAnonymous"
                active-text="开启"
                inactive-text="关闭"
              />
            </div>
            <div class="setting-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>开启后，你的昵称将显示为"匿名用户"</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item class="submit-section">
          <el-button
            type="primary"
            @click="handleSubmit"
            :loading="loading"
            size="large"
            round
            class="submit-btn"
          >
            <el-icon v-if="!loading"><Promotion /></el-icon>
            {{ loading ? '发布中...' : '立即发布' }}
          </el-button>
          <el-button
            @click="$router.back()"
            size="large"
            round
            class="cancel-btn"
          >
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { createPost } from '@/api/post'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import ImageUpload from '@/components/ImageUpload.vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const postForm = reactive({
  title: '',
  content: '',
  category: 'GENERAL',
  isAnonymous: false,
  images: [] as string[]
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await createPost(postForm)
        ElMessage.success('发布成功')
        router.push('/')
      } catch (error) {
        console.error('发布失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.create-post-container {
  min-height: 100vh;
  background: linear-gradient(to bottom, #f5f7fa 0%, #e8eef5 100%);
  padding: 40px 20px;
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

.create-post-card {
  max-width: 900px;
  margin: 0 auto;
  border-radius: 16px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 24px;
  color: #409eff;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.header-tip {
  font-size: 14px;
  color: #909399;
}

.post-form {
  padding: 20px 0;
}

.post-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  font-size: 15px;
}

.post-form :deep(.el-input__inner) {
  border-radius: 8px;
}

.post-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  font-size: 15px;
  line-height: 1.8;
}

.content-textarea :deep(.el-textarea__inner) {
  min-height: 300px !important;
}

.option-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.settings-section {
  width: 100%;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.setting-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.setting-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border-left: 3px solid #409eff;
}

.upload-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
  margin-top: 12px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.submit-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.submit-section :deep(.el-form-item__content) {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.submit-btn {
  min-width: 160px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #5568d3 0%, #6a3f8f 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.cancel-btn {
  min-width: 120px;
  height: 48px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .create-post-container {
    padding: 20px 12px;
  }

  .back-btn {
    top: 60px;
    left: 12px;
  }

  .create-post-card {
    border-radius: 12px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .header-tip {
    font-size: 13px;
  }

  .post-form {
    padding: 12px 0;
  }

  .post-form :deep(.el-form-item__label) {
    font-size: 14px;
  }

  .settings-section {
    padding: 16px;
  }

  .submit-section :deep(.el-form-item__content) {
    flex-direction: column;
  }

  .submit-btn,
  .cancel-btn {
    width: 100%;
  }
}
</style>
