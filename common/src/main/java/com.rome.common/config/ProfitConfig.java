package com.rome.common.config;

import com.sun.deploy.config.Config;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;

import java.util.Properties;


/**
 * @author asus
 */
public class ProfitConfig{

    private Vertx vertx;
    private AsyncSQLClient postgreSQLClient;

    public ProfitConfig(AsyncSQLClient postgreSQLClient, Vertx vertx){
        this.postgreSQLClient=postgreSQLClient;
        this.vertx=vertx;
    }


    public  Single selectProfit(Vertx vertx, io.vertx.reactivex.core.AbstractVerticle verticle, String path){
        return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn->
            conn.rxQueryWithParams("SELECT profit_ratio,cashback_level_start,cashback_level_end FROM profit_details",new JsonArray()).flatMap(res->{
                JsonObject  profit=new JsonObject();
                if (res.getRows().isEmpty()){
                    return Single.just("error");
                }
                profit.put("profitDetails",res.getRows());
                verticle.config().put("profit",profit);
                return Single.just("success");
            })
        );
    }




}
