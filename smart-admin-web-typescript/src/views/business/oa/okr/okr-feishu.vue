<!--
 * OKR 飞书风格首页
 -->
<template>
  <div class="okr-feishu">
    <aside class="okr-feishu-side">
      <a-card size="small" :bordered="false" class="okr-side-card">
        <div class="okr-side-section">
          <div class="okr-side-title">我的 OKR</div>
          <div
            class="okr-owner"
            :class="{ 'is-active': activeOwnerId === myOwner?.employeeId }"
            @click="selectOwner(myOwner?.employeeId)"
          >
            <a-avatar size="small">{{ myOwner?.shortName || '我' }}</a-avatar>
            <div class="okr-owner-info">
              <div class="okr-owner-name">{{ myOwner?.ownerName || '我' }}</div>
              <div class="okr-owner-meta">我负责</div>
            </div>
          </div>
        </div>

        <div class="okr-side-section">
          <div class="okr-side-title">直属下级</div>
          <div v-if="directReportList.length === 0" class="okr-side-empty">暂无直属下级</div>
          <div
            v-for="owner in directReportList"
            :key="owner.employeeId"
            class="okr-owner"
            :class="{ 'is-active': activeOwnerId === owner.employeeId }"
            @click="selectOwner(owner.employeeId)"
          >
            <a-avatar size="small">{{ owner.shortName }}</a-avatar>
            <div class="okr-owner-info">
              <div class="okr-owner-name">{{ owner.ownerName }}</div>
              <div class="okr-owner-meta">{{ owner.objectiveCount }} 个目标</div>
            </div>
          </div>
        </div>

        <div class="okr-side-section">
          <div class="okr-side-title">我的关注</div>
          <div class="okr-side-empty">暂无关注</div>
        </div>
      </a-card>
    </aside>

    <section class="okr-feishu-main">
      <a-card size="small" :bordered="false" class="okr-main-card">
        <div class="okr-nav-bar">
          <a-radio-group v-model:value="activeNav" size="small" class="okr-nav-tabs" @change="onNavChange">
            <a-radio-button value="okr">OKR</a-radio-button>
            <a-radio-button value="map">对齐视图</a-radio-button>
            <a-radio-button value="dashboard">数据看板</a-radio-button>
          </a-radio-group>
          <a-radio-group v-model:value="activeScope" size="small" class="okr-scope-tabs">
            <a-radio-button value="mine">我的 OKR</a-radio-button>
            <a-radio-button value="focus">我的关注</a-radio-button>
          </a-radio-group>
        </div>

        <div class="okr-main-header">
          <div class="okr-header-left">
            <a-input
              v-model:value="queryForm.keywords"
              allowClear
              placeholder="搜索目标 / 关键结果"
              style="width: 220px"
              @pressEnter="queryList"
            />
            <a-button @click="queryList" type="primary" ghost>刷新</a-button>
          </div>
          <div class="okr-header-right">
            <EmployeeSelect width="200px" v-model:value="queryForm.ownerEmployeeId" placeholder="搜索员工" />
            <a-select v-model:value="queryForm.periodId" style="width: 200px" allowClear placeholder="选择周期" @change="queryList">
              <a-select-option v-for="item in periodList" :key="item.periodId" :value="item.periodId">
                {{ item.periodName }}
              </a-select-option>
            </a-select>
            <a-button type="primary" @click="addObjective">新建目标</a-button>
          </div>
        </div>

        <a-spin :spinning="tableLoading">
          <div v-if="activeScope === 'focus'" class="okr-empty">
            <a-empty description="暂无关注" />
          </div>
          <div v-else-if="groupedObjectiveList.length === 0" class="okr-empty">
            <a-empty description="暂无目标" />
          </div>
          <div v-else class="okr-group-list">
            <div v-for="group in groupedObjectiveList" :key="group.ownerId" class="okr-owner-group">
              <div class="okr-owner-header">
                <a-avatar>{{ group.ownerShortName }}</a-avatar>
                <div>
                  <div class="okr-owner-title">{{ group.ownerName }}</div>
                  <div class="okr-owner-sub">{{ group.objectives.length }} 个目标</div>
                </div>
              </div>

              <div class="okr-objective-list">
                <a-card v-for="item in group.objectives" :key="item.objectiveId" size="small" class="okr-objective-card">
                  <div class="okr-objective-header">
                    <div class="okr-objective-title" @click="toDetail(item.objectiveId)">{{ item.title }}</div>
                    <a-tag :color="statusColor(item.status)">
                      {{ $smartEnumPlugin.getDescByValue('OKR_STATUS_ENUM', item.status) }}
                    </a-tag>
                  </div>

                  <div class="okr-objective-sub">
                    <span>周期：{{ item.periodName || '—' }}</span>
                    <span class="okr-sub-divider">·</span>
                    <span>对齐：{{ item.parentObjectiveTitle || '未对齐' }}</span>
                  </div>

                  <div class="okr-owner-row">
                    <a-avatar size="small">{{ (item.ownerName || '未').slice(0, 1) }}</a-avatar>
                    <span class="okr-owner-name">{{ item.ownerName || '未指定' }}</span>
                    <span class="okr-owner-tag">负责人</span>
                  </div>

                  <div class="okr-objective-metrics">
                    <div class="okr-metric-card">
                      <div class="okr-metric-value">{{ Math.round(Number(item.progress || 0)) }}%</div>
                      <div class="okr-metric-label">进度</div>
                    </div>
                    <div class="okr-metric-card">
                      <div class="okr-metric-value">100%</div>
                      <div class="okr-metric-label">权重</div>
                    </div>
                    <div class="okr-metric-card">
                      <div class="okr-metric-value">{{ formatScore(item.score) }}</div>
                      <div class="okr-metric-label">总分</div>
                    </div>
                  </div>

                  <div class="okr-objective-progress">
                    <a-progress :percent="Number(item.progress || 0)" size="small" />
                  </div>

                  <div class="okr-objective-stats">
                    <div class="okr-stat-item">
                      <div class="okr-stat-label">KR 数</div>
                      <div class="okr-stat-value">{{ item.keyResultCount || 0 }}</div>
                    </div>
                    <div class="okr-stat-item">
                      <div class="okr-stat-label">置信度</div>
                      <div class="okr-stat-value">{{ $smartEnumPlugin.getDescByValue('OKR_CONFIDENCE_ENUM', item.confidence) || '—' }}</div>
                    </div>
                    <div class="okr-stat-item">
                      <div class="okr-stat-label">更新时间</div>
                      <div class="okr-stat-value">{{ item.updateTime || '—' }}</div>
                    </div>
                  </div>

                  <div class="okr-objective-actions">
                    <a-button type="link" size="small" @click="toggleExpand(item.objectiveId)">
                      {{ expandedObjectiveIds.has(item.objectiveId) ? '收起' : '展开' }}
                    </a-button>
                    <a-tooltip title="请在详情里设置对齐关系">
                      <a-button type="link" size="small" @click="toDetail(item.objectiveId)">添加对齐</a-button>
                    </a-tooltip>
                    <a-button type="link" size="small" @click="openKeyResult(item.objectiveId)">新增 KR</a-button>
                    <a-button type="link" size="small" @click="openCheckin(item.objectiveId, item.title)">更新进展</a-button>
                    <a-button type="link" size="small" @click="toDetail(item.objectiveId)">详情</a-button>
                  </div>

                  <div v-if="expandedObjectiveIds.has(item.objectiveId)" class="okr-objective-detail">
                    <a-divider />
                    <div class="okr-detail-title">对齐关系</div>
                    <div class="okr-align-block">
                      <div class="okr-align-item">
                        <span class="okr-align-label">上级目标</span>
                        <span>{{ item.parentObjectiveTitle || '未对齐' }}</span>
                      </div>
                      <div class="okr-align-item">
                        <span class="okr-align-label">对齐到我的目标</span>
                        <div class="okr-align-tags">
                          <a-tag v-for="aligned in detailState(item.objectiveId).alignedList" :key="aligned.objectiveId">
                            {{ aligned.title }}（{{ aligned.ownerName }}）
                          </a-tag>
                          <span v-if="detailState(item.objectiveId).alignedList.length === 0">暂无</span>
                        </div>
                      </div>
                    </div>
                    <div class="okr-detail-title">关键结果</div>
                    <a-spin :spinning="detailState(item.objectiveId).loading">
                      <a-list
                        v-if="detailState(item.objectiveId).keyResultList.length"
                        :dataSource="detailState(item.objectiveId).keyResultList"
                      >
                        <template #renderItem="{ item: kr }">
                          <a-list-item class="okr-kr-item">
                            <div class="okr-kr-row">
                              <div class="okr-kr-left">
                                <div class="okr-kr-title">KR · {{ kr.title }}</div>
                                <div class="okr-kr-meta">
                                  <span>{{ kr.metricName || '指标' }}：</span>
                                  <span>{{ kr.currentValue ?? '-' }}/{{ kr.targetValue ?? '-' }}{{ kr.unit || '' }}</span>
                                  <span class="okr-sub-divider">·</span>
                                  <span>权重 {{ formatWeightPercent(kr, detailState(item.objectiveId).keyResultList) }}%</span>
                                  <span class="okr-sub-divider">·</span>
                                  <span>评分 {{ formatScore(kr.score) }}</span>
                                </div>
                              </div>
                              <div class="okr-kr-right">
                                <div class="okr-kr-progress">
                                  <a-progress :percent="Number(kr.progress || 0)" size="small" :showInfo="false" />
                                  <span class="okr-kr-progress-value">{{ Math.round(Number(kr.progress || 0)) }}%</span>
                                </div>
                                <div class="okr-kr-badges">
                                  <a-tag :color="statusColor(kr.status)">{{ $smartEnumPlugin.getDescByValue('OKR_STATUS_ENUM', kr.status) }}</a-tag>
                                  <a-tag color="geekblue">{{ $smartEnumPlugin.getDescByValue('OKR_CONFIDENCE_ENUM', kr.confidence) || '—' }}</a-tag>
                                </div>
                              </div>
                            </div>
                          </a-list-item>
                        </template>
                      </a-list>
                      <a-empty v-else description="暂无关键结果" />
                    </a-spin>

                    <div class="okr-detail-title">进展记录</div>
                    <div v-if="detailState(item.objectiveId).checkinList.length" class="okr-checkin-wrapper">
                      <OkrCheckinTimeline :checkinList="detailState(item.objectiveId).checkinList.slice(0, 3)" />
                    </div>
                    <a-empty v-else description="暂无进展记录" />
                  </div>
                </a-card>
              </div>
            </div>
          </div>
        </a-spin>
      </a-card>
    </section>
  </div>

  <OkrObjectiveFormDrawer ref="objectiveFormRef" @refresh="queryList" />
  <OkrKeyResultFormModal ref="keyResultFormRef" @refresh="queryList" />
  <OkrCheckinFormModal ref="checkinFormRef" @refresh="queryList" />
