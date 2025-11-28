package com.example.gurung_rikesh_s2426621;

import androidx.lifecycle.ViewModel;

public class CurrencyDetailViewModel extends ViewModel {

    private CurrencyRate rate;
    private boolean isSwapped = false;

    public void setCurrencyRate(CurrencyRate rate) {
        this.rate = rate;
    }

    public boolean isSwapped() {
        return isSwapped;
    }

    public void swapCurrencies() {
        isSwapped = !isSwapped;
    }

    public String getTopCurrencyCode() {
        return rate == null ? "" : (isSwapped ? rate.getCurrencyCode() : "GBP");
    }

    public String getBottomCurrencyCode() {
        return rate == null ? "" : (isSwapped ? "GBP" : rate.getCurrencyCode());
    }

    private double parse(String v) {
        return v == null || v.isEmpty() ? -1 : Double.parseDouble(v);
    }

    public String convertTopToBottom(String value) {
        double v = parse(value);
        if (v < 0 || rate == null) return "";

        double r = rate.getRate();
        double result = isSwapped ? v / r : v * r;

        return String.format("%.4f", result);
    }

    public String convertBottomToTop(String value) {
        double v = parse(value);
        if (v < 0 || rate == null) return "";

        double r = rate.getRate();
        double result = isSwapped ? v * r : v / r;

        return String.format("%.4f", result);
    }
}
