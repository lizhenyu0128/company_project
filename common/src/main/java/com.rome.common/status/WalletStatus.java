package com.rome.common.status;

public class WalletStatus {

    /** create cash order*/
    public static final String CREATE_ORDER_SUCCESS="创建订单成功";
    public static final String CREATE_ORDER_FALSE="创建订单失败";

    /**cancel order*/
    public static final String CANCEL_ORDER_SUCCESS="删除订单成功";
    public static final String CANCEL_ORDER_FALSE="删除订单失败";
    public static final String CANCEL_ORDER_TIME_FALSE="审核时间已过，不可修改";

    /**transaction coin*/
    public static final String TRANSACTION_COIN_SUCCESS="交易成功";
    public static final String TRANSACTION_COIN_FALSE="交易失败";
    public static final String TRANSACTION_COIN_AMOUNT_FALSE="余额不足";
    public static final String TRANSACTION_COIN_CHECK_TYPE="请输入正数";
    public static final String TRANSACTION_COIN_PASSWORD_FALSE="支付密码错误";
    public static final String TRANSACTION_COIN_ACCOUNT_FALSE="转账用户不存在";

}
