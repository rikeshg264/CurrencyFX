package com.example.gurung_rikesh_s2426621;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for formatting timestamps consistently.
 */
public final class DateUtils {

    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.UK);

    private DateUtils() {}

    /** Returns: "Last updated: 10 Feb 2025, 14:32" */
    public static String formatLastUpdateTime() {
        return "Last updated: " + FORMAT.format(new Date());
    }

    /** Returns: "Updated 10 Feb 2025, 14:32" */
    public static String formatDetailTimestamp() {
        return "Updated " + FORMAT.format(new Date());
    }
}
