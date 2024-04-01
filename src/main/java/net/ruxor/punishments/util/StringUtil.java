package net.ruxor.punishments.util;

import java.util.Arrays;

public class StringUtil {

    public static String buildString(String[] args, int start) {
        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }
}
