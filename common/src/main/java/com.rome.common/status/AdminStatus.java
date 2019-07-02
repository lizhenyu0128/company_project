package com.rome.common.status;

/**
 * @author asus
 */
public class AdminStatus {

    /** inquire balance*/
    public static final String INQUIREBALANCE_SUCCESS="查询成功";
    public static final String INQUIREBALANCE_PERMISSION="当前用户权限不够";
    public static final String INQUIREBALANCE_FALSE="查询失败";

    /** inquire balance by userAccount*/
    public static final String INQUIREBYUSERACCOUNT_SUCCESS="查询成功";
    public static final String INQUIREBYUSERACCOUNT_PERMISSION="当前用户权限不够";
    public static final String INQUIREBYUSERACCOUNT_FALSE="查询的用户不存在";

    /** Add transaction record to (wallet_native_btc,wallet_native_eos，wallet_native_eth，wallet_native_usdt)*/
    public static final String ADDTRANSACTIONRECORD_SUCCESS="添加订单成功";
    public static final String ADDTRANSACTIONRECORD_PERMISSION="当前用户权限不够";
    public static final String ADDTRANSACTIONRECORD_AMOUNT="当前用户余额不足";
    public static final String ADDTRANSACTIONRECORD_FALSE="添加订单失败";

}
