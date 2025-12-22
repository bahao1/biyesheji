<template>
  <div class="create-carpool-container">
    <div class="header">
      <div class="header-content">
        <h1 class="logo">🚗 发布拼车信息</h1>
        <el-button @click="$router.back()" round>
          <el-icon><ArrowLeft /></el-icon>
          <span>返回</span>
        </el-button>
      </div>
    </div>

    <div class="main-content">
      <el-card shadow="hover" class="form-card">
        <el-form :model="carpoolForm" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="拼车类型" prop="type" required>
            <el-radio-group v-model="carpoolForm.type">
              <el-radio value="DRIVER">车主找乘客</el-radio>
              <el-radio value="PASSENGER">乘客找车主</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="出发地" prop="departurePlace" required>
            <el-input v-model="carpoolForm.departurePlace" placeholder="请输入出发地" />
          </el-form-item>

          <el-form-item label="目的地" prop="destination" required>
            <el-input v-model="carpoolForm.destination" placeholder="请输入目的地" />
          </el-form-item>

          <el-form-item label="出发时间" prop="departureTime" required>
            <el-date-picker
              v-model="carpoolForm.departureTime"
              type="datetime"
              placeholder="选择出发时间"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="座位数" prop="seats" required>
            <el-input-number v-model="carpoolForm.seats" :min="1" :max="10" />
          </el-form-item>

          <el-form-item label="价格/人">
            <el-input-number v-model="carpoolForm.pricePerPerson" :min="0" :precision="2" />
            <span style="margin-left: 8px; color: #909399">元(选填,0表示免费)</span>
          </el-form-item>

          <el-form-item label="联系方式" prop="contact" required>
            <el-input v-model="carpoolForm.contact" placeholder="微信号/QQ号/电话" />
          </el-form-item>

          <el-form-item label="备注信息">
            <el-input
              v-model="carpoolForm.remarks"
              type="textarea"
              :rows="4"
              placeholder="可以填写其他补充信息..."
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting" size="large">
              发布拼车信息
            </el-button>
            <el-button @click="$router.back()" size="large">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createCarpool } from '@/api/carpool'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)

const carpoolForm = reactive({
  type: 'DRIVER',
  departurePlace: '',
  destination: '',
  departureTime: null,
  seats: 4,
  pricePerPerson: 0,
  contact: '',
  remarks: ''
})

const rules = {
  type: [{ required: true, message: '请选择拼车类型', trigger: 'change' }],
  departurePlace: [{ required: true, message: '请输入出发地', trigger: 'blur' }],
  destination: [{ required: true, message: '请输入目的地', trigger: 'blur' }],
  departureTime: [{ required: true, message: '请选择出发时间', trigger: 'change' }],
  seats: [{ required: true, message: '请输入座位数', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    submitting.value = true
    await createCarpool(carpoolForm)
    ElMessage.success('发布成功')
    router.push('/carpool')
  } catch (error) {
    console.error('发布失败:', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.create-carpool-container {
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
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.logo {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.main-content {
  max-width: 900px;
  margin: 24px auto;
  padding: 0 24px;
}

.form-card {
  border-radius: 12px;
}
</style>
