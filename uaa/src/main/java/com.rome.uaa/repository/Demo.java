package com.rome.uaa.repository;

/**
 * Author:
 * Data:2019-05-26 17:05
 * Description:<>
 * @author Trump
 */
public class Demo {
}
//  public Single userLogin(JSONObject jsonObject) {
//    System.out.println("开始请求");
//    return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn -> {
//      return conn.rxQuery("SELECT * FROM basic_account")
//        .flatMap(resultSet -> {
//          System.out.println(resultSet.getRows());
//          System.out.println("-----1");
//          return conn.rxQuery("SELECT * FROM basic_account")
//            .doOnSuccess(res2 -> {
//
//            });
//        });
//
//    });
//  }
//}

//  public Single userLogin(JSONObject jsonObject) {
//    System.out.println("开始请求");
//    return  SQLClientHelper.inTransactionSingle(postgreSQLClient,conn->{
//      return conn.rxQuery("SELECT * FROM basic_account")
//        .flatMap(resultSet -> {
//          System.out.println(resultSet.getRows());
//          System.out.println("-----1");
//          return conn.rxQuery("SELECT * FROM basic_account")
//            .doOnSuccess(res2->{
//              System.out.println(res2.getRows());
//              System.out.println("-----走到2");
//            });
//        });
//
//    });
//  }}


//  public Single userLogin(JSONObject jsonObject) {
//    System.out.println("开始请求");
//    return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn -> {
//        return conn.rxSetTransactionIsolation(TransactionIsolation.NONE)
//          .andThen(conn.rxQuery("SELECT * FROM basic_account"))
//
//
//    });
//  }
//}

//
//  public Maybe<String> userLogin(JSONObject jsonObject) {
//    System.out.println("开始请求");
//    return SQLClientHelper.inTransactionMaybe(postgreSQLClient, conn -> {
//        return conn.rxQuerySingle("SELECT * FROM basic_account").map(ress->{
//          System.out.println("1+++"+ress.getString(1));
//          System.out.println("er ci chaxun ");
//          return "asd";
//        });
//    });
//  }
//}

//  public Single userLogin(JSONObject jsonObject) {
//    System.out.println("开始请求");
//    return SQLClientHelper.inTransactionSingle(postgreSQLClient, conn -> {
//      return conn.rxSetTransactionIsolation(TransactionIsolation.READ_COMMITTED)
//        .andThen(
//          conn.rxQuery("SELECT * FROM basic_account")
//            .flatMap(r -> {
//              return conn.rxQuery("insert into asd values(112)")
//                .concatWith(conn.rxQuery("insert into asd values(113)"))
//                .lastOrError();
//            })
//            .concatWith(conn.rxQuery("SELECT * FROM basic_account"))
//            .lastOrError()
//        );
//    });
//  }
//}


//  public Maybe<String> userLogin(JSONObject jsonObject) {
//    System.out.println("开始请求");
//    return SQLClientHelper.inTransactionMaybe(postgreSQLClient, conn -> {
//      return conn.rxUpdate("insert into asd values(4)").toMaybe().map(rrr -> {
//        System.out.println(rrr.getUpdated());
//        return "asd";
//      });
//    });
//  }
//}


//  public Maybe<List<JsonObject>> userLogin(JSONObject jsonObject) {
//    System.out.println("开始请求");
//    return SQLClientHelper.inTransactionMaybe(postgreSQLClient, conn -> {
//      return conn.rxQuery("SELECT * FROM basic_account").toMaybe().map(rrr->{
//        List<JsonObject> a = rrr.getRows();
//        return a;
//      });
//    });
//  }
//}

//  user login
//  rxExecute 执行给定的sql语句
//  public Maybe<List> userLogin(JSONObject jsonObject) {
//    System.out.println("开始请求");
//    return SQLClientHelper.inTransactionMaybe(postgreSQLClient, conn -> {
//      return conn.rxQuerySingle("SELECT * FROM basic_account").map(rrr->{
//        return rrr.getList();
//      });
//    });
//  }
//}

//  user login
//  rxExecute 执行给定的sql语句
//  public Maybe userLogin(JSONObject jsonObject) {
//    System.out.println("开始请求");
//    return SQLClientHelper.inTransactionMaybe(postgreSQLClient, conn -> {
//      return conn.rxExecute("insert into asd values('222222')").toObservable().
//    });
//  }
//}
//public Maybe userLogin(JSONObject jsonObject) {
//  System.out.println("开始请求");
//  return SQLClientHelper.inTransactionMaybe(postgreSQLClient, conn -> {
//    return conn.rxQuerySingle("SELECT * FROM basic_account")
//      .map(t -> {
//        System.out.println(t);
//        return conn.rxExecute("insert into asd values('11111')").toMaybe();
//      });
//  });
//}
