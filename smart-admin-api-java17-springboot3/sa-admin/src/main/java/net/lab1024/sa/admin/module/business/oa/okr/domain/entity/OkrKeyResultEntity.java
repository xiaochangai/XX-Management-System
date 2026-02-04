package net.lab1024.sa.admin.module.business.oa.okr.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OKR 关键结果
 */
@Data
@TableName("t_okr_key_result")
public class OkrKeyResultEntity {

    @TableId(type = IdType.AUTO)
    private Long keyResultId;

    private Long objectiveId;

    private String title;

    private String metricName;

    private BigDecimal startValue;

    private BigDecimal targetValue;

    private BigDecimal currentValue;

    private String unit;

    private BigDecimal progress;

    private Integer weight;

    private Integer confidence;

    private Integer status;

    private Integer sort;

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
