package net.lab1024.sa.admin.module.business.oa.okr.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.business.oa.okr.domain.form.*;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrObjectiveDetailVO;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrObjectiveSimpleVO;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrObjectiveVO;
import net.lab1024.sa.admin.module.business.oa.okr.domain.vo.OkrPeriodVO;
import net.lab1024.sa.admin.module.business.oa.okr.service.OkrKeyResultService;
import net.lab1024.sa.admin.module.business.oa.okr.service.OkrObjectiveService;
import net.lab1024.sa.admin.module.business.oa.okr.service.OkrPeriodService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import net.lab1024.sa.base.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OKR 模块
 */
@RestController
@Tag(name = AdminSwaggerTagConst.Business.OA_OKR)
@OperateLog
public class OkrController {

    @Resource
    private OkrPeriodService okrPeriodService;

    @Resource
    private OkrObjectiveService okrObjectiveService;

    @Resource
    private OkrKeyResultService okrKeyResultService;

    // ============================ 周期 ============================

    @Operation(summary = "OKR周期-分页查询")
    @PostMapping("/oa/okr/period/query")
    @SaCheckPermission("oa:okr:period:query")
    public ResponseDTO<PageResult<OkrPeriodVO>> queryPeriod(@RequestBody @Valid OkrPeriodQueryForm queryForm) {
        return okrPeriodService.query(queryForm);
    }

    @Operation(summary = "OKR周期-查询全部")
    @GetMapping("/oa/okr/period/listAll")
    @SaCheckPermission("oa:okr:period:query")
    public ResponseDTO<List<OkrPeriodVO>> listAllPeriod() {
        return okrPeriodService.listAll();
    }

    @Operation(summary = "OKR周期-新增")
    @PostMapping("/oa/okr/period/add")
    @RepeatSubmit
    @SaCheckPermission("oa:okr:period:add")
    public ResponseDTO<String> addPeriod(@RequestBody @Valid OkrPeriodAddForm addForm) {
        addForm.setCreateUserId(SmartRequestUtil.getRequestUserId());
        return okrPeriodService.add(addForm);
    }

    @Operation(summary = "OKR周期-更新")
    @PostMapping("/oa/okr/period/update")
    @RepeatSubmit
    @SaCheckPermission("oa:okr:period:update")
    public ResponseDTO<String> updatePeriod(@RequestBody @Valid OkrPeriodUpdateForm updateForm) {
        return okrPeriodService.update(updateForm);
    }

    @Operation(summary = "OKR周期-删除")
    @GetMapping("/oa/okr/period/delete/{periodId}")
    @SaCheckPermission("oa:okr:period:delete")
    public ResponseDTO<String> deletePeriod(@PathVariable Long periodId) {
        return okrPeriodService.delete(periodId);
    }

    // ============================ 目标 ============================

    @Operation(summary = "OKR目标-分页查询")
    @PostMapping("/oa/okr/objective/query")
    @SaCheckPermission("oa:okr:objective:query")
    public ResponseDTO<PageResult<OkrObjectiveVO>> queryObjective(@RequestBody @Valid OkrObjectiveQueryForm queryForm) {
        return okrObjectiveService.query(queryForm);
    }

    @Operation(summary = "OKR目标-详情")
    @GetMapping("/oa/okr/objective/detail/{objectiveId}")
    @SaCheckPermission("oa:okr:objective:detail")
    public ResponseDTO<OkrObjectiveDetailVO> objectiveDetail(@PathVariable Long objectiveId) {
        return okrObjectiveService.detail(objectiveId);
    }

    @Operation(summary = "OKR目标-简要列表")
    @GetMapping("/oa/okr/objective/simple-list")
    @SaCheckPermission("oa:okr:objective:query")
    public ResponseDTO<List<OkrObjectiveSimpleVO>> objectiveSimpleList(@RequestParam(required = false) Long periodId) {
        return okrObjectiveService.simpleList(periodId);
    }

    @Operation(summary = "OKR目标-新增")
    @PostMapping("/oa/okr/objective/add")
    @RepeatSubmit
    @SaCheckPermission("oa:okr:objective:add")
    public ResponseDTO<String> addObjective(@RequestBody @Valid OkrObjectiveAddForm addForm) {
        addForm.setCreateUserId(SmartRequestUtil.getRequestUserId());
        return okrObjectiveService.add(addForm);
    }

    @Operation(summary = "OKR目标-更新")
    @PostMapping("/oa/okr/objective/update")
    @RepeatSubmit
    @SaCheckPermission("oa:okr:objective:update")
    public ResponseDTO<String> updateObjective(@RequestBody @Valid OkrObjectiveUpdateForm updateForm) {
        return okrObjectiveService.update(updateForm);
    }

    @Operation(summary = "OKR目标-删除")
    @GetMapping("/oa/okr/objective/delete/{objectiveId}")
    @SaCheckPermission("oa:okr:objective:delete")
    public ResponseDTO<String> deleteObjective(@PathVariable Long objectiveId) {
        return okrObjectiveService.delete(objectiveId);
    }

    // ============================ 关键结果 ============================

    @Operation(summary = "OKR关键结果-新增")
    @PostMapping("/oa/okr/key-result/add")
    @RepeatSubmit
    @SaCheckPermission("oa:okr:kr:add")
    public ResponseDTO<String> addKeyResult(@RequestBody @Valid OkrKeyResultAddForm addForm) {
        addForm.setCreateUserId(SmartRequestUtil.getRequestUserId());
        return okrKeyResultService.add(addForm);
    }

    @Operation(summary = "OKR关键结果-更新")
    @PostMapping("/oa/okr/key-result/update")
    @RepeatSubmit
    @SaCheckPermission("oa:okr:kr:update")
    public ResponseDTO<String> updateKeyResult(@RequestBody @Valid OkrKeyResultUpdateForm updateForm) {
        return okrKeyResultService.update(updateForm);
    }

    @Operation(summary = "OKR关键结果-删除")
    @GetMapping("/oa/okr/key-result/delete/{keyResultId}")
    @SaCheckPermission("oa:okr:kr:delete")
    public ResponseDTO<String> deleteKeyResult(@PathVariable Long keyResultId) {
        return okrKeyResultService.delete(keyResultId);
    }
}
