package com.rome.wallet.repostiory;


import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import io.vertx.reactivex.redis.RedisClient;

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


    public WalletNativeRepository(AsyncSQLClient postgreSQLClient,Vertx vertx,MailClient mailClient,RedisClient redisClient){
        this.postgreSQLClient = postgreSQLClient;
        this.vertx = vertx;
        this.mailClient = mailClient;
        this.redisClient = redisClient;
    }

    /**
     * transaction coin
     * @param coin
     * @param amount
     * @param userAccount
     * @param toAccount
     * @param message
     * @param orderId
     * @return Single
     * @Author:sunYang
     */
    public Single transactionCoin(String orderId,String coin, String amount, String userAccount, String toAccount,String message){

        JsonArray selectAmount=new JsonArray().add(userAccount);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient,conn ->
            conn.rxQueryWithParams("SELECT balance from v_balance_"+coin.toLowerCase().substring(0,3)+" where user_account= ?", selectAmount)
                .flatMap(res -> {
                    if (Double.parseDouble(res.getRows().get(0).getString("balance"))<Double.parseDouble(amount)){
                        return Single.just("false");
                    }else{
                       JsonArray transactionCoin = new JsonArray().
                            add(orderId).
                            add(userAccount).
                            add(toAccount).
                            add(amount).
                            add(message);
                        return conn.rxUpdateWithParams("INSERT INTO wallet_native_"+coin.toLowerCase().substring(0,3)+"(order_id,user_account,to_account,amount,order_time,message)  VALUES(?,?,?,?,floor(extract(epoch from now())),?)",transactionCoin).flatMap(result ->{
                            if (result.getUpdated()>0){
                                return Single.just("success");
                            }else{
                                return Single.error(new Exception("交易失败"));
                            }
                        });
                    }
                })
        );
    }




}
