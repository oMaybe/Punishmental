package net.ruxor.punishments.profiler.util;

public class MathHelper {
    public static int ceiling_double_int(double value) {
        int i = (int) value;
        return value > (double) i ? i + 1 : i;
    }
}
