package net.lab1024.sa.admin.module.business.oa.okr.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

/**
 * OKR 目标评分/复盘
 */
@Data
public class OkrObjectiveReviewForm {

    @Schema(description = "目标ID")
    @NotNull(message = "目标ID不能为空")
    private Long objectiveId;

    @Schema(description = "评分(0-1)")
    @NotNull(message = "评分不能为空")
    private BigDecimal score;

    @Schema(description = "复盘说明")
    @Length(max = 1000, message = "复盘说明最多1000字符")
    private String reviewNote;

    @Schema(hidden = true)
    private Long reviewUserId;
}
