package net.lab1024.sa.admin.module.business.oa.okr.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OKR 进展更新
 */
@Data
@TableName("t_okr_checkin")
public class OkrCheckinEntity {

    @TableId(type = IdType.AUTO)
    private Long checkinId;

    private Long objectiveId;

    private Long keyResultId;

    private BigDecimal currentValue;

    private BigDecimal progress;

    private Integer status;

    private Integer confidence;

    private String note;

    private Long createUserId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
