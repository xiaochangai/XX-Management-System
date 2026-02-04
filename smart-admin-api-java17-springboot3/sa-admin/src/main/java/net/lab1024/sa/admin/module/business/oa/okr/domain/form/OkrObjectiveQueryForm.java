package net.lab1024.sa.admin.module.business.oa.okr.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

/**
 * OKR 目标分页查询
 */
@Data
public class OkrObjectiveQueryForm extends PageParam {

    @Schema(description = "周期ID")
    private Long periodId;

    @Schema(description = "负责人")
    private Long ownerEmployeeId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "关键字(标题/描述)")
    private String keywords;

    @Schema(hidden = true)
    private Boolean deletedFlag;
}
