package com.rome.uaa.service;

import com.rome.uaa.entity.BasicUserInfo;
import com.rome.uaa.entity.UserSignUp;
import com.rome.uaa.entity.UserSingIn;
import io.reactivex.Completable;
import io.reactivex.Single;


/**
 * Author:
 * Data:2019-05-10 19:36
 * Description:<>
 *
 * @author Trump
 */
public interface UaaService {

    /**
     * user sign up
     *
     * @param userSignUp
     * @return Single
     */
    Single userSignUp(UserSignUp userSignUp);

    /**
     * user login
     *
     * @param userSingIn
     * @return Single
     */
    Single userLogin(UserSingIn userSingIn);

    /**
     * check identity by jwt
     *
     * @param token
     * @return Single
     */
    Single checkIdentity(String token);

    Single resetPassword(String phonePrMail, String codeType, String verificationCode);

    /**
     * update basic user information
     *
     * @param basicUserInfo
     * @return
     */
    Single updateBasicUserInfo(BasicUserInfo basicUserInfo);
}
