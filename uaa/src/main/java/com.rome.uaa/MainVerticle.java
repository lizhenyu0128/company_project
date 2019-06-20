package com.rome.uaa;


import com.alibaba.fastjson.JSON;
import com.rome.common.config.InitConfig;
import com.rome.common.config.RpcConfig;
import com.rome.common.constant.UaaConsts;
import com.rome.common.dbutil.PostgresqlPool;
import com.rome.common.entity.Token;
import com.rome.common.rpc.message.VerificationCodeReq;
import com.rome.common.rpc.message.VerificationCodeServiceGrpc;
import com.rome.common.config.SMTPConfig;
import com.rome.common.service.CommonService;
import com.rome.common.service.CommonServiceImpl;
import com.rome.common.util.ResponseJSON;
import com.rome.uaa.util.ValidatorUtil;
import com.rome.uaa.entity.UserSignUp;
import com.rome.uaa.entity.UserSingIn;
import com.rome.uaa.repository.AccountRepository;
import com.rome.uaa.service.UaaService;
import com.rome.uaa.service.UaaServiceImpl;
import io.grpc.ManagedChannel;
import io.reactivex.Completable;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.*;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.spi.ServiceImporter;
import io.vertx.servicediscovery.consul.ConsulServiceImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Trump
 */
public class MainVerticle extends io.vertx.reactivex.core.AbstractVerticle {

    //private final static String CONFIG_PATH = "F:\\company\\company_project\\uaa\\src\\resources" + File.separator + "config-dev.json";
    private final static String CONFIG_PATH = "/Users/lizhenyu/work_code/company_code/rome-backend/uaa/src/resources" + File.separator + "config-dev.json";

    private final static Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private AsyncSQLClient postgreSQLClient;
    private MailClient mailClient;
    private RedisClient redisClient;
    private UaaService uaaService;
    private CommonService commonService;
    private ServiceDiscovery discovery;

    @Override
    public void start(Future<Void> startFuture) {


        //初始化配置文件
        InitConfig.initConfig(vertx, this, CONFIG_PATH).subscribe(res -> {
            logger.info("初始化配置成功");
            // 连接database
            postgreSQLClient = PostgresqlPool.getInstance(vertx, config().getJsonObject("PostgreSQL")).getPostClient();

            // 生成 SMTP client
            mailClient = SMTPConfig.creatSMTPClient(vertx, config().getJsonObject("ConfigSMTP"));

            // 配置RedisClient
            redisClient = RedisClient.create(vertx, config().getJsonObject("RedisClient"));

            //
            discovery = ServiceDiscovery.create(vertx);

            //发现服务
            consulInit(config().getJsonObject("ConsulConfig")).subscribe(() -> {

                // 配置传递
                uaaService = new UaaServiceImpl(new AccountRepository(postgreSQLClient, vertx, mailClient, redisClient), vertx);
                commonService = new CommonServiceImpl(vertx);

                routerController();
            });

            //运行

        }, err -> logger.error(((Exception) err).getMessage()));

    }

