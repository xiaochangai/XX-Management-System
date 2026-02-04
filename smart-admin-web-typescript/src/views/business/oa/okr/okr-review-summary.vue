<!--
 * OKR 周期复盘汇总
 -->
<template>
  <a-form class="smart-query-form" v-privilege="'oa:okr:review:summary'">
    <a-row class="smart-query-form-row">
      <a-form-item label="周期" class="smart-query-form-item">
        <a-select v-model:value="periodId" style="width: 200px" @change="loadSummary" allowClear placeholder="请选择周期">
          <a-select-option v-for="item in periodList" :key="item.periodId" :value="item.periodId">
            {{ item.periodName }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item class="smart-query-form-item smart-margin-left10">
        <a-button type="primary" @click="loadSummary">
          <template #icon>
            <SearchOutlined />
          </template>
          查询
        </a-button>
      </a-form-item>
    </a-row>
  </a-form>

  <a-card size="small" :bordered="false" :hoverable="true">
    <a-row :gutter="16" class="smart-margin-bottom10">
      <a-col :xs="24" :sm="12" :md="6">
        <a-card size="small" class="okr-stat-card">
          <a-statistic title="目标数量" :value="summary.objectiveCount || 0" />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card size="small" class="okr-stat-card">
          <a-statistic title="已评分" :value="summary.scoredCount || 0" />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card size="small" class="okr-stat-card">
          <a-statistic title="平均评分" :value="formatScore(summary.avgScore)" />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card size="small" class="okr-stat-card">
          <a-statistic title="平均进度" :value="formatPercent(summary.avgProgress)" suffix="%" />
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" class="smart-margin-bottom10">
      <a-col :xs="24" :md="12">
        <a-card size="small" :bordered="false" class="okr-panel">
          <template #title>状态分布</template>
          <div class="okr-status-grid">
            <a-tag color="default">草稿 {{ summary.statusDraftCount || 0 }}</a-tag>
            <a-tag color="blue">正常 {{ summary.statusOnTrackCount || 0 }}</a-tag>
            <a-tag color="orange">有风险 {{ summary.statusAtRiskCount || 0 }}</a-tag>
            <a-tag color="red">偏离 {{ summary.statusOffTrackCount || 0 }}</a-tag>
            <a-tag color="green">已完成 {{ summary.statusDoneCount || 0 }}</a-tag>
            <a-tag>已取消 {{ summary.statusCancelledCount || 0 }}</a-tag>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :md="12">
        <a-card size="small" :bordered="false" class="okr-panel">
          <template #title>评分分布</template>
          <div v-for="bucket in summary.scoreBucketList" :key="bucket.label" class="okr-bucket-row">
            <div class="okr-bucket-label">{{ bucket.label }}</div>
            <a-progress :percent="bucketPercent(bucket.count)" size="small" />
            <div class="okr-bucket-count">{{ bucket.count }}</div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-card size="small" :bordered="false">
      <template #title>目标明细</template>
      <a-table
        size="small"
        :dataSource="summary.objectiveList"
        :columns="columns"
        rowKey="objectiveId"
        :pagination="false"
        bordered
      >
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.dataIndex === 'title'">
            <a @click="toDetail(record.objectiveId)">{{ text }}</a>
          </template>
          <template v-if="column.dataIndex === 'progress'">
            <a-progress :percent="Number(text || 0)" size="small" />
          </template>
          <template v-if="column.dataIndex === 'score'">
            {{ formatScore(text) }}
          </template>
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="statusColor(text)">{{ $smartEnumPlugin.getDescByValue('OKR_STATUS_ENUM', text) }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'confidence'">
            <a-tag color="geekblue">{{ $smartEnumPlugin.getDescByValue('OKR_CONFIDENCE_ENUM', text) }}</a-tag>
          </template>
        </template>
      </a-table>
    </a-card>
  </a-card>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { okrApi } from '/@/api/business/oa/okr-api';
  import { smartSentry } from '/@/lib/smart-sentry';

  const router = useRouter();

  const periodList = ref([]);
  const periodId = ref(undefined);
  const defaultSummary = {
    objectiveCount: 0,
    scoredCount: 0,
    avgScore: null,
    avgProgress: 0,
    statusDraftCount: 0,
    statusOnTrackCount: 0,
    statusAtRiskCount: 0,
    statusOffTrackCount: 0,
    statusDoneCount: 0,
    statusCancelledCount: 0,
    scoreBucketList: [],
    objectiveList: [],
  };
  const summary = ref({ ...defaultSummary });

  const columns = ref([
    { title: '目标', dataIndex: 'title', minWidth: 220, ellipsis: true },
    { title: '负责人', dataIndex: 'ownerName', width: 100 },
    { title: '进度', dataIndex: 'progress', width: 130 },
    { title: '评分', dataIndex: 'score', width: 80 },
    { title: '状态', dataIndex: 'status', width: 90 },
    { title: '置信度', dataIndex: 'confidence', width: 90 },
    { title: '复盘人', dataIndex: 'reviewUserName', width: 90 },
    { title: '复盘时间', dataIndex: 'reviewTime', width: 150 },
    { title: '复盘说明', dataIndex: 'reviewNote', minWidth: 160, ellipsis: true },
  ]);

  async function queryPeriodList() {
    try {
      const result = await okrApi.listAllPeriod();
      periodList.value = result.data || [];
      if (!periodId.value && periodList.value.length > 0) {
        periodId.value = periodList.value[0].periodId;
      }
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  async function loadSummary() {
    if (!periodId.value) {
      summary.value = { ...defaultSummary };
      return;
    }
    try {
      const result = await okrApi.getReviewSummary(periodId.value);
      summary.value = result.data || summary.value;
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  function bucketPercent(count) {
    const total = summary.value.objectiveCount || 0;
    if (!total) {
      return 0;
    }
    return Math.round((count / total) * 100);
  }

  function formatScore(value) {
    if (value === null || value === undefined) {
      return '—';
    }
    return Number(value).toFixed(2);
  }

  function formatPercent(value) {
    if (value === null || value === undefined) {
      return '0.00';
    }
    return Number(value).toFixed(2);
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

  function toDetail(objectiveId) {
    router.push({
      path: '/oa/okr/okr-detail',
      query: { objectiveId },
    });
  }

  onMounted(async () => {
    await queryPeriodList();
    await loadSummary();
  });
</script>

<style scoped>
  .okr-stat-card {
    border-radius: 8px;
    background: #fafafa;
  }

  .okr-panel {
    background: #fafafa;
    border-radius: 8px;
    min-height: 160px;
  }

  .okr-status-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .okr-bucket-row {
    display: grid;
    grid-template-columns: 80px 1fr 40px;
    align-items: center;
    gap: 8px;
    margin-bottom: 6px;
  }

  .okr-bucket-label {
    font-size: 12px;
    color: #595959;
  }

  .okr-bucket-count {
    text-align: right;
    font-size: 12px;
    color: #8c8c8c;
  }
</style>
