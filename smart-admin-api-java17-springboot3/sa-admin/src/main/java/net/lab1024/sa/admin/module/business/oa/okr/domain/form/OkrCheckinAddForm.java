package net.lab1024.sa.admin.module.business.oa.okr.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

/**
 * OKR 进展更新
 */
@Data
public class OkrCheckinAddForm {

    @Schema(description = "目标ID")
    @NotNull(message = "目标不能为空")
    private Long objectiveId;

    @Schema(description = "关键结果ID")
    private Long keyResultId;

    @Schema(description = "当前值")
    private BigDecimal currentValue;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "置信度")
    private Integer confidence;

    @Schema(description = "更新说明")
    @Length(max = 1000, message = "更新说明最多1000字符")
    private String note;

    @Schema(hidden = true)
    private Long createUserId;
}
