package com.rome.common.util.smsutil;

import java.util.Random;

/**
 * Author:
 * Data:2019-05-22 17:01
 * Description:<>
 * @author lizhenyu
 */
public class VerificationCode {
  public static int getRandomNum() {

    Random r = new Random();

    //(Math.random()*(999999-100000)+100000)
    return r.nextInt(900000) + 100000;

  }
}
