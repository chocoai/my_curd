package com.github.qinyou.common.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.qinyou.common.utils.WebUtils;
import com.github.qinyou.system.service.SysServiceLogService;
import com.jfinal.core.Controller;
import com.jfinal.core.Injector;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.*;

/**
 * controller 基类，封装一些Controller 中使用的公共方法
 *
 * @author zhangchuang
 */
public abstract class BaseController extends Controller {

    /**
     * 返回datagrid json 数据
     *
     * @param pageData
     */
    protected void renderDatagrid(Page<?> pageData) {
        Map<String, Object> datagrid = new HashMap<>();
        datagrid.put("rows", pageData.getList());
        datagrid.put("total", pageData.getTotalRow());
        renderJson(datagrid);
    }

    /**
     * 返回datagrid json 数据
     *
     * @param list
     * @param total
     */
    protected void renderDatagrid(List<?> list, int total) {
        renderDatagrid(list, total, null);
    }

    /**
     * 返回datagrid json 数据
     *
     * @param list
     * @param total
     * @param footer
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    protected void renderDatagrid(List<?> list, int total, List<Map<String, Object>> footer) {
        Map<String, Object> datagrid = new HashMap<>();
        datagrid.put("rows", list);
        datagrid.put("total", total);
        if (footer != null && footer.size() > 0) {
            datagrid.put("footer", footer);
        }
        renderJson(datagrid);
    }

    /**
     * 返回datagrid json 数据, 无分页
     *
     * @param list
     */
    protected void renderDatagrid(List<Record> list) {
        Map<String, Object> datagrid = new HashMap<>();
        datagrid.put("rows", list);
        renderJson(datagrid);
    }

    public void renderDatagrid(Collection collection, int total) {
        Map<String, Object> datagrid = new HashMap<>();
        datagrid.put("rows", collection);
        datagrid.put("total", total);
        renderJson(datagrid);
    }


    /**
     * 成功操作
     */
    protected void renderSuccess() {
        Ret ret = Ret.create().setOk();
        renderJson(ret);
    }

    /**
     * 成功操作
     *
     * @param msg
     */
    protected void renderSuccess(String msg) {
        Ret ret = Ret.create().setOk().setIfNotNull("msg", msg);
        renderJson(ret);
    }

    /**
     * 成功操作
     *
     * @param data
     */
    protected void renderSuccess(List<Object> data) {
        Ret ret = Ret.create().setOk().setIfNotNull("data", data);
        renderJson(ret);
    }

    /**
     * 成功操作
     *
     * @param msg
     * @param data
     */
    protected void renderSuccess(String msg, List<Object> data) {
        Ret ret = Ret.create().setOk().setIfNotNull("msg", msg).setIfNotNull("data", data);
        renderJson(ret);
    }


    /**
     * 失败操作
     */
    protected void renderFail() {
        Ret ret = Ret.create().setFail();
        renderJson(ret);
    }

    /**
     * 失败操作
     *
     * @param msg
     */
    protected void renderFail(String msg) {
        Ret ret = Ret.create().setFail().setIfNotNull("msg", msg);
        renderJson(ret);
    }

    /**
     * 失败操作
     *
     * @param data
     */
    protected void renderFail(List<Object> data) {
        Ret ret = Ret.create().setFail().setIfNotNull("data", data);
        renderJson(ret);
    }

    /**
     * 失败操作
     *
     * @param msg
     * @param data
     */
    protected void renderFail(String msg, List<Object> data) {
        Ret ret = Ret.create().setFail().setIfNotNull("msg", msg).setIfNotNull("data", data);
        renderJson(ret);
    }

    /**
     * 获取 http 请求 body json 数据
     */
    protected JSONObject readRawDataAsJson() {
        String rawData = getRawData();
        return JSON.parseObject(rawData);
    }


    /**
     * 添加业务日志
     *
     * @param content
     */
    @NotAction
    public void addServiceLog(String content) {
        SysServiceLogService.addServiceLog(WebUtils.getSessionUsername(this)
                , WebUtils.getRemoteAddress(getRequest())
                , getRequest().getRequestURI(), content);
    }


    /**
     * 接收 bean list
     *
     * @param modelClass
     * @param prefix
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> getBeans(Class<? extends Model> modelClass, String prefix) {
        List<T> beanList = new ArrayList<>();
        int size = getArrayKeys(prefix).size();
        for (int i = 0; i < size; i++) {
            beanList.add((T) Injector.injectBean(modelClass, prefix + "[" + i + "]", getRequest(), false));
        }
        return beanList;
    }

    /**
     * 获得 bean[index] 的 所有 key
     *
     * @param prefix
     * @return
     */
    private Set<String> getArrayKeys(String prefix) {
        Set<String> keys = new HashSet<>();
        String arrayPrefix = prefix + "[";
        String key;
        Enumeration<String> names = getRequest().getParameterNames();
        while (names.hasMoreElements()) {
            key = names.nextElement();
            if (!key.startsWith(arrayPrefix)) {
                continue;
            }
            if (!key.contains("]")) {
                continue;
            }
            keys.add(key.substring(0, key.indexOf("]") + 1));
        }
        return keys;
    }
}

