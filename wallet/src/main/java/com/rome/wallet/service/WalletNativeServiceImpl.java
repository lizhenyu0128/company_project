package com.rome.wallet.service;


import com.rome.wallet.repostiory.WalletRepository;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author:
 * Data:2019-06-03 16:26
 * Description:<>
 * @author Trump
 * 
 */
class WalletNativeServiceImpl implements WalletService {
    final static Logger logger = LoggerFactory.getLogger(WalletNativeServiceImpl.class);
    private WalletRepository walletRepository;
    private JWTAuth provide;
    private Vertx vertx;


}
