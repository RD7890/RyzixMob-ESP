package com.ryzix.mobesp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RyzixMobESP {
    public static final String MOD_ID = "ryzixmobesp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void log(String message) {
        LOGGER.info("[RyzixMobESP] " + message);
    }
}
