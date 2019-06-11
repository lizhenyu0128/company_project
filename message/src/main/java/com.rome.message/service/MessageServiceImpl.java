package com.rome.message.service;

import com.alibaba.fastjson.JSONObject;
import com.rome.common.constant.UaaConsts;
import com.rome.message.repository.AccountRepository;
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
 * Data:2019-06-03 16:26
 * Description:<>
 * @author Trump
 */
public class MessageServiceImpl implements MessageService {
    final static Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private AccountRepository accountRepository;
    private JWTAuth provide;
    private Vertx vertx;

    public MessageServiceImpl(AccountRepository repository, Vertx vertx) {

        this.accountRepository = repository;
        this.vertx = vertx;
        //setAlgorithm算法 //setPublicKey密钥 //setSymmetric是否对成加密
        provide = JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                .setAlgorithm("HS256")
                .setPublicKey("woaimaozedong")
                .setSymmetric(true)));
    }

    @Override
    public Single getVerificationCode(String messageType, String useType, String content) {
        System.out.println("asdsadsadasdads+++++");
        if (UaaConsts.MESSAGE_TYPE_MAIL.equals(messageType)) {
            return sendEmail(useType, content);
        } else {
            return getSmsCodeToLogin(useType, content);
        }

    }

    private Single getSmsCodeToLogin(String useType, String userPhone) {
        return accountRepository.sendSMS(useType, userPhone).doOnError(err ->
            logger.error(((Exception) err).getMessage()));
    }


    private Single sendEmail(String useType, String recipient) {
        return accountRepository.sendEmail(useType,recipient).doOnError(err ->
            logger.error(((Exception) err).getMessage()));
    }
}
