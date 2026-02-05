<!--
 * OKR 对齐视图
 -->
<template>
  <a-form class="smart-query-form" v-privilege="'oa:okr:objective:query'">
    <a-row class="smart-query-form-row">
      <a-form-item label="周期" class="smart-query-form-item">
        <a-select v-model:value="periodId" style="width: 200px" @change="queryList" allowClear placeholder="全部">
          <a-select-option v-for="item in periodList" :key="item.periodId" :value="item.periodId">
            {{ item.periodName }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item class="smart-query-form-item smart-margin-left10">
        <a-button type="primary" @click="queryList">
          <template #icon>
            <SearchOutlined />
          </template>
          刷新
        </a-button>
      </a-form-item>
    </a-row>
  </a-form>

  <a-card size="small" :bordered="false" :hoverable="true">
    <a-tree
      show-line
      :tree-data="treeData"
      :defaultExpandAll="true"
      :field-names="{ title: 'title', key: 'key', children: 'children' }"
    >
      <template #title="node">
        <div class="okr-tree-node" @click="toDetail(node)">
          <div class="okr-tree-title">{{ resolveNodeField(node, 'title') }}</div>
          <div class="okr-tree-meta">
            <span class="okr-tree-owner">{{ resolveNodeField(node, 'ownerName') }}</span>
            <span class="okr-tree-status">{{ $smartEnumPlugin.getDescByValue('OKR_STATUS_ENUM', resolveNodeField(node, 'status')) }}</span>
            <span class="okr-tree-progress">{{ Number(resolveNodeField(node, 'progress') || 0) }}%</span>
          </div>
        </div>
      </template>
    </a-tree>
  </a-card>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { okrApi } from '/@/api/business/oa/okr-api';
  import { PAGE_SIZE } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';

  const router = useRouter();

  const periodList = ref([]);
  const periodId = ref(undefined);
  const treeData = ref([]);

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
        periodId: periodId.value,
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
</script>

<style scoped>
  .okr-tree-node {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 4px 6px;
    border-radius: 6px;
    transition: background 0.2s;
    cursor: pointer;
  }

  .okr-tree-node:hover {
    background: #f5f5f5;
  }

  .okr-tree-title {
    font-weight: 500;
    color: #262626;
    margin-right: 12px;
  }

  .okr-tree-meta {
    display: flex;
    gap: 12px;
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-tree-owner {
    color: #595959;
  }

  .okr-tree-status {
    color: #1677ff;
  }

  .okr-tree-progress {
    color: #389e0d;
  }
</style>
