package com.rome.common.config;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.StartTLSOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.mail.MailClient;

/**
 * Author:
 * Data:2019-05-26 17:19
 * Description:<>
 * @author Trump
 */
public class SMTPConfig {
  public static MailClient creatSMTPClient(Vertx vertx, JsonObject smtpConfig) {
    MailConfig config = new MailConfig();
    config.setHostname(smtpConfig.getString("hostName"));
    config.setPort(smtpConfig.getInteger("port"));
    config.setStarttls(StartTLSOptions.REQUIRED);
    config.setUsername(smtpConfig.getString("userName"));
    config.setPassword(smtpConfig.getString("passWord"));
      return MailClient.createNonShared(vertx, config);
  }
}
