package ru.vk.etcos.tasklist.util;

import java.util.logging.*;

import lombok.extern.java.*;

@Log
public class CLogger {

    public static void info(String message) {
        log.info(message);
    }

    public static void warn(String message) {
        log.warning(message);
    }

    public static void fatal(String message, Exception e) {
        log.log(Level.SEVERE, message, e);
    }
}
