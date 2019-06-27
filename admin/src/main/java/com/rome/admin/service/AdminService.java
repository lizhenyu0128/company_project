package com.rome.admin.service;

import io.reactivex.Single;

/**
 * @author asus
 */
public interface AdminService {

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

    /**
     * Add transaction record  to (wallet_native_btc,wallet_native_eos，wallet_native_eth，wallet_native_usdt)
     * @param userAccount
     * @param toAccount
     * @param fromAccount
     * @param amount
     * @param coinType
     * @param message
     * @return Single
     * @Author: sunYang
     */
    Single addTransactionRecord(String userAccount, String toAccount, String fromAccount,String amount,String coinType,String message);

}
