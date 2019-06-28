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
     *@param userSignUp
     * @param invitationCode
     * @return Single
     */
    Single userSignUp(UserSignUp userSignUp,String invitationCode);

    /**
     * user login
     * @param userSingIn
     * @param userType
     * @return Single
     */
    Single userLogin(UserSingIn userSingIn,String userType);

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

    /**
     *update nickName
     * @param userAccount
     * @param nickName
     * @return Single
     * @Author: sunYang
     */
    Single updateNickName(String userAccount,String nickName);

    /**
     *set headImage
     * @param userAccount
     * @param headImage
     * @return Single
     * @Author: sunYang
     */
    Single setHeadImage(String userAccount,String headImage);

    RoutingContext bbb(RoutingContext routingContext);
}
