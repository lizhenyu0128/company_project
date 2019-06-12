package com.rome.common.util;

import com.alibaba.fastjson.JSON;
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

    private static <T> HashMap resMsg(int code, T data, String message) {
        HashMap<String, Object> res = new HashMap<>(16);
        res.put("code", code);
        res.put("data", data);
        res.put("message", message);
        return res;
    }

    public static <T> void successJson(RoutingContext routingContext, int code, T data, String message) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(code, data, message)));
    }
    public static <T> void successJson(RoutingContext routingContext, int code, String message) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(code, null, message)));
    }
    public static <T> void successJson(RoutingContext routingContext, int code) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(code, null, "0")));
    }

    public static void falseJson(RoutingContext routingContext, int code, String message) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(code, null, message)));
    }

    public static void falseJson(RoutingContext routingContext, int code) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(code, null, "0")));
    }
}
