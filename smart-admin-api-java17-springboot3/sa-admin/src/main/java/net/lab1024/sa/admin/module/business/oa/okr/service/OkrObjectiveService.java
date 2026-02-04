package net.lab1024.sa.admin.module.business.oa.okr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.business.oa.okr.dao.OkrKeyResultDao;
import net.lab1024.sa.admin.module.business.oa.okr.dao.OkrObjectiveDao;
import net.lab1024.sa.admin.module.business.oa.okr.dao.OkrPeriodDao;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrObjectiveEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrPeriodEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrObjectiveAddForm;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrObjectiveQueryForm;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrObjectiveReviewForm;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrObjectiveUpdateForm;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrKeyResultVO;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrObjectiveDetailVO;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrObjectiveSimpleVO;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrObjectiveVO;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrReviewScoreBucketVO;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrReviewSummaryVO;
import net.lab1024.sa.admin.module.system.employee.dao.EmployeeDao;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * OKR 目标
 */
@Service
public class OkrObjectiveService {

    @Resource
    private OkrObjectiveDao okrObjectiveDao;

    @Resource
    private OkrPeriodDao okrPeriodDao;

    @Resource
    private OkrKeyResultDao okrKeyResultDao;

    @Resource
    private EmployeeDao employeeDao;

    /**
     * 分页查询
     */
    public ResponseDTO<PageResult<OkrObjectiveVO>> query(OkrObjectiveQueryForm queryForm) {
        queryForm.setDeletedFlag(Boolean.FALSE);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<OkrObjectiveVO> list = okrObjectiveDao.query(page, queryForm);
        return ResponseDTO.ok(SmartPageUtil.convert2PageResult(page, list));
    }

    /**
     * 目标详情
     */
    public ResponseDTO<OkrObjectiveDetailVO> detail(Long objectiveId) {
        OkrObjectiveDetailVO detail = okrObjectiveDao.getDetail(objectiveId, Boolean.FALSE);
        if (detail == null) {
            return ResponseDTO.userErrorParam("目标不存在");
        }
        List<OkrKeyResultVO> keyResultList = okrKeyResultDao.queryByObjectiveId(objectiveId, Boolean.FALSE);
        detail.setKeyResultList(keyResultList);
        return ResponseDTO.ok(detail);
    }

    /**
     * 简要目标列表
     */
    public ResponseDTO<List<OkrObjectiveSimpleVO>> simpleList(Long periodId) {
        return ResponseDTO.ok(okrObjectiveDao.querySimpleList(periodId, Boolean.FALSE));
    }

    /**
     * 查询对齐目标
     */
    public ResponseDTO<List<OkrObjectiveVO>> alignedList(Long parentObjectiveId) {
        if (parentObjectiveId == null) {
            return ResponseDTO.ok(List.of());
        }
        return ResponseDTO.ok(okrObjectiveDao.queryAlignedList(parentObjectiveId, Boolean.FALSE));
    }

