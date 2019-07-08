package com.rome.uaa;

import com.alibaba.fastjson.JSON;
import com.rome.common.config.InitConfig;
import com.rome.common.config.ProfitConfig;
import com.rome.common.config.RpcConfig;
import com.rome.common.constant.UaaConsts;
import com.rome.common.dbutil.PostgresqlPool;
import com.rome.common.entity.Token;
import com.rome.common.rpc.basic.FileUploadReq;
import com.rome.common.rpc.basic.FileUploadServiceGrpc;
import com.rome.common.rpc.message.VerificationCodeReq;
import com.rome.common.rpc.message.VerificationCodeServiceGrpc;
import com.rome.common.config.SMTPConfig;
import com.rome.common.service.CommonService;
import com.rome.common.service.CommonServiceImpl;
import com.rome.common.status.CommonStatus;
import com.rome.common.status.UaaStatus;
import com.rome.common.util.IpAddress;
import com.rome.common.util.ResponseJSON;
import com.rome.uaa.util.ValidatorUtil;
import com.rome.uaa.entity.UserSignUp;
import com.rome.uaa.entity.UserSingIn;
import com.rome.uaa.repository.AccountRepository;
import com.rome.uaa.service.UaaService;
import com.rome.uaa.service.UaaServiceImpl;
import io.github.novacrypto.bip32.ExtendedPrivateKey;
import io.github.novacrypto.bip32.networks.Bitcoin;
import io.github.novacrypto.bip39.SeedCalculator;
import io.grpc.ManagedChannel;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.ext.web.FileUpload;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.handler.*;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.spi.ServiceImporter;
import io.vertx.servicediscovery.consul.ConsulServiceImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Trump
 */
public class MainVerticle extends io.vertx.reactivex.core.AbstractVerticle {

