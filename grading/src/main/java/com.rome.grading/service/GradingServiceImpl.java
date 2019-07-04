package com.rome.grading.service;

import com.rome.grading.repository.GradingRepository;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author asus
 */
public class GradingServiceImpl implements GradingService{
    final static Logger logger = LoggerFactory.getLogger(GradingServiceImpl.class);
    private GradingRepository gradingRepository;
    private JWTAuth provide;
    public GradingServiceImpl(GradingRepository gradingRepository, Vertx vertx) {

        this.gradingRepository = gradingRepository;
        //setAlgorithm算法 //setPublicKey密钥 //setSymmetric是否对成加密
        provide = JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                .setAlgorithm("HS256")
                .setPublicKey("woaimaozedong")
                .setSymmetric(true)));
    }
}
