package net.lab1024.sa.admin.module.business.oa.okr.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * OKR 周期更新
 */
@Data
public class OkrPeriodUpdateForm extends OkrPeriodAddForm {

    @Schema(description = "周期ID")
    @NotNull(message = "周期ID不能为空")
    private Long periodId;
}