    //        private final static String CONFIG_PATH = "E:\\company\\rome-backend\\uaa\\src\\resources" + File.separator + "config-dev.json";
    private final static String CONFIG_PATH = "/Users/lizhenyu/work_code/company_code/rome-backend/uaa/src/resources" + File.separator + "config-dev.json";
    private final static Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private AsyncSQLClient postgreSQLClient;
    private MailClient mailClient;
    private RedisClient redisClient;
    private UaaService uaaService;
    private CommonService commonService;
    private ServiceDiscovery discovery;
    private WebClient webClient;
    private static Pattern pattern = Pattern.compile("\\d{6}");

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
            consulInit(config().getJsonObject("ConsulConfig")).subscribe(() -> {
                //创建httpclient
                webClient = WebClient.create(vertx);
                // 配置传递
                uaaService = new UaaServiceImpl(new AccountRepository(postgreSQLClient, vertx, mailClient, redisClient, webClient), vertx);
                commonService = new CommonServiceImpl(new ProfitConfig(postgreSQLClient, vertx), vertx);
                commonService.selectProfit(vertx, this, CONFIG_PATH).subscribe();
                routerController();
            });
        }, err -> logger.error(((Exception) err).getMessage()));

    }

    private void routerController() {
        //获取某个服务
        JsonObject uaa01 = discovery.rxGetRecord(new JsonObject().put("name", "uaa01")).blockingGet().getMetadata();
        JsonObject message01 = discovery.rxGetRecord(new JsonObject().put("name", "message01")).blockingGet().getMetadata();
        JsonObject basic01 = discovery.rxGetRecord(new JsonObject().put("name", "basic01")).blockingGet().getMetadata();
        this.config().getJsonObject("ConsulServer").put(uaa01.getString("ServiceName"), uaa01);
        this.config().getJsonObject("ConsulServer").put(message01.getString("ServiceName"), message01);
        Integer port = uaa01.getInteger("ServicePort");
        //message channel
        ManagedChannel messageChannel = RpcConfig.startRpcClient(vertx, message01.getString("ServiceAddress"), message01.getInteger("ServicePort"));
        //basic channel
        ManagedChannel basicChannel = RpcConfig.startRpcClient(vertx, basic01.getString("ServiceAddress"), basic01.getInteger("ServicePort"));
        // Create a router object.
        Router router = Router.router(vertx);
        vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(config().getInteger("port", port));

        // 增加一个处理器，将请求的上下文信息，放到RoutingContext中
        router.route().handler(BodyHandler.create());

        router.route("/*").failureHandler(frc -> ResponseJSON.falseJson(frc, "false"));
        router.route("/user/*")
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
        Router mainRouter = Router.router(vertx);

        router.mountSubRouter("/v1/uaa", router);

        // user sign up
        router.put("/publish/signUp").handler(routingContext -> {
            //转成实体类
            UserSignUp userSignUp = JSON.parseObject(routingContext.getBodyAsJson().toString(), UserSignUp.class);
            //判断实体类
            ValidatorUtil.checkEntity(userSignUp);
            userSignUp.setCreateIp(IpAddress.getIpAddress(routingContext));
            userSignUp.setUsingIp(IpAddress.getIpAddress(routingContext));
            System.out.println(userSignUp);
            uaaService.userSignUp(userSignUp, userSignUp.getInvitationCode()).subscribe(result -> {
                if ("success".equals(result)) {
                    Date date = new Date();
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00");
                    String nowTime = format.format(date);
                    Single<HttpResponse<Buffer>> req = webClient.post(80, "api.caodabi.com", "/v2/account")
                        .putHeader("Authorization", "HRT Principal=bjnpmtq3q562oukvq8ig,Timestamp=" + nowTime + ",SecretKey=Z8IoCswSryuPHWnGhQix0vBlpJ67j4qaUbdNLtY9")
                        .rxSendJsonObject(new JsonObject().put("userID", userSignUp.getUserAccount()));
                    req.subscribe(res -> {
                        if (res.bodyAsJsonObject().getJsonObject("addresses").isEmpty()) {
                            ResponseJSON.falseJson(routingContext, UaaStatus.SIGN_UP_FALSE);
                        } else {
                            ResponseJSON.successJson(routingContext, res.bodyAsJsonObject(), UaaStatus.SIGN_UP_SUCCESS);
                        }

                    }, error -> ResponseJSON.errJson(routingContext));
                } else if ("false".equals(result)) {
                    ResponseJSON.falseJson(routingContext, UaaStatus.SIGN_UP_FALSE);
                } else {
                    ResponseJSON.falseJson(routingContext, UaaStatus.SIGN_UP_RESIGN);
                }
            }, err -> ResponseJSON.falseJson(routingContext, UaaStatus.SIGN_UP_FALSE));
        });

        // user sign in
        router.post("/login").handler(routingContext -> {
            //转成实体类
            UserSingIn userSingIn = JSON.parseObject(routingContext.getBodyAsJson().toString(), UserSingIn.class);
            System.out.println(userSingIn);
            ValidatorUtil.checkEntity(userSingIn);
            userSingIn.setUsingIp(IpAddress.getIpAddress(routingContext));
            uaaService.userLogin(userSingIn).subscribe(result -> ResponseJSON.successJson(routingContext, new JsonObject().put("token", result), UaaStatus.SIGN_IN_SUCCESS)
                , error -> ResponseJSON.falseJson(routingContext, UaaStatus.SIGN_IN_FALSE));
        });

        // send SMS or mail
        router.get("/verifiedCode/messageType/:messageType/useType/:useType/content/:content").handler(routingContext -> {
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
                        ResponseJSON.successJson(routingContext, UaaStatus.SEND_MSG_SUCCESS);
                    } else {
                        ResponseJSON.falseJson(routingContext, UaaStatus.SEND_MSG_FALSE);
                    }
                });
            }
        });

        //    reset password
        router.put("/user/resetPassword").handler(routingContext -> {
            String userAccount = JSON.parseObject(routingContext.get("token"), Token.class).getUser_account();
            String newPassword = routingContext.getBodyAsJson().getString("newPassword");
            String code = routingContext.getBodyAsJson().getString("code");
            String phone = routingContext.getBodyAsJson().getString("phone");
            uaaService.resetPassword(userAccount, newPassword, code, phone).subscribe(result -> {
                if ((Boolean) result) {
                    ResponseJSON.successJson(routingContext, UaaStatus.UPDATE_SUCCESS);
                } else {
                    ResponseJSON.falseJson(routingContext, UaaStatus.UPDATE_FALSE);
                }
            }, error -> ResponseJSON.errJson(routingContext));
        });

        // update payPassword
        router.put("/user/updatePayPassword").handler(routingContext -> {
            String userAccount = JSON.parseObject(routingContext.get("token"), Token.class).getUser_account();
            String payPassword = routingContext.getBodyAsJson().getString("payPassword");
            String newPayPassword = routingContext.getBodyAsJson().getString("newPayPassword");
            if(!pattern.matcher(newPayPassword).matches()||!pattern.matcher(payPassword).matches()){
                System.out.println("哈哈哈哈");
                ResponseJSON.falseJson(routingContext, CommonStatus.FALSE);
                return;
            }
            uaaService.updatePayPassword(userAccount, payPassword, newPayPassword).subscribe(result -> {
                if ("success".equals(result)) {
                    ResponseJSON.successJson(routingContext, UaaStatus.UPDATE_SUCCESS);
                } else {
                    ResponseJSON.falseJson(routingContext, UaaStatus.UPDATE_PAY_PASSWORD_FALSE);
                }
            }, error -> ResponseJSON.errJson(routingContext));
        });

        //update nickName
        router.put("/user/updateNickName").handler(routingContext -> {
            String userAccount = JSON.parseObject(routingContext.get("token"), Token.class).getUser_account();
            String nickName = routingContext.getBodyAsJson().getString("nickName");
            uaaService.updateNickName(userAccount, nickName).subscribe(result -> {
                if ("success".equals(result)) {
                    ResponseJSON.successJson(routingContext, UaaStatus.UPDATE_SUCCESS);
                } else {
                    ResponseJSON.falseJson(routingContext, UaaStatus.UPDATE_FALSE);
                }
            }, error -> ResponseJSON.errJson(routingContext));
        });

        //set head image
        router.put("/user/setHeadImage").handler(routingContext -> {
            String userAccount = JSON.parseObject(routingContext.get("token"), Token.class).getUser_account();
            String imageByte=routingContext.getBodyAsJson().getString("imageByte");
            System.out.println(imageByte+userAccount);
            Set<FileUpload> uploads = routingContext.fileUploads();

         /*   //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
            InputStream in = null;
            byte[] data = null;
            //读取图片字节数组
            try
            {
                in = new FileInputStream(imgFile);
                data = new byte[in.available()];
                in.read(data);
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            //对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(data);//返回Base64编码过的字节数组字符串  */





            FileUploadServiceGrpc.FileUploadServiceVertxStub stub=FileUploadServiceGrpc.newVertxStub(basicChannel);
            FileUploadReq request=FileUploadReq.newBuilder()
                .setImageByte(imageByte)
                .setUserAccount(userAccount)
                .build();
            stub.fileUpload(request,ar->{
                if ("false".equals(ar.result().getResultJson())) {
                    ResponseJSON.falseJson(routingContext,UaaStatus.SET_FALSE);
                } else {
                    System.out.println(ar);
                    System.out.println(ar.succeeded());
                    String headImage=ar.result().getResultJson();
                    System.out.println(headImage);
                    uaaService.setHeadImage(userAccount, headImage).subscribe(result -> {
                        if ("success".equals(result)) {
                            ResponseJSON.successJson(routingContext, UaaStatus.SET_SUCCESS);
                        } else {
                            ResponseJSON.falseJson(routingContext, UaaStatus.SET_FALSE);
                        }
                    }, error -> ResponseJSON.errJson(routingContext));
                }
            });

        });

        //获取一个助记词
        router.get("/user/getMnemonics").handler(routingContext -> {
            String userAccount = JSON.parseObject(routingContext.get("token"), Token.class).getUser_account();
            uaaService.getMnemonics(userAccount).subscribe(res -> {
                ResponseJSON.successJson(routingContext, res, UaaStatus.GET_SUCCESS);
            }, err -> ResponseJSON.falseJson(routingContext, UaaStatus.GET_FALSE));
        });

        //获取一个私钥
        router.get("/user/getPrivateKey").handler(routingContext -> {
            System.out.println("asd");
            String mnemonics = routingContext.getBodyAsJson().getString("mnemonics");
            if (mnemonics == null) {
                ResponseJSON.falseJson(routingContext, UaaStatus.GET_FALSE);
            }
            System.out.println(mnemonics);
            byte[] seed = new SeedCalculator().calculateSeed(mnemonics, "");
            System.out.println(seed);
            ExtendedPrivateKey rootPrivateKey = ExtendedPrivateKey.fromSeed(seed, Bitcoin.MAIN_NET);
            String privateKey = rootPrivateKey.getPrivateKey();
            ResponseJSON.successJson(routingContext, privateKey, UaaStatus.GET_SUCCESS);
        });
        //助记词登陆
//        router.put("")

        //2. 由助记词得到种子
//            byte[] seed = new SeedCalculator().calculateSeed(mnemonics, "");
//            System.out.println(seed);
//            ExtendedPrivateKey rootPrivateKey = ExtendedPrivateKey.fromSeed(seed, Bitcoin.MAIN_NET);
//            byte[] pvc = rootPrivateKey.getKey();
//
//            String src = "1111";
//            String src2 = "1211";
//            BouncyCastleCrypto bcc = new BouncyCastleCrypto();
//            byte[] res = bcc.sign(src.getBytes(StandardCharsets.UTF_8), pvc);
//            System.out.println("签名：" + Hex.encodeHexString(res));
//            System.out.println("验证：" + bcc.verify(src2.getBytes(StandardCharsets.UTF_8), res, bcc.getPublicFor(pvc)));
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
///asdagit
