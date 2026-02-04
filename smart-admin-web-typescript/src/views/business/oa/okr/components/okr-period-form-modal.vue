<template>
  <a-modal
    :open="visible"
    :title="form.periodId ? '编辑周期' : '新建周期'"
    :width="600"
    forceRender
    ok-text="确认"
    cancel-text="取消"
    @ok="onSubmit"
    @cancel="onClose"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
      <a-form-item label="周期名称" name="periodName">
        <a-input v-model:value="form.periodName" placeholder="例如：2026 Q1" />
      </a-form-item>

      <a-form-item label="开始日期" name="startDate">
        <a-date-picker v-model:value="form.startDate" value-format="YYYY-MM-DD" style="width: 100%" />
      </a-form-item>

      <a-form-item label="结束日期" name="endDate">
        <a-date-picker v-model:value="form.endDate" value-format="YYYY-MM-DD" style="width: 100%" />
      </a-form-item>

      <a-form-item label="状态" name="status">
        <SmartEnumSelect width="100%" v-model:value="form.status" enum-name="OKR_PERIOD_STATUS_ENUM" placeholder="请选择" />
      </a-form-item>

      <a-form-item label="备注" name="remark">
        <a-textarea v-model:value="form.remark" placeholder="可选" :auto-size="{ minRows: 3, maxRows: 6 }" />
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
    periodId: undefined,
    periodName: undefined,
    startDate: undefined,
    endDate: undefined,
    status: 1,
    remark: undefined,
  };
  const form = reactive({ ...formDefault });

  const rules = {
    periodName: [{ required: true, message: '请输入周期名称' }],
    startDate: [{ required: true, message: '请选择开始日期' }],
    endDate: [{ required: true, message: '请选择结束日期' }],
    status: [{ required: true, message: '请选择状态' }],
  };

  function showModal(record) {
    Object.assign(form, formDefault);
    if (record) {
      Object.assign(form, record);
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
          if (form.periodId) {
            await okrApi.updatePeriod(form);
          } else {
            await okrApi.addPeriod(form);
          }
          message.success(`${form.periodId ? '编辑' : '新增'}成功`);
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
