package com.rome.grading;

import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;

/**
 * @author asus
 */
public class GradingMain {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        RxHelper.deployVerticle(vertx, new GradingMainVerticle()).subscribe();
    }
}
