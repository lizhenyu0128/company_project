package com.rome.uaa.service.uaa;

import com.alibaba.fastjson.JSONObject;
import com.rome.uaa.entity.UserSignUp;
import com.rome.uaa.entity.UserSingIn;
import com.rome.uaa.repository.AccountRepository;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author:
 * Data:2019-05-11 09:51
 * Description:<>
 *
 * @author lizhenyu
 */
public class UaaServiceImpl implements UaaService {
    final static Logger logger = LoggerFactory.getLogger(UaaServiceImpl.class);
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
    public Single userSignUp(UserSignUp userSignUp) {
        String encryptPassWord = BCrypt.hashpw(userSignUp.getUserPassword(), BCrypt.gensalt());
        JsonArray singUpParam = new JsonArray();
        singUpParam.add(userSignUp.getUserAccount())
            .add(encryptPassWord)
            .add(userSignUp.getUserMail())
            .add(userSignUp.getUserPhone())
            .add(userSignUp.getCreateIp())
            .add(userSignUp.getUsingIp())
            .add(userSignUp.getNickName())
            .add(userSignUp.getLongitude())
            .add(userSignUp.getLatitude());
        return accountRepository.userSignUp(singUpParam).doOnError(err ->
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
        if (!"basic".equals(u.getLoginType())) {
            if ("loginPhone".equals(u.getLoginType())) {
                u.setUserMail("null");
            } else if ("loginMail".equals(u.getLoginType())) {
                u.setUserPhone("null");
            }
        }
        return accountRepository.userLogin(u).doOnError(err ->
            logger.info(((Exception) err).getMessage()));
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
    public Completable getSmsCodeToLogin(String userPhone) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userPhone", userPhone);
        jsonObject.put("useType", "loginPhone");
        return accountRepository.getSmsCodeToLogin(jsonObject).doOnError(
            err -> logger.info(err.getMessage())
        );
    }

    @Override
    public Single sendEmail(String useType, String recipient) {
        JsonObject jsonObject = new JsonObject().put("useType", useType).put("recipient", recipient);
        return accountRepository.sendEmail(jsonObject).doOnError(err -> {

            logger.error(((Exception) err).getMessage());
        });
    }
}
