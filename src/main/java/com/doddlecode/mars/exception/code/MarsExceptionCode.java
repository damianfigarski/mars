package com.doddlecode.mars.exception.code;

public enum MarsExceptionCode {
    E001("Account is already enabled"),
    E002("Repeated password, which user transferred to the server do not match with new password, which has to be assigned to the account."),
    E003("Password reset token was already used to change password"),
    E004("Password reset token date expired"),
    E005("Token is incorrect"),
    E006("UserAccount was not found by given email address"),
    E007("User already exists"),
    E008("Verification token date expired"),
    E009("Verification token not found"),
    E010("Password, which user transferred to the server as the old one do not match with existing one, which is assigned to the account."),
    E011("Wrong token"),
    E012("UserAccount not found"),
    E013("Error occured during authentication");

    private String message;

    MarsExceptionCode(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