    /**
     * 周期复盘汇总
     */
    public ResponseDTO<OkrReviewSummaryVO> reviewSummary(Long periodId) {
        OkrPeriodEntity period = okrPeriodDao.selectById(periodId);
        if (period == null || Boolean.TRUE.equals(period.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("周期不存在");
        }

        List<OkrObjectiveVO> objectiveList = okrObjectiveDao.queryByPeriod(periodId, Boolean.FALSE);
        int total = objectiveList.size();
        int scoredCount = 0;
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalProgress = BigDecimal.ZERO;

        int draftCount = 0;
        int onTrackCount = 0;
        int atRiskCount = 0;
        int offTrackCount = 0;
        int doneCount = 0;
        int cancelledCount = 0;

        int bucket0_4 = 0;
        int bucket4_6 = 0;
        int bucket6_7 = 0;
        int bucket7_10 = 0;

        for (OkrObjectiveVO objective : objectiveList) {
            if (objective.getScore() != null) {
                scoredCount++;
                totalScore = totalScore.add(objective.getScore());
                if (objective.getScore().compareTo(new BigDecimal("0.4")) < 0) {
                    bucket0_4++;
                } else if (objective.getScore().compareTo(new BigDecimal("0.6")) < 0) {
                    bucket4_6++;
                } else if (objective.getScore().compareTo(new BigDecimal("0.7")) < 0) {
                    bucket6_7++;
                } else {
                    bucket7_10++;
                }
            }
            if (objective.getProgress() != null) {
                totalProgress = totalProgress.add(objective.getProgress());
            }
            Integer status = objective.getStatus();
            if (status == null) {
                continue;
            }
            switch (status) {
                case 0:
                    draftCount++;
                    break;
                case 1:
                    onTrackCount++;
                    break;
                case 2:
                    atRiskCount++;
                    break;
                case 3:
                    offTrackCount++;
                    break;
                case 4:
                    doneCount++;
                    break;
                case 5:
                    cancelledCount++;
                    break;
                default:
                    break;
            }
        }

        OkrReviewSummaryVO summaryVO = new OkrReviewSummaryVO();
        summaryVO.setPeriodId(period.getPeriodId());
        summaryVO.setPeriodName(period.getPeriodName());
        summaryVO.setStartDate(period.getStartDate());
        summaryVO.setEndDate(period.getEndDate());
        summaryVO.setObjectiveCount(total);
        summaryVO.setScoredCount(scoredCount);
        summaryVO.setAvgScore(scoredCount == 0 ? null : totalScore.divide(BigDecimal.valueOf(scoredCount), 2, RoundingMode.HALF_UP));
        summaryVO.setAvgProgress(total == 0 ? BigDecimal.ZERO : totalProgress.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP));
        summaryVO.setStatusDraftCount(draftCount);
        summaryVO.setStatusOnTrackCount(onTrackCount);
        summaryVO.setStatusAtRiskCount(atRiskCount);
        summaryVO.setStatusOffTrackCount(offTrackCount);
        summaryVO.setStatusDoneCount(doneCount);
        summaryVO.setStatusCancelledCount(cancelledCount);
        List<OkrReviewScoreBucketVO> bucketList = new ArrayList<>();
        bucketList.add(new OkrReviewScoreBucketVO("0-0.4", bucket0_4));
        bucketList.add(new OkrReviewScoreBucketVO("0.4-0.6", bucket4_6));
        bucketList.add(new OkrReviewScoreBucketVO("0.6-0.7", bucket6_7));
        bucketList.add(new OkrReviewScoreBucketVO("0.7-1.0", bucket7_10));
        summaryVO.setScoreBucketList(bucketList);
        summaryVO.setObjectiveList(objectiveList);
        return ResponseDTO.ok(summaryVO);
    }

