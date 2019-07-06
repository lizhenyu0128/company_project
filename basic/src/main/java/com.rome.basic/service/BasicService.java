package com.rome.basic.service;

import io.reactivex.Single;

/**
 * @author asus
 */
public interface BasicService {

    /**
     * set file
     * @param imageByte
     * @param userAccount
     * @return Single
     */
    Single setFile(String imageByte,String userAccount);

}
