package com.doddlecode.mars.exception.code;

public enum MarsExceptionCode {
    E001("account.is.already.enabled"),
    E002("repeated.password.do.not.match"),
    E003("password.reset.token.was.already.used.to.change.password"),
    E004("password.reset.token.date.expired"),
    E005("token.is.incorrect"),
    E006("user.account.was.not.found.by.email"),
    E007("user.already.exists"),
    E008("verification.token.date.expired"),
    E009("verification.token.not.found"),
    E010("password.do.not.match.with.old.one"),
    E011("wrong.token"),
    E012("user.account.not.found"),
    E013("error.occured.during.authentication"),
    E014("error.during.createtin.mime.message.helper"),
    E015("resource.not.found"),
    E016("value.is.null");

    private String message;

    MarsExceptionCode(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
