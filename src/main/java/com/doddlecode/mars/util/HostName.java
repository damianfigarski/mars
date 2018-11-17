package com.doddlecode.mars.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class HostName {

    private static final Logger log = LoggerFactory.getLogger(HostName.class);
    private static final String HOST_NAME;

    static {
        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
            hostName = "Unknown host";
        }
        HOST_NAME = hostName;
    }

    public static String hostName() {
        return HOST_NAME;
    }

}
