package com.rome.guild.service;

import com.rome.guild.repository.GuildRepository;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author asus
 */
public class GuidServiceImpl implements GuildService{
    final static Logger logger = LoggerFactory.getLogger(GuidServiceImpl.class);
    private GuildRepository guildRepository;
    private JWTAuth provide;
    public GuidServiceImpl(GuildRepository guildRepository, Vertx vertx) {

        this.guildRepository = guildRepository;
        //setAlgorithm算法 //setPublicKey密钥 //setSymmetric是否对成加密
        provide = JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                .setAlgorithm("HS256")
                .setPublicKey("woaimaozedong")
                .setSymmetric(true)));
    }




}
