package net.lab1024.sa.admin.module.business.oa.okr.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * OKR 目标详情
 */
@Data
public class OkrObjectiveDetailVO extends OkrObjectiveVO {

    private List<OkrKeyResultVO> keyResultList;
}
