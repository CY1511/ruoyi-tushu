<template>
  <div class="publish-page">
    <!-- 报头 -->
    <div class="page-masthead">
      <div class="masthead-eyebrow">— 第 002 期 · 摆 摊 登 记 —</div>
      <h1 class="masthead-title">摆 摊</h1>
      <div class="masthead-sub">SUBMIT A LISTING · 印鉴为凭</div>
    </div>

    <div class="form-container">
      <el-form ref="publishForm" :model="form" :rules="rules" label-width="0" class="publish-form">
        <!-- 货物信息 -->
        <section class="form-section">
          <div class="section-header">
            <div class="section-mark"></div>
            <span class="section-eyebrow">PART · 01</span>
            <span class="section-cn">货 物 信 息</span>
          </div>

          <div class="form-row">
            <div class="form-label-block">
              <div class="form-label-cn">书 名</div>
              <div class="form-label-en">NAME</div>
            </div>
            <el-form-item prop="goodsName" class="form-item">
              <el-input v-model="form.goodsName" placeholder="如：高等数学（第七版）" maxlength="50" borderless class="form-input" />
            </el-form-item>
          </div>

          <div class="form-row">
            <div class="form-label-block">
              <div class="form-label-cn">分 类</div>
              <div class="form-label-en">TYPE</div>
            </div>
            <el-form-item prop="categoryId" class="form-item">
              <treeselect
                v-model="form.categoryId"
                :options="categoryOptions"
                :normalizer="normalizer"
                placeholder="选择书类"
                class="form-treeselect"
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <div class="form-label-block">
              <div class="form-label-cn">成 色</div>
              <div class="form-label-en">COND.</div>
            </div>
            <el-form-item prop="qualityLevel" class="form-item">
              <div class="condition-grid">
                <div
                  v-for="opt in conditionOptions"
                  :key="opt.value"
                  class="condition-cell"
                  :class="{ active: form.qualityLevel === opt.value }"
                  @click="form.qualityLevel = opt.value"
                >
                  <div class="cell-mark">{{ opt.value }}</div>
                  <div class="cell-text">{{ opt.label }}</div>
                </div>
              </div>
            </el-form-item>
          </div>
        </section>

        <!-- 价格 -->
        <section class="form-section">
          <div class="section-header">
            <div class="section-mark"></div>
            <span class="section-eyebrow">PART · 02</span>
            <span class="section-cn">定 价</span>
          </div>

          <div class="form-row">
            <div class="form-label-block">
              <div class="form-label-cn">现 价</div>
              <div class="form-label-en">PRICE</div>
            </div>
            <el-form-item prop="price" class="form-item">
              <div class="price-input">
                <span class="price-symbol">¥</span>
                <el-input v-model="form.price" type="number" placeholder="0.00" borderless class="form-input" />
                <span class="price-unit">元</span>
              </div>
            </el-form-item>
          </div>

          <div class="form-row">
            <div class="form-label-block">
              <div class="form-label-cn">原 价</div>
              <div class="form-label-en">ORIG.</div>
            </div>
            <el-form-item class="form-item">
              <div class="price-input">
                <span class="price-symbol">¥</span>
                <el-input v-model="form.originalPrice" type="number" placeholder="选填" borderless class="form-input" />
                <span class="price-unit">元</span>
              </div>
            </el-form-item>
          </div>
        </section>

        <!-- 关联信息 -->
        <section class="form-section">
          <div class="section-header">
            <div class="section-mark"></div>
            <span class="section-eyebrow">PART · 03</span>
            <span class="section-cn">关 联 信 息</span>
          </div>

          <div class="form-row">
            <div class="form-label-block">
              <div class="form-label-cn">课 程</div>
              <div class="form-label-en">COURSE</div>
            </div>
            <el-form-item class="form-item">
              <el-input v-model="form.courseName" placeholder="对应课程名（选填）" maxlength="50" borderless class="form-input" />
            </el-form-item>
          </div>

          <div class="form-row">
            <div class="form-label-block">
              <div class="form-label-cn">ISBN</div>
              <div class="form-label-en">CODE</div>
            </div>
            <el-form-item class="form-item">
              <el-input v-model="form.isbn" placeholder="ISBN 编号（选填）" maxlength="20" borderless class="form-input" />
            </el-form-item>
          </div>
        </section>

        <!-- 摊主笔记 -->
        <section class="form-section">
          <div class="section-header">
            <div class="section-mark"></div>
            <span class="section-eyebrow">PART · 04</span>
            <span class="section-cn">摊 主 笔 记</span>
          </div>

          <div class="form-row column">
            <el-form-item prop="description" class="form-item full">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="4"
                placeholder="「…」（描述成色细节、使用情况、面交地点等）"
                maxlength="200"
                show-word-limit
                class="form-textarea"
                resize="none"
              />
            </el-form-item>
          </div>
        </section>

        <!-- 图片 -->
        <section class="form-section">
          <div class="section-header">
            <div class="section-mark"></div>
            <span class="section-eyebrow">PART · 05</span>
            <span class="section-cn">附 图</span>
          </div>

          <div class="upload-block">
            <el-upload
              list-type="picture-card"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :file-list="fileList"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              :on-error="handleUploadError"
              accept="image/*"
              multiple
            >
              <i class="el-icon-plus"></i>
            </el-upload>
            <div class="upload-tip">建 议 拍 摄 3 张 以 上 · 灯 光 充 足 翻 拍 效 果 更 佳</div>
          </div>
        </section>

        <!-- 提交 -->
        <div class="submit-section">
          <div class="submit-rule"></div>
          <button class="submit-btn" :disabled="submitting" @click="handleSubmit">
            <span v-if="!submitting">— 印 鉴 揭 印 · 挂 牌 摆 摊 —</span>
            <span v-else><i class="el-icon-loading"></i> 印 鉴 中</span>
          </button>
          <div class="submit-tip">提 交 后 将 进 入 审 核 · 通 过 后 即 可 入 场</div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { addGoods } from '@/api/goods'
