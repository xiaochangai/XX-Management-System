SET NAMES utf8mb4;

SET @oa_parent_id := (SELECT menu_id FROM t_menu WHERE path = '/oa' LIMIT 1);

INSERT INTO t_menu
(menu_id, menu_name, menu_type, parent_id, sort, path, component, perms_type, api_perms, web_perms,
 icon, context_menu_id, frame_flag, frame_url, cache_flag, visible_flag, disabled_flag, deleted_flag,
 create_user_id, create_time, update_user_id, update_time)
VALUES
(323, 'OKR首页', 2, IFNULL(@oa_parent_id, 0), 1,
 '/oa/okr/okr-feishu', '/business/oa/okr/okr-feishu.vue',
 1, NULL, NULL, NULL, NULL, 0, NULL, 0, 1, 0, 0,
 1, NOW(), NULL, NOW());

INSERT INTO t_role_menu (role_id, menu_id)
SELECT role_id, 323 FROM t_role WHERE role_name = '超级管理员' LIMIT 1;
