package com.rome.wallet.service;

import io.reactivex.Single;

/**
 * Author:
 * Data:2019-06-03 16:21
 * Description:<>
 *
 * @author Trump
 */
public interface WalletNativeService {

    /**
     * transaction coin
     * @param coinType
     * @param amount
     * @param userAccount
     * @param toAccount
     * @param message
     * @param payPassword
     * @return Single
     * @Author:sunYang
     */

    Single transactionCoin(String coinType, String amount, String userAccount, String toAccount,String message,String payPassword);


}
