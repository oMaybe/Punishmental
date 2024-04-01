package net.ruxor.punishments.util;

import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {
    public static String DATE_FORMAT_NOW;
    public static String DATE_FORMAT_DAY;

    public static String now() {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static long nowlong() {
        return System.currentTimeMillis();
    }

    public static String when(final long time) {
        final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        return sdf.format(time);
    }

    public static String formatTimeMillis(long millis) {
        long seconds = millis / 1000L;

        if (seconds <= 0) {
            return "0 seconds";
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        long day = hours / 24;
        hours = hours % 24;
        long years = day / 365;
        day = day % 365;

        StringBuilder time = new StringBuilder();

        if (years != 0) {
            time.append(years).append(years == 1 ? " year " : " years ");
        }

        if (day != 0) {
            time.append(day).append(day == 1 ? " day " : " days ");
        }

        if (hours != 0) {
            time.append(hours).append(hours == 1 ? " hour " : " hours ");
        }

        if (minutes != 0) {
            time.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
        }

        if (seconds != 0 && day == 0) { // remove && day == 0 if it should show seconds when days are present
            time.append(seconds).append(seconds == 1 ? " second " : " seconds ");
        }

        return time.toString().trim();
    }

    public static long parseTime(String time) {
        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);

        while (matcher.find()) {
            String s = matcher.group();
            Long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            String type = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];

            switch (type) {
                case "s": {
                    totalTime += value;
                    found = true;
                    continue;
                }
                case "m": {
                    totalTime += value * 60L;
                    found = true;
                    continue;
                }
                case "h": {
                    totalTime += value * 60L * 60L;
                    found = true;
                    continue;
                }
                case "d": {
                    totalTime += value * 60L * 60L * 24L;
                    found = true;
                    continue;
                }
                case "w": {
                    totalTime += value * 60L * 60L * 24L * 7L;
                    found = true;
                    continue;
                }
                case "mo": {
                    totalTime += value * 60L * 60L * 24L * 30L;
                    found = true;
                    continue;
                }
                case "y": {
                    totalTime += value * 60L * 60L * 24L * 365L;
                    found = true;
                }
            }
        }

        return found ? (totalTime * 1000L) + 1000L : -1L;
    }

    public static String date() {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    public static String getTime(final int time) {
        final Date timeDiff = new Date();
        timeDiff.setTime(time * 1000);
        final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        final String eventTimeDisplay = timeFormat.format(timeDiff);
        return eventTimeDisplay;
    }

    public static String getFormattedGuiTime(final long time) {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm");
        final String eventTimeDisplay = timeFormat.format(time);
        return eventTimeDisplay;
    }

    public static String since(final long epoch) {
        return "Took " + convertString(System.currentTimeMillis() - epoch, 1, TimeUnit.FIT) + ".";
    }

    public static double convert(final long time, final int trim, TimeUnit type) {
        if (type == TimeUnit.FIT) {
            type = ((time < 60000L) ? TimeUnit.SECONDS : ((time < 3600000L) ? TimeUnit.MINUTES : ((time < 86400000L) ? TimeUnit.HOURS : TimeUnit.DAYS)));
        }
        if (type == TimeUnit.DAYS) {
            return UtilMath.trim(trim, time / 8.64E7);
        }
        if (type == TimeUnit.HOURS) {
            return UtilMath.trim(trim, time / 3600000.0);
        }
        if (type == TimeUnit.MINUTES) {
            return UtilMath.trim(trim, time / 60000.0);
        }
        if (type == TimeUnit.SECONDS) {
            return UtilMath.trim(trim, time / 1000.0);
        }
        return UtilMath.trim(trim, time);
    }
    public static String MakeStr(final long time) {
        return convertString(time, 1, TimeUnit.FIT);
    }

    public static String MakeStr(final long time, final int trim) {
        return convertString(time, trim, TimeUnit.FIT);
    }

    public static String convertString(final long time, final int trim, TimeUnit type) {
        if (time == -1L) {
            return "Permanent";
        }
        if (type == TimeUnit.FIT) {
            type = ((time < 60000L) ? TimeUnit.SECONDS : ((time < 3600000L) ? TimeUnit.MINUTES : ((time < 86400000L) ? TimeUnit.HOURS : TimeUnit.DAYS)));
        }
        if (type == TimeUnit.DAYS) {
            return "" + UtilMath.trim(trim, time / 8.64E7) + " Days";
        }
        if (type == TimeUnit.HOURS) {
            return "" + UtilMath.trim(trim, time / 3600000.0) + " Hours";
        }
        if (type == TimeUnit.MINUTES) {
            return "" + UtilMath.trim(trim, time / 60000.0) + " Minutes";
        }
        if (type == TimeUnit.SECONDS) {
            return "" + UtilMath.trim(trim, time / 1000.0) + " Seconds";
        }
        return "" + UtilMath.trim(trim, time) + " Milliseconds";
    }

    public static boolean elapsed(final long from, final long required) {
        return System.currentTimeMillis() - from > required;
    }

    public static long elapsed(final long starttime) {
        return System.currentTimeMillis() - starttime;
    }

    public static long left(final long start, final long required) {
        return required + start - System.currentTimeMillis();
    }



    static {
        TimeUtils.DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
        TimeUtils.DATE_FORMAT_DAY = "yyyy-MM-dd";
    }

    public enum TimeUnit
    {
        FIT,
        DAYS,
        HOURS,
        MINUTES,
        SECONDS,
        MILLISECONDS;
    }
}