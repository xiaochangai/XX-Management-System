<template>
  <a-modal
    :open="visible"
    :title="form.keyResultId ? '更新关键结果进展' : '更新目标进展'"
    :width="700"
    forceRender
    ok-text="确认"
    cancel-text="取消"
    @ok="onSubmit"
    @cancel="onClose"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
      <a-form-item label="目标">
        <span>{{ objectiveTitle || '-' }}</span>
      </a-form-item>

      <a-form-item label="关键结果" v-if="form.keyResultId">
        <span>{{ keyResultTitle || '-' }}</span>
      </a-form-item>

      <a-form-item label="当前值" v-if="form.keyResultId" name="currentValue">
        <a-input-number v-model:value="form.currentValue" :min="0" style="width: 100%" />
      </a-form-item>

      <a-form-item label="状态">
        <SmartEnumSelect width="100%" v-model:value="form.status" enum-name="OKR_STATUS_ENUM" placeholder="可选" />
      </a-form-item>

      <a-form-item label="置信度">
        <SmartEnumSelect width="100%" v-model:value="form.confidence" enum-name="OKR_CONFIDENCE_ENUM" placeholder="可选" />
      </a-form-item>

      <a-form-item label="更新说明">
        <a-textarea v-model:value="form.note" placeholder="记录本次进展" :auto-size="{ minRows: 3, maxRows: 6 }" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
  import { message } from 'ant-design-vue';
  import { reactive, ref } from 'vue';
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
    objectiveId: undefined,
    keyResultId: undefined,
    currentValue: undefined,
    status: undefined,
    confidence: undefined,
    note: undefined,
  };
  const form = reactive({ ...formDefault });

  const objectiveTitle = ref('');
  const keyResultTitle = ref('');

  const rules = {
    currentValue: [{ required: true, message: '请输入当前值' }],
  };

  function showModal(payload) {
    Object.assign(form, formDefault);
    objectiveTitle.value = payload?.objectiveTitle || '';
    keyResultTitle.value = payload?.keyResultTitle || '';
    Object.assign(form, {
      objectiveId: payload?.objectiveId,
      keyResultId: payload?.keyResultId,
      currentValue: payload?.currentValue,
      status: payload?.status,
      confidence: payload?.confidence,
      note: undefined,
    });
    visible.value = true;
  }

  function onClose() {
    visible.value = false;
  }

  function onSubmit() {
    const needValidate = !!form.keyResultId;
    const task = needValidate ? formRef.value.validate() : Promise.resolve();
    task
      .then(async () => {
        SmartLoading.show();
        try {
          await okrApi.addCheckin(form);
          message.success('更新成功');
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
