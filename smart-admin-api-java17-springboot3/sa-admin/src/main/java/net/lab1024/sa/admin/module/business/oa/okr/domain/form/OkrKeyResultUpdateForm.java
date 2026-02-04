package net.lab1024.sa.admin.module.business.oa.okr.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * OKR 关键结果更新
 */
@Data
public class OkrKeyResultUpdateForm extends OkrKeyResultAddForm {

    @Schema(description = "关键结果ID")
    @NotNull(message = "关键结果ID不能为空")
    private Long keyResultId;
}
