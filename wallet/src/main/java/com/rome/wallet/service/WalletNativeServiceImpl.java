package com.rome.wallet.service;


import com.rome.wallet.repostiory.WalletNativeRepository;
import com.rome.wallet.repostiory.WalletRepository;
import io.reactivex.Single;
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
 * 
 */
public class WalletNativeServiceImpl implements WalletNativeService {
    final static Logger logger = LoggerFactory.getLogger(WalletNativeServiceImpl.class);
    private WalletNativeRepository walletNativeRepository;
    private JWTAuth provide;
    public WalletNativeServiceImpl(WalletNativeRepository repository, Vertx vertx) {

        this.walletNativeRepository = repository;
        //setAlgorithm算法 //setPublicKey密钥 //setSymmetric是否对成加密
        provide = JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                .setAlgorithm("HS256")
                .setPublicKey("woaimaozedong")
                .setSymmetric(true)));
    }


  @Override
  public Single transactionCoin(String orderId,String coin, String amount, String userAccount, String toAccount,String message){
      System.out.println(8888);
      return walletNativeRepository.transactionCoin(orderId, coin,  amount,  userAccount,  toAccount , message).doOnError(err ->{
            logger.info(((Exception) err).getMessage());
        });
  }


}
