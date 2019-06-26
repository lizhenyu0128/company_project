package com.rome.wallet.entity;

import lombok.Data;
import lombok.NonNull;

/**
 * Author:
 * Data:2019-06-20 18:58
 * Description:<>
 * @author Trump
 */
@Data
public class Cash <T> {

    private String orderID;
    @NonNull
    private String toAddr;
    @NonNull
    private String coinPair;
    @NonNull
    private String amount;
    @NonNull
    private String phone;
    private String userID;
    @NonNull
    private String message;

    private T userContext;

}
