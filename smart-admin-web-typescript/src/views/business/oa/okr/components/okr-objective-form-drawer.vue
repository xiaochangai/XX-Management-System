<template>
  <a-drawer
    :title="form.objectiveId ? '编辑目标' : '新建目标'"
    :open="visible"
    :width="720"
    :footerStyle="{ textAlign: 'right' }"
    @close="onClose"
    :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 4 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="目标标题" name="title">
        <a-input v-model:value="form.title" placeholder="请输入目标标题" />
      </a-form-item>

      <a-form-item label="周期" name="periodId">
        <a-select v-model:value="form.periodId" placeholder="请选择周期" @change="onPeriodChange">
          <a-select-option v-for="item in periodList" :key="item.periodId" :value="item.periodId">
            {{ item.periodName }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="负责人" name="ownerEmployeeId">
        <EmployeeSelect v-model:value="form.ownerEmployeeId" placeholder="请选择负责人" />
      </a-form-item>

      <a-form-item label="对齐目标">
        <a-select v-model:value="form.parentObjectiveId" placeholder="可选" allowClear>
          <a-select-option v-for="item in parentObjectiveList" :key="item.objectiveId" :value="item.objectiveId">
            {{ item.title }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="状态" name="status">
        <SmartEnumSelect width="100%" v-model:value="form.status" enum-name="OKR_STATUS_ENUM" placeholder="请选择" />
      </a-form-item>

      <a-form-item label="置信度">
        <SmartEnumSelect width="100%" v-model:value="form.confidence" enum-name="OKR_CONFIDENCE_ENUM" placeholder="可选" />
      </a-form-item>

      <a-form-item label="描述">
        <a-textarea v-model:value="form.description" placeholder="请输入描述" :auto-size="{ minRows: 3, maxRows: 6 }" />
      </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>

<script setup lang="ts">
  import { nextTick, reactive, ref } from 'vue';
  import { message } from 'ant-design-vue';
  import { okrApi } from '/@/api/business/oa/okr-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';
  import EmployeeSelect from '/@/components/system/employee-select/index.vue';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';

  defineExpose({
    showModal,
  });

  const emit = defineEmits(['refresh']);

  const visible = ref(false);
  const formRef = ref();

  const formDefault = {
    objectiveId: undefined,
    periodId: undefined,
    title: undefined,
    description: undefined,
    ownerEmployeeId: undefined,
    parentObjectiveId: undefined,
    status: 1,
    confidence: undefined,
  };
  const form = reactive({ ...formDefault });

  const rules = {
    title: [{ required: true, message: '请输入目标标题' }],
    periodId: [{ required: true, message: '请选择周期' }],
    ownerEmployeeId: [{ required: true, message: '请选择负责人' }],
    status: [{ required: true, message: '请选择状态' }],
  };

  const periodList = ref([]);
  const parentObjectiveList = ref([]);

  async function queryPeriodList() {
    const result = await okrApi.listAllPeriod();
    periodList.value = result.data || [];
    if (!form.periodId && periodList.value.length > 0) {
      form.periodId = periodList.value[0].periodId;
    }
  }

  async function queryParentObjectiveList(periodId) {
    if (!periodId) {
      parentObjectiveList.value = [];
      return;
    }
    const result = await okrApi.getObjectiveSimpleList(periodId);
    const list = result.data || [];
    parentObjectiveList.value = list.filter((item) => item.objectiveId !== form.objectiveId);
  }

  function onPeriodChange(value) {
    form.parentObjectiveId = undefined;
    queryParentObjectiveList(value);
  }

  async function showModal(objectiveId) {
    Object.assign(form, formDefault);
    parentObjectiveList.value = [];
    try {
      await queryPeriodList();
      if (objectiveId) {
        const result = await okrApi.getObjectiveDetail(objectiveId);
        const data = result.data || {};
        Object.assign(form, {
          objectiveId: data.objectiveId,
          periodId: data.periodId,
          title: data.title,
          description: data.description,
          ownerEmployeeId: data.ownerEmployeeId,
          parentObjectiveId: data.parentObjectiveId,
          status: data.status,
          confidence: data.confidence,
        });
      }
      await queryParentObjectiveList(form.periodId);
      visible.value = true;
      nextTick(() => {
        const domArr = document.getElementsByClassName('ant-drawer');
        if (domArr && domArr.length > 0) {
          Array.from(domArr).forEach((item) => {
            if (item.childNodes && item.childNodes.length > 0) {
              Array.from(item.childNodes).forEach((child) => {
                if (child.setAttribute) {
                  child.setAttribute('aria-hidden', 'false');
                }
              });
            }
          });
        }
      });
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  function onClose() {
    visible.value = false;
  }

  function onSubmit() {
    formRef.value
      .validate()
      .then(async () => {
        SmartLoading.show();
        try {
          if (form.objectiveId) {
            await okrApi.updateObjective(form);
          } else {
            await okrApi.addObjective(form);
          }
          message.success(`${form.objectiveId ? '编辑' : '新增'}成功`);
          emit('refresh');
          onClose();
        } catch (e) {
          smartSentry.captureError(e);
        } finally {
          SmartLoading.hide();
        }
      })
      .catch(() => {});
  }
</script>
