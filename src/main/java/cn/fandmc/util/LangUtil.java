package cn.fandmc.util;

import cn.fandmc.Main;

public class LangUtil {
    public static String get(String config) {
        return Main.getconfig().color(config);
    }
}
