package com.example.core.util;

import java.util.Objects;
import org.springframework.core.env.Environment;

public class Util {

    public static int getIntProperty(Environment env, String key){
        String value = Objects.requireNonNull(env.getProperty(key));
        return Integer.parseInt(value);
    }

    public static Long getLongProperty(Environment env, String key){
        String value = Objects.requireNonNull(env.getProperty(key));
        return Long.parseLong(value);
    }

    public static boolean getBooleanProperty(Environment env, String key) {
        String value = Objects.requireNonNull(env.getProperty(key));
        return Boolean.parseBoolean(value);
    }

    public static boolean isNullOrBlank(String value) {
        if (value == null) {
            return true;
        }
        return value.isBlank();
    }
}
