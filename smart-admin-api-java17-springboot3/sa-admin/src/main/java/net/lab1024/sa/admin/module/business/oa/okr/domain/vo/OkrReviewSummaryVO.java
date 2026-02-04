package net.lab1024.sa.admin.module.business.oa.okr.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * OKR 周期复盘汇总
 */
@Data
public class OkrReviewSummaryVO {

    private Long periodId;

    private String periodName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer objectiveCount;

    private Integer scoredCount;

    private BigDecimal avgScore;

    private BigDecimal avgProgress;

    private Integer statusDraftCount;

    private Integer statusOnTrackCount;

    private Integer statusAtRiskCount;

    private Integer statusOffTrackCount;

    private Integer statusDoneCount;

    private Integer statusCancelledCount;

    private List<OkrReviewScoreBucketVO> scoreBucketList;

    private List<OkrObjectiveVO> objectiveList;
}
