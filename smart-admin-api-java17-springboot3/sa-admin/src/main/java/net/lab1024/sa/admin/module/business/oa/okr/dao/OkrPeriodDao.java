package net.lab1024.sa.admin.module.business.oa.okr.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.oa.okr.domain.entity.OkrPeriodEntity;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.OkrPeriodQueryForm;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrPeriodVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OKR 周期
 */
@Mapper
public interface OkrPeriodDao extends BaseMapper<OkrPeriodEntity> {

    /**
     * 周期分页查询
     */
    List<OkrPeriodVO> queryPage(Page page, @Param("queryForm") OkrPeriodQueryForm queryForm);

    /**
     * 查询周期详情
     */
    OkrPeriodVO getDetail(@Param("periodId") Long periodId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 查询全部周期
     */
    List<OkrPeriodVO> queryAll(@Param("deletedFlag") Boolean deletedFlag);
}
