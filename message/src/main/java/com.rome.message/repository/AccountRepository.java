package com.rome.message.repository;
import com.rome.common.status.MessageStatus;
import com.rome.common.util.VerificationCode;
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
    private MailClient mailClient;
    private RedisClient redisClient;

    public AccountRepository(AsyncSQLClient postgreSQLClient, Vertx vertx, MailClient mailClient,
                             RedisClient redisClient) {
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
        int mailCode = VerificationCode.getRandomNum();
        MailMessage message = new MailMessage();
        message.setFrom(MessageStatus.SEND_EMAIL_SET_FROM);
        message.setTo(recipient);
        message.setCc(MessageStatus.SEND_EMAIL_SET_CC);
        message.setText(MessageStatus.SEND_EMAIL_MESSAGE0 + mailCode + MessageStatus.SEND_EMAIL_MESSAGE1 + effSeconds / 60 + MessageStatus.SEND_EMAIL_MESSAGE2);
        return Single.concat(redisClient.rxSetex(recipient + useType, effSeconds, Integer.toString(mailCode)),
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
                //发送短信验证码 .
                //new JavaSmsApi().pushSMS(userPhone, String.valueOf(smsCode));
                return Single.just("success");
            } else {
                return Single.just("false");
            }
        });
    }



}

