package ru.vk.etcos.tasklist.util;

import lombok.extern.java.*;

@Log
public class CLogger {

    public static void info(String message) {
        log.info(message);
    }

    public static void warn(String message) {
        log.warning(message);
    }

}
