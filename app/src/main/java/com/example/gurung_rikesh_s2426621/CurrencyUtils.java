package com.example.gurung_rikesh_s2426621;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CurrencyUtils {

    private CurrencyUtils() {}

    // Currency → Country code lookup
    private static final Map<String, String> CURRENCY_TO_COUNTRY = new HashMap<>();
    static {
        // Major currencies
        CURRENCY_TO_COUNTRY.put("USD", "us");
        CURRENCY_TO_COUNTRY.put("EUR", "eu");
        CURRENCY_TO_COUNTRY.put("GBP", "gb");
        CURRENCY_TO_COUNTRY.put("JPY", "jp");
        CURRENCY_TO_COUNTRY.put("CHF", "ch");

// Americas
        CURRENCY_TO_COUNTRY.put("CAD", "ca");
        CURRENCY_TO_COUNTRY.put("MXN", "mx");
        CURRENCY_TO_COUNTRY.put("BRL", "br");
        CURRENCY_TO_COUNTRY.put("ARS", "ar");
        CURRENCY_TO_COUNTRY.put("CLP", "cl");
        CURRENCY_TO_COUNTRY.put("COP", "co");
        CURRENCY_TO_COUNTRY.put("PEN", "pe");
        CURRENCY_TO_COUNTRY.put("VEF", "ve");
        CURRENCY_TO_COUNTRY.put("BOB", "bo");
        CURRENCY_TO_COUNTRY.put("UYU", "uy");
        CURRENCY_TO_COUNTRY.put("ANG", "ang");
        CURRENCY_TO_COUNTRY.put("XCD", "xcd");

// Europe
        CURRENCY_TO_COUNTRY.put("NOK", "no");
        CURRENCY_TO_COUNTRY.put("SEK", "se");
        CURRENCY_TO_COUNTRY.put("DKK", "dk");
        CURRENCY_TO_COUNTRY.put("ISK", "is");
        CURRENCY_TO_COUNTRY.put("CZK", "cz");
        CURRENCY_TO_COUNTRY.put("PLN", "pl");
        CURRENCY_TO_COUNTRY.put("HUF", "hu");
        CURRENCY_TO_COUNTRY.put("RON", "ro");
        CURRENCY_TO_COUNTRY.put("BGN", "bg");
        CURRENCY_TO_COUNTRY.put("HRK", "hr");
        CURRENCY_TO_COUNTRY.put("RSD", "rs");
        CURRENCY_TO_COUNTRY.put("UAH", "ua");
        CURRENCY_TO_COUNTRY.put("TRY", "tr");
        CURRENCY_TO_COUNTRY.put("RUB", "ru");

// Asia-Pacific
        CURRENCY_TO_COUNTRY.put("CNY", "cn");
        CURRENCY_TO_COUNTRY.put("HKD", "hk");
        CURRENCY_TO_COUNTRY.put("TWD", "tw");
        CURRENCY_TO_COUNTRY.put("KRW", "kr");
        CURRENCY_TO_COUNTRY.put("INR", "in");
        CURRENCY_TO_COUNTRY.put("PKR", "pk");
        CURRENCY_TO_COUNTRY.put("BDT", "bd");
        CURRENCY_TO_COUNTRY.put("LKR", "lk");
        CURRENCY_TO_COUNTRY.put("NPR", "np");
        CURRENCY_TO_COUNTRY.put("IDR", "id");
        CURRENCY_TO_COUNTRY.put("MYR", "my");
        CURRENCY_TO_COUNTRY.put("SGD", "sg");
        CURRENCY_TO_COUNTRY.put("THB", "th");
        CURRENCY_TO_COUNTRY.put("VND", "vn");
        CURRENCY_TO_COUNTRY.put("PHP", "ph");
        CURRENCY_TO_COUNTRY.put("AUD", "au");
        CURRENCY_TO_COUNTRY.put("NZD", "nz");
        CURRENCY_TO_COUNTRY.put("XPF", "xpf");

// Middle East
        CURRENCY_TO_COUNTRY.put("SAR", "sa");
        CURRENCY_TO_COUNTRY.put("AED", "ae");
        CURRENCY_TO_COUNTRY.put("QAR", "qa");
        CURRENCY_TO_COUNTRY.put("KWD", "kw");
        CURRENCY_TO_COUNTRY.put("BHD", "bh");
        CURRENCY_TO_COUNTRY.put("OMR", "om");
        CURRENCY_TO_COUNTRY.put("JOD", "jo");
        CURRENCY_TO_COUNTRY.put("ILS", "il");
        CURRENCY_TO_COUNTRY.put("IQD", "iq");
        CURRENCY_TO_COUNTRY.put("IRR", "ir");

// Africa
        CURRENCY_TO_COUNTRY.put("ZAR", "za");
        CURRENCY_TO_COUNTRY.put("EGP", "eg");
        CURRENCY_TO_COUNTRY.put("NGN", "ng");
        CURRENCY_TO_COUNTRY.put("KES", "ke");
        CURRENCY_TO_COUNTRY.put("TZS", "tz");
        CURRENCY_TO_COUNTRY.put("UGX", "ug");
        CURRENCY_TO_COUNTRY.put("GHS", "gh");
        CURRENCY_TO_COUNTRY.put("MAD", "ma");
        CURRENCY_TO_COUNTRY.put("TND", "tn");
        CURRENCY_TO_COUNTRY.put("DZD", "dz");
        CURRENCY_TO_COUNTRY.put("AOA", "ao");
        CURRENCY_TO_COUNTRY.put("ETB", "et");
        CURRENCY_TO_COUNTRY.put("XOF", "xof");

// Crypto
        CURRENCY_TO_COUNTRY.put("BTC", "bc");
        CURRENCY_TO_COUNTRY.put("ETH", "xx");
        CURRENCY_TO_COUNTRY.put("XRP", "xx");

    }

    // --- Formatting ---

    public static String formatRate(double rate) {
        DecimalFormat df = new DecimalFormat("#,##0.##", new DecimalFormatSymbols(Locale.UK));
        return df.format(rate);
    }

    public static String formatRateDetailed(double rate) {
        DecimalFormat df = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.UK));
        return df.format(rate);
    }

    public static String formatAmount(double amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.UK));
        return df.format(amount);
    }

    // --- Color coding ---

    public static int getColorForRate(Context context, double rate) {
        if (rate >= 100) return context.getColor(R.color.rate_very_high);
        if (rate >= 10)  return context.getColor(R.color.rate_high);
        if (rate >= 2)   return context.getColor(R.color.rate_medium);
        if (rate >= 1)   return context.getColor(R.color.rate_low);
        return context.getColor(R.color.rate_very_low);
    }

    // --- Flags ---

    public static void setFlagIcon(Context context, ImageView view, String currencyCode) {
        if (context == null || view == null) return;
        view.setImageResource(getFlagResource(context, currencyCode));
    }

    @SuppressLint("DiscouragedApi")
    private static int getFlagResource(Context context, String code) {
        String country = getCountryCode(code);
        int id = context.getResources().getIdentifier("flag_" + country, "drawable", context.getPackageName());
        return id != 0 ? id : android.R.drawable.ic_menu_mapmode;
    }

    private static String getCountryCode(String currencyCode) {
        if (currencyCode == null) return "xx";
        String found = CURRENCY_TO_COUNTRY.get(currencyCode);
        return found != null ? found : currencyCode.substring(0, 2).toLowerCase();
    }

    // --- Conversion ---

    public static double convertToTarget(double amount, double rate) {
        return amount * rate;
    }

    public static double convertToBase(double amount, double rate) {
        return amount / rate;
    }
}
