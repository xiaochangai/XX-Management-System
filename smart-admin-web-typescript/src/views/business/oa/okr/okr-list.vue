<!--
 * OKR 目标列表
 -->
<template>
  <a-form class="smart-query-form" v-privilege="'oa:okr:objective:query'">
    <a-row class="smart-query-form-row">
      <a-form-item label="周期" class="smart-query-form-item">
        <a-select v-model:value="queryForm.periodId" style="width: 160px" allowClear placeholder="全部">
          <a-select-option v-for="item in periodList" :key="item.periodId" :value="item.periodId">
            {{ item.periodName }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="负责人" class="smart-query-form-item">
        <EmployeeSelect width="160px" v-model:value="queryForm.ownerEmployeeId" placeholder="请选择" />
      </a-form-item>

      <a-form-item label="状态" class="smart-query-form-item">
        <SmartEnumSelect width="140px" v-model:value="queryForm.status" enum-name="OKR_STATUS_ENUM" placeholder="请选择" />
      </a-form-item>

      <a-form-item label="关键字" class="smart-query-form-item">
        <a-input style="width: 180px" v-model:value="queryForm.keywords" placeholder="目标标题/描述" />
      </a-form-item>

      <a-form-item class="smart-query-form-item smart-margin-left10">
        <a-button-group>
          <a-button type="primary" @click="onSearch">
            <template #icon>
              <SearchOutlined />
            </template>
            查询
          </a-button>
          <a-button @click="resetQuery">
            <template #icon>
              <ReloadOutlined />
            </template>
            重置
          </a-button>
        </a-button-group>
      </a-form-item>
    </a-row>
  </a-form>

  <a-card size="small" :bordered="false" :hoverable="true">
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <a-button type="primary" @click="add" v-privilege="'oa:okr:objective:add'">
          <template #icon>
            <PlusOutlined />
          </template>
          新建目标
        </a-button>
        <a-button @click="toPeriodManage" type="default">
          周期管理
        </a-button>
        <a-button @click="toOkrMap" type="default">
          对齐视图
        </a-button>
        <a-button @click="toReviewSummary" type="default">
          复盘汇总
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.OA.OKR_OBJECTIVE" :refresh="queryList" />
      </div>
    </a-row>

    <a-table
      size="small"
      :dataSource="tableData"
      :columns="columns"
      rowKey="objectiveId"
      :pagination="false"
      :loading="tableLoading"
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
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button type="link" size="small" @click="toDetail(record.objectiveId)">详情</a-button>
            <a-button type="link" size="small" @click="edit(record.objectiveId)" v-privilege="'oa:okr:objective:update'">编辑</a-button>
            <a-button type="link" size="small" danger @click="confirmDelete(record.objectiveId)" v-privilege="'oa:okr:objective:delete'">删除</a-button>
          </div>
        </template>
      </template>
    </a-table>

    <div class="smart-query-table-page">
      <a-pagination
        showSizeChanger
        showQuickJumper
        show-less-items
        :pageSizeOptions="PAGE_SIZE_OPTIONS"
        :defaultPageSize="queryForm.pageSize"
        v-model:current="queryForm.pageNum"
        v-model:pageSize="queryForm.pageSize"
        :total="total"
        @change="queryList"
        :show-total="(total) => `共${total}条`"
      />
    </div>

    <OkrObjectiveFormDrawer ref="objectiveFormRef" @refresh="queryList" />
  </a-card>
</template>

<script setup lang="ts">
  import { onMounted, reactive, ref } from 'vue';
  import { Modal, message } from 'ant-design-vue';
  import { useRouter } from 'vue-router';
  import { okrApi } from '/@/api/business/oa/okr-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { PAGE_SIZE, PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import OkrObjectiveFormDrawer from './components/okr-objective-form-drawer.vue';
  import EmployeeSelect from '/@/components/system/employee-select/index.vue';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';

  const router = useRouter();

  const columns = ref([
    {
      title: '目标',
      dataIndex: 'title',
      minWidth: 200,
      ellipsis: true,
    },
    {
      title: '周期',
      dataIndex: 'periodName',
      width: 120,
    },
    {
      title: '负责人',
      dataIndex: 'ownerName',
      width: 100,
    },
    {
      title: '进度',
      dataIndex: 'progress',
      width: 120,
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
      title: 'KR数',
      dataIndex: 'keyResultCount',
      width: 70,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      width: 160,
    },
    {
      title: '操作',
      dataIndex: 'action',
      width: 180,
    },
  ]);

  const queryFormState = {
    periodId: undefined,
    ownerEmployeeId: undefined,
    status: undefined,
    keywords: '',
    pageNum: 1,
    pageSize: PAGE_SIZE,
    searchCount: true,
  };
  const queryForm = reactive({ ...queryFormState });
  const tableLoading = ref(false);
  const tableData = ref([]);
  const total = ref(0);

  const periodList = ref([]);
  const objectiveFormRef = ref();

  async function queryPeriodList() {
    try {
      const result = await okrApi.listAllPeriod();
      periodList.value = result.data || [];
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  async function queryList() {
    tableLoading.value = true;
    try {
      const result = await okrApi.queryObjective(queryForm);
      tableData.value = result.data.list;
      total.value = result.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  function onSearch() {
    queryForm.pageNum = 1;
    queryList();
  }

  function resetQuery() {
    Object.assign(queryForm, queryFormState);
    queryList();
  }

  function add() {
    objectiveFormRef.value.showModal();
  }

  function edit(objectiveId) {
    objectiveFormRef.value.showModal(objectiveId);
  }

  function toDetail(objectiveId) {
    router.push({
      path: '/oa/okr/okr-detail',
      query: { objectiveId },
    });
  }

  function toPeriodManage() {
    router.push({
      path: '/oa/okr/okr-period-list',
    });
  }

  function toOkrMap() {
    router.push({
      path: '/oa/okr/okr-map',
    });
  }

  function toReviewSummary() {
    router.push({
      path: '/oa/okr/okr-review-summary',
    });
  }

  function confirmDelete(objectiveId) {
    Modal.confirm({
      title: '确认删除该目标吗？',
      onOk: () => deleteObjective(objectiveId),
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

  async function deleteObjective(objectiveId) {
    SmartLoading.show();
    try {
      await okrApi.deleteObjective(objectiveId);
      message.success('删除成功');
      queryList();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  onMounted(() => {
    queryPeriodList();
    queryList();
  });
</script>
