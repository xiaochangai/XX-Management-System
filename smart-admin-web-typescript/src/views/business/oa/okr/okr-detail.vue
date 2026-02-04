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
        <a-button type="default" @click="addKeyResult" v-privilege="'oa:okr:kr:add'">
          新增关键结果
        </a-button>
      </div>
    </a-row>

    <a-descriptions :title="objectiveDetail.title" :column="4" size="small">
      <a-descriptions-item label="周期">{{ objectiveDetail.periodName }}</a-descriptions-item>
      <a-descriptions-item label="负责人">{{ objectiveDetail.ownerName }}</a-descriptions-item>
      <a-descriptions-item label="状态">
        {{ $smartEnumPlugin.getDescByValue('OKR_STATUS_ENUM', objectiveDetail.status) }}
      </a-descriptions-item>
      <a-descriptions-item label="置信度">
        {{ $smartEnumPlugin.getDescByValue('OKR_CONFIDENCE_ENUM', objectiveDetail.confidence) }}
      </a-descriptions-item>
      <a-descriptions-item label="对齐目标" :span="2">{{ objectiveDetail.parentObjectiveTitle || '—' }}</a-descriptions-item>
      <a-descriptions-item label="进度" :span="2">
        <a-progress :percent="Number(objectiveDetail.progress || 0)" size="small" />
      </a-descriptions-item>
      <a-descriptions-item label="描述" :span="4">
        <div style="white-space: pre-wrap">{{ objectiveDetail.description || '—' }}</div>
      </a-descriptions-item>
    </a-descriptions>

    <a-divider />

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
        <template v-if="column.dataIndex === 'status'">
          {{ $smartEnumPlugin.getDescByValue('OKR_STATUS_ENUM', text) }}
        </template>
        <template v-if="column.dataIndex === 'confidence'">
          {{ $smartEnumPlugin.getDescByValue('OKR_CONFIDENCE_ENUM', text) }}
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button type="link" size="small" @click="editKeyResult(record)" v-privilege="'oa:okr:kr:update'">编辑</a-button>
            <a-button type="link" size="small" danger @click="confirmDelete(record.keyResultId)" v-privilege="'oa:okr:kr:delete'">删除</a-button>
          </div>
        </template>
      </template>
    </a-table>
  </a-card>

  <OkrObjectiveFormDrawer ref="objectiveFormRef" @refresh="loadDetail" />
  <OkrKeyResultFormModal ref="keyResultFormRef" @refresh="loadDetail" />
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute } from 'vue-router';
  import { Modal, message } from 'ant-design-vue';
  import { okrApi } from '/@/api/business/oa/okr-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';
  import OkrObjectiveFormDrawer from './components/okr-objective-form-drawer.vue';
  import OkrKeyResultFormModal from './components/okr-key-result-form-modal.vue';

  const route = useRoute();

  const objectiveDetail = ref({});
  const keyResultList = ref([]);

  const objectiveFormRef = ref();
  const keyResultFormRef = ref();

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
      title: '操作',
      dataIndex: 'action',
      width: 140,
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

  function editKeyResult(record) {
    keyResultFormRef.value.showModal(record);
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
</script>