import { getCategoryTree } from '@/api/category'
import { getToken } from '@/utils/auth'

export default {
  name: 'Publish',
  components: { Treeselect },
  data() {
    return {
      form: {
        goodsName: '',
        categoryId: null,
        price: '',
        originalPrice: '',
        qualityLevel: '2',
        courseName: '',
        isbn: '',
        description: '',
        coverImage: ''
      },
      rules: {
        goodsName: [{ required: true, message: '请填写书名', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
        qualityLevel: [{ required: true, message: '请选择成色', trigger: 'change' }],
        price: [{ required: true, message: '请填写价格', trigger: 'blur' }]
      },
      conditionOptions: [
        { label: '全 新', value: '1' },
        { label: '九成新', value: '2' },
        { label: '八成新', value: '3' },
        { label: '七成新', value: '4' }
      ],
      categoryOptions: [],
      fileList: [],
      submitting: false
    }
  },
  computed: {
    baseApi() {
      return process.env.VUE_APP_BASE_API || '/dev-api'
    },
    uploadUrl() {
      return this.baseApi + '/common/upload'
    },
    uploadHeaders() {
      return { Authorization: 'Bearer ' + getToken() }
    }
  },
  created() {
    this.loadCategories()
  },
  methods: {
    normalizer(node) {
      return {
        id: node.categoryId,
        label: node.categoryName,
        children: node.children
      }
    },
    loadCategories() {
      getCategoryTree().then(res => {
        this.categoryOptions = res.data || []
      }).catch(() => {})
    },
    handleUploadSuccess(res, file) {
      if (res.code === 200) {
        file.url = this.baseApi + res.fileName
        this.updateCoverImage()
      }
    },
    handleUploadError() {
      this.$message.error('上传失败')
    },
    handleRemove() {
      this.updateCoverImage()
    },
    updateCoverImage() {
      this.form.coverImage = this.fileList
        .filter(f => f.url)
        .map(f => {
          const url = f.url
          if (url.startsWith(this.baseApi)) {
            return url.substring(this.baseApi.length)
          }
          return url
        })
        .join(',')
    },
    handleSubmit() {
      this.$refs.publishForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        const data = { ...this.form }
        data.price = parseFloat(data.price) || 0
        if (data.originalPrice) {
          data.originalPrice = parseFloat(data.originalPrice) || 0
        } else {
          delete data.originalPrice
        }
        if (!data.courseName) delete data.courseName
        if (!data.isbn) delete data.isbn
        if (!data.description) delete data.description
        addGoods(data).then(() => {
          this.$message.success('印鉴成功！请等待审核')
          this.$router.push('/my-goods')
        }).catch(() => {
          this.$message.error('提交失败，请重试')
        }).finally(() => { this.submitting = false })
      })
    }
  }
}
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  background: var(--paper);
  padding-bottom: 40px;
}

