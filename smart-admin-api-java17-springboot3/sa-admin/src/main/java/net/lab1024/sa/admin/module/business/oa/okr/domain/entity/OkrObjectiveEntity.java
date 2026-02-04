package net.lab1024.sa.admin.module.business.oa.okr.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OKR 目标
 */
@Data
@TableName("t_okr_objective")
public class OkrObjectiveEntity {

    @TableId(type = IdType.AUTO)
    private Long objectiveId;

    private Long periodId;

    private Long parentObjectiveId;

    private Long ownerEmployeeId;

    private String title;

    private String description;

    private BigDecimal progress;

    private Integer confidence;

    private Integer status;

    /**
     * 评分
     */
    private BigDecimal score;

    /**
     * 复盘说明
     */
    private String reviewNote;

    /**
     * 复盘人
     */
    private Long reviewUserId;

    /**
     * 复盘时间
     */
    private LocalDateTime reviewTime;

    private Boolean deletedFlag;

    private Long createUserId;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
