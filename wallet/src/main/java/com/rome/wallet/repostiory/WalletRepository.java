package com.rome.wallet.repostiory;


import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.redis.RedisClient;

/**
 * Author:
 * Data:2019-06-18 15:25
 * Description:<>
 */
public class WalletRepository {
    private MailClient mailClient;
    private RedisClient redisClient;

    public WalletRepository(AsyncSQLClient postgreSQLClient, Vertx vertx, MailClient mailClient,
                             RedisClient redisClient) {
        this.mailClient = mailClient;
        this.redisClient = redisClient;
    }

}
