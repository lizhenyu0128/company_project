package com.rome.common.status;

public class WalletStatus {

    /** create cash order*/
    public static final String CREATEORDER_SUCCESS="创建订单成功";
    public static final String CREATEORDER_FALSE="创建订单失败";

    /**cancel order*/
    public static final String CANCELORDER_SUCCESS="删除订单成功";
    public static final String CANCELORDER_FALSE="删除订单失败";
    public static final String CANCELORDER_TIMEFALSE="审核时间已过，不可修改";

    /**transaction coin*/
    public static final String TRANSACTIONCOIN_SUCCESS="交易成功";
    public static final String TRANSACTIONCOIN_FALSE="交易失败";
    public static final String TRANSACTIONCOIN_AMOUNTFALSE="余额不足";
    public static final String TRANSACTIONCOIN_CHECKTYPE="请输入正数";
    public static final String TRANSACTIONCOIN_PASSWORDFALSE="支付密码错误";

}
