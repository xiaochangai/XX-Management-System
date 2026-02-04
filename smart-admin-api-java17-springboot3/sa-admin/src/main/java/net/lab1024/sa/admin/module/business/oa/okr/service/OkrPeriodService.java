package net.lab1024.sa.admin.module.business.oa.okr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.business.oa.okr.dao.OkrObjectiveDao;
import net.lab1024.sa.admin.module.business.oa.okr.dao.OkrPeriodDao;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrObjectiveEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrPeriodEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrPeriodAddForm;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrPeriodQueryForm;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrPeriodUpdateForm;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrPeriodVO;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * OKR 周期
 */
@Service
public class OkrPeriodService {

    @Resource
    private OkrPeriodDao okrPeriodDao;

    @Resource
    private OkrObjectiveDao okrObjectiveDao;

    /**
     * 分页查询
     */
    public ResponseDTO<PageResult<OkrPeriodVO>> query(OkrPeriodQueryForm queryForm) {
        queryForm.setDeletedFlag(Boolean.FALSE);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<OkrPeriodVO> list = okrPeriodDao.queryPage(page, queryForm);
        return ResponseDTO.ok(SmartPageUtil.convert2PageResult(page, list));
    }

    /**
     * 查询全部周期
     */
    public ResponseDTO<List<OkrPeriodVO>> listAll() {
        return ResponseDTO.ok(okrPeriodDao.queryAll(Boolean.FALSE));
    }

    /**
     * 新建周期
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(OkrPeriodAddForm addForm) {
        ResponseDTO<String> validate = validatePeriod(addForm.getPeriodName(), addForm.getStartDate(), addForm.getEndDate(), null);
        if (!validate.getOk()) {
            return validate;
        }
        OkrPeriodEntity entity = SmartBeanUtil.copy(addForm, OkrPeriodEntity.class);
        entity.setDeletedFlag(Boolean.FALSE);
        okrPeriodDao.insert(entity);
        return ResponseDTO.ok();
    }

    /**
     * 更新周期
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> update(OkrPeriodUpdateForm updateForm) {
        OkrPeriodEntity db = okrPeriodDao.selectById(updateForm.getPeriodId());
        if (db == null || Boolean.TRUE.equals(db.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("周期不存在");
        }
        ResponseDTO<String> validate = validatePeriod(updateForm.getPeriodName(), updateForm.getStartDate(), updateForm.getEndDate(), updateForm.getPeriodId());
        if (!validate.getOk()) {
            return validate;
        }
        OkrPeriodEntity entity = SmartBeanUtil.copy(updateForm, OkrPeriodEntity.class);
        okrPeriodDao.updateById(entity);
        return ResponseDTO.ok();
    }

    /**
     * 删除周期
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> delete(Long periodId) {
        OkrPeriodEntity db = okrPeriodDao.selectById(periodId);
        if (db == null || Boolean.TRUE.equals(db.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("周期不存在");
        }

        Long count = okrObjectiveDao.selectCount(new LambdaQueryWrapper<OkrObjectiveEntity>()
                .eq(OkrObjectiveEntity::getPeriodId, periodId)
                .eq(OkrObjectiveEntity::getDeletedFlag, Boolean.FALSE));
        if (count != null && count > 0) {
            return ResponseDTO.userErrorParam("该周期下已有目标，无法删除");
        }

        OkrPeriodEntity update = new OkrPeriodEntity();
        update.setPeriodId(periodId);
        update.setDeletedFlag(Boolean.TRUE);
        okrPeriodDao.updateById(update);
        return ResponseDTO.ok();
    }

    private ResponseDTO<String> validatePeriod(String periodName, LocalDate startDate, LocalDate endDate, Long excludeId) {
        if (StringUtils.isBlank(periodName)) {
            return ResponseDTO.userErrorParam("周期名称不能为空");
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return ResponseDTO.userErrorParam("开始日期不能晚于结束日期");
        }

        LambdaQueryWrapper<OkrPeriodEntity> wrapper = new LambdaQueryWrapper<OkrPeriodEntity>()
                .eq(OkrPeriodEntity::getDeletedFlag, Boolean.FALSE)
                .eq(OkrPeriodEntity::getPeriodName, periodName);
        if (Objects.nonNull(excludeId)) {
            wrapper.ne(OkrPeriodEntity::getPeriodId, excludeId);
        }
        OkrPeriodEntity exist = okrPeriodDao.selectOne(wrapper);
        if (exist != null) {
            return ResponseDTO.userErrorParam("周期名称已存在");
        }
        return ResponseDTO.ok();
    }
}
