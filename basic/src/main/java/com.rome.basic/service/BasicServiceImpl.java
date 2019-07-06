package com.rome.basic.service;

import com.rome.basic.repository.BasicRepository;
import io.reactivex.Single;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author asus
 */
public class BasicServiceImpl implements BasicService{
    final static Logger logger = LoggerFactory.getLogger(BasicServiceImpl.class);
    private BasicRepository basicRepository;
    private JWTAuth provide;
    private Vertx vertx;

    public BasicServiceImpl(BasicRepository basicRepository, Vertx vertx) {

        this.basicRepository = basicRepository;
        this.vertx = vertx;
        //setAlgorithm算法 //setPublicKey密钥 //setSymmetric是否对成加密
        provide = JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                .setAlgorithm("HS256")
                .setPublicKey("woaimaozedong")
                .setSymmetric(true)));
    }

    @Override
    public  Single setFile(String imageByte,String userAccount){
        return basicRepository.setFile(imageByte,userAccount).doOnError(err ->
            logger.error(((Exception) err).getMessage()));
    }
}
