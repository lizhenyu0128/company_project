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
    private static final int successCode = 200;
    private static final int falseCode = 101;
    private static final int errCode = 102;

    private static <T> HashMap resMsg(int code, T data, String message) {
        HashMap<String, Object> res = new HashMap<>(16);
        res.put("code", code);
        res.put("data", data);
        res.put("message", message);
        return res;
    }

    public static <T> void successJson(RoutingContext routingContext, T data, String message) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(successCode, data, message)));
    }
    public static <T> void successJson(RoutingContext routingContext, String message) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(successCode, null, message)));
    }
    public static <T> void successJson(RoutingContext routingContext) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(successCode, null, "0")));
    }

    public static void falseJson(RoutingContext routingContext, String message) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(falseCode, null, message)));
    }

    public static void falseJson(RoutingContext routingContext) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(falseCode, null, "0")));
    }
    public static void errJson(RoutingContext routingContext) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(JSON.toJSONString(resMsg(errCode, null, "0")));
    }
}
