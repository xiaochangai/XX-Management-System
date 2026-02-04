package net.lab1024.sa.admin.module.business.oa.okr.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * OKR 目标更新
 */
@Data
public class OkrObjectiveUpdateForm extends OkrObjectiveAddForm {

    @Schema(description = "目标ID")
    @NotNull(message = "目标ID不能为空")
    private Long objectiveId;
}
