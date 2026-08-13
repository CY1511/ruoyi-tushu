<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品名称" prop="goodsName">
        <el-input
          v-model="queryParams.goodsName"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:favorite:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:favorite:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="favoriteList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="收藏ID" align="center" prop="favoriteId" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="商品ID" align="center" prop="goodsId" />
      <el-table-column label="商品名称" align="center" prop="goodsName" />
      <el-table-column label="封面图片" align="center" prop="coverImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.coverImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="售价" align="center" prop="price" />
      <el-table-column label="成色" align="center" prop="qualityLevel">
        <template slot-scope="scope">
          <span v-if="scope.row.qualityLevel === '1'">全新</span>
          <span v-else-if="scope.row.qualityLevel === '2'">9成新</span>
          <span v-else-if="scope.row.qualityLevel === '3'">8成新</span>
          <span v-else-if="scope.row.qualityLevel === '4'">7成新</span>
          <span v-else>{{ scope.row.qualityLevel }}</span>
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
      <el-table-column label="收藏时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:favorite:remove']"
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
  </div>
</template>

<script>
import { listFavorite, delFavorite } from "@/api/system/favorite"

export default {
  name: "Favorite",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      favoriteList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        goodsId: null,
        goodsName: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listFavorite(this.queryParams).then(response => {
        this.favoriteList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        favoriteId: null,
        userId: null,
        goodsId: null
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.favoriteId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleDelete(row) {
      const favoriteIds = row.favoriteId || this.ids
      this.$modal.confirm('是否确认删除收藏编号为"' + favoriteIds + '"的数据项？').then(function() {
        return delFavorite(favoriteIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('system/favorite/export', {
        ...this.queryParams
      }, `favorite_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>