package net.lab1024.sa.admin.module.business.oa.okr.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * OKR 置信度
 */
@Getter
@AllArgsConstructor
public enum OkrConfidenceEnum implements BaseEnum {

    VERY_LOW(1, "很低"),

    LOW(2, "偏低"),

    MEDIUM(3, "一般"),

    HIGH(4, "较高"),

    VERY_HIGH(5, "非常高"),

    ;

    private final Integer value;

    private final String desc;
}
