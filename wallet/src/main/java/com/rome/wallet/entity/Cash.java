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

    @NonNull
    private String orderID;
    @NonNull
    private String toAddr;
    @NonNull
    private String coinPair;
    @NonNull
    private Double amount;
    @NonNull
    private String phone;
    @NonNull
    private String userID;

    private T userContext;

}
