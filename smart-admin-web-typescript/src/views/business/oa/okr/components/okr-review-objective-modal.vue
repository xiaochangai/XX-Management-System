<template>
  <a-modal
    :open="visible"
    title="目标复盘评分"
    :width="700"
    forceRender
    ok-text="确认"
    cancel-text="取消"
    @ok="onSubmit"
    @cancel="onClose"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
      <a-form-item label="目标">
        <span>{{ form.title || '-' }}</span>
      </a-form-item>

      <a-form-item label="评分" name="score">
        <a-input-number v-model:value="form.score" :min="0" :max="1" :step="0.1" :precision="2" style="width: 100%" />
        <div class="okr-score-tip">推荐区间：0.6 ~ 0.7</div>
      </a-form-item>

      <a-form-item label="复盘说明">
        <a-textarea v-model:value="form.reviewNote" placeholder="复盘结果、经验与改进" :auto-size="{ minRows: 4, maxRows: 8 }" />
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

  defineExpose({
    showModal,
  });

  const emit = defineEmits(['refresh']);

  const visible = ref(false);
  const formRef = ref();

  const formDefault = {
    objectiveId: undefined,
    title: undefined,
    score: undefined,
    reviewNote: undefined,
  };
  const form = reactive({ ...formDefault });

  const rules = {
    score: [{ required: true, message: '请输入评分' }],
  };

  function showModal(payload) {
    Object.assign(form, formDefault);
    if (payload) {
      Object.assign(form, {
        objectiveId: payload.objectiveId,
        title: payload.title,
        score: payload.score,
        reviewNote: payload.reviewNote,
      });
    }
    visible.value = true;
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
          await okrApi.reviewObjective({
            objectiveId: form.objectiveId,
            score: form.score,
            reviewNote: form.reviewNote,
          });
          message.success('复盘评分已保存');
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

<style scoped>
  .okr-score-tip {
    font-size: 12px;
    color: #8c8c8c;
    margin-top: 4px;
  }
</style>
