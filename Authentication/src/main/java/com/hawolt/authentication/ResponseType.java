package com.hawolt.authentication;

/**
 * Created: 25/08/2023 12:01
 * Author: Twitter @hawolt
 **/

public enum ResponseType {
    TOKEN, ID_TOKEN, CODE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
