package net.lab1024.sa.admin.module.business.oa.okr.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * OKR 周期新增
 */
@Data
public class OkrPeriodAddForm {

    @Schema(description = "周期名称")
    @NotBlank(message = "周期名称不能为空")
    @Length(max = 50, message = "周期名称最多50字符")
    private String periodName;

    @Schema(description = "开始日期")
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @Schema(description = "状态")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注")
    @Length(max = 255, message = "备注最多255字符")
    private String remark;

    @Schema(hidden = true)
    private Long createUserId;
}
