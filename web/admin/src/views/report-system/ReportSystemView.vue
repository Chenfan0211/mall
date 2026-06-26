<template>
  <section class="page-panel">
    <h1 class="page-title">报表与系统</h1>
    <el-tabs model-value="reports">
      <el-tab-pane label="固定报表" name="reports" />
      <el-tab-pane label="权限" name="permissions" />
      <el-tab-pane label="后台账号" name="accounts" />
      <el-tab-pane label="角色" name="roles" />
      <el-tab-pane label="基础配置" name="settings" />
      <el-tab-pane label="数据字典" name="dict" />
      <el-tab-pane label="系统任务" name="jobs" />
      <el-tab-pane label="操作日志" name="logs" />
    </el-tabs>
  </section>
</template>
