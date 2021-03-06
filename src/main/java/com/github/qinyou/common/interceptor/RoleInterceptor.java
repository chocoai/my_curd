package com.github.qinyou.common.interceptor;

import com.github.qinyou.common.annotation.RequireRole;
import com.github.qinyou.common.base.BaseController;
import com.github.qinyou.common.config.Constant;
import com.github.qinyou.common.utils.StringUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.Ret;
import lombok.extern.slf4j.Slf4j;


/**
 * 角色拦截器, 拦截页面中特殊功能按钮
 *
 * @author zhangchuang
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class RoleInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        boolean flag = true;
        RequireRole requireRole = inv.getMethod().getAnnotation(RequireRole.class);
        String roleCodes = inv.getController().getSessionAttr(Constant.SYS_USER_ROLE_CODES);

        if (requireRole != null && roleCodes != null) {
            RequireRole.Relation relation = requireRole.relation();
            if (relation.equals(RequireRole.Relation.OR)) {
                flag = StringUtils.asListOrContains(roleCodes, requireRole.value());
            } else {
                flag = StringUtils.asListAndContains(roleCodes, requireRole.value());
            }
            log.debug("user roleCodes {}, annotation value: {}, annotation relation: {}  = {}"
                    , roleCodes, requireRole.value(), relation.name(), flag);
        }

        if (flag) {
            inv.invoke();
            return;
        }

        BaseController baseController = (BaseController) inv.getController();
        baseController.addServiceLog("RoleInterceptor: 访问无权限路径");
        // 如此 区分 ajax 并不完全准确, 例如 easyui form
        String requestType = baseController.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestType) || StringUtils.notEmpty(inv.getController().getPara("xmlHttpRequest"))) {
            Ret ret = Ret.create().setFail().set("msg", "无权限操作！您的行为已被记录到日志。");
            inv.getController().renderJson(ret);
        } else {
            inv.getController().render("/WEB-INF/views/common/no_permission.ftl");
        }
    }
}
