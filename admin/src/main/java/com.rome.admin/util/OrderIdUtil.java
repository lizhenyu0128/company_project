package com.rome.admin.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Author:
 * Data:2019-06-19 10:46
 * Description:<>
 */

public  class OrderIdUtil {



    private static String date;
    private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
        "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
        "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
        "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
        "W", "X", "Y", "Z" };




    /**
     * 生成订单编号
     *
     * @return
     */
    public static synchronized String getOrderNo(String userAccount) {
        StringBuilder shortBuffer = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        String rString = shortBuffer.toString();
        String str = new SimpleDateFormat("yyMMddHHmm").format(new Date());
        if (date == null || !date.equals(str)) {
            date = str;
        }

        return date + userAccount.subSequence(0, 3)+rString.subSequence(0,5);
    }
}
