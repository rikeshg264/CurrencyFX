package com.example.gurung_rikesh_s2426621;

import android.os.Handler;
import android.util.Log;

import java.util.List;

public class CurrencyRepository {

    private static final String TAG = "CurrencyRepository";
    private static volatile CurrencyRepository instance;

    public interface DataCallback {
        void onDataLoaded(List<CurrencyRate> rates);
        void onError(String errorMessage);
    }

    private CurrencyRepository() {}

    public static CurrencyRepository getInstance() {
        if (instance == null) {
            synchronized (CurrencyRepository.class) {
                if (instance == null) instance = new CurrencyRepository();
            }
        }
        return instance;
    }

    /**
     * Fetch + parse RSS feed (runs in background thread)
     * Results returned on main thread using Handler
     */
    public void fetchAndParseRates(DataCallback callback) {
        Handler mainHandler = new Handler();

        new Thread(() -> {
            try {
                RssFetcherParser parser = new RssFetcherParser();
                List<CurrencyRate> rates = parser.fetchAndParse();

                if (rates == null || rates.isEmpty()) {
                    mainHandler.post(() -> callback.onError("No data received"));
                    return;
                }

                mainHandler.post(() -> callback.onDataLoaded(rates));

            } catch (Exception e) {
                Log.e(TAG, "fetch error: " + e.getMessage());
                mainHandler.post(() -> callback.onError("Network error"));
            }
        }).start();
    }
}
