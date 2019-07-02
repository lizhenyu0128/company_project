package com.rome.admin.repository;

import com.rome.admin.util.OrderIdUtil;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.redis.RedisClient;

/**
 * @author asus
 */
public class AdminRepository {
    private AsyncSQLClient postgreSQLClient;
    private Vertx vertx;
    private MailClient mailClient;
    private RedisClient redisClient;
    private WebClient webClient;


    public AdminRepository(AsyncSQLClient postgreSQLClient, Vertx vertx, MailClient mailClient, RedisClient redisClient, WebClient webClient){
        this.postgreSQLClient = postgreSQLClient;
        this.vertx = vertx;
        this.mailClient = mailClient;
        this.redisClient = redisClient;
        this.webClient =webClient;
    }

    /**
     * inquire balance
     * @param userAccount
     * @return Single
     * @Author: sunYang
     */
    public Single inquireBalance(String userAccount){
        JsonArray viewPermissions = new JsonArray().add(userAccount);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn->
            conn.rxQueryWithParams("SELECT user_type from basic_account where user_account = ?",viewPermissions).flatMap(res->{
                if (2==res.getRows().get(0).getInteger("user_type")){
                    JsonArray checkBalance = new JsonArray();
                    return conn.rxQueryWithParams("select coalesce(v_balance_btc.user_account,v_balance_eos.user_account,v_balance_usdt.user_account,v_balance_eth.user_account) AS user_account, \n" +
                        "coalesce(v_balance_btc.balance, 0) as btc, \n" +
                        "coalesce(v_balance_eth.balance, 0) as eth,\n" +
                        "coalesce(v_balance_usdt.balance, 0) as usdt, \n" +
                        "coalesce(v_balance_eos.balance, 0) as eos\n" +
                        "from v_balance_btc  full outer join v_balance_eos on v_balance_btc.user_account=v_balance_eos.user_account \n" +
                        "full outer join v_balance_eth on v_balance_btc.user_account=v_balance_eth.user_account \n" +
                        "full outer join v_balance_usdt on v_balance_btc.user_account=v_balance_usdt.user_account",checkBalance).flatMap(result ->{
                        System.out.println(result.getRows());
                        if (!result.getRows().isEmpty()){
                            return Single.just(result.getRows());
                        }
                        return  Single.just("false");
                    } );
                }
                return  Single.just("false0");
            })
        );
    }

    /**
     *  inquire balance by userAccount
     * @param userAccount
     * @param inquireAccount
     * @return Single
     * @Author: sunYang
     */
    public Single inquireByUserAccount(String userAccount,String inquireAccount){
        JsonArray viewPermission = new JsonArray().add(userAccount);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient,conn->
            conn.rxQueryWithParams("SELECT user_type from basic_account where user_account = ?",viewPermission).flatMap(res->{
                if (2==res.getRows().get(0).getInteger("user_type")){
                    JsonArray checkBalance = new JsonArray().add(inquireAccount);
                    return conn.rxQueryWithParams("select coalesce(v_balance_btc.user_account,v_balance_eos.user_account,v_balance_usdt.user_account,v_balance_eth.user_account) AS user_account, \n" +
                        "coalesce(v_balance_btc.balance, 0) as btc, \n" +
                        "coalesce(v_balance_eth.balance, 0) as eth,\n" +
                        "coalesce(v_balance_usdt.balance, 0) as usdt, \n" +
                        "coalesce(v_balance_eos.balance, 0) as eos\n" +
                        "from v_balance_btc  full outer join v_balance_eos on v_balance_btc.user_account=v_balance_eos.user_account \n" +
                        "full outer join v_balance_eth on v_balance_btc.user_account=v_balance_eth.user_account \n" +
                        "full outer join v_balance_usdt on v_balance_btc.user_account=v_balance_usdt.user_account " +
                        "where coalesce(v_balance_btc.user_account,v_balance_eos.user_account,v_balance_usdt.user_account,v_balance_eth.user_account) = ? ",checkBalance).flatMap(result ->{
                        if (!result.getRows().isEmpty()){
                            return Single.just(result.getRows());
                        }
                        return  Single.just("false");
                    } );
                }
                return  Single.just("false0");
            })
        );
    }

    /**
     *  Add transaction record  to (wallet_native_btc,wallet_native_eos，wallet_native_eth，wallet_native_usdt)
     * @param userAccount
     * @param toAccount
     * @param fromAccount
     * @param amount
     * @param coinType
     * @param message
     * @return Single
     * @Author: sunYang
     */
    public Single addTransactionRecord(String userAccount, String toAccount, String fromAccount,String amount,String coinType,String message){

        JsonArray permission =new JsonArray().add(userAccount);
        return SQLClientHelper.inTransactionSingle(postgreSQLClient,conn->
            conn.rxQueryWithParams("SELECT user_type from basic_account where user_account = ?",permission).flatMap(res->{
                if (2==res.getRows().get(0).getInteger("user_type")){
                    return conn.rxQueryWithParams("SELECT balance from v_balance_"+coinType+" where user_account = ?",new JsonArray().add(fromAccount)).flatMap(result ->{
                        if (Double.parseDouble(result.getRows().get(0).getString("balance"))>Double.parseDouble(amount)){
                            JsonArray addOrder=new JsonArray().
                                add(OrderIdUtil.getOrderNo(userAccount)).
                                add(fromAccount).
                                add(toAccount).
                                add(amount).
                                add(message);
                            return conn.rxUpdateWithParams("INSERT INTO wallet_native_"+coinType+"(order_id,user_account,to_account,amount,order_time,message) values (?,?,?,?,floor(extract(epoch from now())),?) " ,addOrder).flatMap(resultSet->{
                                if (resultSet.getUpdated()>0){
                                    return Single.just("success");
                                }
                                return  Single.just("false0");
                            });
                        }
                        return Single.just("false1");
                    });
                }
                return Single.just("false2");
            })
        );
    }




}
