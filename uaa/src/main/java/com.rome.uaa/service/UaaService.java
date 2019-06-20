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
     * reset password
     * @param newPassword
     * @param userAccount
     * @param code
     * @param content
     * @return Single
     * @Author: sunYang
     */
    Single resetPassword(String userAccount,String newPassword,String code,String content);

<<<<<<< HEAD

=======
    /**
     * set payPassword
     * @param userAccount
     * @param payPassword
     * @param userPassword
     * @return Single
     * @Author: sunYang
     */
    Single setPayPassword(String userAccount,String payPassword,String userPassword);

    /**
     * set payPassword
     * @param userAccount
     * @param payPassword
     * @param newPayPassword
     * @return Single
     * @Author: sunYang
     */
    Single updatePayPassword(String userAccount,String payPassword,String newPayPassword);

    RoutingContext bbb(RoutingContext routingContext);
>>>>>>> 0a72a34ace449df6f747e73d12433c789d1e9b7f
}
