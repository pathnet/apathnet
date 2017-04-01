package com.pathnet.network.model;

public class ErrorResponseBean<T> {
    private String errorMessage;
    private int code;

    public ErrorResponseBean() {
        super();
    }

    public ErrorResponseBean(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
