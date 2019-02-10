package com.doddlecode.mars.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public final class HostName {

    private static final String HOST_NAME;

    static {
        InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
            hostname = "Unknown host";
        }
        HOST_NAME = hostname;
    }

    public static String hostName() {
        return HOST_NAME;
    }

}