    /**
     * 新建目标
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(OkrObjectiveAddForm addForm) {
        ResponseDTO<String> validate = validateObjective(addForm.getPeriodId(), addForm.getOwnerEmployeeId(), addForm.getParentObjectiveId(), null);
        if (!validate.getOk()) {
            return validate;
        }

        OkrObjectiveEntity entity = SmartBeanUtil.copy(addForm, OkrObjectiveEntity.class);
        entity.setDeletedFlag(Boolean.FALSE);
        entity.setProgress(BigDecimal.ZERO);
        okrObjectiveDao.insert(entity);
        return ResponseDTO.ok();
    }

    /**
     * 更新目标
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> update(OkrObjectiveUpdateForm updateForm) {
        OkrObjectiveEntity db = okrObjectiveDao.selectById(updateForm.getObjectiveId());
        if (db == null || Boolean.TRUE.equals(db.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("目标不存在");
        }
        ResponseDTO<String> validate = validateObjective(updateForm.getPeriodId(), updateForm.getOwnerEmployeeId(), updateForm.getParentObjectiveId(), updateForm.getObjectiveId());
        if (!validate.getOk()) {
            return validate;
        }
        OkrObjectiveEntity entity = SmartBeanUtil.copy(updateForm, OkrObjectiveEntity.class);
        if (entity.getProgress() == null) {
            entity.setProgress(db.getProgress());
        }
        if (entity.getDeletedFlag() == null) {
            entity.setDeletedFlag(db.getDeletedFlag());
        }
        if (entity.getScore() == null) {
            entity.setScore(db.getScore());
        }
        if (entity.getReviewNote() == null) {
            entity.setReviewNote(db.getReviewNote());
        }
        if (entity.getReviewUserId() == null) {
            entity.setReviewUserId(db.getReviewUserId());
        }
        if (entity.getReviewTime() == null) {
            entity.setReviewTime(db.getReviewTime());
        }
        okrObjectiveDao.updateById(entity);
        return ResponseDTO.ok();
    }

    /**
     * 删除目标
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> delete(Long objectiveId) {
        OkrObjectiveEntity db = okrObjectiveDao.selectById(objectiveId);
        if (db == null || Boolean.TRUE.equals(db.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("目标不存在");
        }
        OkrObjectiveEntity update = new OkrObjectiveEntity();
        update.setObjectiveId(objectiveId);
        update.setDeletedFlag(Boolean.TRUE);
        okrObjectiveDao.updateById(update);
        okrKeyResultDao.updateDeletedByObjectiveId(objectiveId, Boolean.TRUE);
        return ResponseDTO.ok();
    }

    /**
     * 目标评分/复盘
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> review(OkrObjectiveReviewForm reviewForm) {
        OkrObjectiveEntity db = okrObjectiveDao.selectById(reviewForm.getObjectiveId());
        if (db == null || Boolean.TRUE.equals(db.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("目标不存在");
        }
        if (reviewForm.getScore() == null
                || reviewForm.getScore().compareTo(BigDecimal.ZERO) < 0
                || reviewForm.getScore().compareTo(BigDecimal.ONE) > 0) {
            return ResponseDTO.userErrorParam("评分范围为0-1");
        }
        OkrObjectiveEntity update = new OkrObjectiveEntity();
        update.setObjectiveId(reviewForm.getObjectiveId());
        update.setScore(reviewForm.getScore());
        update.setReviewNote(reviewForm.getReviewNote());
        update.setReviewUserId(reviewForm.getReviewUserId());
        update.setReviewTime(java.time.LocalDateTime.now());
        okrObjectiveDao.updateById(update);
        return ResponseDTO.ok();
    }

    /**
     * 重新计算目标进度
     */
    @Transactional(rollbackFor = Exception.class)
    public void recalculateObjectiveProgress(Long objectiveId) {
        List<OkrKeyResultVO> keyResultList = okrKeyResultDao.queryByObjectiveId(objectiveId, Boolean.FALSE);
        BigDecimal progress = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(keyResultList)) {
            int weightSum = 0;
            BigDecimal total = BigDecimal.ZERO;
            for (OkrKeyResultVO kr : keyResultList) {
                int weight = kr.getWeight() == null || kr.getWeight() <= 0 ? 1 : kr.getWeight();
                BigDecimal krProgress = kr.getProgress() == null ? BigDecimal.ZERO : kr.getProgress();
                weightSum += weight;
                total = total.add(krProgress.multiply(BigDecimal.valueOf(weight)));
            }
            progress = total.divide(BigDecimal.valueOf(weightSum), 2, RoundingMode.HALF_UP);
        }
        OkrObjectiveEntity update = new OkrObjectiveEntity();
        update.setObjectiveId(objectiveId);
        update.setProgress(progress);
        okrObjectiveDao.updateById(update);
    }

    private ResponseDTO<String> validateObjective(Long periodId, Long ownerEmployeeId, Long parentObjectiveId, Long excludeObjectiveId) {
        OkrPeriodEntity period = okrPeriodDao.selectById(periodId);
        if (period == null || Boolean.TRUE.equals(period.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("周期不存在");
        }
        EmployeeEntity owner = employeeDao.selectById(ownerEmployeeId);
        if (owner == null || Boolean.TRUE.equals(owner.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("负责人不存在");
        }
        if (parentObjectiveId != null) {
            if (Objects.equals(parentObjectiveId, excludeObjectiveId)) {
                return ResponseDTO.userErrorParam("对齐目标不能是自己");
            }
            OkrObjectiveEntity parent = okrObjectiveDao.selectById(parentObjectiveId);
            if (parent == null || Boolean.TRUE.equals(parent.getDeletedFlag())) {
                return ResponseDTO.userErrorParam("对齐目标不存在");
            }
            if (!Objects.equals(parent.getPeriodId(), periodId)) {
                return ResponseDTO.userErrorParam("对齐目标必须属于同一周期");
            }
        }
        return ResponseDTO.ok();
    }
}
