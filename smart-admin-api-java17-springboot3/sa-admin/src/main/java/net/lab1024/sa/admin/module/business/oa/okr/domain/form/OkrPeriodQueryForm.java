package net.lab1024.sa.admin.module.business.oa.okr.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * OKR 周期分页查询
 */
@Data
public class OkrPeriodQueryForm extends PageParam {

    @Schema(description = "关键字(周期名称)")
    private String keywords;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "开始日期-起")
    private LocalDate startDateBegin;

    @Schema(description = "开始日期-止")
    private LocalDate startDateEnd;

    @Schema(hidden = true)
    private Boolean deletedFlag;
}
