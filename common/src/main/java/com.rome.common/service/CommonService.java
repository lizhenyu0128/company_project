package com.rome.common.service;

import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;

/**
 * Author:
 * Data:2019-06-15 14:16
 * Description:<>
 */
public interface CommonService {
    /**
     * check identity by jwt
     *
     * @param token
     * @return Single
     */
    Single checkIdentity(String token);

    /**
     * select profit
     * @return Single
     */
    Single selectProfit(Vertx vertx, io.vertx.reactivex.core.AbstractVerticle verticle, String path);




}
