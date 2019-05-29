package com.rome.common.util;//package com.rome.uaa.util;
//
//import org.mindrot.jbcrypt.BCrypt;
//
///**
// * Author:
// * Data:2019-05-15 10:04
// * Description:<>
// */
//public class BCrypt1 {
//  public static void main(String[] args) {
//
//      String password = "hahhhah";
//      String hashed = BCrypt.hashpw(password,BCrypt.gensalt());//密文
//    // gensalt's log_rounds parameter determines the complexity
//    // the work factor is 2**log_rounds, and the default is 10
//    String hashed2 = BCrypt.hashpw(password, BCrypt.gensalt(12));
//    System.out.println(hashed+"秘文");
//    System.out.println(hashed2+"高级秘闻");
//    if(BCrypt.checkpw(password,hashed)){
//      System.out.println("对了，用1");
//    }
//      if(BCrypt.checkpw(password,hashed2)){
//        System.out.println("对了用2");
//      }
//    System.out.println(BCrypt.checkpw("hahhhah1" ,hashed));
//
//
//  }
//
//}
