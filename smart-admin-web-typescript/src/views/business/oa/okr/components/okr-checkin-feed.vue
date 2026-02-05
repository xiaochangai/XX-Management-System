<template>
  <a-list :dataSource="checkinList" class="okr-feed" itemLayout="vertical">
    <template #renderItem="{ item }">
      <a-list-item class="okr-feed-item">
        <div class="okr-feed-header">
          <a-avatar size="small">{{ shortName(item.createUserName) }}</a-avatar>
          <div class="okr-feed-user">
            <div class="okr-feed-name">{{ item.createUserName || '未知' }}</div>
            <div class="okr-feed-time">{{ item.createTime || '刚刚' }}</div>
          </div>
          <div class="okr-feed-tags">
            <a-tag :color="statusColor(item.status)">{{ statusDesc(item.status) }}</a-tag>
            <a-tag color="geekblue">{{ confidenceDesc(item.confidence) || '—' }}</a-tag>
          </div>
        </div>
        <div class="okr-feed-body">
          <div class="okr-feed-title">
            <span class="okr-feed-label">进度记录</span>
            <span>{{ item.keyResultTitle ? `KR：${item.keyResultTitle}` : '目标更新' }}</span>
          </div>
          <div class="okr-feed-metrics">
            <span>当前值：{{ item.currentValue ?? '-' }}</span>
            <span class="okr-sub-divider">·</span>
            <span>进度 {{ Math.round(Number(item.progress || 0)) }}%</span>
          </div>
          <a-progress :percent="Number(item.progress || 0)" size="small" :showInfo="false" />
        </div>
        <div v-if="item.note" class="okr-feed-note">{{ item.note }}</div>
      </a-list-item>
    </template>
  </a-list>
</template>

<script setup lang="ts">
  import { getCurrentInstance } from 'vue';

  const props = defineProps({
    checkinList: {
      type: Array,
      default: () => [],
    },
  });

  const smartEnumPlugin = getCurrentInstance().appContext.config.globalProperties.$smartEnumPlugin;

  function statusDesc(value) {
    return smartEnumPlugin.getDescByValue('OKR_STATUS_ENUM', value);
  }

  function confidenceDesc(value) {
    return smartEnumPlugin.getDescByValue('OKR_CONFIDENCE_ENUM', value);
  }

  function statusColor(value) {
    switch (value) {
      case 1:
        return 'blue';
      case 2:
        return 'orange';
      case 3:
        return 'red';
      case 4:
        return 'green';
      case 5:
        return 'default';
      default:
        return 'default';
    }
  }

  function shortName(name) {
    if (!name) {
      return '未';
    }
    return String(name).slice(0, 1);
  }
</script>

<style scoped>
  .okr-feed {
    background: transparent;
  }

  .okr-feed-item {
    background: #f8f9fb;
    border-radius: 10px;
    padding: 12px 14px;
    margin-bottom: 10px;
  }

  .okr-feed-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 8px;
  }

  .okr-feed-user {
    flex: 1;
  }

  .okr-feed-name {
    font-size: 13px;
    color: #262626;
    font-weight: 600;
  }

  .okr-feed-time {
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-feed-tags {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
  }

  .okr-feed-body {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .okr-feed-title {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: #262626;
  }

  .okr-feed-label {
    background: #e6f4ff;
    color: #1677ff;
    padding: 2px 6px;
    border-radius: 6px;
    font-size: 11px;
  }

  .okr-feed-metrics {
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-feed-note {
    margin-top: 8px;
    font-size: 13px;
    color: #434343;
    white-space: pre-wrap;
  }
</style>
