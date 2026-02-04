package net.lab1024.sa.admin.module.business.oa.okr.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * OKR 目标新增
 */
@Data
public class OkrObjectiveAddForm {

    @Schema(description = "周期ID")
    @NotNull(message = "周期不能为空")
    private Long periodId;

    @Schema(description = "目标标题")
    @NotBlank(message = "目标标题不能为空")
    @Length(max = 200, message = "目标标题最多200字符")
    private String title;

    @Schema(description = "目标描述")
    @Length(max = 1000, message = "目标描述最多1000字符")
    private String description;

    @Schema(description = "负责人")
    @NotNull(message = "负责人不能为空")
    private Long ownerEmployeeId;

    @Schema(description = "对齐目标ID")
    private Long parentObjectiveId;

    @Schema(description = "状态")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "置信度(1-5)")
    private Integer confidence;

    @Schema(hidden = true)
    private Long createUserId;
}
