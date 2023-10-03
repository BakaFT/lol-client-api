package com.hawolt.virtual.leagueclient.exception;

/**
 * Created: 10/01/2023 19:33
 * Author: Twitter @hawolt
 **/

public class LeagueException extends Exception {
    public enum ErrorType {
        BAD_USERINFORMATION, FAILED_TO_INITIALIZE, UNSUCCESSFUL_INITIALIZATION
    }

    private final ErrorType type;

    public LeagueException(ErrorType type) {
        super(type.name());
        this.type = type;
    }

    public ErrorType getType() {
        return type;
    }
}
