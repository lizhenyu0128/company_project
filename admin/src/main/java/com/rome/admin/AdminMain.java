package com.rome.admin;

import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;

/**
 * @author asus
 */
public class AdminMain {
    public static void main(String[] args) {
        Vertx vertx = io.vertx.reactivex.core.Vertx.vertx();
        RxHelper.deployVerticle(vertx, new AdminMainVerticle()).subscribe();
    }

}
