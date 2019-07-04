package com.rome.grading;

import com.rome.common.config.InitConfig;
import com.rome.common.config.ProfitConfig;
import com.rome.common.config.SMTPConfig;
import com.rome.common.dbutil.PostgresqlPool;
import com.rome.common.service.CommonService;
import com.rome.common.service.CommonServiceImpl;
import com.rome.common.status.CommonStatus;
import com.rome.common.util.ResponseJSON;
import com.rome.grading.repository.GradingRepository;
import com.rome.grading.service.GradingService;
import com.rome.grading.service.GradingServiceImpl;
import io.reactivex.Completable;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.spi.ServiceImporter;
import io.vertx.servicediscovery.consul.ConsulServiceImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author asus
 */
public class GradingMainVerticle extends io.vertx.reactivex.core.AbstractVerticle{

    private final static String CONFIG_PATH = "E:\\company\\rome-backend\\grading\\src\\resources" + File.separator + "config-dev.json";
    private final static Logger logger = LoggerFactory.getLogger(GradingMainVerticle.class);
    private AsyncSQLClient postgreSQLClient;
    private MailClient mailClient;
    private RedisClient redisClient;
    private ServiceDiscovery discovery;
    private WebClient webClient;
    private CommonService commonService;
    private GradingService gradingService;

    @Override
    public void start(Future<Void> startFuture) {


        //初始化配置文件
        InitConfig.initConfig(vertx, this, CONFIG_PATH).subscribe(res -> {
            logger.info(CommonStatus.CONFIGURATION);

            // 连接database
            postgreSQLClient = PostgresqlPool.getInstance(vertx, config().getJsonObject("PostgreSQL")).getPostClient();

            // 生成 SMTP client
            mailClient = SMTPConfig.creatSMTPClient(vertx, config().getJsonObject("ConfigSMTP"));

            // 配置RedisClient
            redisClient = RedisClient.create(vertx, config().getJsonObject("RedisClient"));

            //发现服务
            discovery = ServiceDiscovery.create(vertx);

            //创建httpclient
            webClient = WebClient.create(vertx);

            consulInit(config().getJsonObject("ConsulConfig")).subscribe(() -> {
                // 配置传递
                gradingService = new GradingServiceImpl(new GradingRepository(postgreSQLClient, vertx, mailClient, redisClient, webClient), vertx);
                routerController();
            });
            //运行
        }, err -> logger.error(((Exception) err).getMessage()));
    }

    public void routerController() {
        //获取某个服务
        JsonObject uaa01 = discovery.rxGetRecord(new JsonObject().put("name", "uaa01")).blockingGet().getMetadata();
        JsonObject message01 = discovery.rxGetRecord(new JsonObject().put("name", "message01")).blockingGet().getMetadata();
        JsonObject wallet01 = discovery.rxGetRecord(new JsonObject().put("name", "wallet01")).blockingGet().getMetadata();
        JsonObject admin01 = discovery.rxGetRecord(new JsonObject().put("name", "admin01")).blockingGet().getMetadata();
        JsonObject guild01 = discovery.rxGetRecord(new JsonObject().put("name", "guild01")).blockingGet().getMetadata();
        JsonObject grading01 = discovery.rxGetRecord(new JsonObject().put("name", "grading01")).blockingGet().getMetadata();
        this.config().getJsonObject("ConsulServer").put(uaa01.getString("ServiceName"), uaa01);
        this.config().getJsonObject("ConsulServer").put(message01.getString("ServiceName"), message01);
        this.config().getJsonObject("ConsulServer").put(wallet01.getString("ServiceName"), wallet01);
        this.config().getJsonObject("ConsulServer").put(admin01.getString("ServiceName"), admin01);
        this.config().getJsonObject("ConsulServer").put(guild01.getString("ServiceName"), guild01);
        this.config().getJsonObject("ConsulServer").put(grading01.getString("ServiceName"), grading01);
        Integer port = grading01.getInteger("ServicePort");
        // Create a router object.
        Router router = Router.router(vertx);
        vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(config().getInteger("port", port));

        // 增加一个处理器，将请求的上下文信息，放到RoutingContext中
        router.route().handler(BodyHandler.create());
        router.route("/api/grading/*")
            .handler(routingContext -> {
                String token;
                token = routingContext.request().headers().get("token");
                if (token == null) {
                    ResponseJSON.falseJson(routingContext, CommonStatus.CHECK_TOKEN);
                }
                commonService.checkIdentity(token).subscribe(res -> {
                    routingContext.put("token", res.toString());
                    routingContext.next();
                }, err -> ResponseJSON.errJson(routingContext));
            });


        router.get("/api/test").handler(routingContext -> {
            ResponseJSON.successJson(routingContext,"11111");
        });



    }


    private Completable consulInit(JsonObject config) {
        //consul发现服务
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