.page-masthead {
  text-align: center;
  padding: 16px 20px 18px;
  border-bottom: 2px solid var(--ink);
  position: relative;
}
.page-masthead::after {
  content: '';
  position: absolute;
  inset: auto 0 -5px 0;
  height: 1px;
  background: var(--ink);
}
.masthead-eyebrow {
  font-family: var(--font-serif);
  font-size: 11px;
  letter-spacing: 0.4em;
  color: var(--ink-mute);
  margin-bottom: 4px;
}
.masthead-title {
  font-family: var(--font-serif);
  font-size: 38px;
  font-weight: 900;
  color: var(--ink);
  line-height: 1;
  letter-spacing: 0.4em;
  padding-left: 0.4em;
}
.masthead-sub {
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.4em;
  color: var(--ink-mute);
  margin-top: 6px;
}

/* —— 表单 —— */
.form-container {
  padding: 0 16px;
}
.publish-form { margin: 0; }

.form-section {
  background: var(--white-paper);
  margin-top: 16px;
  border: 1.5px solid var(--ink);
  box-shadow: 4px 4px 0 var(--ink);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: var(--paper-deep);
  border-bottom: 1.5px solid var(--ink);
  position: relative;
}
.section-header::after {
  content: '';
  position: absolute;
  inset: auto 0 -2.5px 0;
  height: 1px;
  background: var(--ink);
  opacity: 0.3;
}
.section-mark {
  width: 4px;
  height: 14px;
  background: var(--vermilion);
}
.section-eyebrow {
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.3em;
  color: var(--vermilion);
  font-weight: 500;
}
.section-cn {
  font-family: var(--font-serif);
  font-size: 14px;
  font-weight: 700;
  color: var(--ink);
  letter-spacing: 0.2em;
}

.form-row {
  display: flex;
  align-items: center;
  border-bottom: 1px dashed var(--line);
  padding: 8px 14px;
  min-height: 48px;
}
.form-row:last-child { border-bottom: none; }
.form-row.column {
  display: block;
  padding: 10px 14px 14px;
}
.form-label-block {
  width: 70px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.form-label-cn {
  font-family: var(--font-serif);
  font-size: 13px;
  font-weight: 600;
  color: var(--ink);
  letter-spacing: 0.2em;
}
.form-label-en {
  font-family: var(--font-mono);
  font-size: 8px;
  letter-spacing: 0.3em;
  color: var(--ink-mute);
}
.form-item {
  flex: 1;
  margin: 0 !important;
}
.form-item >>> .el-form-item__content { line-height: 1; }
.form-item.full { width: 100%; }

.form-input >>> .el-input__inner {
  padding-left: 0 !important;
  font-size: 14px !important;
  font-family: var(--font-serif) !important;
  color: var(--ink) !important;
  background: transparent !important;
  border: none !important;
  border-bottom: 1px solid var(--line) !important;
  border-radius: 0 !important;
  letter-spacing: 0.05em;
}
.form-input >>> .el-input__inner:focus {
  border-bottom-color: var(--vermilion) !important;
}
.form-input >>> .el-input__inner::placeholder {
  color: var(--ink-fade);
  font-family: var(--font-serif);
  letter-spacing: 0.05em;
}

/* —— 树形选择 —— */
.form-treeselect >>> .vue-treeselect__control {
  border: none !important;
  border-bottom: 1px solid var(--line) !important;
  border-radius: 0 !important;
  background: transparent !important;
  padding: 0 !important;
  min-height: 32px;
}
.form-treeselect >>> .vue-treeselect__placeholder {
  color: var(--ink-fade) !important;
  font-family: var(--font-serif) !important;
  letter-spacing: 0.05em;
}
.form-treeselect >>> .vue-treeselect__single-value {
  font-family: var(--font-serif) !important;
  color: var(--ink) !important;
  letter-spacing: 0.05em;
}

/* —— 成色格 —— */
.condition-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  width: 100%;
}
.condition-cell {
  border: 1.5px solid var(--ink);
  background: var(--paper);
  padding: 8px 4px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}
