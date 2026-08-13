<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品名称" prop="goodsName">
        <el-input
          v-model="queryParams.goodsName"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="售价" prop="price">
        <el-input
          v-model="queryParams.price"
          placeholder="请输入售价"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="原价" prop="originalPrice">
        <el-input
          v-model="queryParams.originalPrice"
          placeholder="请输入原价"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发布人" prop="publisherName">
        <el-input
          v-model="queryParams.publisherName"
          placeholder="请输入发布人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input
          v-model="queryParams.contactPhone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="交易地点" prop="tradeAddress">
        <el-input
          v-model="queryParams.tradeAddress"
          placeholder="请输入交易地点"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核人" prop="auditUserName">
        <el-input
          v-model="queryParams.auditUserName"
          placeholder="请输入审核人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核时间" prop="auditTime">
        <el-date-picker clearable
          v-model="queryParams.auditTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择审核时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:goods:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:goods:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:goods:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:goods:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="goodsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品ID" align="center" prop="goodsId" />
      <el-table-column label="商品名称" align="center" prop="goodsName" />
      <el-table-column label="分类" align="center" prop="categoryName" />
      <el-table-column label="封面图片" align="center" prop="coverImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.coverImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="售价" align="center" prop="price" />
      <el-table-column label="原价" align="center" prop="originalPrice" />
      <el-table-column label="成色" align="center" prop="qualityLevel">
        <template slot-scope="scope">
          <span v-if="scope.row.qualityLevel === '1'">全新</span>
          <span v-else-if="scope.row.qualityLevel === '2'">9成新</span>
          <span v-else-if="scope.row.qualityLevel === '3'">8成新</span>
          <span v-else-if="scope.row.qualityLevel === '4'">7成新</span>
          <span v-else>{{ scope.row.qualityLevel }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发布人" align="center" prop="publisherName" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" />
      <el-table-column label="交易地点" align="center" prop="tradeAddress" />
      <el-table-column label="浏览量" align="center" prop="viewCount" />
      <el-table-column label="收藏量" align="center" prop="collectCount" />
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template slot-scope="scope">
          <span v-if="scope.row.auditStatus === '0'">待审核</span>
          <span v-else-if="scope.row.auditStatus === '1'">通过</span>
          <span v-else-if="scope.row.auditStatus === '2'">驳回</span>
          <span v-else>{{ scope.row.auditStatus }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品状态" align="center" prop="goodsStatus">
        <template slot-scope="scope">
          <span v-if="scope.row.goodsStatus === '0'">待审核</span>
          <span v-else-if="scope.row.goodsStatus === '1'">上架</span>
          <span v-else-if="scope.row.goodsStatus === '2'">已预订</span>
          <span v-else-if="scope.row.goodsStatus === '3'">已售出</span>
          <span v-else-if="scope.row.goodsStatus === '4'">已下架</span>
          <span v-else>{{ scope.row.goodsStatus }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核人" align="center" prop="auditUserName" />
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:goods:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleAudit(scope.row, '1')"
            v-hasPermi="['system:goods:audit']"
            v-if="scope.row.auditStatus === '0'"
          >审核通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleAudit(scope.row, '2')"
            v-hasPermi="['system:goods:audit']"
            v-if="scope.row.auditStatus === '0'"
          >审核驳回</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-top"
            @click="handleOnline(scope.row)"
            v-hasPermi="['system:goods:online']"
            v-if="scope.row.goodsStatus === '4'"
          >上架</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-bottom"
            @click="handleOffline(scope.row)"
            v-hasPermi="['system:goods:offline']"
            v-if="scope.row.goodsStatus === '1'"
          >下架</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:goods:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改商品信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="商品名称" prop="goodsName">
              <el-input v-model="form.goodsName" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="分类" prop="categoryId">
              <treeselect v-model="form.categoryId" :options="categoryTreeOptions" :normalizer="normalizer" placeholder="请选择分类" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="封面图片" prop="coverImage">
              <image-upload v-model="form.coverImage"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="售价" prop="price">
              <el-input v-model="form.price" placeholder="请输入售价" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="原价" prop="originalPrice">
              <el-input v-model="form.originalPrice" placeholder="请输入原价" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="成色" prop="qualityLevel">
              <el-select v-model="form.qualityLevel" placeholder="请选择成色" style="width: 100%">
                <el-option label="全新" value="1" />
                <el-option label="9成新" value="2" />
                <el-option label="8成新" value="3" />
                <el-option label="7成新" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品描述" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发布人" prop="publisherId">
              <el-input v-model="form.publisherName" disabled style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="交易地点" prop="tradeAddress">
              <el-input v-model="form.tradeAddress" placeholder="请输入交易地点" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品状态" prop="goodsStatus">
              <el-select v-model="form.goodsStatus" placeholder="请选择商品状态" style="width: 100%">
                <el-option label="待审核" value="0" />
                <el-option label="上架" value="1" />
                <el-option label="已预订" value="2" />
                <el-option label="已售出" value="3" />
                <el-option label="已下架" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核人" prop="auditUser">
              <el-select v-model="form.auditUser" placeholder="请选择审核人" style="width: 100%">
                <el-option
                  v-for="item in userOptions"
                  :key="item.userId"
                  :label="item.userName"
                  :value="item.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核时间" prop="auditTime">
              <el-date-picker clearable
                v-model="form.auditTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择审核时间"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核意见" prop="auditRemark">
              <el-input v-model="form.auditRemark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listGoods, getGoods, delGoods, addGoods, updateGoods, auditGoods, offlineGoods, onlineGoods } from "@/api/system/goods"
import { listCategory } from "@/api/system/category"
import { listUser } from "@/api/system/user"
import Treeselect from "@riophae/vue-treeselect"
import "@riophae/vue-treeselect/dist/vue-treeselect.css"

export default {
  name: "Goods",
  components: {
    Treeselect
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 商品信息表格数据
      goodsList: [],
      // 分类选项
      categoryOptions: [],
      // 分类树形选项
      categoryTreeOptions: [],
      // 用户选项
      userOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsName: null,
        price: null,
        originalPrice: null,
        qualityLevel: null,
        description: null,
        publisherName: null,
        contactPhone: null,
        tradeAddress: null,
        viewCount: null,
        collectCount: null,
        auditStatus: null,
        goodsStatus: null,
        auditUserName: null,
        auditTime: null,
        auditRemark: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        goodsName: [
          { required: true, message: "商品名称不能为空", trigger: "blur" }
        ],
        categoryId: [
          { required: true, message: "分类不能为空", trigger: "change" }
        ],
        price: [
          { required: true, message: "售价不能为空", trigger: "blur" }
        ],
        publisherId: [
          { required: true, message: "发布人不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
    this.getCategoryOptions()
    this.getUserOptions()
  },
  methods: {
    /** 查询商品信息列表 */
    getList() {
      this.loading = true
      listGoods(this.queryParams).then(response => {
        this.goodsList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 获取分类选项 */
    getCategoryOptions() {
      listCategory().then(response => {
        this.categoryOptions = response.data
        this.categoryTreeOptions = this.handleTree(response.data, "categoryId", "parentId")
      })
    },
    /** 转换分类数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.categoryId,
        label: node.categoryName,
        children: node.children
      }
    },
    /** 获取用户选项 */
    getUserOptions() {
      listUser().then(response => {
        this.userOptions = response.rows
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        goodsId: null,
        categoryId: null,
        goodsName: null,
        coverImage: null,
        price: null,
        originalPrice: null,
        qualityLevel: null,
        description: null,
        publisherId: null,
        contactPhone: null,
        tradeAddress: null,
        viewCount: null,
        collectCount: null,
        auditStatus: null,
        goodsStatus: null,
        auditUser: null,
        auditTime: null,
        auditRemark: null,
        remark: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.goodsId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.form.publisherId = this.$store.state.user.id
      this.form.publisherName = this.$store.state.user.name
      this.open = true
      this.title = "添加商品信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const goodsId = row.goodsId || this.ids
      getGoods(goodsId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改商品信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.goodsId != null) {
            updateGoods(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addGoods(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const goodsIds = row.goodsId || this.ids
      this.$modal.confirm('是否确认删除商品信息编号为"' + goodsIds + '"的数据项？').then(function() {
        return delGoods(goodsIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 审核按钮操作 */
    handleAudit(row, auditStatus) {
      const goodsId = row.goodsId
      const auditText = auditStatus === '1' ? '通过' : '驳回'
      this.$modal.confirm('是否确认' + auditText + '商品名称为"' + row.goodsName + '"的数据项？').then(() => {
        if (auditStatus === '2') {
          this.$prompt('请输入审核意见', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            inputPattern: /.+/,
            inputErrorMessage: '审核意见不能为空'
          }).then(({ value }) => {
            return auditGoods({
              goodsId: goodsId,
              auditStatus: auditStatus,
              auditUser: this.$store.state.user.userId,
              auditRemark: value
            })
          }).then(() => {
            this.getList()
            this.$modal.msgSuccess('审核' + auditText + '成功')
          }).catch(() => {})
        } else {
          return auditGoods({
            goodsId: goodsId,
            auditStatus: auditStatus,
            auditUser: this.$store.state.user.userId
          })
        }
      }).then(() => {
        if (auditStatus === '1') {
          this.getList()
          this.$modal.msgSuccess('审核' + auditText + '成功')
        }
      }).catch(() => {})
    },
    /** 上架按钮操作 */
    handleOnline(row) {
      const goodsId = row.goodsId
      this.$modal.confirm('是否确认上架商品名称为"' + row.goodsName + '"的数据项？').then(function() {
        return onlineGoods(goodsId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("上架成功")
      }).catch(() => {})
    },
    /** 下架按钮操作 */
    handleOffline(row) {
      const goodsId = row.goodsId
      this.$modal.confirm('是否确认下架商品名称为"' + row.goodsName + '"的数据项？').then(function() {
        return offlineGoods(goodsId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("下架成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/goods/export', {
        ...this.queryParams
      }, `goods_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
