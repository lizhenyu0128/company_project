package com.rome.common.config;

import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;

import java.util.logging.Logger;

/**
 * Author:
 * Data:2019-06-07 14:00
 * Description:<>
 * @author Trump
 */
public class InitConfig {

    public static Single initConfig(Vertx vertx, io.vertx.reactivex.core.AbstractVerticle verticle, String path) {
        //"/Users/lizhenyu/work_code/company_code/rome-backend/message/src/resources" + File.separator + "config-dev.json"
     return Single.create(op->{
         vertx.fileSystem().readFile(path,
             res -> {
                 if (res.succeeded()) {
                     Buffer buf = res.result();
                     verticle.config().mergeIn(buf.toJsonObject());











                     op.onSuccess(buf);
                 } else {
                     op.onError(new Error("Initialization configuration failed"));
                 }
             });

      });
    }
}