.condition-cell::before {
  content: '';
  position: absolute;
  top: 3px;
  left: 3px;
  right: 3px;
  height: 1px;
  background: var(--ink);
  opacity: 0.15;
}
.condition-cell:active { transform: scale(0.96); }
.condition-cell.active {
  background: var(--vermilion);
  border-color: var(--vermilion);
  color: var(--paper);
  box-shadow: 2px 2px 0 var(--ink);
}
.cell-mark {
  font-family: var(--font-mono);
  font-size: 14px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 4px;
}
.cell-text {
  font-family: var(--font-serif);
  font-size: 11px;
  letter-spacing: 0.1em;
}

/* —— 价格输入 —— */
.price-input {
  display: flex;
  align-items: center;
  gap: 4px;
  width: 100%;
  border-bottom: 1px solid var(--line);
}
.price-input >>> .el-input__inner {
  flex: 1;
  border: none !important;
  padding-left: 0 !important;
  font-family: var(--font-serif) !important;
  font-size: 16px !important;
  font-weight: 600;
  color: var(--vermilion) !important;
}
.price-symbol {
  font-family: var(--font-serif);
  font-size: 16px;
  font-weight: 700;
  color: var(--vermilion);
}
.price-unit {
  font-family: var(--font-serif);
  font-size: 12px;
  color: var(--ink-mute);
  letter-spacing: 0.1em;
}

/* —— 描述框 —— */
.form-textarea >>> .el-textarea__inner {
  background: var(--paper) !important;
  border: 1px solid var(--line) !important;
  border-radius: 0 !important;
  font-family: var(--font-serif) !important;
  color: var(--ink) !important;
  padding: 10px 12px !important;
  letter-spacing: 0.05em;
  line-height: 1.7;
}
.form-textarea >>> .el-textarea__inner::placeholder {
  color: var(--ink-fade);
  font-family: var(--font-serif);
}

/* —— 上传 —— */
.upload-block {
  padding: 14px;
}
.upload-block >>> .el-upload--picture-card {
  background: var(--paper) !important;
  border: 1.5px dashed var(--ink) !important;
  border-radius: 0 !important;
  width: 76px !important;
  height: 76px !important;
}
.upload-block >>> .el-upload-list--picture-card .el-upload-list__item {
  border-radius: 0 !important;
  border: 1px solid var(--ink) !important;
}
.upload-tip {
  font-family: var(--font-serif);
  font-size: 11px;
  color: var(--ink-mute);
  letter-spacing: 0.1em;
  margin-top: 10px;
  padding-left: 4px;
}

/* —— 提交 —— */
.submit-section {
  margin-top: 24px;
  text-align: center;
  padding: 0 4px;
}
.submit-rule {
  height: 1px;
  background: var(--ink);
  position: relative;
  margin: 0 60px 20px;
}
.submit-rule::after {
  content: '';
  position: absolute;
  inset: 2px 0 0 0;
  height: 1px;
  background: var(--ink);
  opacity: 0.3;
}
.submit-btn {
  width: 100%;
  height: 50px;
  background: var(--vermilion);
  color: var(--paper);
  border: 1.5px solid var(--vermilion);
  font-family: var(--font-serif);
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.3em;
  cursor: pointer;
  position: relative;
  box-shadow: 4px 4px 0 var(--ink);
  transition: all 0.2s;
}
.submit-btn::before, .submit-btn::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 10px;
  height: 10px;
  border: 1.5px solid currentColor;
  transform: translateY(-50%) rotate(45deg);
}
.submit-btn::before { left: 20px; }
.submit-btn::after { right: 20px; }
.submit-btn:active { transform: translate(2px, 2px); box-shadow: 2px 2px 0 var(--ink); }
.submit-btn:disabled { opacity: 0.6; }
.submit-tip {
  font-family: var(--font-serif);
  font-size: 11px;
  color: var(--ink-mute);
  letter-spacing: 0.2em;
  margin-top: 12px;
}
</style>
