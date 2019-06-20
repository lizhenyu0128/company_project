package com.rome.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.HashMap;

/**
 * Author:
 * Data:2019-06-12 17:59
 * Description:<>
 *
 * @author Trump
 */
public class ResponseJSON {
    private static final int SUCCESS_CODE = 200;
    private static final int FALSE_CODE = 101;
    private static final int ERR_CODE = 102;

    private static <T> HashMap<String, Object> resMsg(int code, T data, String message) {
        HashMap<String, Object> res = new HashMap<>(16);
        res.put("code", code);
        res.put("data", data);
        res.put("message", message);
        return res;
    }

    public static <T> void successJson(RoutingContext routingContext, T data, String message) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSONObject.toJSONString(resMsg(SUCCESS_CODE, data, message)));
    }
    public static <T> void successJson(RoutingContext routingContext, String message) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(SUCCESS_CODE, null, message)));
    }
    public static <T> void successJson(RoutingContext routingContext) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(SUCCESS_CODE, null, "0")));
    }

    public static void falseJson(RoutingContext routingContext, String message) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(FALSE_CODE, null, message)));
    }

    public static void falseJson(RoutingContext routingContext) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(FALSE_CODE, null, "0")));
    }
    public static void errJson(RoutingContext routingContext) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(ERR_CODE, null, "0")));
    }
}
