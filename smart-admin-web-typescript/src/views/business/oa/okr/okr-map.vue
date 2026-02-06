<!--
 * OKR 对齐视图
 -->
<template>
  <div class="okr-map">
    <div class="okr-map-nav">
      <a-radio-group v-model:value="activeNav" size="small" class="okr-map-tabs" @change="onNavChange">
        <a-radio-button value="okr">OKR</a-radio-button>
        <a-radio-button value="map">对齐视图</a-radio-button>
        <a-radio-button value="dashboard">数据看板</a-radio-button>
      </a-radio-group>
    </div>

    <div class="okr-map-toolbar">
      <div class="okr-map-title">对齐视图</div>
      <div class="okr-map-filters">
        <span class="okr-map-label">周期</span>
        <a-select v-model:value="periodId" class="okr-map-select" @change="queryList" allowClear placeholder="全部">
          <a-select-option v-for="item in periodList" :key="item.periodId" :value="item.periodId">
            {{ item.periodName }}
          </a-select-option>
        </a-select>
        <div class="okr-map-zoom">
          <span class="okr-map-label">缩放</span>
          <a-slider v-model:value="zoomPercent" :min="80" :max="120" :step="5" class="okr-map-slider" />
          <span class="okr-map-zoom-value">{{ zoomPercent }}%</span>
        </div>
        <a-button type="primary" ghost @click="queryList">刷新</a-button>
      </div>
    </div>

    <div class="okr-map-summary">
      <div class="okr-map-summary-card">
        <div class="okr-map-summary-label">目标数</div>
        <div class="okr-map-summary-value">{{ totalObjectives }}</div>
      </div>
      <div class="okr-map-summary-card">
        <div class="okr-map-summary-label">对齐率</div>
        <div class="okr-map-summary-value">{{ alignmentRate }}%</div>
      </div>
      <div class="okr-map-summary-card">
        <div class="okr-map-summary-label">平均进度</div>
        <div class="okr-map-summary-value">{{ averageProgress }}%</div>
      </div>
      <div class="okr-map-summary-card">
        <div class="okr-map-summary-label">根目标</div>
        <div class="okr-map-summary-value">{{ treeRoots.length }}</div>
      </div>
    </div>

    <a-card size="small" :bordered="false" class="okr-map-card">
      <a-spin :spinning="tableLoading">
        <div v-if="treeRoots.length === 0" class="okr-map-empty">
          <a-empty description="暂无对齐目标" />
        </div>
        <div v-else class="okr-map-list" :style="{ transform: `scale(${zoomScale})` }">
          <div v-for="root in treeRoots" :key="root.objectiveId" class="okr-map-block">
            <div class="okr-map-root-card" :class="{ 'is-risk': isRiskStatus(root.status) }" @click="toDetailById(root.objectiveId)">
              <div class="okr-map-root-header">
                <div class="okr-map-root-owner">
                  <a-avatar size="small">{{ shortName(root.ownerName) }}</a-avatar>
                  <div>
                    <div class="okr-map-root-name">{{ root.ownerName || '未指定' }}</div>
                    <a-tag :color="statusColor(root.status)">{{ statusDesc(root.status) }}</a-tag>
                  </div>
                </div>
                <div class="okr-map-root-progress">
                  <a-progress type="circle" :percent="formatProgress(root.progress)" :width="46" :strokeWidth="8" />
                  <div class="okr-map-root-metric">
                    <div class="okr-map-root-label">进度</div>
                    <div class="okr-map-root-value">{{ formatProgress(root.progress) }}%</div>
                  </div>
                </div>
              </div>
              <div class="okr-map-root-title">目标：{{ root.title }}</div>
              <div class="okr-map-root-meta">
                <span>KR {{ root.keyResultCount || 0 }}</span>
                <span class="okr-sub-divider">·</span>
                <span>评分 {{ formatScore(root.score) }}</span>
                <span class="okr-sub-divider">·</span>
                <span>周期 {{ root.periodName || '—' }}</span>
              </div>
              <div class="okr-map-root-actions">
                <a-button type="text" size="small" class="okr-map-collapse" @click.stop="toggleRoot(root.objectiveId)">
                  <DownOutlined :class="{ 'is-collapsed': isRootCollapsed(root.objectiveId) }" />
                  {{ isRootCollapsed(root.objectiveId) ? '展开' : '收起' }}
                </a-button>
              </div>
            </div>

            <div v-if="root.children.length && !isRootCollapsed(root.objectiveId)" class="okr-map-children">
              <div
                v-for="child in root.children"
                :key="child.objectiveId"
                class="okr-map-child-card"
                :class="{ 'is-risk': isRiskStatus(child.status) }"
                @click="toDetailById(child.objectiveId)"
              >
                <div class="okr-map-child-header">
                  <a-avatar size="small">{{ shortName(child.ownerName) }}</a-avatar>
                  <div class="okr-map-child-owner">{{ child.ownerName || '未指定' }}</div>
                  <a-tag :color="statusColor(child.status)">{{ statusDesc(child.status) }}</a-tag>
                </div>
                <div class="okr-map-child-title">{{ child.title }}</div>
                <div class="okr-map-child-meta">
                  <span>{{ formatProgress(child.progress) }}%</span>
                  <span class="okr-sub-divider">·</span>
                  <span>评分 {{ formatScore(child.score) }}</span>
                </div>
                <div class="okr-map-child-actions">
                  <a-button
                    v-if="child.children.length"
                    type="link"
                    size="small"
                    class="okr-map-child-toggle"
                    @click.stop="toggleChild(child.objectiveId)"
                  >
                    {{ isChildCollapsed(child.objectiveId) ? '展开对齐' : '收起对齐' }}
                  </a-button>
                </div>
                <div v-if="child.children.length && !isChildCollapsed(child.objectiveId)" class="okr-map-grandchildren">
                  <a-tag v-for="grand in child.children" :key="grand.objectiveId" class="okr-map-grand-tag">
                    {{ shortName(grand.ownerName) }} · {{ trimTitle(grand.title) }}
                  </a-tag>
                </div>
              </div>
            </div>
            <div v-else-if="!isRootCollapsed(root.objectiveId)" class="okr-map-children-empty">暂无对齐目标</div>
          </div>
        </div>
      </a-spin>
    </a-card>
  </div>
