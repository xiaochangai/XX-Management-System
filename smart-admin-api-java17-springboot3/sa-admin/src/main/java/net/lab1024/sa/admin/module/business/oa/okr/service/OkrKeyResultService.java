package net.lab1024.sa.admin.module.business.oa.okr.service;

import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.business.oa.okr.dao.OkrKeyResultDao;
import net.lab1024.sa.admin.module.business.oa.okr.dao.OkrObjectiveDao;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrKeyResultEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrObjectiveEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrKeyResultAddForm;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrKeyResultUpdateForm;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * OKR 关键结果
 */
@Service
public class OkrKeyResultService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Resource
    private OkrKeyResultDao okrKeyResultDao;

    @Resource
    private OkrObjectiveDao okrObjectiveDao;

    @Resource
    private OkrObjectiveService okrObjectiveService;

    /**
     * 新建关键结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(OkrKeyResultAddForm addForm) {
        OkrObjectiveEntity objective = okrObjectiveDao.selectById(addForm.getObjectiveId());
        if (objective == null || Boolean.TRUE.equals(objective.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("目标不存在");
        }
        OkrKeyResultEntity entity = SmartBeanUtil.copy(addForm, OkrKeyResultEntity.class);
        entity.setDeletedFlag(Boolean.FALSE);
        entity.setCurrentValue(defaultIfNull(entity.getCurrentValue()));
        entity.setStartValue(defaultIfNull(entity.getStartValue()));
        entity.setProgress(calculateProgress(entity.getStartValue(), entity.getTargetValue(), entity.getCurrentValue()));
        okrKeyResultDao.insert(entity);
        okrObjectiveService.recalculateObjectiveProgress(addForm.getObjectiveId());
        return ResponseDTO.ok();
    }

    /**
     * 更新关键结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> update(OkrKeyResultUpdateForm updateForm) {
        OkrKeyResultEntity db = okrKeyResultDao.selectById(updateForm.getKeyResultId());
        if (db == null || Boolean.TRUE.equals(db.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("关键结果不存在");
        }
        OkrObjectiveEntity objective = okrObjectiveDao.selectById(updateForm.getObjectiveId());
        if (objective == null || Boolean.TRUE.equals(objective.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("目标不存在");
        }
        OkrKeyResultEntity entity = SmartBeanUtil.copy(updateForm, OkrKeyResultEntity.class);
        entity.setCurrentValue(defaultIfNull(entity.getCurrentValue()));
        entity.setStartValue(defaultIfNull(entity.getStartValue()));
        entity.setProgress(calculateProgress(entity.getStartValue(), entity.getTargetValue(), entity.getCurrentValue()));
        okrKeyResultDao.updateById(entity);
        okrObjectiveService.recalculateObjectiveProgress(updateForm.getObjectiveId());
        if (!Objects.equals(db.getObjectiveId(), updateForm.getObjectiveId())) {
            okrObjectiveService.recalculateObjectiveProgress(db.getObjectiveId());
        }
        return ResponseDTO.ok();
    }

    /**
     * 删除关键结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> delete(Long keyResultId) {
        OkrKeyResultEntity db = okrKeyResultDao.selectById(keyResultId);
        if (db == null || Boolean.TRUE.equals(db.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("关键结果不存在");
        }
        okrKeyResultDao.updateDeletedFlag(keyResultId, Boolean.TRUE);
        okrObjectiveService.recalculateObjectiveProgress(db.getObjectiveId());
        return ResponseDTO.ok();
    }

    private BigDecimal calculateProgress(BigDecimal startValue, BigDecimal targetValue, BigDecimal currentValue) {
        if (targetValue == null) {
            return BigDecimal.ZERO;
        }
        if (currentValue == null) {
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

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
