/*
 * OKR
 */

import { SmartEnum } from '/@/types/smart-enum';

export const OKR_PERIOD_STATUS_ENUM: SmartEnum<number> = {
  DRAFT: {
    value: 0,
    desc: '草稿',
  },
  ACTIVE: {
    value: 1,
    desc: '进行中',
  },
  CLOSED: {
    value: 2,
    desc: '已归档',
  },
};

export const OKR_STATUS_ENUM: SmartEnum<number> = {
  DRAFT: {
    value: 0,
    desc: '草稿',
  },
  ON_TRACK: {
    value: 1,
    desc: '正常',
  },
  AT_RISK: {
    value: 2,
    desc: '有风险',
  },
  OFF_TRACK: {
    value: 3,
    desc: '偏离',
  },
  DONE: {
    value: 4,
    desc: '已完成',
  },
  CANCELLED: {
    value: 5,
    desc: '已取消',
  },
};

export const OKR_CONFIDENCE_ENUM: SmartEnum<number> = {
  VERY_LOW: {
    value: 1,
    desc: '很低',
  },
  LOW: {
    value: 2,
    desc: '偏低',
  },
  MEDIUM: {
    value: 3,
    desc: '一般',
  },
  HIGH: {
    value: 4,
    desc: '较高',
  },
  VERY_HIGH: {
    value: 5,
    desc: '非常高',
  },
};

export default {
  OKR_PERIOD_STATUS_ENUM,
  OKR_STATUS_ENUM,
  OKR_CONFIDENCE_ENUM,
};
