package com.rome.basic.repository;

import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.redis.RedisClient;

import java.io.*;

/**
 * @author asus
 */
public class BasicRepository {
    private MailClient mailClient;
    private RedisClient redisClient;
    private AsyncSQLClient postgreSQLClient;
    private Vertx vertx;

    public BasicRepository(AsyncSQLClient postgreSQLClient, Vertx vertx, MailClient mailClient,
                             RedisClient redisClient) {
        this.mailClient = mailClient;
        this.redisClient = redisClient;
        this.postgreSQLClient = postgreSQLClient;
        this.vertx = vertx;
    }

    public Single setFile(String imageByte,String userAccount) {
        String path="E:\\company\\image\\headImage";
        if(imageByte.length() > 0){
            try {
                File file=new File(path,imageByte);
                FileOutputStream fos=new FileOutputStream(file);

                FileInputStream fis = new FileInputStream(imageByte);

                byte[] b = new byte[1024];
                int nRead = 0;
                while ((nRead = fis.read(b)) != -1) {
                    fos.write(b, 0, nRead);
                }
                fos.flush();
                fos.close();
                fis.close();
                return Single.just(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Single.just("false");
    }


}
