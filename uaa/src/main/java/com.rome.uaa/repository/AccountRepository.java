package com.rome.uaa.repository;


import com.rome.common.status.UaaStatus;
import com.rome.common.util.BouncyCastleCrypto;
import com.rome.uaa.entity.UserSingIn;
import com.rome.uaa.util.InvitationCodeUtil;
import io.github.novacrypto.bip32.ExtendedPrivateKey;
import io.github.novacrypto.bip32.networks.Bitcoin;
import io.github.novacrypto.bip39.SeedCalculator;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.redis.RedisClient;
import org.apache.commons.codec.binary.Hex;
import org.mindrot.jbcrypt.BCrypt;
import org.nightcode.bip39.Bip39;
import org.nightcode.bip39.EntropyDesc;
import org.nightcode.bip39.dictionary.Dictionary;
import org.nightcode.bip39.dictionary.EnglishDictionary;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.print.DocFlavor;

/**
 * Author:
 * Data:2019-05-13 13:10
 * Description:<>
 *
 * @author Trump
 */
public class AccountRepository {
    private AsyncSQLClient postgreSQLClient;
    private Vertx vertx;
    private MailClient mailClient;
    private RedisClient redisClient;
    private WebClient webClient;

    public AccountRepository(AsyncSQLClient postgreSQLClient, Vertx vertx, MailClient mailClient, RedisClient redisClient, WebClient webClient) {
        this.postgreSQLClient = postgreSQLClient;
        this.vertx = vertx;
        this.mailClient = mailClient;
        this.redisClient = redisClient;
        this.webClient = webClient;
    }

