package net.lab1024.sa.admin.module.business.oa.okr.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

/**
 * OKR 关键结果新增
 */
@Data
public class OkrKeyResultAddForm {

    @Schema(description = "目标ID")
    @NotNull(message = "目标不能为空")
    private Long objectiveId;

    @Schema(description = "关键结果标题")
    @NotBlank(message = "关键结果标题不能为空")
    @Length(max = 200, message = "关键结果标题最多200字符")
    private String title;

    @Schema(description = "度量名称")
    @Length(max = 100, message = "度量名称最多100字符")
    private String metricName;

    @Schema(description = "起始值")
    private BigDecimal startValue;

    @Schema(description = "目标值")
    @NotNull(message = "目标值不能为空")
    private BigDecimal targetValue;

    @Schema(description = "当前值")
    private BigDecimal currentValue;

    @Schema(description = "单位")
    @Length(max = 20, message = "单位最多20字符")
    private String unit;

    @Schema(description = "状态")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "置信度(1-5)")
    private Integer confidence;

    @Schema(description = "权重")
    private Integer weight;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(hidden = true)
    private Long createUserId;
}
