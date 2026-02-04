package net.lab1024.sa.admin.module.business.oa.okr.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrCheckinEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrCheckinVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OKR 进展更新
 */
@Mapper
public interface OkrCheckinDao extends BaseMapper<OkrCheckinEntity> {

    /**
     * 查询目标下的更新记录
     */
    List<OkrCheckinVO> queryByObjectiveId(@Param("objectiveId") Long objectiveId);
}
