package com.rome.common.service;

import com.rome.common.config.ProfitConfig;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author:
 * Data:2019-06-15 14:17
 * Description:<>
 *
 * @author Trump
 */
public class CommonServiceImpl implements CommonService {

    final static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);
    private JWTAuth provide;
    private ProfitConfig profitConfig;

    public CommonServiceImpl(ProfitConfig profitConfig,Vertx vertx) {

        this.profitConfig = profitConfig;
        //setAlgorithm算法 //setPublicKey密钥 //setSymmetric是否对成加密
        provide = JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                .setAlgorithm("HS256")
                .setPublicKey("woaimaozedong")
                .setSymmetric(true)));
    }

    @Override
    public Single checkIdentity(String token) {
        return Single.create(emit ->
            provide.authenticate(new JsonObject().put("jwt", token), auth -> {
                if (auth.succeeded()) {
                    emit.onSuccess(auth.result().principal());
                } else {
                    emit.onError(new RuntimeException("false"));
                }
            })).doOnError(err -> logger.info(err.getMessage()));
    }

    @Override
    public Single selectProfit(Vertx vertx, io.vertx.reactivex.core.AbstractVerticle verticle, String path){
        return profitConfig.selectProfit(vertx,verticle,path).doOnError(err ->{
            logger.info(((Exception) err).getMessage());
        });
    }

}
