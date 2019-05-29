package com.rome.uaa;


import com.alibaba.fastjson.JSON;
import com.rome.common.dbutil.PostgresqlPool;
import com.rome.common.smtp.SMTPConfig;
import com.rome.common.util.ResponseContent;
import com.rome.uaa.util.ValidatorUtil;
import com.rome.uaa.entity.UserSignUp;
import com.rome.uaa.entity.UserSingIn;
import com.rome.uaa.repository.AccountRepository;
import com.rome.uaa.service.uaa.UaaService;
import com.rome.uaa.service.uaa.UaaServiceImpl;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.*;
import io.vertx.reactivex.redis.RedisClient;

import java.util.logging.Logger;


/**
 * @author lizhenyu
 */
public class MainVerticle extends io.vertx.reactivex.core.AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {


        RedisClient redisClient;

        // 连接database
        AsyncSQLClient postgreSQLClient = PostgresqlPool.getInstance(vertx).getPostClient();

        // 生成 SMTP client
        MailClient mailClient = SMTPConfig.creatSMTPClient(vertx, 587, "smtp.qq.com", "879681805@qq.com", "urquajrzliwdbfjf");

        // 配置RedisClient
        redisClient = RedisClient.create(vertx, new JsonObject().put("port", 6379));


        // 配置传递
        UaaService uaaService = new UaaServiceImpl(new AccountRepository(postgreSQLClient, vertx, mailClient, redisClient), vertx);

        // Create a router object.
        Router router = Router.router(vertx);
        vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(config().getInteger("port", 8080)
            );


        // 路由业务
        // 增加一个处理器，将请求的上下文信息，放到RoutingContext中
        router.route().handler(BodyHandler.create());
        router.route("/api/user/*")
            .handler(routingContext -> {
                String token;
                token = routingContext.request().headers().get("token");
                if (token == null) {
                    ResponseContent.success(routingContext, 205, "false");
                }
                uaaService.checkIdentity(token).subscribe(res -> routingContext.next()
                    , err -> ResponseContent.success(routingContext, 205, "false"));
            });

        // protect router demo
        router.get("/api/user/sss").handler(routingContext -> ResponseContent.success(routingContext, 205, "来到以后方法"));


        // user sign up
        router.put("/api/signUp").handler(routingContext -> {
            //转成实体类
            UserSignUp userSignUp = JSON.parseObject(routingContext.getBodyAsJson().toString(), UserSignUp.class);
            //判断实体类
            ValidatorUtil.checkEntity(userSignUp);
            uaaService.userSignUp(userSignUp).subscribe(result ->
                ResponseContent.success(routingContext, 200, "success"), err -> {
                if ("1".equals(((Exception) err).getMessage())) {
                    ResponseContent.success(routingContext, 205, "please check you the size of the account or password");
                }
                ResponseContent.success(routingContext, 205, "false");
            });
        });

        // user sign in
        router.post("/api/login").handler(routingContext -> {
            //转成实体类
            UserSingIn userSingIn = JSON.parseObject(routingContext.getBodyAsJson().toString(), UserSingIn.class);
            System.out.println(userSingIn);
            uaaService.userLogin(userSingIn).subscribe(result -> ResponseContent.success(routingContext, 200, result)
                , error -> ResponseContent.success(routingContext, 205, "false"));
        });

        // login get SMS code
        router.get("/api/smsCode/phone/:phone").handler(routingContext -> {
            String userPhone = routingContext.request().getParam("phone");
            uaaService.getSmsCodeToLogin(userPhone).subscribe(() -> ResponseContent.success(routingContext, 200, "success")
                , error -> ResponseContent.success(routingContext, 205, "false"));
        });

        // send a e-mail demo QQ mail
        router.get("/api/email/useType/:useType/recipient/:recipient").handler(routingContext -> {
            String useType = routingContext.request().getParam("useType");
            String recipient = routingContext.request().getParam("recipient");

            uaaService.sendEmail(useType, recipient).subscribe(result -> ResponseContent.success(routingContext, 200, "success")
                , error -> ResponseContent.success(routingContext, 205, "false"));
        });
    }
}
