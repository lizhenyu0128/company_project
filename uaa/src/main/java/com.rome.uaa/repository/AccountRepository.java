package com.rome.uaa.repository;


import com.rome.uaa.entity.BasicUserInfo;
import com.rome.uaa.entity.UserSingIn;
import io.reactivex.Maybe;
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
import io.vertx.reactivex.redis.RedisClient;
import org.mindrot.jbcrypt.BCrypt;
import org.mvel2.templates.TemplateRuntimeError;

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

    public AccountRepository(AsyncSQLClient postgreSQLClient, Vertx vertx, MailClient mailClient,
                             RedisClient redisClient) {
        this.postgreSQLClient = postgreSQLClient;
        this.vertx = vertx;
        this.mailClient = mailClient;
        this.redisClient = redisClient;
    }

    /**
     * @param singUpParam
     * @return
     * @description user sign up
     * 1 判断是否插入，只能看error面
     * 2 flatMap咋样返回常数
     * 3 用户id的生成规则
     */
    public Single userSignUp(JsonArray singUpParam) {
        System.out.println(singUpParam);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn ->
            conn.rxUpdateWithParams("INSERT INTO basic_account (user_account,user_password,user_mail,user_phone,user_type,create_ip,using_ip,last_login_time,create_time,use_status,nick_name,longitude,latitude)VALUES(?,?,?,?,2,?,?," +
                "floor(extract(epoch from now())), floor(extract(epoch from now())),1,?,?,?)", singUpParam)
                .map(singUpRes ->
                    "success sign up"));
    }

    /**
     * login_type phone mail basic
     *
     * @param u
     * @return
     * @description login_type phone mail basic
     */
    public Single userLogin(UserSingIn u) {
        if (!"basic".equals(u.getLoginType())) {
            return userLoginByCode(u);
        }
        JsonArray loginParam = new JsonArray();
        loginParam.add(u.getUserAccount());
        System.out.println(loginParam);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn -> conn.rxQueryWithParams(
            "SELECT a.user_account,a.use_status,a.user_mail,a.user_phone,a.user_password,b.identity_id FROM " +
                "basic_account a LEFT JOIN \"identity\" b ON a.user_type=b.identity_id WHERE " +
                "user_account=?", loginParam)
            .flatMap(res -> {
                if (res.getRows().isEmpty()) {
                    return Single.error(new Exception("用户不存在"));
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
                        "floor(extract(epoch from now())),longitude=?,latitude=? WHERE user_account=?", loginParam)
                        .flatMap(updateResult -> {
                            if (updateResult.getUpdated() > 0) {
                                //更新jwt返回
                                JsonObject jwtParam = new JsonObject()
                                    .put("user_account", loginView.getValue("user_account"))
                                    //自 定义参数
                                    .put("identity_id", loginView.getValue("identity_id"));
                                System.out.println(jwtParam+"库卡技术");
                                return sendToken(jwtParam);

                            }
                            return Single.error(new Exception("false"));
                        });
                } else {
                    return Single.error(new Exception("密码错误"));
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
     * @return
     * @description login_type phone mail basic
     */
    private Single userLoginByCode(UserSingIn u) {
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
                throw new Error("验证码错误");
            }
        }).flatMapSingle(resData -> SQLClientHelper.inTransactionSingle(postgreSQLClient, conn -> {
            System.out.println("萨达萨达");
            JsonArray loginParam = new JsonArray();
            loginParam.add(u.getUsingIp());
            loginParam.add(u.getLongitude());
            loginParam.add(u.getLatitude());
            loginParam.add(phoneOrMail);
            loginParam.add(phoneOrMail);
            System.out.println(loginParam);
            return conn.rxUpdateWithParams("UPDATE basic_account SET using_ip=?,last_login_time=" +
                "floor(extract(epoch from now())),longitude=?,latitude=? WHERE user_phone=? or user_mail=?", loginParam)
                .filter((updateResult) -> updateResult.getUpdated() > 0)
                .flatMapSingle(updateResult ->
                    conn.rxQueryWithParams("SELECT user_account,b.identity_id FROM " +
                            "basic_account a LEFT JOIN \"identity\" b ON a.user_type = b.identity_id WHERE " +
                            "a.user_phone = ? or a.user_mail = ?",
                        new JsonArray().add(phoneOrMail).add(phoneOrMail))
                        .flatMap(queryRes -> {
                            JsonObject jwtObj = queryRes.getRows().get(0);
                            System.out.println(jwtObj+"哈哈哈");
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
        String encryptPassWord =  BCrypt.hashpw(newPassword, BCrypt.gensalt());
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
    public Single setPayPassword(String userAccount,String payPassword,String userPassword){
        JsonArray selPayPassword=new JsonArray().
            add(userAccount);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient,conn ->
            conn.rxQuerySingleWithParams("SELECT user_password ,pay_password FROM basic_account WHERE" +
                " user_account = ? ",selPayPassword).flatMapSingle(res ->{
                System.out.println(res.getString(1)+"74777");
                if (("").equals(res.getString(1))||res.getString(1).equals("NULL")) {
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
                }else{
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
    public Single updatePayPassword(String userAccount,String payPassword,String newPayPassword){
        System.out.println(userAccount+"/"+payPassword+"/"+newPayPassword);
        JsonArray selectPayPassword=new JsonArray().
            add(userAccount);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient,conn ->
            conn.rxQuerySingleWithParams("SELECT pay_password FROM basic_account WHERE user_account= ?",selectPayPassword)
                            .flatMapSingle(res ->{
                                if (BCrypt.checkpw(payPassword,res.getString(0))){
                                    System.out.println(BCrypt.hashpw(newPayPassword, BCrypt.gensalt()));
                                    JsonArray updatePayPassword=new JsonArray().
                                       add(BCrypt.hashpw(newPayPassword,BCrypt.gensalt())).
                                       add(userAccount);
                                    System.out.println(updatePayPassword);
                                    return conn.rxUpdateWithParams("UPDATE basic_account SET pay_password=? where user_account=?",updatePayPassword)
                                       .flatMap(result ->{
                                       System.out.println(result.getUpdated());
                                       if (result.getUpdated() > 0){
                                           return Single.just("success");
                                       }else{
                                           return Single.error(new Exception("error"));
                                       }
                                   });
                               }else{
                                   return Single.just("false");
                               }
                })
        );
    }




}



