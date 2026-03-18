<template>
  <div class="document-list-container">
    <!-- 搜索和筛选区域 -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文档名称"
            clearable
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterType" placeholder="文档类型" clearable @change="handleSearch">
            <el-option
              v-for="item in documentTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterStatus" placeholder="文档状态" clearable @change="handleSearch">
            <el-option
              v-for="item in documentStatuses"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-col>
        <el-col :span="6" style="text-align: right;">
          <el-button type="success" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新建文档
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 文档列表表格 -->
    <el-card class="table-card">
      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="文档名称" min-width="200" />
        <el-table-column prop="documentType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.documentType)">
              {{ getTypeLabel(row.documentType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getDocumentList, deleteDocument } from '@/api/document'
import type { Document, DocumentListParams } from '@/types/document'
import { documentTypes, documentStatuses } from '@/types/document'

const router = useRouter()

// 数据状态
const loading = ref(false)
const tableData = ref<Document[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 筛选状态
const searchKeyword = ref('')
const filterType = ref('')
const filterStatus = ref('')

// 获取文档列表
const fetchData = async () => {
  loading.value = true
  try {
    const params: DocumentListParams = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined,
      documentType: filterType.value || undefined,
      status: filterStatus.value || undefined
    }
    
    const res = await getDocumentList(params)
    if (res.code === 200) {
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取文档列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
  fetchData()
}

// 页码改变
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

// 新建文档
const handleCreate = () => {
  router.push('/documents/create')
}

// 查看文档
const handleView = (row: Document) => {
  router.push(`/documents/${row.id}`)
}

// 编辑文档
const handleEdit = (row: Document) => {
  router.push(`/documents/${row.id}/edit`)
}

// 删除文档
const handleDelete = (row: Document) => {
  ElMessageBox.confirm('确定要删除该文档吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteDocument(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 工具函数
const getTypeLabel = (type: string) => {
  const item = documentTypes.find(t => t.value === type)
  return item?.label || type
}

const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    technical: 'primary',
    product: 'success',
    requirement: 'warning',
    test: 'info',
    manual: ''
  }
  return typeMap[type] || ''
}

const getStatusLabel = (status: string) => {
  const item = documentStatuses.find(s => s.value === status)
  return item?.label || status
}

const getStatusTagType = (status: string) => {
  const statusMap: Record<string, string> = {
    draft: 'info',
    published: 'success',
    archived: ''
  }
  return statusMap[status] || ''
}

const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 初始加载
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.document-list-container {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.table-card {
  min-height: 500px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.el-select {
  width: 100%;
}
</style>