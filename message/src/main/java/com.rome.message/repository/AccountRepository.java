package com.rome.message.repository;

import com.rome.common.util.VerificationCode;

import com.rome.common.util.smsutil.JavaSmsApi;
import io.reactivex.Single;

import io.vertx.ext.mail.MailMessage;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.mail.MailClient;
import io.vertx.reactivex.redis.RedisClient;


/**
 * Author:
 * Data:2019-05-13 13:10
 * Description:<>
 *
 * @author Trump
 */
public class AccountRepository {
    private AsyncSQLClient postgreSQLClient;
    private Vertx vertx;
    private MailClient mailClient;
    private RedisClient redisClient;

    public AccountRepository(AsyncSQLClient postgreSQLClient, Vertx vertx, MailClient mailClient,
                             RedisClient redisClient) {
        this.postgreSQLClient = postgreSQLClient;
        this.vertx = vertx;
        this.mailClient = mailClient;
        this.redisClient = redisClient;
    }

    /**
     * send E-mail
     *
     * @param useType
     * @param recipient
     * @return
     */
    public Single sendEmail(String useType, String recipient) {
        System.out.println(useType + recipient + "----");
        short effSeconds = 300;
        Integer mailCode = VerificationCode.getRandomNum();

        MailMessage message = new MailMessage();
        message.setFrom("879681805@qq.com");
        message.setTo(recipient);
        message.setCc("Another User <another@example.net>");
        message.setText("您的验证码:" + mailCode + ",有效期为:" + effSeconds / 60 + "分钟");
        return Single.concat(redisClient.rxSetex(recipient + useType, effSeconds, mailCode.toString()),
            mailClient.rxSendMail(message)).lastOrError();
    }


    public Single sendSMS(String useType, String userPhone) {
        System.out.println(userPhone + useType + "----");
        short effSeconds = 300;
        int smsCode = VerificationCode.getRandomNum();
        return redisClient.rxSetex(userPhone + useType, effSeconds, Integer.toString(smsCode)).flatMap(res -> {
            System.out.println(smsCode);
            System.out.println(res + "结果");
            if ("OK".equals(res)) {
                //发送短信验证码
                //new JavaSmsApi().pushSMS(userPhone, String.valueOf(smsCode));
                return Single.just("success");
            } else {
                return Single.just("false");
            }
        });

    }


//    public Completable getSmsCodeToLogin(JSONObject object) {
//        short effSeconds = 300;
//        System.out.println(object);
//        String userPhone = (String) object.get("userPhone");
//        String useType = (String) object.get("useType");
//        int smsCode = VerificationCode.getRandomNum();
//        System.out.println(smsCode);
//        return redisClient.rxSetex(userPhone + useType, effSeconds, Integer.toString(smsCode))
//            .filter("OK"::equals)
//            .flatMapCompletable((ignore) -> {
//                new JavaSmsApi().pushSMS(userPhone, String.valueOf(smsCode));
//                return Completable.complete();
//            });
//    }

}

