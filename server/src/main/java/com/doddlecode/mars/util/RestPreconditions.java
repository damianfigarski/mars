package com.doddlecode.mars.util;

import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.exception.code.MarsExceptionCode;

public class RestPreconditions {

    private RestPreconditions() {
        throw new AssertionError();
    }

    /**
     * Check if some value was found, otherwise throw exception.
     *
     * @param expression
     *            has value true if found, otherwise false
     * @throws MarsRuntimeException
     *             if expression is false, means value not found.
     */
    public static void checkFound(final boolean expression) {
        if (!expression) {
            throw new MarsRuntimeException(MarsExceptionCode.E015);
        }
    }

    /**
     * Check if some value was found, otherwise throw exception.
     *
     * @param resource
     *            has value true if found, otherwise false
     * @throws MarsRuntimeException
     *             if expression is false, means value not found.
     */
    public static <T> T checkFound(final T resource) {
        if (resource == null) {
            throw new MarsRuntimeException(MarsExceptionCode.E015);
        }

        return resource;
    }

    /**
     * Check if some value is not null, otherwise throw exception.
     *
     * @param resource
     *            has value true if not null, otherwise false
     * @throws MarsRuntimeException
     *             if expression is false, means value is null.
     */
    public static <T> T checkNotNull(final T resource) {
        if (resource == null) {
            throw new MarsRuntimeException(MarsExceptionCode.E016);
        }

        return resource;
    }

}