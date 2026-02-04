package net.lab1024.sa.admin.module.business.oa.okr.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

/**
 * OKR 关键结果评分/复盘
 */
@Data
public class OkrKeyResultReviewForm {

    @Schema(description = "关键结果ID")
    @NotNull(message = "关键结果ID不能为空")
    private Long keyResultId;

    @Schema(description = "评分(0-1)")
    @NotNull(message = "评分不能为空")
    private BigDecimal score;

    @Schema(description = "复盘说明")
    @Length(max = 1000, message = "复盘说明最多1000字符")
    private String reviewNote;

    @Schema(hidden = true)
    private Long reviewUserId;
}
