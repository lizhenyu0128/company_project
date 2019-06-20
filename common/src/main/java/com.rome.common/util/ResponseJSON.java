package com.rome.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

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



    private static  String resMsg(int code, JsonObject data, String message) {
        JsonObject ss = new JsonObject();
        return ss.put("code", code).put("data", data).put("message", message).toString();
    }

    public static <T> void successJson(RoutingContext routingContext, JsonObject data, String message) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(resMsg(SUCCESS_CODE, data, message));
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