    /**
     * @param singUpParam
     * @param invitationCode
     * @return
     * @description user sign up
     * 1 判断是否插入，只能看error面
     * 2 flatMap咋样返回常数
     * 3 用户id的生成规则
     */
    public Single userSignUp(JsonArray singUpParam, String invitationCode) {
        String code = InvitationCodeUtil.generateShortUuid();
        JsonArray memberRelation = new JsonArray();
        return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn ->
            conn.rxQueryWithParams("SELECT user_password from basic_account where user_account=?", new JsonArray().add(singUpParam.getString(0))).flatMap(resultSet -> {
                if (!resultSet.getRows().isEmpty()) {
                    return Single.just("false0");
                } else {
                    return conn.rxQueryWithParams("SELECT uid,level FROM member_relation WHERE invitation_code = ?", new JsonArray().add(invitationCode)).flatMap(result -> {
                        if (result.getRows().isEmpty()) {
                            if ("".equals(invitationCode)) {
                                memberRelation.add(singUpParam.getString(0)).
                                    add(0).
                                    add(0).
                                    add(code);
                            } else {
                                return Single.just("false");
                            }
                        } else {
                            memberRelation.add(singUpParam.getString(0)).
                                add(result.getRows().get(0).getInteger("level")).
                                add(result.getRows().get(0).getString("uid")).
                                add(code);
                        }
                        return conn.rxUpdateWithParams("INSERT INTO basic_account (user_account,user_password,user_mail,user_phone,create_ip,using_ip,last_login_time,create_time,use_status,nick_name,longitude,latitude,user_type) VALUES (?,?,?,?,?,?, floor(extract(epoch from now())), floor(extract(epoch from now())),1,?,?,?,1)", singUpParam).flatMap(reu -> {
                            if (reu.getUpdated() > 0) {
                                return conn.rxUpdateWithParams("INSERT INTO member_relation (uid,level,puid,invitation_code) VALUES (?,?,?,?)", memberRelation).flatMap(rest -> {
                                    if (rest.getUpdated() > 0) {
                                        return Single.just("success");
                                    }
                                    return Single.just("false");
                                });
                            }
                            return Single.just("false");
                        });
                    });
                }
            })
        );
    }

    /**
     * login_type phone mail basic
     *
     * @param u
     * @param userType
     * @return
     * @description login_type phone mail basic
     */
    public Single userLogin(UserSingIn u, String userType) {
        if (!"basic".equals(u.getLoginType())) {
            return userLoginByCode(u, userType);
        }
        JsonArray loginParam = new JsonArray();
        loginParam.add(u.getUserAccount())
            .add(Integer.parseInt(userType));
        System.out.println(loginParam);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn -> conn.rxQueryWithParams(
            "SELECT a.user_account,a.use_status,a.user_mail,a.user_phone,a.user_password,b.identity_id FROM " +
                "basic_account a LEFT JOIN \"identity\" b ON a.user_type=b.identity_id WHERE " +
                "user_account=? AND user_type= ?", loginParam)
            .flatMap(res -> {
                if (res.getRows().isEmpty()) {
                    return Single.error(new Exception(UaaStatus.SIGN_IN_USER));
                }
                JsonObject loginView = res.getRows().get(0);
                System.out.println(loginView);
                if (BCrypt.checkpw(u.getUserPassword(), loginView.getString("user_password"))) {
                    System.out.println("用户存在密码正确");
                    //更新登陆状态
                    loginParam.clear();
                    loginParam.add(u.getUsingIp());
                    loginParam.add(u.getLongitude());
                    loginParam.add(u.getLatitude());
                    loginParam.add(loginView.getValue("user_account"));
                    return conn.rxUpdateWithParams("UPDATE basic_account SET using_ip=?,last_login_time=" +
                        "floor(extract(epoch from now())),longitude=?,latitude=? WHERE user_account=? ", loginParam)
                        .flatMap(updateResult -> {
                            if (updateResult.getUpdated() > 0) {
                                //更新jwt返回
                                JsonObject jwtParam = new JsonObject()
                                    .put("user_account", loginView.getValue("user_account"))
                                    //自 定义参数
                                    .put("identity_id", loginView.getValue("identity_id"));
                                System.out.println(jwtParam + "库卡技术");
                                return sendToken(jwtParam);

                            }
                            return Single.error(new Exception("false"));
                        });
                } else {
                    return Single.error(new Exception(UaaStatus.SIGN_IN_PASSWORD_ERROR));
                }
            }));
    }

    private Single<String> sendToken(JsonObject object) {
        System.out.println(object);
        //   jwt 需要传userAccount identityId
        JWTAuth provide = JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                // 算法
                .setAlgorithm("HS256")
                // 密钥
                .setPublicKey("woaimaozedong")
                // 是否对成加密
                .setSymmetric(true)));
        String token = provide.generateToken(
            // 自定义参数
            object,
            new JWTOptions()
                // 签发人
                .setIssuer("王二麻子")
                .setAudience(null)
                // 过期时间
                .setExpiresInSeconds(600000)
                .setNoTimestamp(false));
        return Single.just(token);
    }

    /**
     * @param u
     * @param userType
     * @return
     * @description login_type phone mail basic
     */
    private Single userLoginByCode(UserSingIn u, String userType) {
        //查缓存
        String code = u.getVerificationCode();
        String loginType = u.getLoginType();
        String phoneOrMail = "mail".equals(loginType) ? u.getUserMail() : u.getUserPhone();
        System.out.println("使用" + u.getLoginType() + "登陆");
        System.out.println(phoneOrMail + loginType);
        return redisClient.rxGet(phoneOrMail + "login").filter((resData) -> {
            if (resData.equals(code)) {
                redisClient.rxDel(phoneOrMail + "login").subscribe();
                System.out.println("哈哈哈哈哈");
                return true;
            } else {
                throw new Error(UaaStatus.SIGN_IN_CODE_ERROR);
            }
        }).flatMapSingle(resData -> SQLClientHelper.inTransactionSingle(postgreSQLClient, conn -> {
            System.out.println("萨达萨达");
            JsonArray loginParam = new JsonArray()
                .add(u.getUsingIp())
                .add(u.getLongitude())
                .add(u.getLatitude())
                .add(phoneOrMail)
                .add(phoneOrMail)
                .add(Integer.parseInt(userType));
            System.out.println(loginParam);
            return conn.rxUpdateWithParams("UPDATE basic_account SET using_ip=?,last_login_time=" +
                "floor(extract(epoch from now())),longitude=?,latitude=? WHERE user_phone=? or user_mail=?", loginParam)
                .filter((updateResult) -> updateResult.getUpdated() > 0)
                .flatMapSingle(updateResult ->
                    conn.rxQueryWithParams("SELECT user_account,b.identity_id FROM " +
                            "basic_account a LEFT JOIN \"identity\" b ON a.user_type = b.identity_id WHERE " +
                            "a.user_phone = ? or a.user_mail = ?  AND user_type= ? ",
                        new JsonArray().add(phoneOrMail).add(phoneOrMail))
                        .flatMap(queryRes -> {
                            JsonObject jwtObj = queryRes.getRows().get(0);
                            System.out.println(jwtObj + "哈哈哈");
                            return sendToken(jwtObj);
                        }));
        }));
    }

    /**
     * @param newPassword
     * @param userAccount
     * @return Single
     * @description reset password
     * @Author: sunYang
     */
    public Single resetPassword(String userAccount, String newPassword, String code, String content) {
        System.out.println(content + "resetPassword");
        System.out.println(content + userAccount);
        String encryptPassWord = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        JsonArray resetPassword = new JsonArray().
            add(encryptPassWord).
            add(userAccount);
        return redisClient.rxGet(content + "resetPassword").flatMapSingle(resData -> {
            if (resData.equals(code)) {
                redisClient.rxDel(content + "resetPassword").subscribe();
                return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn ->
                    conn.rxUpdateWithParams("UPDATE basic_account SET user_password=? WHERE user_account=?", resetPassword)
                        .flatMap(updateResult -> {
                            if (updateResult.getUpdated() > 0) {
                                return Single.just(true);
                            } else {
                                return Single.just(false);
                            }
                        }));
            } else {
                redisClient.rxDel(content + "resetPassword").subscribe();
                return Single.just(false);
            }
        });
    }

    /**
     * @param userAccount
     * @param payPassword
     * @param userPassword
     * @return Single
     * @description set payPassword
     * @Author: sunYang
     */
    public Single setPayPassword(String userAccount, String payPassword, String userPassword) {
        JsonArray queryPaymentPassword = new JsonArray().
            add(userAccount);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn ->
            conn.rxQuerySingleWithParams("SELECT user_password ,pay_password FROM basic_account WHERE" +
                " user_account = ? ", queryPaymentPassword).flatMapSingle(res -> {
                if (("").equals(res.getString(1)) || (res.getString(1)) == null) {
                    if (BCrypt.checkpw(userPassword, res.getString(0))) {
                        JsonArray setPayPassword = new JsonArray().
                            add(BCrypt.hashpw(payPassword, BCrypt.gensalt())).
                            add(userAccount);
                        return conn.rxUpdateWithParams("UPDATE basic_account SET pay_password=? where user_account=?", setPayPassword)
                            .flatMap(result -> {
                                if (result.getUpdated() > 0) {
                                    return Single.just("success");
                                } else {
                                    return Single.error(new Exception("error"));
                                }
                            });
                    } else {
                        return Single.just("false");
                    }
                } else {
                    return Single.just("false0");
                }
            })
        );
    }

    /**
     * @param userAccount
     * @param payPassword
     * @param newPayPassword
     * @return Single
     * @description update payPassword
     * @Author: sunYang
     */
    public Single updatePayPassword(String userAccount, String payPassword, String newPayPassword) {
        System.out.println(44);
        System.out.println(userAccount + "/" + payPassword + "/" + newPayPassword);
        JsonArray selectPayPassword = new JsonArray().
            add(userAccount);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn ->
            conn.rxQuerySingleWithParams("SELECT pay_password FROM basic_account WHERE user_account= ?", selectPayPassword)
                .flatMapSingle(res -> {
                    if (BCrypt.checkpw(payPassword, res.getString(0))) {
                        System.out.println(BCrypt.hashpw(newPayPassword, BCrypt.gensalt()));
                        JsonArray updatePayPassword = new JsonArray().
                            add(BCrypt.hashpw(newPayPassword, BCrypt.gensalt())).
                            add(userAccount);
                        System.out.println(updatePayPassword);
                        return conn.rxUpdateWithParams("UPDATE basic_account SET pay_password=? where user_account=?", updatePayPassword)
                            .flatMap(result -> {
                                System.out.println(result.getUpdated());
                                if (result.getUpdated() > 0) {
                                    return Single.just("success");
                                } else {
                                    return Single.error(new Exception("error"));
                                }
                            });
                    } else {
                        return Single.just("false");
                    }
                })
        );
    }

    /**
     * update nickName
     *
     * @param userAccount
     * @param nickName
     * @return Single
     * @Author: sunYang
     */
    public Single updateNickName(String userAccount, String nickName) {
        System.out.println(userAccount + nickName);
        JsonArray updateNickName = new JsonArray().
            add(nickName).
            add(userAccount);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn ->
            conn.rxUpdateWithParams("UPDATE basic_account SET nick_name=? where user_account=? ", updateNickName).flatMap(res -> {
                if (res.getUpdated() > 0) {
                    return Single.just("success");
                }
                return Single.just("false");
            })
        );
    }

    /**
     * set headImage
     *
     * @param userAccount
     * @param headImage
     * @return Single
     * @Author: sunYang
     */
    public Single setHeadImage(String userAccount, String headImage) {
        System.out.println(1111);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn ->
            conn.rxQueryWithParams("SELECT head_image FROM basic_account WHERE user_account= ?", new JsonArray().add(userAccount)).flatMap(res -> {
                String headUrl = res.getRows().get(0).getString("head_image");
                String path = "E:\\company\\image\\headImage\\" + userAccount + "." + (headImage.substring((headImage.lastIndexOf(".") + 1), headImage.length()));
                System.out.println(res.getRows().get(0).getString("head_image"));
               if (!res.getRows().get(0).getString("head_image").isEmpty()||"null".equals(res.getRows().get(0).getString("head_image"))) {
                    vertx.fileSystem().rxDelete(headUrl).subscribe();
                }
                JsonArray setHeadImage = new JsonArray().
                    add(path).
                    add(userAccount);
                return vertx.fileSystem().rxCopy(headImage, path).andThen(
                    conn.rxUpdateWithParams("UPDATE basic_account SET head_image=? where user_account=? ", setHeadImage).flatMap(result -> {
                        if (result.getUpdated() > 0) {
                            return Single.just("success");
                        }
                        return Single.just("false");
                    }));
            }));
    }

    public Single getMnemonics(String userAccount) {
        Dictionary dictionary = EnglishDictionary.instance();
        Bip39 bip39 = new Bip39(dictionary);
        byte[] entropy = bip39.generateEntropy(EntropyDesc.ENT_128);
        String mnemonics = bip39.createMnemonic(entropy);
        //2. 由助记词得到种子
        byte[] seed = new SeedCalculator().calculateSeed(mnemonics, "");
        ExtendedPrivateKey rootPrivateKey = ExtendedPrivateKey.fromSeed(seed, Bitcoin.MAIN_NET);
        //私钥
        byte[] pvc = rootPrivateKey.getKey();
        BouncyCastleCrypto bcc = new BouncyCastleCrypto();
        byte[] res = bcc.sign(userAccount.getBytes(StandardCharsets.UTF_8), pvc);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn ->
            conn.rxUpdateWithParams(" UPDATE " +
                    "basic_account  SET pk_sign = ? " +
                    "WHERE user_account  = ?",
                new JsonArray().add(res).add(userAccount)).flatMap(result -> {
                if (result.getUpdated() > 0) {
                    return Single.just(mnemonics);
                } else {
                    return Single.error(new Exception("No such user"));
                }
            })
        );
    }
}












