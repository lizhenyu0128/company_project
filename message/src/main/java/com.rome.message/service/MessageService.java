package com.rome.message.service;

import io.reactivex.Single;

/**
 * Author:
 * Data:2019-06-03 16:21
 * Description:<>
 *
 * @author Trump
 */
public interface MessageService {


    /**
     * 发送一个验证码
     *
     * @param messageType 消息类型
     * @param useType     使用的类型 mail or phone
     * @param content     验证码内容
     * @return Single
     */
    Single getVerificationCode(String messageType, String useType, String content);

}
