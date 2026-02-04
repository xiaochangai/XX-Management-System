/*
 * OKR
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const okrApi = {
  // ---------------- 周期 ----------------
  queryPeriod(param) {
    return postRequest('/oa/okr/period/query', param);
  },
  listAllPeriod() {
    return getRequest('/oa/okr/period/listAll');
  },
  addPeriod(param) {
    return postRequest('/oa/okr/period/add', param);
  },
  updatePeriod(param) {
    return postRequest('/oa/okr/period/update', param);
  },
  deletePeriod(periodId) {
    return getRequest(`/oa/okr/period/delete/${periodId}`);
  },

  // ---------------- 目标 ----------------
  queryObjective(param) {
    return postRequest('/oa/okr/objective/query', param);
  },
  getObjectiveDetail(objectiveId) {
    return getRequest(`/oa/okr/objective/detail/${objectiveId}`);
  },
  getObjectiveAlignedList(objectiveId) {
    return getRequest(`/oa/okr/objective/align-list/${objectiveId}`);
  },
  getReviewSummary(periodId) {
    return getRequest(`/oa/okr/review/summary/${periodId}`);
  },
  getObjectiveSimpleList(periodId) {
    let url = '/oa/okr/objective/simple-list';
    if (periodId) {
      url += `?periodId=${periodId}`;
    }
    return getRequest(url);
  },
  addObjective(param) {
    return postRequest('/oa/okr/objective/add', param);
  },
  updateObjective(param) {
    return postRequest('/oa/okr/objective/update', param);
  },
  deleteObjective(objectiveId) {
    return getRequest(`/oa/okr/objective/delete/${objectiveId}`);
  },
  reviewObjective(param) {
    return postRequest('/oa/okr/objective/review', param);
  },

  // ---------------- 关键结果 ----------------
  addKeyResult(param) {
    return postRequest('/oa/okr/key-result/add', param);
  },
  updateKeyResult(param) {
    return postRequest('/oa/okr/key-result/update', param);
  },
  deleteKeyResult(keyResultId) {
    return getRequest(`/oa/okr/key-result/delete/${keyResultId}`);
  },
  reviewKeyResult(param) {
    return postRequest('/oa/okr/key-result/review', param);
  },

  // ---------------- 进展更新 ----------------
  queryCheckin(objectiveId) {
    return getRequest(`/oa/okr/checkin/query/${objectiveId}`);
  },
  addCheckin(param) {
    return postRequest('/oa/okr/checkin/add', param);
  },
};
