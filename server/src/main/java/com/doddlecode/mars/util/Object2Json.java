package com.doddlecode.mars.util;

import com.doddlecode.mars.exception.MarsRuntimeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

import static com.doddlecode.mars.exception.code.MarsExceptionCode.E016;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Object2Json {

    public static String convert(Object object) throws JsonProcessingException, MarsRuntimeException {
        object = getObjectIfNotNull(object);
        return new ObjectMapper()
                .writeValueAsString(object);
    }

    private static Object getObjectIfNotNull(Object object) {
        return Optional.ofNullable(object)
                .orElseThrow(() -> new MarsRuntimeException(E016));
    }

}
