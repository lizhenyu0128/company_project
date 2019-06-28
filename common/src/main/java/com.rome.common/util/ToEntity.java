package com.rome.common.util;

import com.alibaba.fastjson.JSON;
import com.rome.common.entity.Token;

/**
 * Author:
 * Data:2019-06-28 16:01
 * Description:<>
 */
public class ToEntity {
    public static<T> T jsonStrToEntity(String text,Class<T> Obj){
       try {
           return JSON.parseObject(text, Obj);
       }catch (Exception ex){
           throw new RuntimeException(ex);
       }
    }
}
