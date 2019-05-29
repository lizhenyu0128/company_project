package com.rome.uaa.entity;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

/**
 * Author:
 * Data:2019-05-14 18:49
 * Description:<>
 * @author lizhenyu
 */


@Data
public class UserSignUp {
  @NonNull
  @Length(min = 8, max = 18, message = "用户名长度必须为8-18")
  private String userAccount;
  @NonNull
  @Length(min = 8, max = 18, message = "密码长度必须为8-18")
  private String userPassword;
  @NonNull
  @Email
  private String userMail;
  @NonNull
  @Length(min = 10, max = 18, message = "手机号长度必须为8-18")
  private String userPhone;
  @NonNull
  private String createIp;
  @NonNull
  private String usingIp;
  private Long lastLoginTime;
  private Long createTime;
  @NonNull
  private String nickName;
  @NonNull
  private String longitude;
  @NonNull
  private String latitude;


}
