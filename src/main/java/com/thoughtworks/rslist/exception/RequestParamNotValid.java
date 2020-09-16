package com.thoughtworks.rslist.exception;

public class RequestParamNotValid extends RuntimeException {
    private String errorMessage;

    public RequestParamNotValid(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
