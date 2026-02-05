<!--
 * OKR 对齐视图
 -->
<template>
  <a-form class="smart-query-form" v-privilege="'oa:okr:objective:query'">
    <a-row class="smart-query-form-row">
      <a-form-item label="周期" class="smart-query-form-item">
        <a-select v-model:value="queryForm.periodId" style="width: 200px" allowClear placeholder="全部" @change="queryList">
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

      <a-form-item label="快速筛选" class="smart-query-form-item">
        <a-radio-group v-model:value="quickOwner" button-style="solid">
          <a-radio-button value="all">全部</a-radio-button>
          <a-radio-button value="mine">我负责</a-radio-button>
        </a-radio-group>
      </a-form-item>

      <a-form-item label="关键字" class="smart-query-form-item">
        <a-input style="width: 180px" v-model:value="queryForm.keywords" placeholder="目标标题/描述" />
      </a-form-item>

      <a-form-item class="smart-query-form-item smart-margin-left10">
        <a-button type="primary" @click="queryList">
          <template #icon>
            <SearchOutlined />
          </template>
          查询
        </a-button>
      </a-form-item>
    </a-row>
  </a-form>

  <a-card size="small" :bordered="false" :hoverable="true">
    <a-empty v-if="treeData.length === 0" description="暂无对齐目标数据" />
    <a-tree
      v-else
      show-line
      block-node
      :tree-data="treeData"
      :defaultExpandAll="true"
      :field-names="{ title: 'title', key: 'key', children: 'children' }"
    >
      <template #title="node">
        <div class="okr-tree-node" @click.stop="toDetail(node)">
          <div class="okr-tree-header">
            <div class="okr-tree-title">{{ resolveNodeField(node, 'title') }}</div>
            <a-tag :color="statusColor(resolveNodeField(node, 'status'))" class="okr-tree-status-tag">
              {{ $smartEnumPlugin.getDescByValue('OKR_STATUS_ENUM', resolveNodeField(node, 'status')) }}
            </a-tag>
          </div>
          <div class="okr-tree-meta">
            <span class="okr-tree-owner">{{ resolveNodeField(node, 'ownerName') || '未指定' }}</span>
            <span>KR {{ resolveNodeField(node, 'keyResultCount') || 0 }}</span>
            <span>置信度 {{ $smartEnumPlugin.getDescByValue('OKR_CONFIDENCE_ENUM', resolveNodeField(node, 'confidence')) || '-' }}</span>
            <span>进度 {{ Number(resolveNodeField(node, 'progress') || 0) }}%</span>
          </div>
          <a-progress :percent="Number(resolveNodeField(node, 'progress') || 0)" size="small" />
        </div>
      </template>
    </a-tree>
  </a-card>
</template>

<script setup lang="ts">
  import { computed, onMounted, reactive, ref, watch } from 'vue';
  import { useRouter } from 'vue-router';
  import { okrApi } from '/@/api/business/oa/okr-api';
  import { PAGE_SIZE } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { useUserStore } from '/@/store/modules/system/user';
  import EmployeeSelect from '/@/components/system/employee-select/index.vue';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';

  const router = useRouter();
  const userStore = useUserStore();

  const periodList = ref([]);
  const queryFormState = {
    periodId: undefined,
    ownerEmployeeId: undefined,
    status: undefined,
    keywords: '',
  };
  const queryForm = reactive({ ...queryFormState });
  const quickOwner = ref('all');
  const treeData = ref([]);

  async function queryPeriodList() {
    try {
      const result = await okrApi.listAllPeriod();
      periodList.value = result.data || [];
      if (!queryForm.periodId && periodList.value.length > 0) {
        queryForm.periodId = periodList.value[0].periodId;
      }
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  function buildTree(list) {
    const map = new Map();
    const roots = [];
    list.forEach((item) => {
      map.set(item.objectiveId, {
        key: item.objectiveId,
        title: item.title,
        data: item,
        children: [],
      });
    });
    list.forEach((item) => {
      const node = map.get(item.objectiveId);
      if (item.parentObjectiveId && map.has(item.parentObjectiveId)) {
        map.get(item.parentObjectiveId).children.push(node);
      } else {
        roots.push(node);
      }
    });
    return roots;
  }

  async function queryList() {
    try {
      const result = await okrApi.queryObjective({
        periodId: queryForm.periodId,
        ownerEmployeeId: queryForm.ownerEmployeeId,
        status: queryForm.status,
        keywords: queryForm.keywords,
        pageNum: 1,
        pageSize: PAGE_SIZE * 5,
        searchCount: false,
      });
      treeData.value = buildTree(result.data.list || []);
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  function resolveNodeData(node) {
    if (!node) {
      return {};
    }
    return node.data || node.dataRef || node;
  }

  function resolveNodeField(node, field) {
    const data = resolveNodeData(node);
    return data ? data[field] : undefined;
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

  function toDetail(node) {
    const objectiveId = resolveNodeField(node, 'objectiveId');
    if (!objectiveId) {
      return;
    }
    router.push({
      path: '/oa/okr/okr-detail',
      query: { objectiveId },
    });
  }

  onMounted(async () => {
    await queryPeriodList();
    await queryList();
  });

  const currentEmployeeId = computed(() => {
    if (!userStore.employeeId) {
      return undefined;
    }
    const idNumber = Number(userStore.employeeId);
    return Number.isNaN(idNumber) ? userStore.employeeId : idNumber;
  });

  watch(quickOwner, (value) => {
    if (value === 'mine') {
      queryForm.ownerEmployeeId = currentEmployeeId.value;
      queryList();
    } else if (value === 'all') {
      queryForm.ownerEmployeeId = undefined;
      queryList();
    }
  });

  watch(
    () => queryForm.ownerEmployeeId,
    (value) => {
      if (!value) {
        quickOwner.value = 'all';
        return;
      }
      if (String(value) === String(currentEmployeeId.value)) {
        quickOwner.value = 'mine';
        return;
      }
      quickOwner.value = undefined;
    }
  );
</script>

<style scoped>
  .okr-tree-node {
    background: #fafafa;
    padding: 10px 12px;
    border-radius: 10px;
    transition: box-shadow 0.2s, transform 0.2s;
    cursor: pointer;
  }

  .okr-tree-node:hover {
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
    transform: translateY(-1px);
  }

  .okr-tree-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
    margin-bottom: 6px;
  }

  .okr-tree-title {
    font-weight: 600;
    color: #1f1f1f;
  }

  .okr-tree-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    font-size: 12px;
    color: #595959;
    margin-bottom: 6px;
  }

  .okr-tree-owner {
    color: #262626;
  }

  .okr-tree-status-tag {
    margin: 0;
  }
</style>
