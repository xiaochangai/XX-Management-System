-- OKR menu and permissions

-- 1) 找到 OA 办公的父菜单
SET @oa_parent_id = NULL;
SELECT t_menu.menu_id INTO @oa_parent_id FROM t_menu WHERE t_menu.menu_name = 'OA办公' AND t_menu.deleted_flag = 0 ORDER BY t_menu.menu_id LIMIT 1;
SET @oa_parent_id = IFNULL(@oa_parent_id, 0);

-- 2) OKR目标 菜单
SET @okr_menu_id = NULL;
SELECT t_menu.menu_id INTO @okr_menu_id FROM t_menu WHERE t_menu.menu_name = 'OKR目标' AND t_menu.parent_id = @oa_parent_id AND t_menu.deleted_flag = 0 ORDER BY t_menu.menu_id LIMIT 1;
INSERT INTO t_menu (menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id)
SELECT 'OKR目标', 2, @oa_parent_id, '/oa/okr/okr-list', '/business/oa/okr/okr-list.vue', false, false, true, false, 1, 1
WHERE @okr_menu_id IS NULL;
SELECT t_menu.menu_id INTO @okr_menu_id FROM t_menu WHERE t_menu.menu_name = 'OKR目标' AND t_menu.parent_id = @oa_parent_id AND t_menu.deleted_flag = 0 ORDER BY t_menu.menu_id LIMIT 1;

-- 3) OKR详情(隐藏)
SET @okr_detail_id = NULL;
SELECT t_menu.menu_id INTO @okr_detail_id FROM t_menu WHERE t_menu.menu_name = 'OKR详情' AND t_menu.parent_id = @okr_menu_id AND t_menu.deleted_flag = 0 ORDER BY t_menu.menu_id LIMIT 1;
INSERT INTO t_menu (menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id)
SELECT 'OKR详情', 2, @okr_menu_id, '/oa/okr/okr-detail', '/business/oa/okr/okr-detail.vue', false, false, false, false, 1, 1
WHERE @okr_detail_id IS NULL;

-- 4) OKR周期 菜单
SET @okr_period_menu_id = NULL;
SELECT t_menu.menu_id INTO @okr_period_menu_id FROM t_menu WHERE t_menu.menu_name = 'OKR周期' AND t_menu.parent_id = @oa_parent_id AND t_menu.deleted_flag = 0 ORDER BY t_menu.menu_id LIMIT 1;
INSERT INTO t_menu (menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id)
SELECT 'OKR周期', 2, @oa_parent_id, '/oa/okr/okr-period-list', '/business/oa/okr/okr-period-list.vue', false, false, true, false, 1, 1
WHERE @okr_period_menu_id IS NULL;
SELECT t_menu.menu_id INTO @okr_period_menu_id FROM t_menu WHERE t_menu.menu_name = 'OKR周期' AND t_menu.parent_id = @oa_parent_id AND t_menu.deleted_flag = 0 ORDER BY t_menu.menu_id LIMIT 1;

-- 4.1) OKR对齐视图 菜单
SET @okr_map_menu_id = NULL;
SELECT t_menu.menu_id INTO @okr_map_menu_id FROM t_menu WHERE t_menu.menu_name = 'OKR对齐视图' AND t_menu.parent_id = @oa_parent_id AND t_menu.deleted_flag = 0 ORDER BY t_menu.menu_id LIMIT 1;
INSERT INTO t_menu (menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id)
SELECT 'OKR对齐视图', 2, @oa_parent_id, '/oa/okr/okr-map', '/business/oa/okr/okr-map.vue', false, false, true, false, 1, 1
WHERE @okr_map_menu_id IS NULL;

-- 4.2) OKR复盘汇总 菜单
SET @okr_review_menu_id = NULL;
SELECT t_menu.menu_id INTO @okr_review_menu_id FROM t_menu WHERE t_menu.menu_name = 'OKR复盘汇总' AND t_menu.parent_id = @oa_parent_id AND t_menu.deleted_flag = 0 ORDER BY t_menu.menu_id LIMIT 1;
INSERT INTO t_menu (menu_name, menu_type, parent_id, path, component, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, create_user_id)
SELECT 'OKR复盘汇总', 2, @oa_parent_id, '/oa/okr/okr-review-summary', '/business/oa/okr/okr-review-summary.vue', false, false, true, false, 1, 1
WHERE @okr_review_menu_id IS NULL;

