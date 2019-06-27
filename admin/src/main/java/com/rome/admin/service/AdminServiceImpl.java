package com.rome.admin.service;

import com.rome.wallet.repostiory.AdminRepository;
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
public class AdminServiceImpl implements AdminService{

    final static Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    private AdminRepository adminRepository;
    private JWTAuth provide;
    public AdminServiceImpl(AdminRepository adminRepository, Vertx vertx) {

        this.adminRepository = adminRepository;
        //setAlgorithm算法 //setPublicKey密钥 //setSymmetric是否对成加密
        provide = JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                .setAlgorithm("HS256")
                .setPublicKey("woaimaozedong")
                .setSymmetric(true)));
    }


    @Override
    public Single inquireBalance(String userAccount){
        return adminRepository.inquireBalance(userAccount).doOnError(err -> {
            logger.info(((Exception) err).getMessage());
        });
    }

    @Override
    public  Single inquireByUserAccount(String userAccount,String inquireAccount){
        return adminRepository.inquireByUserAccount(userAccount,inquireAccount).doOnError(err -> {
            logger.info(((Exception) err).getMessage());
        });
    }

    @Override
    public Single addTransactionRecord(String userAccount, String toAccount, String fromAccount,String amount,String coinType,String message){
        return adminRepository.addTransactionRecord(userAccount,toAccount,fromAccount,amount,coinType,message).doOnError(err -> {

            logger.info(((Exception) err).getMessage());
        });
    }



}
