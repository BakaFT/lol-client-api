package com.hawolt.virtual.client;

/**
 * Created: 23/08/2023 13:24
 * Author: Twitter @hawolt
 **/

public class RiotClientException extends Exception {
    public RiotClientException(RiotClientExceptionType type) {
        super(type.name());
    }
}
