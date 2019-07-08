package com.rome.uaa.service;

import com.rome.uaa.entity.UserSignUp;
import com.rome.uaa.entity.UserSingIn;
import com.rome.uaa.repository.AccountRepository;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author:
 * Data:2019-05-11 09:51
 * Description:<>
 *
 * @author Trump
 */
public class UaaServiceImpl implements UaaService {
    private final static Logger logger = LoggerFactory.getLogger(UaaServiceImpl.class);
    private AccountRepository accountRepository;
    private JWTAuth provide;

    public UaaServiceImpl(AccountRepository repository, Vertx vertx) {

        this.accountRepository = repository;
        //setAlgorithm算法 //setPublicKey密钥 //setSymmetric是否对成加密
        provide = JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                .setAlgorithm("HS256")
                .setPublicKey("woaimaozedong")
                .setSymmetric(true)));
    }


    @Override
    public Single userSignUp(UserSignUp userSignUp, String invitationCode) {
        String encryptPassWord = BCrypt.hashpw(userSignUp.getUserPassword(), BCrypt.gensalt());
        String encryptPayPassWord = BCrypt.hashpw(userSignUp.getPayPassword(), BCrypt.gensalt());
        JsonArray singUpParam = new JsonArray()
            .add(userSignUp.getUserAccount())
            .add(encryptPassWord)
            .add(encryptPayPassWord)
            .add(userSignUp.getUserMail())
            .add(userSignUp.getUserPhone())
            .add(userSignUp.getCreateIp())
            .add(userSignUp.getUsingIp())
            .add(userSignUp.getNickName())
            .add(userSignUp.getLongitude())
            .add(userSignUp.getLatitude())
            .add(userSignUp.getLabel());
        return accountRepository.userSignUp(singUpParam, invitationCode).doOnError(err ->
            logger.info(((Exception) err).getMessage()));
    }

    /**
     * //  {
     * //    "loginType":"loginPhone",
     * //    "smsCode":"612269",
     * //    "userPhone":"15524835211",
     * //    "usingIp":"1.1.1.1",
     * //    "lastLoginTime":999999,
     * //    "longitude":"231.啊啊啊",
     * //    "latitude":"123.啊啊啊"
     * //  }
     *
     * @param u
     * @return
     */
    @Override
    public Single userLogin(UserSingIn u) {
        return accountRepository.userLogin(u).doOnError(err ->
            logger.info(((Exception) err).getMessage()));
    }

    @Override
    public Single resetPassword(String userAccount, String newPassword, String code, String content) {
        return accountRepository.resetPassword(userAccount, newPassword, code, content).doOnError(err -> {
            logger.info(((Exception) err).getMessage());
        });

    }

    @Override
    public Single updatePayPassword(String userAccount, String payPassword, String newPayPassword) {
        return accountRepository.updatePayPassword(userAccount, payPassword, newPayPassword).doOnError(err -> {
        });
    }

    @Override
    public Single getMnemonics(String userAccount) {
        return accountRepository.getMnemonics(userAccount).doOnError(
            err -> logger.info(((Exception) err).getMessage()));
    }

    @Override
    public Single updateNickName(String userAccount, String nickName) {
        return accountRepository.updateNickName(userAccount,nickName).doOnError(err ->{
            logger.info(((Exception) err).getMessage());
        });
    }

    @Override
    public Single setHeadImage(String userAccount,String headImage){
        return accountRepository.setHeadImage(userAccount,headImage).doOnError(err ->{
            logger.info(((Exception) err).getMessage());
        });
    }

}
