package com.rome.uaa.entity;

import lombok.Data;

/**
 * Author:
 * Data:2019-06-12 09:40
 * Description:<>
 */
@Data
public class Token {
    private int identity_id;
    private String iss;
    private String user_account;
    private long exp;
    private String iat;
}
