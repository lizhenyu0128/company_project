package com.rome.guild;


import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;

/**
 * @author asus
 */
public class GuildMain {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        RxHelper.deployVerticle(vertx, new GuildMainVerticle()).subscribe();
    }

}
