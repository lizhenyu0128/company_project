package com.rome.message;

import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;

/**
 * Author:
 * Data:2019-05-10 18:34
 * Description:<>
 */
public class M1essageMainVertx {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    RxHelper.deployVerticle(vertx, new MainVerticle()).subscribe();
  }
}
