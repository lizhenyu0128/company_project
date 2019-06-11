package com.rome.uaa;

import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;


/**
 * Author:
 * Data:2019-05-10 18:34
 * Description:<>
 *
 * @author Trump
 */
public class UaaMainVertx {
    public static void main(String[] args) {
        Vertx vertx = io.vertx.reactivex.core.Vertx.vertx();
        RxHelper.deployVerticle(vertx, new MainVerticle()).subscribe();

    }
}
