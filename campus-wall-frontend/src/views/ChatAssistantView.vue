<template>
  <div class="chat-container">
    <el-card class="chat-card">
      <template #header>
        <div class="chat-header">
          <h2>🤖 AI校园助手</h2>
          <p class="subtitle">有问题尽管问我，食堂、教室、图书馆我都知道</p>
        </div>
      </template>

      <div class="chat-body">
        <!-- 消息列表 -->
        <div class="message-list" ref="messageListRef">
          <!-- 欢迎消息 -->
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="welcome-icon">👋</div>
            <h3>你好！我是校园助手</h3>
            <p>你可以问我：</p>
            <div class="quick-questions">
              <el-tag
                v-for="question in quickQuestions"
                :key="question"
                @click="sendQuickQuestion(question)"
                class="quick-question"
              >
                {{ question }}
              </el-tag>
            </div>
          </div>

          <!-- 聊天消息 -->
          <div
            v-for="(message, index) in messages"
            :key="index"
            :class="['message-item', message.role === 'user' ? 'user-message' : 'assistant-message']"
          >
            <div class="message-avatar">
              <el-avatar v-if="message.role === 'user'" :size="36">
                {{ userStore.user?.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <el-avatar v-else :size="36" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                🤖
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            </div>
          </div>

          <!-- 加载中 -->
          <div v-if="loading" class="message-item assistant-message">
            <div class="message-avatar">
              <el-avatar :size="36" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                🤖
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-text typing">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入框 -->
        <div class="chat-input">
          <el-input
            v-model="inputMessage"
            placeholder="输入你的问题..."
            @keyup.enter="sendMessage"
            :disabled="loading"
            class="input-field"
          >
            <template #append>
              <el-button
                @click="sendMessage"
                :loading="loading"
                :disabled="!inputMessage.trim()"
                type="primary"
                icon="Promotion"
              >
                发送
              </el-button>
            </template>
          </el-input>
        </div>

        <!-- 清空聊天 -->
        <div class="chat-actions">
          <el-button
            @click="clearChat"
            size="small"
            text
            icon="Delete"
            v-if="messages.length > 0"
          >
            清空聊天记录
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { chatAssistant } from '@/api/ai'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

interface Message {
  role: 'user' | 'assistant'
  content: string
  timestamp: number
}

const messages = ref<Message[]>([])
const inputMessage = ref('')
const loading = ref(false)
const messageListRef = ref<HTMLElement>()

// 快捷问题
const quickQuestions = [
  '今天食堂有什么好吃的？',
  '明德楼怎么走？',
  '图书馆几点关门？',
  '校园卡在哪里办理？',
  '体育馆开放时间',
  '校园网如何连接？'
]

// 发送消息
const sendMessage = async () => {
  const question = inputMessage.value.trim()
  if (!question || loading.value) return

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: question,
    timestamp: Date.now()
  })

  inputMessage.value = ''
  scrollToBottom()

  // 调用AI接口
  loading.value = true
  try {
    // 构建历史消息（最近10条）
    const history = messages.value.slice(-10).map(msg => ({
      role: msg.role,
      content: msg.content
    }))

    const response = await chatAssistant({
      question,
      history
    })

    // 添加助手回复
    messages.value.push({
      role: 'assistant',
      content: response.data,
      timestamp: Date.now()
    })

    scrollToBottom()
  } catch (error: any) {
    ElMessage.error(error.message || '获取回复失败，请重试')
    // 移除用户消息
    messages.value.pop()
  } finally {
    loading.value = false
  }
}

// 发送快捷问题
const sendQuickQuestion = (question: string) => {
  inputMessage.value = question
  sendMessage()
}

// 清空聊天
const clearChat = () => {
  ElMessageBox.confirm(
    '确定要清空所有聊天记录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    messages.value = []
    ElMessage.success('聊天记录已清空')
  }).catch(() => {
    // 取消
  })
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

// 格式化时间
const formatTime = (timestamp: number) => {
  const date = new Date(timestamp)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}
</script>

<style scoped>
.chat-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  height: calc(100vh - 120px);
}

.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.chat-header {
  text-align: center;
}

.chat-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.chat-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  scroll-behavior: smooth;
}

/* 欢迎消息 */
.welcome-message {
  text-align: center;
  padding: 60px 20px;
}

.welcome-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.welcome-message h3 {
  margin: 0 0 12px 0;
  font-size: 24px;
  color: #303133;
}

.welcome-message p {
  margin: 0 0 20px 0;
  font-size: 14px;
  color: #909399;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  max-width: 600px;
  margin: 0 auto;
}

.quick-question {
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  padding: 8px 16px;
}

.quick-question:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 消息项 */
.message-item {
  display: flex;
  margin-bottom: 24px;
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  margin: 0 12px;
}

.message-content {
  max-width: 70%;
}

.user-message .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
}

.user-message .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.assistant-message .message-text {
  background: #f4f4f5;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.message-time {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

/* 输入中动画 */
.typing {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 16px;
}

.typing span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #909399;
  animation: typing 1.4s infinite;
}

.typing span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.7;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

/* 输入框 */
.chat-input {
  padding: 16px 20px;
  border-top: 1px solid #e4e7ed;
  background: white;
}

.input-field {
  width: 100%;
}

.chat-actions {
  padding: 8px 20px;
  text-align: center;
}

/* 滚动条美化 */
.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

@media (max-width: 768px) {
  .chat-container {
    padding: 0;
    height: calc(100vh - 60px);
  }

  .chat-card {
    border-radius: 0;
  }

  .message-content {
    max-width: 80%;
  }

  .welcome-message {
    padding: 40px 20px;
  }

  .quick-questions {
    max-width: 100%;
  }
}
</style>
