package net.lab1024.sa.admin.module.business.oa.okr.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * OKR 评分区间统计
 */
@Data
@AllArgsConstructor
public class OkrReviewScoreBucketVO {

    private String label;

    private Integer count;
}
