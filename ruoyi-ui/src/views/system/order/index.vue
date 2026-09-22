<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单编号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品ID" prop="goodsId">
        <el-input
          v-model="queryParams.goodsId"
          placeholder="请输入商品ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="卖家ID" prop="sellerId">
        <el-input
          v-model="queryParams.sellerId"
          placeholder="请输入卖家ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="买家ID" prop="buyerId">
        <el-input
          v-model="queryParams.buyerId"
          placeholder="请输入买家ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成交价格" prop="orderPrice">
        <el-input
          v-model="queryParams.orderPrice"
          placeholder="请输入成交价格"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select v-model="queryParams.orderStatus" placeholder="请选择订单状态" clearable style="width: 160px">
          <el-option
            v-for="item in statusOptions"
            :key="item.code"
            :label="item.label"
            :value="item.code"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="完成时间" prop="finishTime">
        <el-date-picker clearable
          v-model="queryParams.finishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择完成时间">
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
          v-hasPermi="['system:order:add']"
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
          v-hasPermi="['system:order:edit']"
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
          v-hasPermi="['system:order:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:order:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单编号" align="center" prop="orderNo" />
      <el-table-column label="商品ID" align="center" prop="goodsId" />
      <el-table-column label="卖家ID" align="center" prop="sellerId" />
      <el-table-column label="买家ID" align="center" prop="buyerId" />
      <el-table-column label="成交价格" align="center" prop="orderPrice" />
      <el-table-column label="订单状态" align="center" prop="orderStatus">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.orderStatus)">
            {{ statusLabel(scope.row.orderStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="完成时间" align="center" prop="finishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.finishTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="取消原因" align="center" prop="cancelReason" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleChangeStatus(scope.row)"
            v-hasPermi="['system:order:changeStatus']"
            v-if="!isFinalStatus(scope.row.orderStatus)"
          >流转</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-time"
            @click="handleStatusLog(scope.row)"
            v-hasPermi="['system:order:query']"
          >记录</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:order:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:order:remove']"
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

    <!-- 添加或修改商品交易订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="订单编号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="请输入订单编号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品ID" prop="goodsId">
              <el-input v-model="form.goodsId" placeholder="请输入商品ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="卖家ID" prop="sellerId">
              <el-input v-model="form.sellerId" placeholder="请输入卖家ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="买家ID" prop="buyerId">
              <el-input v-model="form.buyerId" placeholder="请输入买家ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="成交价格" prop="orderPrice">
              <el-input v-model="form.orderPrice" placeholder="请输入成交价格" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="完成时间" prop="finishTime">
              <el-date-picker clearable
                v-model="form.finishTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择完成时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="取消原因" prop="cancelReason">
              <el-input v-model="form.cancelReason" type="textarea" placeholder="请输入内容" />
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

    <!-- 订单状态流转对话框：可选状态由后端 /nextStatus 返回，界面上根本选不出非法状态 -->
    <el-dialog title="订单状态流转" :visible.sync="statusOpen" width="520px" append-to-body>
      <el-form ref="statusForm" :model="statusForm" :rules="statusRules" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ statusForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="当前状态">
          <el-tag :type="statusTagType(statusForm.fromStatus)">{{ statusLabel(statusForm.fromStatus) }}</el-tag>
        </el-form-item>
        <el-form-item label="目标状态" prop="toStatus">
          <el-select v-model="statusForm.toStatus" placeholder="请选择目标状态" style="width: 100%">
            <el-option
              v-for="item in nextStatusOptions"
              :key="item.code"
              :label="item.label"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="statusForm.remark" type="textarea" placeholder="取消订单时建议填写原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitChangeStatus">确 定</el-button>
        <el-button @click="statusOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 订单状态流转记录（时间线） -->
    <el-dialog title="状态流转记录" :visible.sync="logOpen" width="660px" append-to-body>
      <el-timeline v-if="statusLogs.length > 0">
        <el-timeline-item
          v-for="log in statusLogs"
          :key="log.logId"
          :timestamp="parseTime(log.createTime)"
          placement="top"
        >
          <p>
            <span v-if="log.fromStatus !== null && log.fromStatus !== undefined">
              {{ statusLabel(log.fromStatus) }} →
            </span>
            <b>{{ statusLabel(log.toStatus) }}</b>
            <span style="color: #909399">
              ｜{{ log.operatorName }}（{{ operatorRoleLabel(log.operatorRole) }}）
            </span>
          </p>
          <p v-if="log.remark" style="color: #606266">{{ log.remark }}</p>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无流转记录" />
    </el-dialog>
  </div>
</template>

<script>
import {
  listOrder,
  getOrder,
  delOrder,
  addOrder,
  updateOrder,
  listNextStatus,
  changeOrderStatus,
  listStatusLog
} from "@/api/system/order"

// 订单状态：与后端 IdleOrderStatus 枚举一一对应，这里只用于展示与筛选
const ORDER_STATUS = [
  { code: "0", label: "待确认", tag: "warning" },
  { code: "1", label: "交易中", tag: "" },
  { code: "2", label: "已完成", tag: "success" },
  { code: "3", label: "已取消", tag: "info" }
]

export default {
  name: "Order",
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
      // 商品交易订单表格数据
      orderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 状态下拉选项
      statusOptions: ORDER_STATUS,
      // 状态流转弹窗
      statusOpen: false,
      statusForm: {},
      // 当前订单允许流转到的状态（后端返回，前端不硬编码流转规则）
      nextStatusOptions: [],
      statusRules: {
        toStatus: [{ required: true, message: "请选择目标状态", trigger: "change" }]
      },
      // 流转记录弹窗
      logOpen: false,
      statusLogs: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: null,
        goodsId: null,
        sellerId: null,
        buyerId: null,
        orderPrice: null,
        orderStatus: null,
        finishTime: null,
        cancelReason: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderNo: [
          { required: true, message: "订单编号不能为空", trigger: "blur" }
        ],
        goodsId: [
          { required: true, message: "商品ID不能为空", trigger: "blur" }
        ],
        sellerId: [
          { required: true, message: "卖家ID不能为空", trigger: "blur" }
        ],
        buyerId: [
          { required: true, message: "买家ID不能为空", trigger: "blur" }
        ],
        orderPrice: [
          { required: true, message: "成交价格不能为空", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询商品交易订单列表 */
    getList() {
      this.loading = true
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows
        this.total = response.total
        this.loading = false
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
        orderId: null,
        orderNo: null,
        goodsId: null,
        sellerId: null,
        buyerId: null,
        orderPrice: null,
        orderStatus: null,
        finishTime: null,
        cancelReason: null,
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
      this.ids = selection.map(item => item.orderId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加商品交易订单"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const orderId = row.orderId || this.ids
      getOrder(orderId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改商品交易订单"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 订单状态不从这个表单提交：后端已禁止，改状态请用「流转」按钮
          const { orderStatus, ...payload } = this.form
          if (payload.orderId != null) {
            updateOrder(payload).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addOrder(payload).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 状态码 -> 文案 */
    statusLabel(code) {
      const item = ORDER_STATUS.find(s => s.code === String(code))
      return item ? item.label : code
    },
    /** 状态码 -> 标签颜色 */
    statusTagType(code) {
      const item = ORDER_STATUS.find(s => s.code === String(code))
      return item ? item.tag : "info"
    },
    /** 是否为终态（已完成 / 已取消） */
    isFinalStatus(code) {
      return String(code) === "2" || String(code) === "3"
    },
    /** 操作角色文案 */
    operatorRoleLabel(role) {
      const map = { BUYER: "买家", SELLER: "卖家", ADMIN: "管理端", SYSTEM: "系统" }
      return map[role] || role
    },
    /** 打开状态流转弹窗：先问后端这个订单能去哪几个状态 */
    handleChangeStatus(row) {
      this.statusForm = {
        orderId: row.orderId,
        orderNo: row.orderNo,
        fromStatus: row.orderStatus,
        toStatus: null,
        remark: null
      }
      this.nextStatusOptions = []
      listNextStatus(row.orderId).then(response => {
        this.nextStatusOptions = response.data || []
        this.statusOpen = true
        this.$nextTick(() => {
          this.$refs["statusForm"] && this.$refs["statusForm"].clearValidate()
        })
      })
    },
    /** 提交流转 */
    submitChangeStatus() {
      this.$refs["statusForm"].validate(valid => {
        if (valid) {
          changeOrderStatus(
            this.statusForm.orderId,
            this.statusForm.toStatus,
            this.statusForm.remark
          ).then(response => {
            this.$modal.msgSuccess("状态流转成功")
            this.statusOpen = false
            this.getList()
          })
        }
      })
    },
    /** 查看状态流转记录 */
    handleStatusLog(row) {
      listStatusLog(row.orderId).then(response => {
        this.statusLogs = response.data || []
        this.logOpen = true
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const orderIds = row.orderId || this.ids
      this.$modal.confirm('是否确认删除商品交易订单编号为"' + orderIds + '"的数据项？').then(function() {
        return delOrder(orderIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/order/export', {
        ...this.queryParams
      }, `order_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
