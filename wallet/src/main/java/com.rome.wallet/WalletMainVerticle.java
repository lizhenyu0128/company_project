package com.rome.wallet;

import com.alibaba.fastjson.JSON;
import com.rome.common.config.InitConfig;
import com.rome.common.config.ProfitConfig;
import com.rome.common.config.SMTPConfig;
import com.rome.common.dbutil.PostgresqlPool;
import com.rome.common.entity.Token;
import com.rome.common.service.CommonService;
import com.rome.common.service.CommonServiceImpl;
import com.rome.common.status.CommonStatus;
import com.rome.common.status.UaaStatus;
import com.rome.common.status.WalletStatus;
import com.rome.common.util.ResponseJSON;
import com.rome.wallet.entity.Cash;
import com.rome.wallet.repostiory.WalletNativeRepository;
import com.rome.wallet.repostiory.WalletRepository;
import com.rome.wallet.service.WalletNativeService;
import com.rome.wallet.service.WalletNativeServiceImpl;
import com.rome.wallet.service.WalletService;
import com.rome.wallet.service.WalletServiceImpl;
import com.rome.wallet.util.OrderIdUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
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
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Author:
 * Data:2019-06-15 11:58
 * Description:<>
 *
 * @author Trump
 */
public class WalletMainVerticle extends io.vertx.reactivex.core.AbstractVerticle {
//    private final static String CONFIG_PATH = "E:\\company\\rome-backend\\wallet\\src\\main\\resources" + File.separator + "config-dev.json";
    private final static String CONFIG_PATH = "/Users/lizhenyu/work_code/company_code/rome-backend/wallet/src/main/resources" + File.separator + "config-dev.json";
    private final static Logger logger = LoggerFactory.getLogger(WalletMainVerticle.class);
    private AsyncSQLClient postgreSQLClient;
    private MailClient mailClient;
    private RedisClient redisClient;
    private ServiceDiscovery discovery;
    private CommonService commonService;
    private WalletNativeService walletNativeService;
    private WalletService walletService;
    private WebClient webClient;

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
                walletNativeService = new WalletNativeServiceImpl(new WalletNativeRepository(postgreSQLClient, vertx, mailClient, redisClient,webClient), vertx);
                walletService = new WalletServiceImpl(new WalletRepository(postgreSQLClient, vertx, mailClient, redisClient, webClient), vertx);
                commonService = new CommonServiceImpl(new ProfitConfig(postgreSQLClient, vertx), vertx);
                commonService.selectProfit(vertx, this, CONFIG_PATH).subscribe();
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
        JsonObject admin01 = discovery.rxGetRecord(new JsonObject().put("name", "admin01")).blockingGet().getMetadata();
        this.config().getJsonObject("ConsulServer").put(uaa01.getString("ServiceName"), uaa01);
        this.config().getJsonObject("ConsulServer").put(message01.getString("ServiceName"), message01);
        this.config().getJsonObject("ConsulServer").put(wallet01.getString("ServiceName"), wallet01);
        this.config().getJsonObject("ConsulServer").put(admin01.getString("ServiceName"), admin01);
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
                    ResponseJSON.falseJson(routingContext, CommonStatus.CHECK_TOKEN);
                }
                commonService.checkIdentity(token).subscribe(res -> {
                    routingContext.put("token", res.toString());
                    routingContext.next();
                }, err -> ResponseJSON.errJson(routingContext));
            });


        // get coin wallet
        router.get("/api/wallet/account").handler(routingContext -> {
            System.out.println("asd");
            String account = JSON.parseObject(routingContext.get("token"), Token.class).getUser_account();
            Date ss = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00");
            String nowTime = format.format(ss);
            System.out.println(nowTime);
            Single<HttpResponse<Buffer>> req = webClient.get(80, "api.caodabi.com", "/v2/account/" + account)
                .putHeader("Authorization", "HRT Principal=bjnpmtq3q562oukvq8ig,Timestamp=" + nowTime + ",SecretKey=Z8IoCswSryuPHWnGhQix0vBlpJ67j4qaUbdNLtY9").rxSend();
            req.subscribe(res -> {
                System.out.println(res.bodyAsJsonObject());
                if (res.bodyAsJsonObject().getJsonObject("addresses") == null) {
                    ResponseJSON.falseJson(routingContext);
                } else {
                    ResponseJSON.successJson(routingContext, res.bodyAsJsonObject(), null);
                }
            }, error -> ResponseJSON.errJson(routingContext));
        });

        //get transaction coin by hash
        router.get("/api/wallet/coin/:coinPair/transaction/:hash").handler(routingContext -> {
            String coin = routingContext.request().getParam("coinPair");
            String hash = routingContext.request().getParam("hash");
            Date ss = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00");
            String nowTime = format.format(ss);
            Single<HttpResponse<Buffer>> req = webClient.get(80, "api.caodabi.com", "/v2/coin/" + coin + "/transaction/" + hash)
                .putHeader("Authorization", "HRT Principal=bjnpmtq3q562oukvq8ig,Timestamp=" + nowTime + ",SecretKey=Z8IoCswSryuPHWnGhQix0vBlpJ67j4qaUbdNLtY9").rxSend();
            req.subscribe(res -> {
              System.out.println(res.body().toJsonArray());
                if (res.body().toJsonArray() == null) {
                    ResponseJSON.falseJson(routingContext);
                } else {
                    ResponseJSON.successJson(routingContext, res.body().toJsonArray(), null);
                }
            }, error -> ResponseJSON.errJson(routingContext));
        });

        //get get transaction coin by address
        router.get("/api/wallet/coin/:coinPair/transaction/:address/:page/:size").handler(routingContext -> {
            System.out.println("asda");
            String coin = routingContext.request().getParam("coinPair");
            String address = routingContext.request().getParam("address");
            String page = routingContext.request().getParam("page");
            String size = routingContext.request().getParam("size");
            System.out.println(coin+"/"+address+"/"+page+"/"+size+"/");
            Date ss = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00");
            String nowTime = format.format(ss);
            System.out.println(nowTime);
            Single<HttpResponse<Buffer>> req = webClient.get(80, "api.caodabi.com", "/v2/coin/" + coin + "/transaction")
                .putHeader("Authorization", "HRT Principal=bjnpmtq3q562oukvq8ig,Timestamp=" + nowTime + ",SecretKey=Z8IoCswSryuPHWnGhQix0vBlpJ67j4qaUbdNLtY9")
                .addQueryParam("address", address)
                .addQueryParam("page", page)
                .addQueryParam("size", size)
                .rxSend();

            req.subscribe(res -> {
                System.out.println(res.bodyAsJsonObject());
                if (res.bodyAsJsonObject().getJsonArray("items") == null) {
                    ResponseJSON.falseJson(routingContext);
                } else {
                    ResponseJSON.successJson(routingContext,JSON.parse( res.bodyAsJsonObject().getJsonArray("items").toString()), null);
                }
            }, error -> ResponseJSON.errJson(routingContext));
        });

        //create cash order
        router.post("/api/wallet/cash").handler(routingContext -> {
            System.out.println(111);
            String userAccount = JSON.parseObject(routingContext.get("token"), Token.class).getUser_account();
            String coinType = routingContext.getBodyAsJson().getString("coinType");
            Cash cash = JSON.parseObject(routingContext.getBodyAsJson().toString(), Cash.class);
            cash.setOrderID(OrderIdUtil.getOrderNo(userAccount));
            cash.setUserID(userAccount);
            walletNativeService.createCashOrder(userAccount,cash,coinType).subscribe(result ->{
                if ("success".equals(result)){
                    ResponseJSON.successJson(routingContext, WalletStatus.CREATE_ORDER_SUCCESS);
                }else{
                    ResponseJSON.falseJson(routingContext,WalletStatus.CREATE_ORDER_FALSE);
                }
            }, error -> ResponseJSON.errJson(routingContext));
        });

        //   cancel order
        router.put("/api/wallet/cancelOrder").handler(routingContext -> {
            String cashId = routingContext.getBodyAsJson().getString("cashId");
            String coinType = routingContext.getBodyAsJson().getString("coinType");
            String userAccount = JSON.parseObject(routingContext.get("token"), Token.class).getUser_account();
            walletNativeService.cancelOrder(cashId,coinType,userAccount).subscribe(result ->{
                if ("success".equals(result)){
                    Date date = new Date();
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00");
                    String nowTime = format.format(date);
                    Single<HttpResponse<Buffer>> req = webClient.put(80, "api.caodabi.com", "/v2/cash/" + cashId + "/cancel")
                        .putHeader("Authorization", "HRT Principal=bjnpmtq3q562oukvq8ig,Timestamp=" + nowTime + ",SecretKey=Z8IoCswSryuPHWnGhQix0vBlpJ67j4qaUbdNLtY9").rxSend();
                    req.subscribe(res -> {
                        System.out.println(res.body());
                        if (res.statusCode()==200||"200".equals(res.statusCode())){
                            ResponseJSON.successJson(routingContext,WalletStatus.CANCEL_ORDER_SUCCESS);
                        }else if (res.statusCode()==500||"500".equals(res.statusCode())){
                            ResponseJSON.falseJson(routingContext,WalletStatus.CANCEL_ORDER_FALSE);
                        }
                    }, error -> ResponseJSON.errJson(routingContext));
                }else {
                    ResponseJSON.falseJson(routingContext,WalletStatus.CANCEL_ORDER_TIME_FALSE);
                }
            });
        });



        // transaction coin
        router.post("/api/wallet/transaction").handler(routingContext -> {
            System.out.println(11);
            String coinType = routingContext.getBodyAsJson().getString("coinType");;
            String userAccount = JSON.parseObject(routingContext.get("token"), Token.class).getUser_account();
            String toAccount = routingContext.getBodyAsJson().getString("toAccount");
            String message = routingContext.getBodyAsJson().getString("message");
            String amount = routingContext.getBodyAsJson().getString("amount");
            String payPassword = routingContext.getBodyAsJson().getString("payPassword");
            System.out.println(userAccount);
            String regEx = "([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])";
            Pattern pattern = Pattern.compile(regEx);

            if ("".equals(amount) || !pattern.matcher(amount).matches() || Double.parseDouble(amount) < 0) {
                ResponseJSON.falseJson(routingContext, WalletStatus.TRANSACTION_COIN_CHECK_TYPE);
            } else {
                walletNativeService.transactionCoin(coinType, amount, userAccount, toAccount, message, payPassword).subscribe(result -> {
                    System.out.println(result);
                    if (("success").equals(result)) {
                        ResponseJSON.successJson(routingContext,WalletStatus.TRANSACTION_COIN_SUCCESS);
                    } else if (("false1").equals(result)) {
                        ResponseJSON.falseJson(routingContext, WalletStatus.TRANSACTION_COIN_PASSWORD_FALSE);
                    } else if (("false2").equals(result)) {
                        ResponseJSON.falseJson(routingContext, WalletStatus.TRANSACTION_COIN_AMOUNT_FALSE);
                    } else if ("false0".equals(result)){
                        ResponseJSON.falseJson(routingContext, WalletStatus.TRANSACTION_COIN_ACCOUNT_FALSE);
                    }else {
                        ResponseJSON.falseJson(routingContext, WalletStatus.TRANSACTION_COIN_FALSE);
                    }
                }, error -> ResponseJSON.errJson(routingContext));
            }
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