-- 5) OKR目标权限
INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '查询', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:objective:query', 'oa:okr:objective:query', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '查询' AND api_perms = 'oa:okr:objective:query' AND parent_id = @okr_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '添加', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:objective:add', 'oa:okr:objective:add', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '添加' AND api_perms = 'oa:okr:objective:add' AND parent_id = @okr_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '更新', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:objective:update', 'oa:okr:objective:update', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '更新' AND api_perms = 'oa:okr:objective:update' AND parent_id = @okr_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '删除', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:objective:delete', 'oa:okr:objective:delete', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '删除' AND api_perms = 'oa:okr:objective:delete' AND parent_id = @okr_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '详情', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:objective:detail', 'oa:okr:objective:detail', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '详情' AND api_perms = 'oa:okr:objective:detail' AND parent_id = @okr_menu_id);

-- 6) 关键结果权限
INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '新增关键结果', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:kr:add', 'oa:okr:kr:add', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '新增关键结果' AND api_perms = 'oa:okr:kr:add' AND parent_id = @okr_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '更新关键结果', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:kr:update', 'oa:okr:kr:update', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '更新关键结果' AND api_perms = 'oa:okr:kr:update' AND parent_id = @okr_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '删除关键结果', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:kr:delete', 'oa:okr:kr:delete', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '删除关键结果' AND api_perms = 'oa:okr:kr:delete' AND parent_id = @okr_menu_id);

-- 6.0) 复盘评分权限
INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '目标复盘评分', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:review:objective', 'oa:okr:review:objective', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '目标复盘评分' AND api_perms = 'oa:okr:review:objective' AND parent_id = @okr_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '关键结果复盘评分', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:review:kr', 'oa:okr:review:kr', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '关键结果复盘评分' AND api_perms = 'oa:okr:review:kr' AND parent_id = @okr_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '复盘汇总查询', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:review:summary', 'oa:okr:review:summary', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '复盘汇总查询' AND api_perms = 'oa:okr:review:summary' AND parent_id = @okr_menu_id);

-- 6.1) 进展更新权限
INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '进展更新查询', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:checkin:query', 'oa:okr:checkin:query', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '进展更新查询' AND api_perms = 'oa:okr:checkin:query' AND parent_id = @okr_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '进展更新新增', 3, @okr_menu_id, false, false, true, false, 1, 'oa:okr:checkin:add', 'oa:okr:checkin:add', @okr_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '进展更新新增' AND api_perms = 'oa:okr:checkin:add' AND parent_id = @okr_menu_id);

-- 7) OKR周期权限
INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '查询', 3, @okr_period_menu_id, false, false, true, false, 1, 'oa:okr:period:query', 'oa:okr:period:query', @okr_period_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '查询' AND api_perms = 'oa:okr:period:query' AND parent_id = @okr_period_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '添加', 3, @okr_period_menu_id, false, false, true, false, 1, 'oa:okr:period:add', 'oa:okr:period:add', @okr_period_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '添加' AND api_perms = 'oa:okr:period:add' AND parent_id = @okr_period_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '更新', 3, @okr_period_menu_id, false, false, true, false, 1, 'oa:okr:period:update', 'oa:okr:period:update', @okr_period_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '更新' AND api_perms = 'oa:okr:period:update' AND parent_id = @okr_period_menu_id);

INSERT INTO t_menu (menu_name, menu_type, parent_id, frame_flag, cache_flag, visible_flag, disabled_flag, perms_type, api_perms, web_perms, context_menu_id, create_user_id)
SELECT '删除', 3, @okr_period_menu_id, false, false, true, false, 1, 'oa:okr:period:delete', 'oa:okr:period:delete', @okr_period_menu_id, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '删除' AND api_perms = 'oa:okr:period:delete' AND parent_id = @okr_period_menu_id);
