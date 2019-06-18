package com.rome.common.config;

import io.grpc.ManagedChannel;
import io.vertx.grpc.VertxChannelBuilder;
import io.vertx.reactivex.core.Vertx;

/**
 * Author:
 * Data:2019-06-03 14:20
 * Description:<>
 *
 * @author Trump
 */
public class RpcConfig {

    public static ManagedChannel startRpcClient(Vertx vertx,String host, int port) {
        System.out.println("客户端启动"+port);
        return VertxChannelBuilder
            .forAddress(vertx.getDelegate(), host, port)
            .usePlaintext(true)
            .build();
    }
}
