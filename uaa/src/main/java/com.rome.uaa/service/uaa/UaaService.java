package com.rome.uaa.service.uaa;

import com.rome.uaa.entity.UserSignUp;
import com.rome.uaa.entity.UserSingIn;
import io.reactivex.Completable;
import io.reactivex.Single;


/**
 * Author:
 * Data:2019-05-10 19:36
 * Description:<>
 * @author lizhenyu
 */
public interface UaaService {

  /**
   *  user sign up
   * @param userSignUp
   * @return Single
   */
  Single userSignUp( UserSignUp userSignUp);

  /**
   *  user login
   * @param userSingIn
   * @return Single
   */
  Single userLogin(  UserSingIn userSingIn);

  /**
   *  check identity by jwt
   * @param token
   * @return Single
   */
  Single checkIdentity(String token);

  /**
   * get SMS code
   * @param userPhone
   * @return Single
   */
  Completable getSmsCodeToLogin(String userPhone);

  /**
   * send a mail
   * @param useType
   * @param recipient
   * @return Single
   */
  Single sendEmail(String useType,String recipient);

}
