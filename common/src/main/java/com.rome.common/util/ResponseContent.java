package com.rome.common.util;

import com.alibaba.fastjson.JSON;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.HashMap;

/**
 * Author:
 * Data:2019-05-11 13:33
 * Description:<>
 * @author Trump
 */
public class ResponseContent {

  private static  HashMap<String, Object> resMsg(int result, String msg) {
    HashMap<String, Object> res = new HashMap<>(16);
    res.put("result", result);
    res.put("msg", msg);
    return res;
  }

  private static <T> HashMap<String, Object> resMsg(int code,  T data) {
    HashMap<String, Object> res = new HashMap<>(16);
    res.put("result", code);
    res.put("data", data);
    return res;
  }

  public static <T> void success(RoutingContext routingContext, int code,T value) {

    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(JSON.toJSONString(resMsg(code,  value)));
  }

  public static void success(RoutingContext routingContext, int code, String msg) {

    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(JSON.toJSONString(resMsg(code, msg)));
  }


}
