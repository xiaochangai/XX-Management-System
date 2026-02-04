package net.lab1024.sa.admin.module.business.oa.okr.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OKR 关键结果
 */
@Data
public class OkrKeyResultVO {

    private Long keyResultId;

    private Long objectiveId;

    private String title;

    private String metricName;

    private BigDecimal startValue;

    private BigDecimal targetValue;

    private BigDecimal currentValue;

    private String unit;

    private BigDecimal progress;

    private Integer weight;

    private Integer confidence;

    private Integer status;

    private Integer sort;

    private BigDecimal score;

    private String reviewNote;

    private Long reviewUserId;

    private String reviewUserName;

    private LocalDateTime reviewTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
