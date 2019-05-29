package com.rome.common.smtp;

import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.StartTLSOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.mail.MailClient;

/**
 * Author:
 * Data:2019-05-26 17:19
 * Description:<>
 * @author lizhenyu
 */
public class SMTPConfig {
  public static MailClient creatSMTPClient(Vertx vertx, int port, String hostName, String userName, String passWord) {
    MailConfig config = new MailConfig();
    config.setHostname(hostName);
    config.setPort(port);
    config.setStarttls(StartTLSOptions.REQUIRED);
    config.setUsername(userName);
    config.setPassword(passWord);
    return MailClient.createNonShared(vertx, config);
  }
}
