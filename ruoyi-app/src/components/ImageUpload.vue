<template>
  <div class="image-upload">
    <el-upload
      :action="uploadUrl"
      :headers="uploadHeaders"
      :file-list="fileList"
      :on-success="handleSuccess"
      :on-remove="handleRemove"
      :on-preview="handlePreview"
      :before-upload="beforeUpload"
      :limit="limit"
      :on-exceed="handleExceed"
      list-type="picture-card"
      accept="image/*"
    >
      <i class="el-icon-plus"></i>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible" append-to-body>
      <img width="100%" :src="dialogImageUrl" alt="" />
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ImageUpload',
  props: {
    // 已有的图片列表
    value: {
      type: Array,
      default: () => []
    },
    // 最大上传数量
    limit: {
      type: Number,
      default: 5
    }
  },
  data() {
    return {
      dialogVisible: false,
      dialogImageUrl: '',
      uploadUrl: process.env.VUE_APP_BASE_API ? process.env.VUE_APP_BASE_API + '/common/upload' : '/dev-api/common/upload'
    }
  },
  computed: {
    uploadHeaders() {
      const token = this.$store.state.token
      return token ? { Authorization: 'Bearer ' + token } : {}
    },
    fileList() {
      return this.value.map((url, index) => {
        return { name: 'image_' + index, url: url }
      })
    }
  },
  methods: {
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5
      if (!isImage) {
        this.$message.error('只能上传图片文件！')
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过5MB！')
      }
      return isImage && isLt5M
    },
    handleSuccess(response, file, fileList) {
      if (response.code === 200) {
        // 上传成功，将url添加到列表
        const images = [...this.value, response.url]
        this.$emit('input', images)
      } else {
        this.$message.error(response.msg || '上传失败')
        // 移除失败的文件
        const index = fileList.indexOf(file)
        if (index > -1) {
          fileList.splice(index, 1)
        }
      }
    },
    handleRemove(file, fileList) {
      // 移除图片
      const images = fileList.map(f => {
        if (f.response && f.response.url) {
          return f.response.url
        }
        return f.url
      })
      this.$emit('input', images)
    },
    handlePreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    handleExceed() {
      this.$message.warning('最多上传' + this.limit + '张图片')
    }
  }
}
</script>

<style scoped>
.image-upload >>> .el-upload-list--picture-card .el-upload-list__item {
  width: 80px;
  height: 80px;
}
.image-upload >>> .el-upload--picture-card {
  width: 80px;
  height: 80px;
  line-height: 86px;
}
</style>
