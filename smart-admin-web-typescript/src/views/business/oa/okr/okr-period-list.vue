<!--
 * OKR 周期
 -->
<template>
  <a-form class="smart-query-form" v-privilege="'oa:okr:period:query'">
    <a-row class="smart-query-form-row">
      <a-form-item label="关键字" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.keywords" placeholder="周期名称" />
      </a-form-item>

      <a-form-item label="状态" class="smart-query-form-item">
        <SmartEnumSelect
          width="140px"
          v-model:value="queryForm.status"
          enum-name="OKR_PERIOD_STATUS_ENUM"
          placeholder="请选择"
        />
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
        <a-button type="primary" @click="add" v-privilege="'oa:okr:period:add'">
          <template #icon>
            <PlusOutlined />
          </template>
          新建周期
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.OA.OKR_PERIOD" :refresh="queryList" />
      </div>
    </a-row>

    <a-table
      size="small"
      :dataSource="tableData"
      :columns="columns"
      rowKey="periodId"
      :pagination="false"
      :loading="tableLoading"
      bordered
    >
      <template #bodyCell="{ column, record, text }">
        <template v-if="column.dataIndex === 'status'">
          {{ $smartEnumPlugin.getDescByValue('OKR_PERIOD_STATUS_ENUM', text) }}
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button type="link" size="small" @click="edit(record)" v-privilege="'oa:okr:period:update'">编辑</a-button>
            <a-button type="link" size="small" danger @click="confirmDelete(record.periodId)" v-privilege="'oa:okr:period:delete'"
              >删除</a-button
            >
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

    <OkrPeriodFormModal ref="periodFormRef" @refresh="queryList" />
  </a-card>
</template>

<script setup lang="ts">
  import { onMounted, reactive, ref } from 'vue';
  import { Modal, message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { okrApi } from '/@/api/business/oa/okr-api';
  import { PAGE_SIZE, PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import OkrPeriodFormModal from './components/okr-period-form-modal.vue';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';

  const columns = ref([
    {
      title: '周期名称',
      dataIndex: 'periodName',
      minWidth: 180,
      ellipsis: true,
    },
    {
      title: '开始日期',
      dataIndex: 'startDate',
      width: 120,
    },
    {
      title: '结束日期',
      dataIndex: 'endDate',
      width: 120,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
    },
    {
      title: '创建人',
      dataIndex: 'createUserName',
      width: 100,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 160,
    },
    {
      title: '操作',
      dataIndex: 'action',
      width: 140,
    },
  ]);

  const queryFormState = {
    keywords: '',
    status: undefined,
    pageNum: 1,
    pageSize: PAGE_SIZE,
    searchCount: true,
  };
  const queryForm = reactive({ ...queryFormState });
  const tableLoading = ref(false);
  const tableData = ref([]);
  const total = ref(0);

  const periodFormRef = ref();

  async function queryList() {
    tableLoading.value = true;
    try {
      const result = await okrApi.queryPeriod(queryForm);
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
    periodFormRef.value.showModal();
  }

  function edit(record) {
    periodFormRef.value.showModal(record);
  }

  function confirmDelete(periodId) {
    Modal.confirm({
      title: '确认删除该周期吗？',
      onOk: () => deletePeriod(periodId),
    });
  }

  async function deletePeriod(periodId) {
    SmartLoading.show();
    try {
      await okrApi.deletePeriod(periodId);
      message.success('删除成功');
      queryList();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  onMounted(queryList);
</script>
