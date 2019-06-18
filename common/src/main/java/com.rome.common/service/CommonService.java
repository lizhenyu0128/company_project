package com.rome.common.service;

import io.reactivex.Single;

/**
 * Author:
 * Data:2019-06-15 14:16
 * Description:<>
 */
public interface CommonService {
    /**
     * check identity by jwt
     *
     * @param token
     * @return Single
     */
    Single checkIdentity(String token);
}
