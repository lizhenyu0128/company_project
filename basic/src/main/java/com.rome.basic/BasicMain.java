package com.rome.basic;

import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;

/**
 * @author asus
 */
public class BasicMain {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        RxHelper.deployVerticle(vertx, new BasicMainVerticle()).subscribe();
    }
}
