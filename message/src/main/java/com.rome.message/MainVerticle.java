package com.rome.message;


import com.rome.common.config.InitConfig;
import com.rome.common.dbutil.PostgresqlPool;
import com.rome.common.rpc.message.VerificationCodeReq;
import com.rome.common.rpc.message.VerificationCodeRes;
import com.rome.common.rpc.message.VerificationCodeServiceGrpc;
import com.rome.common.config.SMTPConfig;
import com.rome.message.repository.AccountRepository;
import com.rome.message.service.MessageService;
import com.rome.message.service.MessageServiceImpl;
import io.reactivex.Completable;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.grpc.*;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.spi.ServiceImporter;
import io.vertx.servicediscovery.consul.ConsulServiceImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author Trump
 */
public class MainVerticle extends io.vertx.reactivex.core.AbstractVerticle {
    private final static Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    // private final static String CONFIG_PATH = "/Users/lizhenyu/work_code/company_code/rome-backend/uaa/src/resources" + File.separator + "config-dev.json";
    private final static String CONFIG_PATH = "F:\\company_project\\message\\src\\resources" + File.separator + "config-dev.json";
    private AsyncSQLClient postgreSQLClient;
    private MailClient mailClient;
    private RedisClient redisClient;
    private MessageService messageService;
    private ServiceDiscovery discovery;

    @Override
    public void start(Future<Void> startFuture) throws IOException {

        //初始化配置文件
        InitConfig.initConfig(vertx, this, CONFIG_PATH).subscribe(res -> {
            // 连接database
            postgreSQLClient = PostgresqlPool.getInstance(vertx, config().getJsonObject("PostgreSQL")).getPostClient();

            // 生成 SMTP client
            mailClient = SMTPConfig.creatSMTPClient(vertx, config().getJsonObject("ConfigSMTP"));

            // 配置RedisClient
            redisClient = RedisClient.create(vertx, config().getJsonObject("RedisClient"));

            discovery = ServiceDiscovery.create(vertx);

            //发现服务
            consulInit(config().getJsonObject("ConsulConfig")).subscribe(() -> {
                // 配置传递
                messageService = new MessageServiceImpl(new AccountRepository(postgreSQLClient, vertx, mailClient, redisClient), vertx);
                toCommRPCController();
            });
        }, err -> logger.error(((Exception) err).getMessage()));
    }

    private void toCommRPCController() throws IOException {

        //获取某个服务
        JsonObject message01 = discovery.rxGetRecord(new JsonObject().put("name", "message01")).blockingGet().getMetadata();

        //处理一个rpc client的消息
        System.out.println("消息模块服务");
        VerificationCodeServiceGrpc.VerificationCodeServiceVertxImplBase service = new VerificationCodeServiceGrpc.VerificationCodeServiceVertxImplBase() {
            @Override
            public void getVerificationCode(VerificationCodeReq request, Future<VerificationCodeRes> response) {
                messageService.getVerificationCode(request.getMessageType(), request.getUseType(), request.getMessageContent())
                    .subscribe(res -> response.complete(VerificationCodeRes.newBuilder().setResultJson("success").build()),
                        err -> response.complete(VerificationCodeRes.newBuilder().setResultJson("false").build()));
            }
        };

        VertxServer rpcServer = VertxServerBuilder
            .forAddress(vertx.getDelegate(), message01.getString("ServiceAddress"), message01.getInteger("ServicePort"))
            .addService(service)
            .build();

        rpcServer.start();
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
