package com.rome.wallet.repostiory;


import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.redis.RedisClient;

/**
 * Author:
 * Data:2019-06-18 15:25
 * Description:<>
 */
public class WalletRepository {
    private AsyncSQLClient postgreSQLClient;
    private Vertx vertx;
    private MailClient mailClient;
    private RedisClient redisClient;
    private WebClient webClient;

    public WalletRepository(AsyncSQLClient postgreSQLClient, Vertx vertx, MailClient mailClient,
                            RedisClient redisClient, WebClient webClient) {
        this.postgreSQLClient = postgreSQLClient;
        this.vertx = vertx;
        this.mailClient = mailClient;
        this.redisClient = redisClient;
        this.webClient = webClient;
    }

}
