package com.rome.wallet;

import com.alibaba.fastjson.JSON;
import com.rome.common.config.InitConfig;
import com.rome.common.config.SMTPConfig;
import com.rome.common.dbutil.PostgresqlPool;
import com.rome.common.entity.Token;
import com.rome.common.service.CommonService;
import com.rome.common.service.CommonServiceImpl;
import com.rome.common.util.ResponseJSON;
import com.rome.wallet.repostiory.WalletNativeRepository;
import com.rome.wallet.repostiory.WalletRepository;
import com.rome.wallet.service.WalletNativeService;
import com.rome.wallet.service.WalletNativeServiceImpl;
import com.rome.wallet.service.WalletService;
import com.rome.wallet.service.WalletServiceImpl;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.spi.ServiceImporter;
import io.vertx.servicediscovery.consul.ConsulServiceImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Response;
import java.io.File;
import java.util.regex.Pattern;

/**
 * Author:
 * Data:2019-06-15 11:58
 * Description:<>
 *
 * @author Trump
 */
public class WalletMainVerticle extends io.vertx.reactivex.core.AbstractVerticle {

    //private final static String CONFIG_PATH = "F:\\company\\company_project\\uaa\\src\\resources" + File.separator + "config-dev.json";
    private final static String CONFIG_PATH = "/Users/lizhenyu/work_code/company_code/rome-backend/wallet/src/main/resources" + File.separator + "config-dev.json";
    private final static Logger logger = LoggerFactory.getLogger(WalletMainVerticle.class);
    private AsyncSQLClient postgreSQLClient;
    private MailClient mailClient;
    private RedisClient redisClient;
    private ServiceDiscovery discovery;
    private CommonService commonService;
    private WalletNativeService walletNativeService;
    private WalletService walletService;

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
                walletNativeService = new WalletNativeServiceImpl(new WalletNativeRepository(postgreSQLClient, vertx, mailClient, redisClient), vertx);
                walletService = new WalletServiceImpl(new WalletRepository(postgreSQLClient, vertx, mailClient, redisClient), vertx);
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
        JsonObject wallet01 = discovery.rxGetRecord(new JsonObject().put("name", "wallet01")).blockingGet().getMetadata();
        this.config().getJsonObject("ConsulServer").put(uaa01.getString("ServiceName"), uaa01);
        this.config().getJsonObject("ConsulServer").put(message01.getString("ServiceName"), message01);
        this.config().getJsonObject("ConsulServer").put(wallet01.getString("ServiceName"), wallet01);
        Integer port = wallet01.getInteger("ServicePort");
        // Create a router object.
        Router router = Router.router(vertx);
        vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(config().getInteger("port", port));

        // 增加一个处理器，将请求的上下文信息，放到RoutingContext中
        router.route().handler(BodyHandler.create());
        router.route("/api/wallet/*")
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
        // get all
        router.get("/api/wallet/sss").handler(routingContext -> {
            ResponseJSON.successJson(routingContext, "来到以后方法");
        });


        router.get("/api/wallet/http").handler(routingContext -> {
//            WebClient webClient = WebClient.create(vertx);
            // 以get方式请求远程地址
//            webClient.postAbs("http://api.caodabi.com/v2/account")
//                .send(handle -> {
//                    // 处理响应的结果
//                    if (handle.succeeded()) {
//                        // 这里拿到的结果就是一个HTML文本，直接打印出来
//                        System.out.println(handle.result().bodyAsString());
//                    }else {
//                        System.out.println("哈哈");
//                    }
//                });


            ResponseJSON.successJson(routingContext, "来到以后方法");
        });
        // get coin wallet
        router.get("/api/wallet/account/:account").handler(routingContext -> {
//            WebClient webClient = WebClient.create(vertx);
//            webClient.postAbs("http://api.caodabi.com/v2/account")
//                .send(handle -> {
//                    // 处理响应的结果
//                    if (handle.succeeded()) {
//                        // 这里拿到的结果就是一个HTML文本，直接打印出来
//                        System.out.println(handle.result().bodyAsString());
//                    } else {
//                        System.out.println("哈哈");
//                    }
//                });
            ResponseJSON.successJson(routingContext, "来到以后方法");
        });


        ///////////////
        // transaction coin

       router.post("/api/wallet/transaction/:coinType").handler(routingContext ->{

           String coinType=routingContext.request().getParam("coinType");
           String userAccount = JSON.parseObject(routingContext.get("token"), Token.class).getUser_account();
           String toAccount = routingContext.getBodyAsJson().getString("toAccount");
           String message = routingContext.getBodyAsJson().getString("message");
           String amount = routingContext.getBodyAsJson().getString("amount");
           String payPassword=routingContext.getBodyAsJson().getString("payPassword");

           String regEx = "([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])";
           Pattern pattern = Pattern.compile(regEx);
           System.out.println(pattern.matcher(amount).matches());

           if ("".equals(amount) || !pattern.matcher(amount).matches() || Double.parseDouble(amount) < 0){
               ResponseJSON.falseJson(routingContext,"请输入正数");
           }else{
               walletNativeService.transactionCoin(coinType,amount,userAccount,toAccount,message,payPassword).subscribe(result -> {
                   if (("success").equals(result)){
                       ResponseJSON.successJson(routingContext,"交易成功");
                   }else if (("false1").equals(result)){
                       ResponseJSON.successJson(routingContext,"支付密码错误");
                   }
                   else if (("false2").equals(result)){
                       ResponseJSON.successJson(routingContext,"余额不足");
                   }else{
                       ResponseJSON.successJson(routingContext,"交易失败");
                   }
               },error -> ResponseJSON.falseJson(routingContext));
           }



       });



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
