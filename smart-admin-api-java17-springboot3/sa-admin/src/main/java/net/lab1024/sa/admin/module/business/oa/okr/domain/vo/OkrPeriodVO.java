package net.lab1024.sa.admin.module.business.oa.okr.domain.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * OKR 周期
 */
@Data
public class OkrPeriodVO {

    private Long periodId;

    private String periodName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer status;

    private String remark;

    private String createUserName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
