package net.lab1024.sa.admin.module.business.oa.okr.service;

import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.business.oa.okr.dao.OkrCheckinDao;
import net.lab1024.sa.admin.module.business.oa.okr.dao.OkrKeyResultDao;
import net.lab1024.sa.admin.module.business.oa.okr.dao.OkrObjectiveDao;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrCheckinEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrKeyResultEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrObjectiveEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrCheckinAddForm;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrCheckinVO;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * OKR 进展更新
 */
@Service
public class OkrCheckinService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Resource
    private OkrCheckinDao okrCheckinDao;

    @Resource
    private OkrObjectiveDao okrObjectiveDao;

    @Resource
    private OkrKeyResultDao okrKeyResultDao;

    @Resource
    private OkrObjectiveService okrObjectiveService;

    /**
     * 查询目标下的更新记录
     */
    public ResponseDTO<List<OkrCheckinVO>> queryByObjectiveId(Long objectiveId) {
        return ResponseDTO.ok(okrCheckinDao.queryByObjectiveId(objectiveId));
    }

    /**
     * 新增进展更新
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(OkrCheckinAddForm addForm) {
        OkrObjectiveEntity objective = okrObjectiveDao.selectById(addForm.getObjectiveId());
        if (objective == null || Boolean.TRUE.equals(objective.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("目标不存在");
        }

        BigDecimal progress = objective.getProgress() == null ? BigDecimal.ZERO : objective.getProgress();

        if (addForm.getKeyResultId() != null) {
            OkrKeyResultEntity keyResult = okrKeyResultDao.selectById(addForm.getKeyResultId());
            if (keyResult == null || Boolean.TRUE.equals(keyResult.getDeletedFlag())) {
                return ResponseDTO.userErrorParam("关键结果不存在");
            }
            if (!addForm.getObjectiveId().equals(keyResult.getObjectiveId())) {
                return ResponseDTO.userErrorParam("关键结果不属于当前目标");
            }

            BigDecimal currentValue = addForm.getCurrentValue();
            if (currentValue == null) {
                currentValue = keyResult.getCurrentValue();
            }

            BigDecimal krProgress = calculateProgress(keyResult.getStartValue(), keyResult.getTargetValue(), currentValue);

            OkrKeyResultEntity updateKr = new OkrKeyResultEntity();
            updateKr.setKeyResultId(keyResult.getKeyResultId());
            updateKr.setCurrentValue(currentValue);
            updateKr.setProgress(krProgress);
            if (addForm.getStatus() != null) {
                updateKr.setStatus(addForm.getStatus());
            } else {
                updateKr.setStatus(keyResult.getStatus());
            }
            if (addForm.getConfidence() != null) {
                updateKr.setConfidence(addForm.getConfidence());
            } else {
                updateKr.setConfidence(keyResult.getConfidence());
            }
            okrKeyResultDao.updateById(updateKr);

            okrObjectiveService.recalculateObjectiveProgress(addForm.getObjectiveId());
            OkrObjectiveEntity updatedObjective = okrObjectiveDao.selectById(addForm.getObjectiveId());
            if (updatedObjective != null && updatedObjective.getProgress() != null) {
                progress = updatedObjective.getProgress();
            }

            OkrCheckinEntity checkin = SmartBeanUtil.copy(addForm, OkrCheckinEntity.class);
            checkin.setProgress(krProgress);
            checkin.setCurrentValue(currentValue);
            okrCheckinDao.insert(checkin);
            return ResponseDTO.ok();
        }

        // 目标级别更新
        OkrObjectiveEntity updateObjective = new OkrObjectiveEntity();
        updateObjective.setObjectiveId(objective.getObjectiveId());
        if (addForm.getStatus() != null) {
            updateObjective.setStatus(addForm.getStatus());
        }
        if (addForm.getConfidence() != null) {
            updateObjective.setConfidence(addForm.getConfidence());
        }
        okrObjectiveDao.updateById(updateObjective);

        OkrCheckinEntity checkin = SmartBeanUtil.copy(addForm, OkrCheckinEntity.class);
        checkin.setProgress(progress);
        okrCheckinDao.insert(checkin);
        return ResponseDTO.ok();
    }

    private BigDecimal calculateProgress(BigDecimal startValue, BigDecimal targetValue, BigDecimal currentValue) {
        if (targetValue == null || currentValue == null) {
            return BigDecimal.ZERO;
        }
        if (startValue == null) {
            startValue = BigDecimal.ZERO;
        }
        BigDecimal denominator = targetValue.subtract(startValue);
        BigDecimal progress;
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            progress = currentValue.compareTo(targetValue) >= 0 ? HUNDRED : BigDecimal.ZERO;
        } else {
            progress = currentValue.subtract(startValue)
                    .divide(denominator, 4, RoundingMode.HALF_UP)
                    .multiply(HUNDRED);
        }
        if (progress.compareTo(BigDecimal.ZERO) < 0) {
            progress = BigDecimal.ZERO;
        }
        if (progress.compareTo(HUNDRED) > 0) {
            progress = HUNDRED;
        }
        return progress.setScale(2, RoundingMode.HALF_UP);
    }
}
