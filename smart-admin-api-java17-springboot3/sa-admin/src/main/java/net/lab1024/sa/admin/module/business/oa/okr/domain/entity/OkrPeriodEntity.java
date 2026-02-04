package net.lab1024.sa.admin.module.business.oa.okr.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * OKR 周期
 */
@Data
@TableName("t_okr_period")
public class OkrPeriodEntity {

    @TableId(type = IdType.AUTO)
    private Long periodId;

    private String periodName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer status;

    private String remark;

    private Boolean deletedFlag;

    private Long createUserId;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
