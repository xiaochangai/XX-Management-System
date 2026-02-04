package net.lab1024.sa.admin.module.business.oa.okr.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrObjectiveEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrObjectiveQueryForm;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrObjectiveDetailVO;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrObjectiveSimpleVO;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrObjectiveVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OKR 目标
 */
@Mapper
public interface OkrObjectiveDao extends BaseMapper<OkrObjectiveEntity> {

    /**
     * 目标分页查询
     */
    List<OkrObjectiveVO> query(Page page, @Param("queryForm") OkrObjectiveQueryForm queryForm);

    /**
     * 目标详情
     */
    OkrObjectiveDetailVO getDetail(@Param("objectiveId") Long objectiveId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 目标简要列表
     */
    List<OkrObjectiveSimpleVO> querySimpleList(@Param("periodId") Long periodId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 查询对齐目标列表
     */
    List<OkrObjectiveVO> queryAlignedList(@Param("parentObjectiveId") Long parentObjectiveId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 查询周期复盘目标列表
     */
    List<OkrObjectiveVO> queryByPeriod(@Param("periodId") Long periodId, @Param("deletedFlag") Boolean deletedFlag);
}
