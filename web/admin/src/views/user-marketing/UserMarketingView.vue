<template>
  <section class="page-panel">
    <h1 class="page-title">用户与营销</h1>
    <el-alert type="info" show-icon :closable="false" title="当前版本只包含用户、评论、前台运营位、Banner、活动页基础配置和消息触达。" />
    <el-tabs model-value="users" class="tabs">
      <el-tab-pane label="用户中心" name="users" />
      <el-tab-pane label="评论中心" name="comments" />
      <el-tab-pane label="前台运营位" name="placements" />
      <el-tab-pane label="Banner" name="banner" />
      <el-tab-pane label="活动页" name="activity" />
      <el-tab-pane label="消息触达" name="message" />
    </el-tabs>
  </section>
</template>

<style lang="scss" scoped>
.tabs {
  margin-top: 16px;
}
</style>