</template>

<script setup lang="ts">
  import { computed, getCurrentInstance, onMounted, ref, watch } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { DownOutlined } from '@ant-design/icons-vue';
  import { okrApi } from '/@/api/business/oa/okr-api';
  import { PAGE_SIZE } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';

  const router = useRouter();
  const route = useRoute();
  const smartEnumPlugin = getCurrentInstance().appContext.config.globalProperties.$smartEnumPlugin;

  const periodList = ref([]);
  const periodId = ref(undefined);
  const tableLoading = ref(false);
  const objectiveList = ref([]);
  const activeNav = ref('map');
  const zoomPercent = ref(100);
  const collapsedRoots = ref(new Set());
  const collapsedChildren = ref(new Set());

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
        ...item,
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

  const treeRoots = computed(() => buildTree(objectiveList.value));
  const totalObjectives = computed(() => objectiveList.value.length);
  const alignmentRate = computed(() => {
    if (!objectiveList.value.length) {
      return 0;
    }
    const aligned = objectiveList.value.filter((item) => item.parentObjectiveId).length;
    return Math.round((aligned / objectiveList.value.length) * 100);
  });
  const averageProgress = computed(() => {
    if (!objectiveList.value.length) {
      return 0;
    }
    const total = objectiveList.value.reduce((sum, item) => sum + Number(item.progress || 0), 0);
    return Math.round(total / objectiveList.value.length);
  });
  const zoomScale = computed(() => zoomPercent.value / 100);

  async function queryList() {
    tableLoading.value = true;
    try {
      const result = await okrApi.queryObjective({
        periodId: periodId.value,
        pageNum: 1,
        pageSize: PAGE_SIZE * 5,
        searchCount: false,
      });
      objectiveList.value = result.data.list || [];
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  function toDetailById(objectiveId) {
    if (!objectiveId) {
      return;
    }
    router.push({
      path: '/oa/okr/okr-detail',
      query: { objectiveId },
    });
  }

  function statusDesc(value) {
    return smartEnumPlugin.getDescByValue('OKR_STATUS_ENUM', value) || '未开始';
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
      default:
        return 'default';
    }
  }

  function isRiskStatus(value) {
    return value === 2 || value === 3;
  }

  function formatProgress(value) {
    return Math.round(Number(value || 0));
  }

  function formatScore(value) {
    if (value === null || value === undefined) {
      return '—';
    }
    return Number(value).toFixed(2);
  }

  function shortName(name) {
    if (!name) {
      return '未';
    }
    return String(name).slice(0, 1);
  }

  function trimTitle(value) {
    if (!value) {
      return '';
    }
    const text = String(value);
    return text.length > 16 ? `${text.slice(0, 16)}…` : text;
  }

  function toggleRoot(objectiveId) {
    if (collapsedRoots.value.has(objectiveId)) {
      collapsedRoots.value.delete(objectiveId);
    } else {
      collapsedRoots.value.add(objectiveId);
    }
  }

  function isRootCollapsed(objectiveId) {
    return collapsedRoots.value.has(objectiveId);
  }

  function toggleChild(objectiveId) {
    if (collapsedChildren.value.has(objectiveId)) {
      collapsedChildren.value.delete(objectiveId);
    } else {
      collapsedChildren.value.add(objectiveId);
    }
  }

  function isChildCollapsed(objectiveId) {
    return collapsedChildren.value.has(objectiveId);
  }

  function onNavChange() {
    switch (activeNav.value) {
      case 'okr':
        router.push({ path: '/oa/okr/okr-feishu' });
        break;
      case 'dashboard':
        router.push({ path: '/oa/okr/okr-review-summary' });
        break;
      default:
        router.push({ path: '/oa/okr/okr-map' });
        break;
    }
  }

  function syncActiveNav() {
    if (route.path.includes('okr-review-summary')) {
      activeNav.value = 'dashboard';
    } else if (route.path.includes('okr-map')) {
      activeNav.value = 'map';
    } else {
      activeNav.value = 'okr';
    }
  }

  onMounted(async () => {
    await queryPeriodList();
    await queryList();
    syncActiveNav();
  });

  watch(
    () => route.path,
    () => syncActiveNav()
  );
</script>

<style scoped>
  .okr-map {
    display: flex;
    flex-direction: column;
    gap: 12px;
    background: #f5f6f8;
    padding: 12px;
    border-radius: 14px;
  }

  .okr-map-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    flex-wrap: wrap;
  }

  .okr-map-nav {
    display: flex;
    justify-content: flex-start;
  }

  .okr-map-tabs {
    display: flex;
    gap: 6px;
  }

  .okr-map-title {
    font-weight: 600;
    color: #262626;
    font-size: 16px;
  }

  .okr-map-filters {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
  }

  .okr-map-label {
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-map-select {
    width: 200px;
  }

  .okr-map-zoom {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .okr-map-slider {
    width: 140px;
  }

  .okr-map-zoom-value {
    font-size: 12px;
    color: #595959;
    min-width: 44px;
  }

  .okr-map-summary {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: 10px;
  }

  .okr-map-summary-card {
    background: #ffffff;
    border-radius: 10px;
    padding: 10px 12px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  }

  .okr-map-summary-label {
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-map-summary-value {
    margin-top: 4px;
    font-size: 18px;
    color: #262626;
    font-weight: 600;
  }

  .okr-map-card {
    border-radius: 12px;
    background: #ffffff;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  }

  .okr-map-empty {
    padding: 20px 0;
  }

  .okr-map-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
    transform-origin: top left;
    transition: transform 0.2s;
  }

  .okr-map-block {
    padding: 12px;
    border-radius: 12px;
    background: #f8f9fb;
  }

  .okr-map-root-card {
    background: #ffffff;
    border-radius: 12px;
    padding: 14px 16px;
    border: 1px solid #f0f0f0;
    cursor: pointer;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
    position: relative;
  }

  .okr-map-root-card:hover {
    border-color: #91caff;
  }

  .okr-map-root-card.is-risk {
    border-color: #ffd666;
    box-shadow: 0 0 0 1px rgba(250, 173, 20, 0.2);
  }

  .okr-map-root-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    flex-wrap: wrap;
  }

  .okr-map-root-owner {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .okr-map-root-name {
    font-weight: 600;
    color: #262626;
  }

  .okr-map-root-progress {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .okr-map-root-metric {
    text-align: center;
  }

  .okr-map-root-label {
    font-size: 11px;
    color: #8c8c8c;
  }

  .okr-map-root-value {
    font-size: 12px;
    color: #262626;
    font-weight: 600;
  }

  .okr-map-root-title {
    margin-top: 10px;
    font-weight: 600;
    color: #262626;
  }

  .okr-map-root-meta {
    margin-top: 6px;
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-map-root-actions {
    position: absolute;
    right: 12px;
    bottom: 8px;
  }

  .okr-map-collapse {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    color: #595959;
  }

  .okr-map-collapse :deep(.anticon) {
    transition: transform 0.2s;
  }

  .okr-map-collapse :deep(.anticon.is-collapsed) {
    transform: rotate(-90deg);
  }

  .okr-map-children {
    position: relative;
    margin-top: 16px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 12px;
  }

  .okr-map-children::before {
    content: '';
    position: absolute;
    top: -8px;
    left: 12px;
    right: 12px;
    height: 2px;
    background: #e6e9f0;
  }

  .okr-map-child-card {
    background: #ffffff;
    border-radius: 10px;
    padding: 12px;
    border: 1px solid #f0f0f0;
    cursor: pointer;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
    position: relative;
  }

  .okr-map-child-card:hover {
    border-color: #91caff;
  }

  .okr-map-child-card.is-risk {
    border-color: #ffd666;
  }

  .okr-map-child-card::before {
    content: '';
    position: absolute;
    top: -12px;
    left: 20px;
    width: 2px;
    height: 12px;
    background: #e6e9f0;
  }

  .okr-map-child-header {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-wrap: wrap;
  }

  .okr-map-child-owner {
    font-size: 12px;
    color: #262626;
    font-weight: 600;
  }

  .okr-map-child-title {
    margin-top: 6px;
    font-size: 13px;
    color: #262626;
  }

  .okr-map-child-meta {
    margin-top: 4px;
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-map-child-actions {
    margin-top: 6px;
  }

  .okr-map-child-toggle {
    padding: 0;
  }

  .okr-map-grandchildren {
    margin-top: 8px;
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
  }

  .okr-map-grand-tag {
    background: #f5f7fb;
    border: none;
    color: #595959;
  }

  .okr-map-children-empty {
    margin-top: 12px;
    font-size: 12px;
    color: #bfbfbf;
  }

  .okr-sub-divider {
    margin: 0 6px;
    color: #bfbfbf;
  }
</style>
