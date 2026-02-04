package net.lab1024.sa.admin.module.business.oa.okr.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrKeyResultEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrKeyResultVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OKR 关键结果
 */
@Mapper
public interface OkrKeyResultDao extends BaseMapper<OkrKeyResultEntity> {

    /**
     * 根据目标查询关键结果
     */
    List<OkrKeyResultVO> queryByObjectiveId(@Param("objectiveId") Long objectiveId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 根据目标删除关键结果
     */
    void updateDeletedByObjectiveId(@Param("objectiveId") Long objectiveId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 删除单条关键结果
     */
    void updateDeletedFlag(@Param("keyResultId") Long keyResultId, @Param("deletedFlag") Boolean deletedFlag);
}
