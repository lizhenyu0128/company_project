package com.rome.uaa.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Author:
 * Data:2019-05-14 18:49
 * Description:<>
 *
 * @author Trump
 */


@Data
public class UserSignUp {
    @NotNull
    private String userAccount;
    @NotNull
    @Length(min = 8, max = 18, message = "密码长度必须为8-18")
    private String userPassword;
    @NotNull
    @Pattern(regexp="\\d{6}",message="支付密码必须是6位数字")
    private String payPassword;
    @NotNull
    @Email
    private String userMail;
    @NotNull
    @Length(min = 10, max = 18, message = "手机号长度必须为8-18")
    private String userPhone;
    private String createIp;
    private String usingIp;
    private Long lastLoginTime;
    private Long createTime;
    @NotNull
    private String nickName;
    @NotNull
    private String longitude;
    @NotNull
    private String latitude;
    @NotNull
    private String label;
    private String invitationCode;


}
