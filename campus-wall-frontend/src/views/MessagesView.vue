<template>
  <div class="messages-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">💬 消息中心</h1>
        <div class="header-actions">
          <el-button @click="$router.push('/')" round>
            <el-icon><House /></el-icon>
            <span>返回首页</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-container">
      <!-- 左侧会话列表 -->
      <el-aside width="350px" class="sidebar">
        <el-card shadow="hover" class="conversations-card">
          <template #header>
            <div class="sidebar-header">
              <h3>消息</h3>
              <el-badge :value="totalUnread" :hidden="totalUnread === 0" class="unread-badge">
                <el-icon><Bell /></el-icon>
              </el-badge>
            </div>
          </template>

          <el-scrollbar class="conversations-list">
            <div
              v-for="conversation in conversations"
              :key="conversation.userId"
              :class="['conversation-item', { active: selectedUserId === conversation.userId }]"
              @click="selectConversation(conversation)"
            >
              <el-avatar :size="48">{{ conversation.nickname?.charAt(0) || 'U' }}</el-avatar>
              <div class="conversation-info">
                <div class="conversation-header">
                  <span class="nickname">{{ conversation.nickname }}</span>
                  <span class="time">{{ formatTime(conversation.lastMessageTime) }}</span>
                </div>
                <div class="last-message">
                  {{ conversation.lastMessage }}
                </div>
              </div>
              <el-badge
                v-if="conversation.unreadCount > 0"
                :value="conversation.unreadCount"
                class="message-badge"
              />
            </div>

            <el-empty
              v-if="conversations.length === 0 && !loading"
              description="暂无消息"
              :image-size="100"
            />
          </el-scrollbar>
        </el-card>
      </el-aside>

      <!-- 右侧聊天区域 -->
      <div class="main-content">
        <el-card shadow="hover" class="chat-card" style="height: calc(100vh - 180px)">
          <template #header>
            <div class="chat-header" v-if="selectedUserId">
              <el-avatar :size="40">{{ selectedUserName?.charAt(0) || 'U' }}</el-avatar>
              <span class="chat-user-name">{{ selectedUserName }}</span>
            </div>
          </template>

          <template v-if="selectedUserId">
            <!-- 聊天消息列表 -->
            <el-scrollbar ref="messageScrollbar" class="chat-messages">
              <div
                v-for="message in messages"
                :key="message.id"
                :class="['message-item', message.senderId === currentUserId ? 'sent' : 'received']"
              >
                <el-avatar :size="36">
                  {{ (message.senderId === currentUserId ? currentUserName : selectedUserName)?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="message-content">
                  <div class="message-text">{{ message.content }}</div>
                  <div class="message-time">{{ formatMessageTime(message.createTime) }}</div>
                </div>
              </div>
            </el-scrollbar>

            <!-- 输入框 -->
            <div class="chat-input">
              <el-input
                v-model="inputMessage"
                type="textarea"
                :rows="3"
                placeholder="输入消息..."
                @keydown.enter.prevent="handleSend"
                :disabled="sending"
              />
              <el-button
                type="primary"
                @click="handleSend"
                :loading="sending"
                :disabled="!inputMessage.trim()"
              >
                发送
              </el-button>
            </div>
          </template>

          <el-empty
            v-else
            description="选择一个会话开始聊天"
            :image-size="200"
            class="empty-chat"
          />
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getConversationList, getChatHistory, sendMessage, markAsRead, getUnreadCount } from '@/api/message'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const conversations = ref<any[]>([])
const messages = ref<any[]>([])
const selectedUserId = ref<number | null>(null)
const selectedUserName = ref('')
const inputMessage = ref('')
const sending = ref(false)
const totalUnread = ref(0)
const messageScrollbar = ref()

const currentUserId = userStore.user?.id
const currentUserName = userStore.user?.nickname

// 加载会话列表
const loadConversations = async () => {
  loading.value = true
  try {
    const res = await getConversationList()
    conversations.value = res.data

    // 获取未读消息总数
    const unreadRes = await getUnreadCount()
    totalUnread.value = unreadRes.data
  } catch (error: any) {
    ElMessage.error(error.message || '加载会话列表失败')
  } finally {
    loading.value = false
  }
}

// 选择会话
const selectConversation = async (conversation: any) => {
  selectedUserId.value = conversation.userId
  selectedUserName.value = conversation.nickname

  // 加载聊天记录
  await loadMessages()

  // 标记为已读
  await markAsRead(conversation.userId)

  // 更新会话列表中的未读数
  conversation.unreadCount = 0

  // 更新总未读数
  const unreadRes = await getUnreadCount()
  totalUnread.value = unreadRes.data
}

// 加载聊天记录
const loadMessages = async () => {
  if (!selectedUserId.value) return

  try {
    const res = await getChatHistory(selectedUserId.value)
    messages.value = res.data

    // 滚动到底部
    await nextTick()
    scrollToBottom()
  } catch (error: any) {
    ElMessage.error(error.message || '加载聊天记录失败')
  }
}

// 发送消息
const handleSend = async () => {
  if (!inputMessage.value.trim() || !selectedUserId.value) return

  const content = inputMessage.value.trim()
  sending.value = true

  try {
    const res = await sendMessage({
      receiverId: selectedUserId.value,
      content
    })

    // 添加到消息列表
    messages.value.push(res.data)

    // 清空输入框
    inputMessage.value = ''

    // 滚动到底部
    await nextTick()
    scrollToBottom()

    // 重新加载会话列表以更新最后消息
    loadConversations()
  } catch (error: any) {
    ElMessage.error(error.message || '发送失败')
  } finally {
    sending.value = false
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messageScrollbar.value) {
    const scrollbarRef = messageScrollbar.value
    nextTick(() => {
      scrollbarRef.setScrollTop(scrollbarRef.wrapRef.scrollHeight)
    })
  }
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

// 格式化消息时间
const formatMessageTime = (time: string) => {
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(async () => {
  await loadConversations()

  // 如果URL中有userId参数,直接打开该会话
  const userId = route.query.userId
  if (userId) {
    const conversationId = Number(userId)
    const conversation = conversations.value.find(c => c.userId === conversationId)
    if (conversation) {
      selectConversation(conversation)
    } else {
      // 如果会话列表中没有,设置为新会话，并假设用户信息可以通过ID获取
      selectedUserId.value = conversationId
      // 这里可以根据需要添加获取用户信息的API调用
      // 临时设置一个默认用户名
      selectedUserName.value = `用户${conversationId}`
    }
  }
})
</script>

<style scoped>
.messages-container {
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

.main-container {
  width: 100%;
  max-width: 100%;
  margin: 24px auto;
  padding: 0 24px;
  display: flex;
  gap: 24px;
}

.sidebar {
  flex-shrink: 0;
}

.conversations-card {
  height: calc(100vh - 180px);
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.unread-badge {
  font-size: 18px;
  cursor: pointer;
}

.conversations-list {
  flex: 1;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.3s;
  border-bottom: 1px solid #f5f5f5;
  position: relative;
}

.conversation-item:hover {
  background: #f5f7fa;
}

.conversation-item.active {
  background: #ecf5ff;
}

.conversation-info {
  flex: 1;
  margin-left: 12px;
  min-width: 0;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.nickname {
  font-weight: 600;
  color: #303133;
  font-size: 15px;
}

.time {
  font-size: 12px;
  color: #909399;
}

.last-message {
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.message-badge {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.main-content {
  flex: 1;
  min-width: 0;
}

.chat-card {
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  color: #303133;
}

.chat-user-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message-item.sent {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 60%;
}

.message-item.sent .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  word-break: break-word;
  white-space: pre-wrap;
  line-height: 1.6;
}

.message-item.received .message-text {
  background: white;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.message-item.sent .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-time {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.chat-input {
  padding: 20px;
  background: white;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.chat-input .el-textarea {
  flex: 1;
}

.chat-input .el-button {
  height: 40px;
  padding: 0 24px;
}

.empty-chat {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 968px) {
  .main-container {
    flex-direction: column;
  }

  .sidebar {
    width: 100% !important;
  }

  .conversation-item {
    padding: 12px 16px;
  }

  .message-content {
    max-width: 80%;
  }

  .chat-input {
    padding: 16px;
  }
}
</style>
