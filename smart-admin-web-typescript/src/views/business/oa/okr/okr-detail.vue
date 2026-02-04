<!--
 * OKR 目标详情
 -->
<template>
  <a-card size="small" :bordered="false" :hoverable="true">
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <a-button type="primary" @click="editObjective" v-privilege="'oa:okr:objective:update'">
          编辑目标
        </a-button>
        <a-button type="default" @click="addObjectiveCheckin" v-privilege="'oa:okr:checkin:add'">
          更新目标进展
        </a-button>
        <a-button type="default" @click="reviewObjective" v-privilege="'oa:okr:review:objective'">
          复盘评分
        </a-button>
        <a-button type="default" @click="addKeyResult" v-privilege="'oa:okr:kr:add'">
          新增关键结果
        </a-button>
      </div>
    </a-row>

    <a-descriptions :title="objectiveDetail.title" :column="4" size="small">
      <a-descriptions-item label="周期">{{ objectiveDetail.periodName }}</a-descriptions-item>
      <a-descriptions-item label="负责人">{{ objectiveDetail.ownerName }}</a-descriptions-item>
      <a-descriptions-item label="状态">
        <a-tag :color="statusColor(objectiveDetail.status)">
          {{ $smartEnumPlugin.getDescByValue('OKR_STATUS_ENUM', objectiveDetail.status) }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="置信度">
        <a-tag color="geekblue">
          {{ $smartEnumPlugin.getDescByValue('OKR_CONFIDENCE_ENUM', objectiveDetail.confidence) }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="评分">{{ formatScore(objectiveDetail.score) }}</a-descriptions-item>
      <a-descriptions-item label="复盘人">{{ objectiveDetail.reviewUserName || '—' }}</a-descriptions-item>
      <a-descriptions-item label="复盘时间">{{ objectiveDetail.reviewTime || '—' }}</a-descriptions-item>
      <a-descriptions-item label="对齐目标" :span="2">{{ objectiveDetail.parentObjectiveTitle || '—' }}</a-descriptions-item>
      <a-descriptions-item label="进度" :span="2">
        <a-progress :percent="Number(objectiveDetail.progress || 0)" size="small" />
      </a-descriptions-item>
      <a-descriptions-item label="描述" :span="4">
        <div style="white-space: pre-wrap">{{ objectiveDetail.description || '—' }}</div>
      </a-descriptions-item>
      <a-descriptions-item label="复盘说明" :span="4">
        <div style="white-space: pre-wrap">{{ objectiveDetail.reviewNote || '—' }}</div>
      </a-descriptions-item>
    </a-descriptions>

    <a-divider />

    <a-card size="small" :bordered="false" class="smart-margin-bottom10">
      <template #title>对齐关系</template>
      <a-row>
        <a-col :span="12">
          <div class="okr-align-block">
            <div class="okr-align-title">对齐上级目标</div>
            <div class="okr-align-content">{{ objectiveDetail.parentObjectiveTitle || '未对齐' }}</div>
          </div>
        </a-col>
        <a-col :span="12">
          <div class="okr-align-block">
            <div class="okr-align-title">对齐到我的目标</div>
            <div class="okr-align-content">
              <div v-if="alignedList.length === 0">暂无对齐目标</div>
              <div v-else>
                <div v-for="item in alignedList" :key="item.objectiveId" class="okr-align-item" @click="toDetail(item.objectiveId)">
                  {{ item.title }}（{{ item.ownerName }}）
                </div>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <a-card size="small" :bordered="false" class="smart-margin-bottom10">
      <template #title>关键结果</template>
      <a-table
        size="small"
        :dataSource="keyResultList"
        :columns="columns"
        rowKey="keyResultId"
        :pagination="false"
        bordered
      >
        <template #bodyCell="{ column, record, text }">
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
          <template v-if="column.dataIndex === 'action'">
            <div class="smart-table-operate">
              <a-button type="link" size="small" @click="reviewKeyResult(record)" v-privilege="'oa:okr:review:kr'">评分</a-button>
              <a-button type="link" size="small" @click="openCheckin(record)" v-privilege="'oa:okr:checkin:add'">更新进展</a-button>
              <a-button type="link" size="small" @click="editKeyResult(record)" v-privilege="'oa:okr:kr:update'">编辑</a-button>
              <a-button type="link" size="small" danger @click="confirmDelete(record.keyResultId)" v-privilege="'oa:okr:kr:delete'">删除</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-card size="small" :bordered="false">
      <template #title>进展更新</template>
      <OkrCheckinTimeline :checkinList="checkinList" />
    </a-card>
  </a-card>

  <OkrObjectiveFormDrawer ref="objectiveFormRef" @refresh="loadDetail" />
  <OkrKeyResultFormModal ref="keyResultFormRef" @refresh="loadDetail" />
  <OkrCheckinFormModal ref="checkinFormRef" @refresh="loadDetail" />
  <OkrReviewObjectiveModal ref="reviewObjectiveRef" @refresh="loadDetail" />
  <OkrReviewKrModal ref="reviewKrRef" @refresh="loadDetail" />
</template>

<script setup lang="ts">
  import { onMounted, ref, watch } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { Modal, message } from 'ant-design-vue';
  import { okrApi } from '/@/api/business/oa/okr-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';
  import OkrObjectiveFormDrawer from './components/okr-objective-form-drawer.vue';
  import OkrKeyResultFormModal from './components/okr-key-result-form-modal.vue';
  import OkrCheckinFormModal from './components/okr-checkin-form-modal.vue';
  import OkrCheckinTimeline from './components/okr-checkin-timeline.vue';
  import OkrReviewObjectiveModal from './components/okr-review-objective-modal.vue';
  import OkrReviewKrModal from './components/okr-review-kr-modal.vue';

  const route = useRoute();
  const router = useRouter();

  const objectiveDetail = ref({});
  const keyResultList = ref([]);
  const checkinList = ref([]);
  const alignedList = ref([]);

  const objectiveFormRef = ref();
  const keyResultFormRef = ref();
  const checkinFormRef = ref();
  const reviewObjectiveRef = ref();
  const reviewKrRef = ref();

  const columns = ref([
    {
      title: '关键结果',
      dataIndex: 'title',
      minWidth: 200,
      ellipsis: true,
    },
    {
      title: '度量',
      dataIndex: 'metricName',
      width: 120,
    },
    {
      title: '当前值',
      dataIndex: 'currentValue',
      width: 100,
    },
    {
      title: '目标值',
      dataIndex: 'targetValue',
      width: 100,
    },
    {
      title: '单位',
      dataIndex: 'unit',
      width: 80,
    },
    {
      title: '进度',
      dataIndex: 'progress',
      width: 130,
    },
    {
      title: '评分',
      dataIndex: 'score',
      width: 80,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 90,
    },
    {
      title: '置信度',
      dataIndex: 'confidence',
      width: 90,
    },
    {
      title: '复盘人',
      dataIndex: 'reviewUserName',
      width: 90,
    },
    {
      title: '复盘说明',
      dataIndex: 'reviewNote',
      minWidth: 160,
      ellipsis: true,
    },
    {
      title: '复盘时间',
      dataIndex: 'reviewTime',
      width: 150,
    },
    {
      title: '操作',
      dataIndex: 'action',
      width: 200,
    },
  ]);


  async function loadDetail() {
    if (!route.query.objectiveId) {
      return;
    }
    try {
      const result = await okrApi.getObjectiveDetail(route.query.objectiveId);
      objectiveDetail.value = result.data || {};
      keyResultList.value = result.data?.keyResultList || [];
      const checkinRes = await okrApi.queryCheckin(route.query.objectiveId);
      checkinList.value = checkinRes.data || [];
      const alignedRes = await okrApi.getObjectiveAlignedList(route.query.objectiveId);
      alignedList.value = alignedRes.data || [];
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  function editObjective() {
    if (route.query.objectiveId) {
      objectiveFormRef.value.showModal(route.query.objectiveId);
    }
  }

  function addKeyResult() {
    if (route.query.objectiveId) {
      keyResultFormRef.value.showModal({ objectiveId: route.query.objectiveId });
    }
  }

  function addObjectiveCheckin() {
    if (route.query.objectiveId) {
      checkinFormRef.value.showModal({
        objectiveId: route.query.objectiveId,
        objectiveTitle: objectiveDetail.value.title,
      });
    }
  }

  function reviewObjective() {
    if (route.query.objectiveId) {
      reviewObjectiveRef.value.showModal({
        objectiveId: route.query.objectiveId,
        title: objectiveDetail.value.title,
        score: objectiveDetail.value.score,
        reviewNote: objectiveDetail.value.reviewNote,
      });
    }
  }

  function openCheckin(record) {
    checkinFormRef.value.showModal({
      objectiveId: route.query.objectiveId,
      objectiveTitle: objectiveDetail.value.title,
      keyResultId: record.keyResultId,
      keyResultTitle: record.title,
      currentValue: record.currentValue,
      status: record.status,
      confidence: record.confidence,
    });
  }

  function editKeyResult(record) {
    keyResultFormRef.value.showModal(record);
  }

  function reviewKeyResult(record) {
    reviewKrRef.value.showModal({
      keyResultId: record.keyResultId,
      title: record.title,
      score: record.score,
      reviewNote: record.reviewNote,
    });
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

  function formatScore(value) {
    if (value === null || value === undefined) {
      return '—';
    }
    return Number(value).toFixed(2);
  }

  function toDetail(objectiveId) {
    router.push({
      path: '/oa/okr/okr-detail',
      query: { objectiveId },
    });
  }

  function confirmDelete(keyResultId) {
    Modal.confirm({
      title: '确认删除该关键结果吗？',
      onOk: () => deleteKeyResult(keyResultId),
    });
  }

  async function deleteKeyResult(keyResultId) {
    SmartLoading.show();
    try {
      await okrApi.deleteKeyResult(keyResultId);
      message.success('删除成功');
      loadDetail();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  onMounted(loadDetail);
  watch(
    () => route.query.objectiveId,
    () => {
      loadDetail();
    }
  );
</script>

<style scoped>
  .okr-align-block {
    padding: 8px 12px;
    background: #fafafa;
    border-radius: 6px;
    min-height: 72px;
  }

  .okr-align-title {
    font-size: 12px;
    color: #8c8c8c;
    margin-bottom: 6px;
  }

  .okr-align-content {
    font-size: 14px;
    color: #262626;
  }

  .okr-align-item {
    cursor: pointer;
    padding: 2px 0;
    color: #1677ff;
  }
</style>
