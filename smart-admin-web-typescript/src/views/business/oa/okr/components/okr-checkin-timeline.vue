<template>
  <a-timeline>
    <a-timeline-item v-for="item in checkinList" :key="item.checkinId" :color="statusColor(item.status)">
      <div class="okr-checkin-item">
        <div class="okr-checkin-header">
          <div class="okr-checkin-title">
            <span v-if="item.keyResultTitle" class="okr-kr">KR：{{ item.keyResultTitle }}</span>
            <span v-else class="okr-kr">目标更新</span>
          </div>
          <div class="okr-checkin-meta">
            <span>{{ item.createUserName }}</span>
            <span class="okr-dot">•</span>
            <span>{{ item.createTime }}</span>
          </div>
        </div>
        <div class="okr-checkin-body">
          <div class="okr-checkin-stat">
            <span class="okr-label">当前值</span>
            <span>{{ item.currentValue ?? '-' }}</span>
          </div>
          <div class="okr-checkin-stat">
            <span class="okr-label">进度</span>
            <a-progress :percent="Number(item.progress || 0)" size="small" />
          </div>
          <div class="okr-checkin-stat">
            <span class="okr-label">状态</span>
            <a-tag :color="statusColor(item.status)">{{ statusDesc(item.status) }}</a-tag>
          </div>
          <div class="okr-checkin-stat">
            <span class="okr-label">置信度</span>
            <a-tag color="geekblue">{{ confidenceDesc(item.confidence) || '-' }}</a-tag>
          </div>
        </div>
        <div v-if="item.note" class="okr-checkin-note">{{ item.note }}</div>
      </div>
    </a-timeline-item>
  </a-timeline>
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
</script>

<style scoped>
  .okr-checkin-item {
    background: #fafafa;
    padding: 10px 14px;
    border-radius: 8px;
  }

  .okr-checkin-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 6px;
  }

  .okr-checkin-title {
    font-weight: 600;
    color: #262626;
  }

  .okr-checkin-meta {
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-dot {
    margin: 0 6px;
  }

  .okr-checkin-body {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 8px 16px;
  }

  .okr-checkin-stat {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 12px;
    color: #595959;
  }

  .okr-label {
    color: #8c8c8c;
  }

  .okr-checkin-note {
    margin-top: 8px;
    color: #434343;
    font-size: 13px;
    white-space: pre-wrap;
  }
</style>
