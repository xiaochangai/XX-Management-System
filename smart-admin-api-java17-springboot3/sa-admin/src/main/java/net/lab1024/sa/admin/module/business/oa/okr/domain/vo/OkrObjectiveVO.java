package net.lab1024.sa.admin.module.business.oa.okr.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OKR 目标列表
 */
@Data
public class OkrObjectiveVO {

    private Long objectiveId;

    private Long periodId;

    private String periodName;

    private Long parentObjectiveId;

    private String parentObjectiveTitle;

    private Long ownerEmployeeId;

    private String ownerName;

    private String title;

    private String description;

    private BigDecimal progress;

    private Integer status;

    private Integer confidence;

    private Integer keyResultCount;

    private BigDecimal score;

    private String reviewNote;

    private Long reviewUserId;

    private String reviewUserName;

    private LocalDateTime reviewTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
