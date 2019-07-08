package com.rome.basic.repository;

import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import io.vertx.reactivex.redis.RedisClient;
import sun.misc.BASE64Decoder;

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
        System.out.println(imageByte+userAccount);
        String path="E:\\company\\image\\headImage\\"+userAccount+".png";
        return SQLClientHelper.inTransactionSingle(postgreSQLClient,conn->
            conn.rxQueryWithParams("SELECT head_image FROM basic_account WHERE user_account= ?", new JsonArray().add(userAccount)).flatMap(res -> {
                System.out.println(444);
                String path0 = res.getRows().get(0).getString("head_image");
                    if (!path0.isEmpty() ||!"null".equals(path0)) {
                        vertx.fileSystem().rxDelete(path0).subscribe();
                    }
            return Single.just("success");
            }).flatMap(result->{
                System.out.println(result);
                if ("success".equals(result)){
                    if (imageByte == null){
                        return Single.just("false");
                    }
                    BASE64Decoder decoder = new BASE64Decoder();
                    try {
                        // Base64解码
                        byte[] b = decoder.decodeBuffer(imageByte);
                        for (int i = 0; i < b.length; ++i) {
                            if (b[i] < 0) {
                                b[i] += 256;
                            }
                        }
                        // 生成新的图片
                        OutputStream out = new FileOutputStream(path);
                        vertx.fileSystem().rxWriteFile(path,Buffer.buffer(b)).subscribe();
                        out.flush();
                        out.close();
                        return Single.just(path);
                    } catch (Exception e) {
                        return Single.just("false");
                    }
                }else{
                    return Single.just("false");
                }
            }));
    }





}
