package com.rome.uaa.entity;

import io.reactivex.annotations.NonNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;

/**
 * Author:
 * Data:2019-05-26 11:03
 * Description:<>
 * @author Trump
 */

@Data
public class UserSingIn {

  private String userAccount;

  @Length(min = 8, max = 18, message = "密码长度必须为8-18")
  private String userPassword;

  @Email
  private String userMail;

  @Length(min = 10, max = 18, message = "手机号长度必须为8-18")
  private String userPhone;

  private String verificationCode;

  private String usingIp;

  private Long lastLoginTime;

  @NonNull
  private String longitude;

  @NonNull
  private String latitude;
}
