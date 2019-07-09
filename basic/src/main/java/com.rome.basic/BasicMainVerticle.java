package com.rome.basic;

import com.rome.basic.repository.BasicRepository;
import com.rome.basic.service.BasicService;
import com.rome.basic.service.BasicServiceImpl;
import com.rome.common.config.InitConfig;
import com.rome.common.config.SMTPConfig;
import com.rome.common.dbutil.PostgresqlPool;
import com.rome.common.rpc.basic.FileUploadReq;
import com.rome.common.rpc.basic.FileUploadRes;
import com.rome.common.rpc.basic.FileUploadServiceGrpc;
import io.reactivex.Completable;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
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
 * @author asus
 */
public class BasicMainVerticle extends io.vertx.reactivex.core.AbstractVerticle{
    private final static Logger logger = LoggerFactory.getLogger(BasicMainVerticle.class);
    private final static String CONFIG_PATH = "basic/src/resources/config-dev.json";
    private AsyncSQLClient postgreSQLClient;
    private MailClient mailClient;
    private RedisClient redisClient;
    private BasicService basicService;
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
                basicService = new BasicServiceImpl(new BasicRepository(postgreSQLClient, vertx, mailClient, redisClient), vertx);
                toBasicRPCController();
            });
        }, err -> logger.error(((Exception) err).getMessage()));
    }

    private void toBasicRPCController() throws IOException {

        //获取某个服务
        JsonObject basic01 = discovery.rxGetRecord(new JsonObject().put("name", "basic01")).blockingGet().getMetadata();

        //处理一个rpc client的消息
        FileUploadServiceGrpc.FileUploadServiceVertxImplBase service=new FileUploadServiceGrpc.FileUploadServiceVertxImplBase() {
            @Override
            public void fileUpload(FileUploadReq request, Future<FileUploadRes> response) {
                System.out.println(11111);
                basicService.setFile(request.getImageByte(),request.getUserAccount())
                    .subscribe(res ->{
                        if ("false".equals(res.toString())){
                            response.complete(FileUploadRes.newBuilder().setResultJson("false").build());
                        }else{
                            System.out.println(res);
                            response.complete(FileUploadRes.newBuilder().setResultJson(res.toString()).build());
                        } });
            }
        };

        VertxServer rpcServer = VertxServerBuilder
            .forAddress(vertx.getDelegate(), basic01.getString("ServiceAddress"), basic01.getInteger("ServicePort"))
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