    private void routerController() {

        //获取某个服务
        JsonObject uaa01 = discovery.rxGetRecord(new JsonObject().put("name", "uaa01")).blockingGet().getMetadata();
        JsonObject message01 = discovery.rxGetRecord(new JsonObject().put("name", "message01")).blockingGet().getMetadata();
        this.config().getJsonObject("ConsulServer").put(uaa01.getString("ServiceName"), uaa01);
        this.config().getJsonObject("ConsulServer").put(message01.getString("ServiceName"), message01);
        Integer port = uaa01.getInteger("ServicePort");
        //message channel
        ManagedChannel messageChannel = RpcConfig.startRpcClient(vertx, message01.getString("ServiceAddress"), message01.getInteger("ServicePort"));

        // Create a router object.
        Router router = Router.router(vertx);
        vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(config().getInteger("port", port));

        // 增加一个处理器，将请求的上下文信息，放到RoutingContext中
        router.route().handler(BodyHandler.create());
        router.route("/api/user/*")
            .handler(routingContext -> {
                String token;
                token = routingContext.request().headers().get("token");

                if (token == null) {
                    ResponseJSON.falseJson(routingContext, "验证错误");
                }
                commonService.checkIdentity(token).subscribe(res -> {
                    routingContext.put("token", res.toString());
                    routingContext.next();
                }, err -> ResponseJSON.errJson(routingContext));
            });

        // protect router demo
        router.get("/api/user/sss").handler(routingContext -> {
            Token token = JSON.parseObject(routingContext.get("token"), Token.class);
            System.out.println(token.getUser_account());
            ResponseJSON.successJson(routingContext, "来到以后方法");
        });

        // user sign up
        router.put("/api/signUp").handler(routingContext -> {
            System.out.println(111);
            //转成实体类
            UserSignUp userSignUp = JSON.parseObject(routingContext.getBodyAsJson().toString(), UserSignUp.class);
            //判断实体类
            ValidatorUtil.checkEntity(userSignUp);
            uaaService.userSignUp(userSignUp).subscribe(result ->
                ResponseJSON.successJson(routingContext), err -> {
                if ("1".equals(((Exception) err).getMessage())) {
                    ResponseJSON.falseJson(routingContext);
                }
                ResponseJSON.falseJson(routingContext);
            });
        });

        // user sign in
        router.post("/api/login").handler(routingContext -> {

            //转成实体类
            UserSingIn userSingIn = JSON.parseObject(routingContext.getBodyAsJson().toString(), UserSingIn.class);
            System.out.println(userSingIn);
            uaaService.userLogin(userSingIn).subscribe(result -> ResponseJSON.successJson(routingContext, result, "登陆成功")
                , error -> ResponseJSON.falseJson(routingContext, "登陆失败"));
        });

        // send SMS or mail
        router.get("/api/verifiedCode/messageType/:messageType/useType/:useType/content/:content").handler(routingContext -> {

            String messageType = routingContext.request().getParam("messageType");
            String useType = routingContext.request().getParam("useType");
            String content = routingContext.request().getParam("content");
            if (!UaaConsts.MESSAGE_TYPE_MAIL.equals(messageType) && !UaaConsts.MESSAGE_TYPE_PHONE.equals(messageType)) {
                ResponseJSON.falseJson(routingContext);
            } else {
                VerificationCodeServiceGrpc.VerificationCodeServiceVertxStub stub = VerificationCodeServiceGrpc.newVertxStub(messageChannel);
                VerificationCodeReq request = VerificationCodeReq.newBuilder()
                    .setMessageContent(content)
                    .setUseType(useType)
                    .setMessageType(messageType)
                    .build();
                stub.getVerificationCode(request, ar -> {
                    if (ar.succeeded()) {
                        ResponseJSON.successJson(routingContext, "发送成功");
                    } else {
                        ResponseJSON.falseJson(routingContext, "发送失败");
                    }
                });
            }
        });

        //    reset password
        router.put("/api/user/resetPassword").handler(routingContext -> {
            System.out.println(routingContext.get("token").toString());
            String userAccount = JSON.parseObject(routingContext.get("token"), Token.class).getUser_account();
            System.out.println(userAccount);
            String newPassword = routingContext.getBodyAsJson().getString("newPassword");
            String code = routingContext.getBodyAsJson().getString("code");
            String phone = routingContext.getBodyAsJson().getString("phone");
            uaaService.resetPassword(userAccount, newPassword, code, phone).subscribe(result -> {
                    System.out.println(result.toString() + "23423424");
                    if ((Boolean) result) {
                        ResponseJSON.successJson(routingContext, "修改成功");
                    } else {
                        ResponseJSON.falseJson(routingContext, "修改失败");
                    }
                }
                , error -> ResponseJSON.errJson(routingContext));
        });

        // set payPassword
        router.put("/api/user/setPayPassword").handler(routingContext -> {
            String payPassword=routingContext.getBodyAsJson().getString("payPassword");

        });



        // update payPassword

    }

    private Completable consulInit(JsonObject config) {
        //consol发现服务
        return Completable.create((emitter) -> discovery.registerServiceImporter(ServiceImporter.newInstance(new ConsulServiceImporter()),
            new JsonObject()
                //发现远端注册中心
                //主机
                .put("host", config.getString("host"))
                //端口
                .put("port", config.getInteger("port"))
                //检察时间
                .put("scan-period", 2000), res -> {
                if (res.succeeded()) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable("err"));
                }
            }));
    }
}
