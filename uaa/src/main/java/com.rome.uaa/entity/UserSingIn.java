package com.rome.uaa.entity;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
/**
 * Author:
 * Data:2019-05-26 11:03
 * Description:<>
 * @author lizhenyu
 */

@Data
public class UserSingIn {
  @NonNull
  private String loginType;
  @NonNull
  @Length(min = 8, max = 18, message = "密码长度必须为8-18")
  private String userPassword;
  @NonNull
  private String userMail;
  @NonNull
  @Length(min = 10, max = 18, message = "手机号长度必须为8-18")
  private String userPhone;
  @NonNull
  private String smsCode;
  @NonNull
  private String usingIp;
  private Long lastLoginTime;
  @NonNull
  private String longitude;
  @NonNull
  private String latitude;
}