</template>

<script setup lang="ts">
  import { computed, onMounted, reactive, ref, watch } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { okrApi } from '/@/api/business/oa/okr-api';
  import { PAGE_SIZE } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { useUserStore } from '/@/store/modules/system/user';
  import EmployeeSelect from '/@/components/system/employee-select/index.vue';
  import OkrObjectiveFormDrawer from './components/okr-objective-form-drawer.vue';
  import OkrKeyResultFormModal from './components/okr-key-result-form-modal.vue';
  import OkrCheckinFormModal from './components/okr-checkin-form-modal.vue';
  import OkrCheckinTimeline from './components/okr-checkin-timeline.vue';

  const router = useRouter();
  const route = useRoute();
  const userStore = useUserStore();

  const periodList = ref([]);
  const tableLoading = ref(false);
  const objectiveList = ref([]);
  const allObjectiveList = ref([]);
  const expandedObjectiveIds = ref(new Set());

  const detailMap = reactive({});

  const queryForm = reactive({
    periodId: undefined,
    ownerEmployeeId: undefined,
    keywords: '',
    pageNum: 1,
    pageSize: PAGE_SIZE * 5,
    searchCount: false,
  });

  const objectiveFormRef = ref();
  const keyResultFormRef = ref();
  const checkinFormRef = ref();

  const activeNav = ref('okr');
  const activeScope = ref('mine');

  const myOwner = computed(() => {
    return {
      employeeId: userStore.employeeId ? Number(userStore.employeeId) : undefined,
      ownerName: userStore.actualName || userStore.loginName || '我',
      shortName: (userStore.actualName || userStore.loginName || '我').slice(0, 1),
      objectiveCount: allObjectiveList.value.filter((item) => item.ownerEmployeeId === Number(userStore.employeeId)).length,
    };
  });

  const directReportList = computed(() => {
    const map = new Map();
    allObjectiveList.value.forEach((item) => {
      if (!item.ownerEmployeeId || item.ownerEmployeeId === Number(userStore.employeeId)) {
        return;
      }
      if (!map.has(item.ownerEmployeeId)) {
        map.set(item.ownerEmployeeId, {
          employeeId: item.ownerEmployeeId,
          ownerName: item.ownerName,
          shortName: (item.ownerName || '').slice(0, 1),
          objectiveCount: 0,
        });
      }
      map.get(item.ownerEmployeeId).objectiveCount += 1;
    });
    return Array.from(map.values());
  });

  const activeOwnerId = ref(undefined);

  const groupedObjectiveList = computed(() => {
    const map = new Map();
    objectiveList.value.forEach((item) => {
      if (!map.has(item.ownerEmployeeId)) {
        map.set(item.ownerEmployeeId, {
          ownerId: item.ownerEmployeeId,
          ownerName: item.ownerName || '未指定',
          ownerShortName: (item.ownerName || '未').slice(0, 1),
          objectives: [],
        });
      }
      map.get(item.ownerEmployeeId).objectives.push(item);
    });
    return Array.from(map.values());
  });

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

  async function queryList() {
    tableLoading.value = true;
    try {
      await queryOwnerOverview();
      const result = await okrApi.queryObjective(queryForm);
      objectiveList.value = result.data.list || [];
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  async function queryOwnerOverview() {
    try {
      const overviewForm = {
        ...queryForm,
        ownerEmployeeId: undefined,
        pageNum: 1,
        pageSize: PAGE_SIZE * 10,
        searchCount: false,
      };
      const result = await okrApi.queryObjective(overviewForm);
      allObjectiveList.value = result.data.list || [];
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  function selectOwner(ownerId) {
    queryForm.ownerEmployeeId = ownerId;
  }

  watch(
    () => queryForm.ownerEmployeeId,
    (value) => {
      activeOwnerId.value = value;
      queryList();
    }
  );

  function detailState(objectiveId) {
    if (!detailMap[objectiveId]) {
      detailMap[objectiveId] = {
        loading: false,
        loaded: false,
        keyResultList: [],
        checkinList: [],
        alignedList: [],
      };
    }
    return detailMap[objectiveId];
  }

  async function loadDetail(objectiveId) {
    const state = detailState(objectiveId);
    if (state.loaded || state.loading) {
      return;
    }
    state.loading = true;
    try {
      const detailRes = await okrApi.getObjectiveDetail(objectiveId);
      state.keyResultList = detailRes.data?.keyResultList || [];
      const checkinRes = await okrApi.queryCheckin(objectiveId);
      state.checkinList = checkinRes.data || [];
      const alignedRes = await okrApi.getObjectiveAlignedList(objectiveId);
      state.alignedList = alignedRes.data || [];
      state.loaded = true;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      state.loading = false;
    }
  }

  function toggleExpand(objectiveId) {
    if (expandedObjectiveIds.value.has(objectiveId)) {
      expandedObjectiveIds.value.delete(objectiveId);
    } else {
      expandedObjectiveIds.value.add(objectiveId);
      loadDetail(objectiveId);
    }
  }

  function formatWeightPercent(item, list) {
    if (!list || list.length === 0) {
      return 0;
    }
    const weightSum = list.reduce((sum, kr) => sum + (kr.weight && kr.weight > 0 ? kr.weight : 1), 0);
    const weight = item.weight && item.weight > 0 ? item.weight : 1;
    if (!weightSum) {
      return Math.round(100 / list.length);
    }
    return Math.round((weight / weightSum) * 100);
  }

  function addObjective() {
    objectiveFormRef.value.showModal();
  }

  function openKeyResult(objectiveId) {
    keyResultFormRef.value.showModal({ objectiveId });
  }

  function openCheckin(objectiveId, objectiveTitle) {
    checkinFormRef.value.showModal({ objectiveId, objectiveTitle });
  }

  function toDetail(objectiveId) {
    router.push({
      path: '/oa/okr/okr-detail',
      query: { objectiveId },
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

  function onNavChange() {
    switch (activeNav.value) {
      case 'map':
        router.push({ path: '/oa/okr/okr-map' });
        break;
      case 'dashboard':
        router.push({ path: '/oa/okr/okr-review-summary' });
        break;
      default:
        router.push({ path: '/oa/okr/okr-feishu' });
        break;
    }
  }

  function syncActiveNav() {
    if (route.path.includes('okr-map')) {
      activeNav.value = 'map';
    } else if (route.path.includes('okr-review-summary')) {
      activeNav.value = 'dashboard';
    } else {
      activeNav.value = 'okr';
    }
  }

  onMounted(async () => {
    await queryPeriodList();
    syncActiveNav();
    if (userStore.employeeId) {
      queryForm.ownerEmployeeId = Number(userStore.employeeId);
    } else {
      await queryList();
    }
  });

  watch(
    () => route.path,
    () => syncActiveNav()
  );
</script>

<style scoped>
  .okr-feishu {
    display: grid;
    grid-template-columns: 260px 1fr;
    gap: 16px;
    min-height: 100%;
  }

  .okr-feishu-side {
    position: sticky;
    top: 16px;
    align-self: start;
  }

  .okr-side-card {
    border-radius: 12px;
    background: #ffffff;
  }

  .okr-side-section {
    margin-bottom: 16px;
  }

  .okr-side-title {
    font-weight: 600;
    color: #262626;
    margin-bottom: 8px;
  }

  .okr-owner {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 6px 8px;
    border-radius: 8px;
    cursor: pointer;
    transition: background 0.2s;
  }

  .okr-owner:hover {
    background: #f5f7fb;
  }

  .okr-owner.is-active {
    background: #e6f4ff;
  }

  .okr-owner-info {
    display: flex;
    flex-direction: column;
  }

  .okr-owner-name {
    font-size: 13px;
    color: #262626;
  }

  .okr-owner-meta {
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-side-empty {
    font-size: 12px;
    color: #bfbfbf;
    padding: 4px 0;
  }

  .okr-main-card {
    border-radius: 12px;
    background: #ffffff;
  }

  .okr-nav-bar {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    flex-wrap: wrap;
    padding: 4px 0 12px;
    border-bottom: 1px solid #f0f0f0;
    margin-bottom: 12px;
  }

  .okr-nav-tabs,
  .okr-scope-tabs {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
  }

  .okr-main-header {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    flex-wrap: wrap;
    margin-bottom: 16px;
  }

  .okr-header-left,
  .okr-header-right {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
  }

  .okr-group-list {
    display: flex;
    flex-direction: column;
    gap: 18px;
  }

  .okr-owner-group {
    background: #f8f9fb;
    border-radius: 12px;
    padding: 12px;
  }

  .okr-owner-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 12px;
  }

  .okr-owner-title {
    font-weight: 600;
    color: #262626;
  }

  .okr-owner-sub {
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-objective-list {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 12px;
  }

  .okr-objective-card {
    border-radius: 10px;
  }

  .okr-objective-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 10px;
    margin-bottom: 8px;
  }

  .okr-objective-title {
    font-weight: 600;
    color: #262626;
    cursor: pointer;
  }

  .okr-objective-sub {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #8c8c8c;
    margin-bottom: 8px;
    flex-wrap: wrap;
  }

  .okr-sub-divider {
    color: #bfbfbf;
  }

  .okr-owner-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 10px;
    font-size: 12px;
    color: #595959;
  }

  .okr-owner-tag {
    background: #f5f5f5;
    border-radius: 10px;
    padding: 2px 6px;
    font-size: 11px;
    color: #8c8c8c;
  }

  .okr-objective-metrics {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 8px;
    margin-bottom: 8px;
  }

  .okr-metric-card {
    background: #f5f7fb;
    border-radius: 8px;
    padding: 8px;
    text-align: center;
  }

  .okr-metric-value {
    font-weight: 600;
    color: #262626;
    font-size: 14px;
  }

  .okr-metric-label {
    font-size: 11px;
    color: #8c8c8c;
    margin-top: 2px;
  }

  .okr-objective-progress {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }

  .okr-objective-stats {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 8px;
    margin-bottom: 6px;
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-stat-item {
    background: #f8f9fb;
    border-radius: 6px;
    padding: 6px 8px;
  }

  .okr-stat-label {
    font-size: 11px;
    color: #8c8c8c;
  }

  .okr-stat-value {
    font-size: 12px;
    color: #262626;
    margin-top: 2px;
  }

  .okr-objective-actions {
    display: flex;
    gap: 6px;
    justify-content: flex-end;
  }

  .okr-objective-detail {
    margin-top: 10px;
  }

  .okr-detail-title {
    font-weight: 600;
    font-size: 13px;
    color: #262626;
    margin-bottom: 6px;
  }

  .okr-align-block {
    background: #f8f9fb;
    border-radius: 8px;
    padding: 8px 10px;
    margin-bottom: 10px;
  }

  .okr-align-item {
    display: flex;
    gap: 8px;
    font-size: 12px;
    color: #595959;
    margin-bottom: 6px;
  }

  .okr-align-label {
    color: #8c8c8c;
    min-width: 70px;
  }

  .okr-align-tags {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
  }

  .okr-kr-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  .okr-kr-row {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 12px;
    width: 100%;
  }

  .okr-kr-left {
    display: flex;
    flex-direction: column;
    gap: 4px;
    flex: 1 1 auto;
  }

  .okr-kr-title {
    font-size: 13px;
    color: #262626;
  }

  .okr-kr-meta {
    display: flex;
    gap: 10px;
    font-size: 12px;
    color: #8c8c8c;
    flex-wrap: wrap;
  }

  .okr-kr-right {
    display: flex;
    flex-direction: column;
    gap: 6px;
    align-items: flex-end;
    min-width: 150px;
  }

  .okr-kr-progress {
    display: flex;
    align-items: center;
    gap: 8px;
    min-width: 160px;
  }

  .okr-kr-progress-value {
    font-size: 12px;
    color: #595959;
    width: 36px;
    text-align: right;
  }

  .okr-kr-badges {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
    justify-content: flex-end;
  }

  .okr-checkin-wrapper {
    margin-top: 8px;
  }

  .okr-empty {
    padding: 24px 0;
  }

  @media (max-width: 1200px) {
    .okr-feishu {
      grid-template-columns: 1fr;
    }

    .okr-feishu-side {
      position: static;
    }
  }
</style>
