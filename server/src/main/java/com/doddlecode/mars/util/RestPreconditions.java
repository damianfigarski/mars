package com.doddlecode.mars.util;

import com.doddlecode.mars.exception.MarsRuntimeException;

import java.util.Optional;

import static com.doddlecode.mars.exception.code.MarsExceptionCode.E016;

public class RestPreconditions {

    private RestPreconditions() {
        throw new AssertionError();
    }

    /**
     * Check if some value is not null, otherwise throw exception.
     *
     * @param resource has value true if not null, otherwise false
     * @throws MarsRuntimeException if expression is false, means value is null.
     */
    public static <T> T checkNotNull(T resource) {
        return Optional.ofNullable(resource)
                .orElseThrow(() -> new MarsRuntimeException(E016));
    }

}