package com.rome.wallet.service;

import com.rome.wallet.entity.Cash;
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

    /**
     *  cancel Order
     * @param cashId
     * @param coinType
     * @param userAccount
     * @return Single
     * @Author: sunYang
     */
    Single cancelOrder(String cashId,String coinType,String userAccount);

    /**
     *  create cash order
     * @param userAccount
     * @param cash
     * @param coinType
     * @return Single
     * @Author: sunYang
     */
    Single createCashOrder(String userAccount, Cash cash, String coinType);

    /**
     *  inquire balance
     * @param userAccount
     * @return Single
     * @Author: sunYang
     */
    Single inquireBalance(String userAccount);

    /**
     *  inquire balance by userAccount
     * @param userAccount
     * @param inquireAccount
     * @return Single
     * @Author: sunYang
     */
    Single inquireByUserAccount(String userAccount,String inquireAccount);
}
