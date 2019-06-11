package com.rome.message;

import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;

/**
 * Author:
 * Data:2019-05-10 18:34
 * Description:<>
 * @author Trump
 */
public class Main {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    RxHelper.deployVerticle(vertx, new MainVerticle()).subscribe();
  }
}
