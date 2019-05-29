package com.rome.common.dbutil;


import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.asyncsql.PostgreSQLClient;

/**
 * Author:
 * Data:2019-05-17 15:59
 * Description:<>
 *
 * @author lizhenyu
 */
public class PostgresqlPool {

  private static volatile PostgresqlPool postgresqlPool = null;
  private static AsyncSQLClient asyncSQLClient;

  /**
   * 设置数据库连接池参数
   */
  private static JsonObject dbConfig = new JsonObject()
    .put("host", "localhost")
    .put("driver_class", "org.postgresql.Driver")
    .put("username", "lizhenyu")
    .put("password", "")
    .put("database", "company_db");

  private PostgresqlPool() {
  }

  public static PostgresqlPool getInstance(Vertx vertx) {
    if (postgresqlPool == null) {
      synchronized (PostgresqlPool.class) {
        if (postgresqlPool == null) {
          postgresqlPool = new PostgresqlPool();
          asyncSQLClient = PostgreSQLClient.createShared(vertx, dbConfig);
        }
      }
    }
    return postgresqlPool;
  }

  public AsyncSQLClient getPostClient() {
    return asyncSQLClient;
  }

}
