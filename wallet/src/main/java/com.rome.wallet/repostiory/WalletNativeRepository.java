package com.rome.wallet.repostiory;


import com.rome.common.util.ResponseJSON;
import com.rome.wallet.entity.Cash;
import com.rome.wallet.util.OrderIdUtil;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.redis.RedisClient;
import org.mindrot.jbcrypt.BCrypt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Author:
 * Data:2019-06-18 15:25
 * Description:<>
 * @author sunYang
 */
public class WalletNativeRepository {
    private AsyncSQLClient postgreSQLClient;
    private Vertx vertx;
    private MailClient mailClient;
    private RedisClient redisClient;
    private WebClient webClient;


    public WalletNativeRepository(AsyncSQLClient postgreSQLClient,Vertx vertx,MailClient mailClient,RedisClient redisClient,WebClient webClient){
        this.postgreSQLClient = postgreSQLClient;
        this.vertx = vertx;
        this.mailClient = mailClient;
        this.redisClient = redisClient;
        this.webClient =webClient;
    }

    /**
     * transaction coin
     * @param coinType
     * @param amount
     * @param userAccount
     * @param toAccount
     * @param message
     * @param orderId
     * @param payPassword
     * @return Single
     * @Author:sunYang
     */
    public Single transactionCoin(String orderId,String coinType, String amount, String userAccount, String toAccount,String message,String payPassword){
        JsonArray selectAmount=new JsonArray().add(userAccount);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient,conn ->
            conn.rxQueryWithParams("select user_password from basic_account where user_account=?",new JsonArray().add(toAccount)).flatMap(resultSet->{
                if (resultSet.getRows().isEmpty()){
                    return Single.just("false0");
                }else {
                    return conn.rxQueryWithParams("select pay_password,balance from basic_account,v_balance_"+coinType+" where basic_account.user_account = v_balance_"+coinType+".user_account  and basic_account.user_account=?", selectAmount)
                        .flatMap(res -> {
                            System.out.println(res.getRows());
                            if ((res.getRows()).isEmpty()){
                                System.out.println(222);
                                return Single.just("false");
                            }else if (!BCrypt.checkpw(payPassword, res.getRows().get(0).getString("pay_password"))){
                                return Single.just("false1");
                            }else if (Double.parseDouble(res.getRows().get(0).getString("balance"))<Double.parseDouble(amount)||"null".equals(res.getRows().get(0).getString("balance"))){
                                return Single.just("false2");
                            } else {
                                JsonArray transactionCoin = new JsonArray().
                                    add(orderId).
                                    add(userAccount).
                                    add(toAccount).
                                    add(amount).
                                    add(message);
                                return conn.rxUpdateWithParams("INSERT INTO wallet_native_"+coinType+"(order_id,user_account,to_account,amount,order_time,message)  VALUES(?,?,?,?,floor(extract(epoch from now())),?)",transactionCoin).flatMap(result ->{
                                    System.out.println(result.getUpdated());
                                    if (result.getUpdated()>0){
                                        return Single.just("success");
                                    }else{
                                        return Single.error(new Exception("err"));
                                    }
                                });
                            }
                        });
                }
            })
        );
    }


    /**
     *   cancel Order
     * @param cashId
     * @param coinType
     * @param userAccount
     * @return Single
     * @Author: sunYang
     */
    public Single cancelOrder(String cashId,String coinType,String userAccount){
        JsonArray cancelOrder=new JsonArray().
            add(userAccount).
            add(userAccount).
            add(cashId);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient,conn ->
            conn.rxUpdateWithParams("UPDATE wallet_"+coinType+" SET validate=3 where user_account=?  AND (SELECT validate FROM wallet_"+coinType+" WHERE user_account =? AND order_id =?)=0",cancelOrder).flatMap(res->{
                System.out.println(res.getUpdated());
                if (res.getUpdated()>0){
                    return Single.just("success");
                }else{
                    return Single.just("false");
                }
            }));
    }


    /**
     * create cashOrder
     * @param userAccount
     * @param cash
     * @param coinType
     * @return Single
     * @Author: sunYang
     */
    public Single createCashOrder(String userAccount, Cash cash, String coinType){
        JsonArray sba=new JsonArray().
            add(userAccount);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient,conn ->
            conn.rxQueryWithParams("SELECT user_mail,user_phone,create_ip,using_ip,last_login_time,create_time,use_status,nick_name,longitude,latitude,user_type  from basic_account where user_account= ?",sba).flatMap(res ->{
                if (!res.getRows().isEmpty()){
                    System.out.println(111);
                    cash.setUserContext(res.getRows().get(0).toString());
                    Date date = new Date();
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00");
                    String nowTime = format.format(date);
                    Single<HttpResponse<Buffer>> req = webClient.post(80, "api.caodabi.com", "/v2/cash")
                        .putHeader("Authorization", "HRT Principal=bjnpmtq3q562oukvq8ig,Timestamp=" + nowTime + ",SecretKey=Z8IoCswSryuPHWnGhQix0vBlpJ67j4qaUbdNLtY9")
                        .rxSendJson(new JsonObject().
                            put("orderId",cash.getOrderID()).
                            put("toAddr", cash.getToAddr()).
                            put("coinPair", cash.getCoinPair()).
                            put("amount", (cash.getAmount())).
                            put("phone", cash.getPhone()).
                            put("userId", cash.getUserID()).
                            put("userContext", cash.getUserContext().toString()));
                    req.subscribe(result -> {
                        System.out.println(result.bodyAsJsonObject());
                        System.out.println("result:"+result.bodyAsJsonObject().getJsonArray("hash")+"98858252");
                        JsonArray walletJson=new JsonArray().
                            add(result.bodyAsJsonObject().getJsonArray("hash")).
                            add(cash.getOrderID()).
                            add(userAccount).
                            add(cash.getToAddr()).
                            add(cash.getAmount()).
                            add(cash.getCoinPair()).
                            add(cash.getUserContext().toString()).
                            add(nowTime).
                            add(cash.getMessage());
                       conn.rxUpdateWithParams("INSERT INTO wallet_"+coinType+"(hash,order_id,user_account,to_addr,amount,order_type,con_pair,user_context,order_time,message,validate values(?,?,?,?,?,0,?,?,?,?,0)) ",walletJson).flatMap(re->{
                            if (re.getUpdated()>0){
                                return Single.just("success");
                            }else{
                                return Single.just("false");
                           }
                       });
                    });
                }
                return Single.just("false");
                })

            );

    }



}
