package com.rome.wallet;

import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;

/**
 * Author: Trump
 * Data:2019-06-15 11:57
 * Description:<>
 */
public class WalletMain {
    public static void main(String[] args) {
        Vertx vertx = io.vertx.reactivex.core.Vertx.vertx();
        RxHelper.deployVerticle(vertx, new WalletMainVerticle()).subscribe();
    }
}
