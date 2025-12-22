<template>
  <div class="create-product-container">
    <!-- 返回按钮 -->
    <el-button @click="$router.back()" class="back-btn" circle>
      <el-icon><ArrowLeft /></el-icon>
    </el-button>

    <el-card class="create-product-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><ShoppingCart /></el-icon>
            <h2>发布商品</h2>
          </div>
          <div class="header-tip">出售你的闲置物品</div>
        </div>
      </template>

      <el-form :model="productForm" :rules="rules" ref="formRef" label-width="100px" class="product-form">
        <el-form-item label="商品标题" prop="title">
          <el-input
            v-model="productForm.title"
            placeholder="给你的商品起个名字..."
            size="large"
            maxlength="100"
            show-word-limit
          >
            <template #prefix>
              <el-icon><Goods /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="商品分类" prop="category">
          <el-select
            v-model="productForm.category"
            placeholder="选择商品分类"
            size="large"
            style="width: 100%"
          >
            <el-option label="📚 图书教材" value="BOOKS" />
            <el-option label="💻 电子产品" value="ELECTRONICS" />
            <el-option label="🛍️ 生活用品" value="DAILY_GOODS" />
            <el-option label="🏀 运动器材" value="SPORTS" />
            <el-option label="✏️ 文具用品" value="STATIONERY" />
            <el-option label="👕 服装鞋帽" value="CLOTHES" />
            <el-option label="📦 其他" value="OTHERS" />
          </el-select>
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="6"
            placeholder="详细描述你的商品...&#10;&#10;包括：商品状况、使用时长、购买渠道等"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <div class="form-row">
          <el-form-item label="出售价格" prop="price" class="price-item">
            <el-input
              v-model.number="productForm.price"
              placeholder="0.00"
              size="large"
              type="number"
              min="0"
              step="0.01"
            >
              <template #prefix>
                <span class="price-prefix">¥</span>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="原价" prop="originalPrice" class="price-item">
            <el-input
              v-model.number="productForm.originalPrice"
              placeholder="选填"
              size="large"
              type="number"
              min="0"
              step="0.01"
            >
              <template #prefix>
                <span class="price-prefix">¥</span>
              </template>
            </el-input>
          </el-form-item>
        </div>

        <el-form-item label="成色描述" prop="productCondition">
          <el-select
            v-model="productForm.productCondition"
            placeholder="选择商品成色"
            size="large"
            style="width: 100%"
          >
            <el-option label="全新" value="全新" />
            <el-option label="九成新" value="九成新" />
            <el-option label="八成新" value="八成新" />
            <el-option label="七成新" value="七成新" />
            <el-option label="六成新及以下" value="六成新及以下" />
          </el-select>
        </el-form-item>

        <el-form-item label="交易地点" prop="location">
          <el-input
            v-model="productForm.location"
            placeholder="例如：学校东门、宿舍楼下"
            size="large"
          >
            <template #prefix>
              <el-icon><Location /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="联系方式" prop="contact">
          <el-input
            v-model="productForm.contact"
            placeholder="例如：微信号、QQ号"
            size="large"
          >
            <template #prefix>
              <el-icon><Phone /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="商品图片">
          <ImageUpload v-model="productForm.images" :limit="9" />
          <div class="upload-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>最多可上传9张图片，每张图片不超过10MB</span>
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
            <el-icon v-if="!loading"><Check /></el-icon>
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
import { createProduct } from '@/api/product'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import ImageUpload from '@/components/ImageUpload.vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const productForm = reactive({
  title: '',
  description: '',
  category: '',
  price: undefined as number | undefined,
  originalPrice: undefined as number | undefined,
  productCondition: '',
  location: '',
  contact: '',
  images: [] as string[]
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [
    { required: true, message: '请输入出售价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await createProduct(productForm)
        ElMessage.success('发布成功')
        router.push('/marketplace')
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
.create-product-container {
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

.create-product-card {
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
  color: #f093fb;
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

.product-form {
  padding: 20px 0;
}

.product-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  font-size: 15px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.price-item {
  margin-bottom: 18px;
}

.price-prefix {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

.image-upload-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #ecf5ff;
  border-radius: 8px;
  border-left: 3px solid #409eff;
  font-size: 14px;
  color: #409eff;
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
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #e082ea 0%, #e4465b 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(240, 147, 251, 0.4);
}

.cancel-btn {
  min-width: 120px;
  height: 48px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .create-product-container {
    padding: 20px 12px;
  }

  .back-btn {
    top: 60px;
    left: 12px;
  }

  .form-row {
    grid-template-columns: 1fr;
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
