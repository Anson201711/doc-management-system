<template>
  <div class="home-container">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <span>欢迎使用文档管理系统</span>
        </div>
      </template>
      <div class="content">
        <p>这是一个基于 Vue 3 + Element Plus 的文档管理系统。</p>
        <p>当前登录用户：{{ username }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const username = ref('')

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const user = JSON.parse(userStr)
    username.value = user.username || user.nickname || '用户'
  }
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: 600;
}

.content p {
  margin: 10px 0;
  color: #666;
}
</style>