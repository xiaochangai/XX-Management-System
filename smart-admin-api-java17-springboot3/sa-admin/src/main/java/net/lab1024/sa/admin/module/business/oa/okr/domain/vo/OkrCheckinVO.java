package net.lab1024.sa.admin.module.business.oa.okr.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OKR 进展更新
 */
@Data
public class OkrCheckinVO {

    private Long checkinId;

    private Long objectiveId;

    private Long keyResultId;

    private String keyResultTitle;

    private BigDecimal currentValue;

    private BigDecimal progress;

    private Integer status;

    private Integer confidence;

    private String note;

    private Long createUserId;

    private String createUserName;

    private LocalDateTime createTime;
}
