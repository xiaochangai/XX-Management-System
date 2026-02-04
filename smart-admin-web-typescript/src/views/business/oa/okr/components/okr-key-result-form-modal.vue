<template>
  <a-modal
    :open="visible"
    :title="form.keyResultId ? '编辑关键结果' : '新增关键结果'"
    :width="700"
    forceRender
    ok-text="确认"
    cancel-text="取消"
    @ok="onSubmit"
    @cancel="onClose"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
      <a-form-item label="关键结果" name="title">
        <a-input v-model:value="form.title" placeholder="请输入关键结果" />
      </a-form-item>

      <a-form-item label="度量名称">
        <a-input v-model:value="form.metricName" placeholder="例如：销售额" />
      </a-form-item>

      <a-form-item label="起始值">
        <a-input-number v-model:value="form.startValue" :min="0" style="width: 100%" />
      </a-form-item>

      <a-form-item label="目标值" name="targetValue">
        <a-input-number v-model:value="form.targetValue" :min="0" style="width: 100%" />
      </a-form-item>

      <a-form-item label="当前值">
        <a-input-number v-model:value="form.currentValue" :min="0" style="width: 100%" />
      </a-form-item>

      <a-form-item label="单位">
        <a-input v-model:value="form.unit" placeholder="例如：万元" />
      </a-form-item>

      <a-form-item label="状态" name="status">
        <SmartEnumSelect width="100%" v-model:value="form.status" enum-name="OKR_STATUS_ENUM" placeholder="请选择" />
      </a-form-item>

      <a-form-item label="置信度">
        <SmartEnumSelect width="100%" v-model:value="form.confidence" enum-name="OKR_CONFIDENCE_ENUM" placeholder="可选" />
      </a-form-item>

      <a-form-item label="权重">
        <a-input-number v-model:value="form.weight" :min="0" :max="100" style="width: 100%" />
      </a-form-item>

      <a-form-item label="排序">
        <a-input-number v-model:value="form.sort" :min="0" style="width: 100%" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
  import { message } from 'ant-design-vue';
  import { nextTick, reactive, ref } from 'vue';
  import { okrApi } from '/@/api/business/oa/okr-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';

  defineExpose({
    showModal,
  });

  const emit = defineEmits(['refresh']);

  const visible = ref(false);
  const formRef = ref();

  const formDefault = {
    keyResultId: undefined,
    objectiveId: undefined,
    title: undefined,
    metricName: undefined,
    startValue: 0,
    targetValue: undefined,
    currentValue: 0,
    unit: undefined,
    status: 1,
    confidence: undefined,
    weight: undefined,
    sort: undefined,
  };
  const form = reactive({ ...formDefault });

  const rules = {
    title: [{ required: true, message: '请输入关键结果' }],
    targetValue: [{ required: true, message: '请输入目标值' }],
    status: [{ required: true, message: '请选择状态' }],
  };

  function showModal(record) {
    Object.assign(form, formDefault);
    if (record) {
      Object.assign(form, {
        keyResultId: record.keyResultId,
        objectiveId: record.objectiveId,
        title: record.title,
        metricName: record.metricName,
        startValue: record.startValue ?? 0,
        targetValue: record.targetValue,
        currentValue: record.currentValue ?? 0,
        unit: record.unit,
        status: record.status ?? 1,
        confidence: record.confidence,
        weight: record.weight,
        sort: record.sort,
      });
    }
    visible.value = true;
    nextTick(() => {
      const domArr = document.getElementsByClassName('ant-modal');
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
          if (form.keyResultId) {
            await okrApi.updateKeyResult(form);
          } else {
            await okrApi.addKeyResult(form);
          }
          message.success(`${form.keyResultId ? '编辑' : '新增'}成功`);
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
