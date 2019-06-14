package com.rome.uaa.service;

import com.rome.uaa.entity.BasicUserInfo;
import com.rome.uaa.entity.UserSignUp;
import com.rome.uaa.entity.UserSingIn;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.reactivex.ext.web.RoutingContext;


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

    /**
     * reset password
     * @param newPassword
     * @param userAccount
     * @param code
     * @param content
     * @return Single
     * @Author: sunYang
     */
    Single resetPassword(String userAccount,String newPassword,String code,String content);

    RoutingContext bbb(RoutingContext routingContext);
}
