package com.rome.guild.repository;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.redis.RedisClient;

/**
 * @author asus
 */
public class GuildRepository {

    private AsyncSQLClient postgreSQLClient;
    private Vertx vertx;
    private MailClient mailClient;
    private RedisClient redisClient;
    private WebClient webClient;


    public GuildRepository(AsyncSQLClient postgreSQLClient, Vertx vertx, MailClient mailClient, RedisClient redisClient, WebClient webClient){
        this.postgreSQLClient = postgreSQLClient;
        this.vertx = vertx;
        this.mailClient = mailClient;
        this.redisClient = redisClient;
        this.webClient =webClient;
    }
}
