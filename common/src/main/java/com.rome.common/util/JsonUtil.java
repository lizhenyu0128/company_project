package com.rome.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author:
 * Data:2019-05-13 16:06
 * Description:<>
 * @author lizhenyu
 */
public class JsonUtil {
  private static Pattern humpPattern = Pattern.compile("[A-Z]");

  /**
   * @description  驼峰转下划线
   * @param str
   * @return
   */
  public static JSONObject humpToLine(String str) {
    Matcher matcher = humpPattern.matcher(str);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
    }
    matcher.appendTail(sb);
    return JSONObject.parseObject(sb.toString());
  }

  /**
   * @description 将json转成对象
   * @param toClass
   * @param jsonObject
   * @return
   */
  public static Object jsonToEntity(Class toClass, JSON jsonObject) {
    return JSONObject.toJavaObject( jsonObject, toClass);
  }

}
