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
        <a-radio-group v-model:value="filterMode" size="small" class="okr-map-filter-tabs">
          <a-radio-button value="all">全部</a-radio-button>
          <a-radio-button value="mine">我负责</a-radio-button>
          <a-radio-button value="risk">有风险</a-radio-button>
          <a-radio-button value="unaligned">未对齐</a-radio-button>
        </a-radio-group>
        <span class="okr-map-label">周期</span>
        <a-select v-model:value="periodId" class="okr-map-select" @change="queryList" allowClear placeholder="全部">
          <a-select-option v-for="item in periodList" :key="item.periodId" :value="item.periodId">
            {{ item.periodName }}
          </a-select-option>
        </a-select>
        <a-button @click="expandAll" class="okr-map-action">展开全部</a-button>
        <a-button @click="collapseAll" class="okr-map-action">收起全部</a-button>
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
        <div class="okr-map-summary-track">
          <div class="okr-map-summary-bar" :style="{ width: `${alignmentRate}%` }"></div>
        </div>
      </div>
      <div class="okr-map-summary-card">
        <div class="okr-map-summary-label">平均进度</div>
        <div class="okr-map-summary-value">{{ averageProgress }}%</div>
        <div class="okr-map-summary-track">
          <div class="okr-map-summary-bar is-success" :style="{ width: `${averageProgress}%` }"></div>
        </div>
      </div>
      <div class="okr-map-summary-card">
        <div class="okr-map-summary-label">根目标</div>
        <div class="okr-map-summary-value">{{ treeRoots.length }}</div>
      </div>
    </div>

    <div v-if="filterMode !== 'all'" class="okr-map-alert">
      <div class="okr-map-alert-title">已应用筛选</div>
      <div class="okr-map-alert-content">
        当前为
        <span class="okr-map-alert-tag">{{ filterLabel }}</span>
        视图
        <span>·</span>
        <span>共 {{ totalObjectives }} 个目标</span>
      </div>
      <a-button type="link" size="small" @click="resetFilter">清除筛选</a-button>
    </div>

    <div class="okr-map-suggestions">
      <div v-if="!isSuggestionDismissed('risk')" class="okr-map-suggestion-card">
        <div class="okr-map-suggestion-header">
          <span class="okr-map-suggestion-title">建议关注</span>
          <a-button type="link" size="small" @click="dismissSuggestion('risk')">忽略</a-button>
        </div>
        <div class="okr-map-suggestion-body">
          有 <strong>{{ riskObjectiveCount }}</strong> 个目标处于有风险或延期状态
        </div>
        <div class="okr-map-suggestion-meta">建议：关注风险目标并及时对齐资源</div>
        <div class="okr-map-suggestion-toggle" @click="toggleSuggestion('risk')">
          {{ expandedSuggestion === 'risk' ? '收起' : '查看详情' }}
        </div>
        <div v-if="expandedSuggestion === 'risk'" class="okr-map-suggestion-detail">
          <div v-if="riskObjectiveList.length === 0" class="okr-map-suggestion-empty">暂无风险目标</div>
          <div v-else class="okr-map-suggestion-items">
            <div v-for="item in riskObjectiveList" :key="item.objectiveId" class="okr-map-suggestion-item">
              <span class="okr-map-suggestion-name">{{ item.title }}</span>
              <span class="okr-map-suggestion-owner">{{ item.ownerName || '未指定' }}</span>
              <span class="okr-map-suggestion-progress">{{ formatProgress(item.progress) }}%</span>
              <a-button type="link" size="small" @click="toDetailById(item.objectiveId)">查看</a-button>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!isSuggestionDismissed('unaligned')" class="okr-map-suggestion-card">
        <div class="okr-map-suggestion-header">
          <span class="okr-map-suggestion-title">建议关注</span>
          <a-button type="link" size="small" @click="dismissSuggestion('unaligned')">忽略</a-button>
        </div>
        <div class="okr-map-suggestion-body">
          有 <strong>{{ unalignedObjectiveCount }}</strong> 个目标尚未对齐
        </div>
        <div class="okr-map-suggestion-meta">建议：尽快建立对齐关系，避免目标偏移</div>
        <div class="okr-map-suggestion-toggle" @click="toggleSuggestion('unaligned')">
          {{ expandedSuggestion === 'unaligned' ? '收起' : '查看详情' }}
        </div>
        <div v-if="expandedSuggestion === 'unaligned'" class="okr-map-suggestion-detail">
          <div v-if="unalignedObjectiveList.length === 0" class="okr-map-suggestion-empty">暂无未对齐目标</div>
          <div v-else class="okr-map-suggestion-items">
            <div v-for="item in unalignedObjectiveList" :key="item.objectiveId" class="okr-map-suggestion-item">
              <span class="okr-map-suggestion-name">{{ item.title }}</span>
              <span class="okr-map-suggestion-owner">{{ item.ownerName || '未指定' }}</span>
              <span class="okr-map-suggestion-progress">{{ formatProgress(item.progress) }}%</span>
              <a-button type="link" size="small" @click="toDetailById(item.objectiveId)">查看</a-button>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!isSuggestionDismissed('progress')" class="okr-map-suggestion-card">
        <div class="okr-map-suggestion-header">
          <span class="okr-map-suggestion-title">建议关注</span>
          <a-button type="link" size="small" @click="dismissSuggestion('progress')">忽略</a-button>
        </div>
        <div class="okr-map-suggestion-body">
          平均进度 <strong>{{ averageProgress }}%</strong>，低于 60%
        </div>
        <div class="okr-map-suggestion-meta">建议：检查阻塞项，推动阶段性里程碑完成</div>
        <div class="okr-map-suggestion-toggle" @click="toggleSuggestion('progress')">
          {{ expandedSuggestion === 'progress' ? '收起' : '查看详情' }}
        </div>
        <div v-if="expandedSuggestion === 'progress'" class="okr-map-suggestion-detail">
          <div v-if="slowObjectiveList.length === 0" class="okr-map-suggestion-empty">暂无进度偏慢目标</div>
          <div v-else class="okr-map-suggestion-items">
            <div v-for="item in slowObjectiveList" :key="item.objectiveId" class="okr-map-suggestion-item">
              <span class="okr-map-suggestion-name">{{ item.title }}</span>
              <span class="okr-map-suggestion-owner">{{ item.ownerName || '未指定' }}</span>
              <span class="okr-map-suggestion-progress">{{ formatProgress(item.progress) }}%</span>
              <a-button type="link" size="small" @click="toDetailById(item.objectiveId)">查看</a-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <a-card size="small" :bordered="false" class="okr-map-card">
      <a-spin :spinning="tableLoading">
        <div v-if="treeRoots.length === 0" class="okr-map-empty">
          <a-empty description="暂无对齐目标" />
        </div>
        <div v-else class="okr-map-list" :style="{ transform: `scale(${zoomScale})` }">
          <div v-for="root in treeRoots" :key="root.objectiveId" class="okr-map-block">
            <a-popover
              placement="right"
              trigger="hover"
              overlayClassName="okr-map-popover"
              @visibleChange="(visible) => handlePopoverVisible(root.objectiveId, visible)"
            >
              <template #content>
                <div class="okr-map-popover-title">{{ root.title }}</div>
                <div class="okr-map-popover-row">
                  <span>负责人</span>
                  <span>{{ root.ownerName || '未指定' }}</span>
                </div>
                <div class="okr-map-popover-row">
                  <span>状态</span>
                  <span>{{ statusDesc(root.status) }}</span>
                </div>
                <div class="okr-map-popover-row">
                  <span>进度</span>
                  <span>{{ formatProgress(root.progress) }}%</span>
                </div>
                <div class="okr-map-popover-row">
                  <span>KR 数</span>
                  <span>{{ root.keyResultCount || 0 }}</span>
                </div>
                <div class="okr-map-popover-row">
                  <span>评分</span>
                  <span>{{ formatScore(root.score) }}</span>
                </div>
                <div class="okr-map-popover-row">
                  <span>周期</span>
                  <span>{{ root.periodName || '—' }}</span>
                </div>
                <div class="okr-map-popover-divider"></div>
                <div class="okr-map-popover-subtitle">关键结果</div>
                <div v-if="getKrSummary(root.objectiveId).length === 0" class="okr-map-popover-empty">暂无关键结果</div>
                <div v-else class="okr-map-popover-kr">
                  <div v-for="kr in getKrSummary(root.objectiveId)" :key="kr.keyResultId" class="okr-map-popover-kr-item">
                    <span class="okr-map-popover-kr-title">{{ kr.title }}</span>
                    <span class="okr-map-popover-kr-progress">{{ formatProgress(kr.progress) }}%</span>
                  </div>
                </div>
              </template>
              <div
                class="okr-map-root-card"
                :class="{
                  'is-risk': isRiskStatus(root.status),
                  'has-children': root.children.length && !isRootCollapsed(root.objectiveId),
                }"
                @click="toDetailById(root.objectiveId)"
              >
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
                <div class="okr-map-root-progress-row">
                  <div class="okr-map-status-dot" :class="statusDotClass(root.status)"></div>
                  <a-progress :percent="formatProgress(root.progress)" size="small" :showInfo="false" />
                  <span class="okr-map-progress-text">{{ formatProgress(root.progress) }}%</span>
                </div>
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
            </a-popover>

            <div
              v-if="root.children.length && !isRootCollapsed(root.objectiveId)"
              class="okr-map-children"
              :class="{ 'is-linked': root.children.length }"
            >
              <a-popover
                v-for="child in root.children"
                :key="child.objectiveId"
                placement="right"
                trigger="hover"
                overlayClassName="okr-map-popover"
                @visibleChange="(visible) => handlePopoverVisible(child.objectiveId, visible)"
              >
                <template #content>
                  <div class="okr-map-popover-title">{{ child.title }}</div>
                  <div class="okr-map-popover-row">
                    <span>负责人</span>
                    <span>{{ child.ownerName || '未指定' }}</span>
                  </div>
                  <div class="okr-map-popover-row">
                    <span>状态</span>
                    <span>{{ statusDesc(child.status) }}</span>
                  </div>
                  <div class="okr-map-popover-row">
                    <span>进度</span>
                    <span>{{ formatProgress(child.progress) }}%</span>
                  </div>
                  <div class="okr-map-popover-row">
                    <span>评分</span>
                    <span>{{ formatScore(child.score) }}</span>
                  </div>
                  <div class="okr-map-popover-row">
                    <span>周期</span>
                    <span>{{ child.periodName || '—' }}</span>
                  </div>
                  <div class="okr-map-popover-row">
                    <span>对齐到</span>
                    <span>{{ root.ownerName || '未指定' }}</span>
                  </div>
                  <div class="okr-map-popover-divider"></div>
                  <div class="okr-map-popover-subtitle">关键结果</div>
                  <div v-if="getKrSummary(child.objectiveId).length === 0" class="okr-map-popover-empty">暂无关键结果</div>
                  <div v-else class="okr-map-popover-kr">
                    <div v-for="kr in getKrSummary(child.objectiveId)" :key="kr.keyResultId" class="okr-map-popover-kr-item">
                      <span class="okr-map-popover-kr-title">{{ kr.title }}</span>
                      <span class="okr-map-popover-kr-progress">{{ formatProgress(kr.progress) }}%</span>
                    </div>
                  </div>
                </template>
                <div
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
                  <div class="okr-map-child-progress-row">
                    <div class="okr-map-status-dot" :class="statusDotClass(child.status)"></div>
                    <a-progress :percent="formatProgress(child.progress)" size="small" :showInfo="false" />
                    <span class="okr-map-progress-text">{{ formatProgress(child.progress) }}%</span>
                  </div>
                  <div class="okr-map-child-meta">
                    <span>{{ formatProgress(child.progress) }}%</span>
                    <span class="okr-sub-divider">·</span>
                    <span>评分 {{ formatScore(child.score) }}</span>
                    <span class="okr-sub-divider">·</span>
                    <span>对齐到 {{ root.ownerName || '—' }}</span>
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
              </a-popover>
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
  import localKey from '/@/constants/local-storage-key-const';
  import { localRead, localSave } from '/@/utils/local-util';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { useUserStore } from '/@/store/modules/system/user';

  const router = useRouter();
  const route = useRoute();
  const smartEnumPlugin = getCurrentInstance().appContext.config.globalProperties.$smartEnumPlugin;
  const userStore = useUserStore();

  const periodList = ref([]);
  const periodId = ref(undefined);
  const tableLoading = ref(false);
  const objectiveList = ref([]);
  const activeNav = ref('map');
  const filterMode = ref('all');
  const zoomPercent = ref(100);
  const collapsedRoots = ref(new Set());
  const collapsedChildren = ref(new Set());
  const expandedSuggestion = ref(null);
  const dismissedSuggestions = ref(new Set());
  const detailMap = ref(new Map());

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

  function flattenTree(nodes) {
    const result = [];
    nodes.forEach((node) => {
      result.push(node);
      if (node.children?.length) {
        result.push(...flattenTree(node.children));
      }
    });
    return result;
  }

  function applyFilter(roots) {
    if (filterMode.value === 'all') {
      return roots;
    }
    const isMine = (node) => Number(node.ownerEmployeeId) === Number(userStore.employeeId);
    const isRisk = (node) => isRiskStatus(node.status);
    const isUnaligned = (node) => !node.parentObjectiveId;
    const predicate = (node) => {
      switch (filterMode.value) {
        case 'mine':
          return isMine(node);
        case 'risk':
          return isRisk(node);
        case 'unaligned':
          return isUnaligned(node);
        default:
          return true;
      }
    };

    const includeDescendants = filterMode.value === 'unaligned';
    const filterNode = (node) => {
      const childMatches = (node.children || []).map(filterNode).filter(Boolean);
      const matched = predicate(node);
      if (matched) {
        return {
          ...node,
          children: includeDescendants ? node.children : childMatches,
        };
      }
      if (childMatches.length) {
        return {
          ...node,
          children: childMatches,
        };
      }
      return null;
    };

    return roots.map(filterNode).filter(Boolean);
  }

  const treeRoots = computed(() => applyFilter(buildTree(objectiveList.value)));
  const filteredFlatList = computed(() => flattenTree(treeRoots.value));
  const totalObjectives = computed(() => filteredFlatList.value.length);
  const filterLabel = computed(() => {
    switch (filterMode.value) {
      case 'mine':
        return '我负责';
      case 'risk':
        return '有风险';
      case 'unaligned':
        return '未对齐';
      default:
        return '全部';
    }
  });
  const alignmentRate = computed(() => {
    if (!filteredFlatList.value.length) {
      return 0;
    }
    const aligned = filteredFlatList.value.filter((item) => item.parentObjectiveId).length;
    return Math.round((aligned / filteredFlatList.value.length) * 100);
  });
  const unalignedObjectiveCount = computed(() => filteredFlatList.value.filter((item) => !item.parentObjectiveId).length);
  const riskObjectiveCount = computed(() => filteredFlatList.value.filter((item) => isRiskStatus(item.status)).length);
  const riskObjectiveList = computed(() => filteredFlatList.value.filter((item) => isRiskStatus(item.status)).slice(0, 5));
  const unalignedObjectiveList = computed(() => filteredFlatList.value.filter((item) => !item.parentObjectiveId).slice(0, 5));
  const slowObjectiveList = computed(() => filteredFlatList.value.filter((item) => Number(item.progress || 0) < 60).slice(0, 5));
  const averageProgress = computed(() => {
    if (!filteredFlatList.value.length) {
      return 0;
    }
    const total = filteredFlatList.value.reduce((sum, item) => sum + Number(item.progress || 0), 0);
    return Math.round(total / filteredFlatList.value.length);
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

  function statusDotClass(value) {
    switch (value) {
      case 2:
        return 'is-risk';
      case 3:
        return 'is-delay';
      case 4:
        return 'is-success';
      default:
        return 'is-normal';
    }
  }

  async function handlePopoverVisible(objectiveId, visible) {
    if (!visible) {
      return;
    }
    const state = detailMap.value.get(objectiveId);
    if (state?.loaded || state?.loading) {
      return;
    }
    detailMap.value.set(objectiveId, { loading: true, loaded: false, list: [] });
    try {
      const result = await okrApi.getObjectiveDetail(objectiveId);
      detailMap.value.set(objectiveId, {
        loading: false,
        loaded: true,
        list: result.data?.keyResultList || [],
      });
    } catch (e) {
      smartSentry.captureError(e);
      detailMap.value.set(objectiveId, { loading: false, loaded: true, list: [] });
    }
  }

  function getKrSummary(objectiveId) {
    const state = detailMap.value.get(objectiveId);
    if (!state?.list) {
      return [];
    }
    return state.list.slice(0, 3);
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
    const next = new Set(collapsedRoots.value);
    if (next.has(objectiveId)) {
      next.delete(objectiveId);
    } else {
      next.add(objectiveId);
    }
    collapsedRoots.value = next;
  }

  function isRootCollapsed(objectiveId) {
    return collapsedRoots.value.has(objectiveId);
  }

  function toggleChild(objectiveId) {
    const next = new Set(collapsedChildren.value);
    if (next.has(objectiveId)) {
      next.delete(objectiveId);
    } else {
      next.add(objectiveId);
    }
    collapsedChildren.value = next;
  }

  function isChildCollapsed(objectiveId) {
    return collapsedChildren.value.has(objectiveId);
  }

  function collapseAll() {
    const rootIds = treeRoots.value.map((item) => item.objectiveId);
    const childIds = treeRoots.value.flatMap((item) => item.children.map((child) => child.objectiveId));
    collapsedRoots.value = new Set(rootIds);
    collapsedChildren.value = new Set(childIds);
  }

  function expandAll() {
    collapsedRoots.value = new Set();
    collapsedChildren.value = new Set();
  }

  function resetFilter() {
    filterMode.value = 'all';
  }

  function toggleSuggestion(type) {
    expandedSuggestion.value = expandedSuggestion.value === type ? null : type;
  }

  function dismissSuggestion(type) {
    const next = new Set(dismissedSuggestions.value);
    next.add(type);
    dismissedSuggestions.value = next;
    persistDismissedSuggestions();
    if (expandedSuggestion.value === type) {
      expandedSuggestion.value = null;
    }
  }

  function isSuggestionDismissed(type) {
    return dismissedSuggestions.value.has(type);
  }

  function persistDismissedSuggestions() {
    localSave(localKey.OKR_MAP_SUGGESTION_DISMISSED, JSON.stringify(Array.from(dismissedSuggestions.value)));
  }

  function loadDismissedSuggestions() {
    const raw = localRead(localKey.OKR_MAP_SUGGESTION_DISMISSED);
    if (!raw) {
      return;
    }
    try {
      const list = JSON.parse(raw);
      if (Array.isArray(list)) {
        dismissedSuggestions.value = new Set(list);
      }
    } catch (e) {
      // ignore malformed storage
    }
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
    loadDismissedSuggestions();
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

  .okr-map-filter-tabs {
    display: flex;
    gap: 6px;
  }

  .okr-map-label {
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-map-select {
    width: 200px;
  }

  .okr-map-action {
    background: #ffffff;
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

  .okr-map-summary-track {
    height: 6px;
    background: #f0f0f0;
    border-radius: 999px;
    margin-top: 8px;
    overflow: hidden;
  }

  .okr-map-summary-bar {
    height: 100%;
    background: #1677ff;
    border-radius: 999px;
  }

  .okr-map-summary-bar.is-success {
    background: #52c41a;
  }

  .okr-map-card {
    border-radius: 12px;
    background: #ffffff;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  }

  .okr-map-alert {
    background: #fff7e6;
    border: 1px solid #ffd591;
    border-radius: 10px;
    padding: 10px 12px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    flex-wrap: wrap;
  }

  .okr-map-alert-title {
    font-weight: 600;
    color: #d46b08;
  }

  .okr-map-alert-content {
    font-size: 12px;
    color: #8c8c8c;
    display: flex;
    align-items: center;
    gap: 6px;
    flex-wrap: wrap;
  }

  .okr-map-alert-tag {
    background: #fff1b8;
    color: #ad6800;
    padding: 2px 6px;
    border-radius: 999px;
    font-weight: 600;
  }

  .okr-map-suggestions {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 12px;
  }

  .okr-map-suggestion-card {
    background: #ffffff;
    border: 1px solid #ffd591;
    border-radius: 12px;
    padding: 12px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  }

  .okr-map-suggestion-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
  }

  .okr-map-suggestion-title {
    font-weight: 600;
    color: #d46b08;
  }

  .okr-map-suggestion-body {
    font-size: 13px;
    color: #262626;
  }

  .okr-map-suggestion-meta {
    margin-top: 6px;
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-map-suggestion-toggle {
    margin-top: 8px;
    font-size: 12px;
    color: #1677ff;
    cursor: pointer;
  }

  .okr-map-suggestion-detail {
    margin-top: 8px;
    padding-top: 8px;
    border-top: 1px dashed #ffd591;
  }

  .okr-map-suggestion-empty {
    font-size: 12px;
    color: #bfbfbf;
  }

  .okr-map-suggestion-items {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .okr-map-suggestion-item {
    display: grid;
    grid-template-columns: 1fr auto auto auto;
    gap: 8px;
    align-items: center;
    font-size: 12px;
    color: #595959;
  }

  .okr-map-suggestion-name {
    color: #262626;
  }

  .okr-map-suggestion-owner {
    color: #8c8c8c;
  }

  .okr-map-suggestion-progress {
    color: #262626;
    font-weight: 600;
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

  .okr-map-root-card.has-children::after {
    content: '';
    position: absolute;
    left: 32px;
    bottom: -12px;
    width: 2px;
    height: 12px;
    background: #e6e9f0;
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

  .okr-map-root-progress-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 8px;
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
    margin-top: 18px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 12px;
    padding-left: 20px;
  }

  .okr-map-children.is-linked::before {
    content: '';
    position: absolute;
    top: -10px;
    left: 20px;
    right: 20px;
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

  .okr-map-child-card::after {
    content: '';
    position: absolute;
    left: -12px;
    top: 20px;
    width: 12px;
    height: 2px;
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

  .okr-map-child-progress-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 6px;
  }

  .okr-map-child-meta {
    margin-top: 4px;
    font-size: 12px;
    color: #8c8c8c;
  }

  .okr-map-status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #d9d9d9;
    flex-shrink: 0;
  }

  .okr-map-status-dot.is-normal {
    background: #1677ff;
  }

  .okr-map-status-dot.is-risk {
    background: #faad14;
  }

  .okr-map-status-dot.is-delay {
    background: #ff4d4f;
  }

  .okr-map-status-dot.is-success {
    background: #52c41a;
  }

  .okr-map-progress-text {
    font-size: 12px;
    color: #595959;
    min-width: 36px;
    text-align: right;
  }

  .okr-map-child-actions {
    margin-top: 6px;
  }

  .okr-map-child-toggle {
    padding: 0;
  }

  .okr-map-popover-title {
    font-weight: 600;
    color: #262626;
    margin-bottom: 6px;
  }

  .okr-map-popover-row {
    display: flex;
    justify-content: space-between;
    gap: 10px;
    font-size: 12px;
    color: #595959;
    margin-bottom: 4px;
  }

  .okr-map-popover-divider {
    height: 1px;
    background: #f0f0f0;
    margin: 8px 0;
  }

  .okr-map-popover-subtitle {
    font-size: 12px;
    color: #8c8c8c;
    margin-bottom: 6px;
  }

  .okr-map-popover-empty {
    font-size: 12px;
    color: #bfbfbf;
  }

  .okr-map-popover-kr {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .okr-map-popover-kr-item {
    display: flex;
    justify-content: space-between;
    gap: 8px;
    font-size: 12px;
    color: #595959;
  }

  .okr-map-popover-kr-title {
    color: #262626;
  }

  .okr-map-popover-kr-progress {
    color: #1677ff;
    font-weight: 600;
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
