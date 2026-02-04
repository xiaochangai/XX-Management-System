package net.lab1024.sa.admin.module.business.oa.okr.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * OKR 周期状态
 */
@Getter
@AllArgsConstructor
public enum OkrPeriodStatusEnum implements BaseEnum {

    DRAFT(0, "草稿"),

    ACTIVE(1, "进行中"),

    CLOSED(2, "已归档"),

    ;

    private final Integer value;

    private final String desc;
}
