<template>
  <div class="love-letter-container">
    <el-card class="love-letter-card">
      <template #header>
        <div class="card-header">
          <h2>AI情书生成器 💌</h2>
          <p class="subtitle">一键生成浪漫情话，助你表白成功</p>
        </div>
      </template>

      <el-form :model="form" label-width="100px" class="love-letter-form">
        <el-form-item label="对方名字">
          <el-input
            v-model="form.targetName"
            placeholder="请输入对方的名字"
            clearable
          />
        </el-form-item>

        <el-form-item label="关键词">
          <div class="keywords-input">
            <el-tag
              v-for="keyword in form.keywords"
              :key="keyword"
              closable
              @close="removeKeyword(keyword)"
              class="keyword-tag"
            >
              {{ keyword }}
            </el-tag>
            <el-input
              v-if="showKeywordInput"
              ref="keywordInputRef"
              v-model="newKeyword"
              size="small"
              @keyup.enter="addKeyword"
              @blur="addKeyword"
              placeholder="输入关键词"
              class="keyword-input"
            />
            <el-button
              v-else
              size="small"
              @click="showKeywordInput = true"
              class="add-keyword-btn"
            >
              + 添加关键词
            </el-button>
          </div>
          <div class="form-tip">添加一些关键词，如：温柔、可爱、聪明等</div>
        </el-form-item>

        <el-form-item label="情书风格">
          <el-radio-group v-model="form.style">
            <el-radio value="funny">沙雕风 😆</el-radio>
            <el-radio value="romantic">深情风 💕</el-radio>
            <el-radio value="cute">土味风 🌻</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="generateLetter"
            :loading="letterLoading"
            :disabled="!canGenerate"
            size="large"
            class="generate-btn"
          >
            {{ letterLoading ? '生成中...' : '生成情书' }}
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider v-if="letterResult">生成结果</el-divider>

      <div v-if="letterResult" class="result-container">
        <div class="result-content">
          {{ letterResult }}
        </div>
        <div class="result-actions">
          <el-button @click="copyResult(letterResult)" icon="DocumentCopy">
            复制内容
          </el-button>
          <el-button type="primary" @click="shareToWall">
            发布到表白墙
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card class="poem-card">
      <template #header>
        <div class="card-header">
          <h2>藏头诗生成器 📜</h2>
          <p class="subtitle">输入2-4个字，生成专属藏头诗</p>
        </div>
      </template>

      <el-form :model="poemForm" label-width="100px" class="poem-form">
        <el-form-item label="藏头文字">
          <el-input
            v-model="poemForm.text"
            placeholder="输入2-4个字，如：我爱你"
            maxlength="4"
            show-word-limit
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="generatePoem"
            :loading="poemLoading"
            :disabled="!canGeneratePoem"
            size="large"
            class="generate-btn"
          >
            {{ poemLoading ? '生成中...' : '生成藏头诗' }}
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider v-if="poemResult">生成结果</el-divider>

      <div v-if="poemResult" class="result-container">
        <div class="result-content poem-content">
          {{ poemResult }}
        </div>
        <div class="result-actions">
          <el-button @click="copyResult(poemResult)" icon="DocumentCopy">
            复制内容
          </el-button>
          <el-button type="primary" @click="shareToWall">
            发布到表白墙
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { generateLoveLetter, generateAcrosticPoem } from '@/api/ai'

const router = useRouter()

// 情书表单
const form = ref({
  targetName: '',
  keywords: [] as string[],
  style: 'romantic'
})

const newKeyword = ref('')
const showKeywordInput = ref(false)
const keywordInputRef = ref()
const letterLoading = ref(false)
const letterResult = ref('')

// 藏头诗表单
const poemForm = ref({
  text: ''
})

const poemLoading = ref(false)
const poemResult = ref('')

// 是否可以生成情书
const canGenerate = computed(() => {
  return form.value.targetName && form.value.keywords.length > 0
})

// 是否可以生成藏头诗
const canGeneratePoem = computed(() => {
  return poemForm.value.text.length >= 2 && poemForm.value.text.length <= 4
})

// 添加关键词
const addKeyword = () => {
  if (newKeyword.value && !form.value.keywords.includes(newKeyword.value)) {
    form.value.keywords.push(newKeyword.value)
    newKeyword.value = ''
  }
  showKeywordInput.value = false
}

// 移除关键词
const removeKeyword = (keyword: string) => {
  const index = form.value.keywords.indexOf(keyword)
  if (index > -1) {
    form.value.keywords.splice(index, 1)
  }
}

// 生成情书
const generateLetter = async () => {
  if (!canGenerate.value) {
    ElMessage.warning('请填写完整信息')
    return
  }

  letterLoading.value = true
  try {
    const response = await generateLoveLetter({
      targetName: form.value.targetName,
      keywords: form.value.keywords,
      style: form.value.style
    })
    // 兼容data和message两种格式
    letterResult.value = response.data || response.message || ''
    console.log('情书生成响应:', response)
    console.log('情书内容:', letterResult.value)
    ElMessage.success('生成成功')
  } catch (error: any) {
    console.error('情书生成失败:', error)
    ElMessage.error(error.message || '生成失败，请重试')
  } finally {
    letterLoading.value = false
  }
}

// 生成藏头诗
const generatePoem = async () => {
  if (!canGeneratePoem.value) {
    ElMessage.warning('请输入2-4个字')
    return
  }

  poemLoading.value = true
  try {
    const response = await generateAcrosticPoem({
      text: poemForm.value.text
    })
    // 兼容data和message两种格式
    poemResult.value = response.data || response.message || ''
    console.log('藏头诗生成响应:', response)
    console.log('藏头诗内容:', poemResult.value)
    ElMessage.success('生成成功')
  } catch (error: any) {
    console.error('藏头诗生成失败:', error)
    ElMessage.error(error.message || '生成失败，请重试')
  } finally {
    poemLoading.value = false
  }
}

// 复制结果
const copyResult = (text: string) => {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('复制成功')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

// 分享到表白墙
const shareToWall = () => {
  router.push('/posts/create')
  ElMessage.info('跳转到发帖页面，可以将内容粘贴发布')
}

// 监听显示输入框
nextTick(() => {
  if (showKeywordInput.value && keywordInputRef.value) {
    keywordInputRef.value.focus()
  }
})
</script>

<style scoped>
.love-letter-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.love-letter-card,
.poem-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.love-letter-form,
.poem-form {
  margin-top: 24px;
}

.keywords-input {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.keyword-tag {
  font-size: 14px;
}

.keyword-input {
  width: 120px;
}

.add-keyword-btn {
  border-style: dashed;
}

.form-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.generate-btn {
  width: 100%;
  margin-top: 16px;
  font-size: 16px;
  height: 48px;
}

.result-container {
  margin-top: 24px;
}

.result-content {
  padding: 20px;
  background: linear-gradient(135deg, #ffeef8 0%, #fff5f7 100%);
  border-radius: 8px;
  line-height: 2;
  font-size: 15px;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-word;
  min-height: 200px;
}

.poem-content {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  font-family: 'KaiTi', 'STKaiti', serif;
  font-size: 16px;
  text-align: center;
}

.result-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
  justify-content: center;
}

@media (max-width: 768px) {
  .love-letter-container {
    padding: 12px;
  }

  .card-header h2 {
    font-size: 20px;
  }

  .result-content {
    padding: 16px;
    font-size: 14px;
  }

  .result-actions {
    flex-direction: column;
  }

  .result-actions .el-button {
    width: 100%;
  }
}
</style>
