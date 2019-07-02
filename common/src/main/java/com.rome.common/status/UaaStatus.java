package com.rome.common.status;

/**
 * @author asus
 */
public class UaaStatus {

    /** Sign up */
    public static final String SIGNUP_SUCCESS="注册成功";
    public static final String SIGNUP_FALSE="注册失败";
    public static final String SIGNUP_REFALSE="账户已经注册了";

    /**  user sign in */
    public static final String SIGNIN_SUCCESS="登录成功";
    public static final String SIGNIN_FALSE="登录失败";
    public static final String SIGNIN_NULLFALSE="用户不存在";
    public static final String SIGNIN_PASSWORDFALSE="密码错误";
    public static final String SIGNIN_CODEFALSE="验证码错误";

    /**  send SMS or mail */
    public static final String SENGMSG_SUCCESS="发送成功";
    public static final String SENGMSG_FALSE="发送失败";

    /**   reset password */
    public static final String RESETPASSWORD_SUCCESS="修改成功";
    public static final String RESETPASSWORD_FALSE="修改失败";

    /**  set payPassword */
    public static final String SETPAYPASSWORD_SUCCESS="设置成功";
    public static final String SETPAYPASSWORD_PASSWORDFALSE="密码验证失败";
    public static final String SETPAYPASSWORD_PAYPASSWORDINPUT="请输入支付密码";
    public static final String SETPAYPASSWORD_PAYPASSWORDRESET="已设置支付密码";

    /**  update payPassword */
    public static final String UPDATEPAYPASSWORD_SUCCESS="修改成功";
    public static final String UPDATEPAYPASSWORD_FALSE="支付密码输入错误";

    /**  update nickName*/
    public static final String UPDATENICKNAME_SUCCESS="修改成功";
    public static final String UPDATENICKNAME_FALSE="修改失败";

    /**  set head image */
    public static final String SETHEADIMAGE_SUCCESS="设置成功";
    public static final String SETHEADIMAGE_FALSE="设置失败";

    /**  get  Mnemonics*/
    public static final String GETMNEMONICS_SUCCESS="获取成功";
    public static final String GETMNEMONICS_FALSE="获取失败";

    /**  get  PrivateKey*/
    public static final String GETPRIVATEKEY_SUCCESS="获取成功";
    public static final String GETPRIVATEKEY_FALSE="获取失败";








}
