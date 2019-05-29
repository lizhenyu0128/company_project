package com.rome.message;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class MainVerticle extends io.vertx.reactivex.core.AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    System.out.println("wowowoowowo");
    vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(8888, http -> {
      if (http.succeeded()) {
        startFuture.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startFuture.fail(http.cause());
      }
    });
  }
}
