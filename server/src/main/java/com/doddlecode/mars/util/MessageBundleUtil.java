package com.doddlecode.mars.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ResourceBundle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageBundleUtil {

    public static String getMessage(String key) {
        return ResourceBundle.getBundle("messages/messages")
                .getString(key);
    }

}
