package net.lab1024.sa.admin.module.business.oa.okr.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * OKR 目标/关键结果状态
 */
@Getter
@AllArgsConstructor
public enum OkrStatusEnum implements BaseEnum {

    DRAFT(0, "草稿"),

    ON_TRACK(1, "正常"),

    AT_RISK(2, "有风险"),

    OFF_TRACK(3, "偏离"),

    DONE(4, "已完成"),

    CANCELLED(5, "已取消"),

    ;

    private final Integer value;

    private final String desc;
}
