package com.example.gurung_rikesh_s2426621;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class CurrencyViewModel extends ViewModel {

    private static final String TAG = "CurrencyViewModel";
    private static final long AUTO_UPDATE_INTERVAL_MS = 60000;

    private final CurrencyRepository repository;
    private final MutableLiveData<List<CurrencyRate>> currencyRates = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> lastUpdateTime = new MutableLiveData<>();

    private boolean isFetching = false;

    private final Handler autoUpdateHandler = new Handler(Looper.getMainLooper());
    private boolean autoUpdateEnabled = false;

    /** FIX: declare Runnable here, initialize in constructor */
    private Runnable autoUpdateRunnable;

    public CurrencyViewModel() {
        repository = CurrencyRepository.getInstance();

        /** FIX: Runnable defined safely here */
        autoUpdateRunnable = () -> {
            if (autoUpdateEnabled) {
                refreshCurrencyData();
                autoUpdateHandler.postDelayed(autoUpdateRunnable, AUTO_UPDATE_INTERVAL_MS);
            }
        };
    }

    public LiveData<List<CurrencyRate>> getCurrencyRates() { return currencyRates; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getLastUpdateTime() { return lastUpdateTime; }

    public void fetchCurrencyData() {
        if (isFetching) return;
        performFetch();
    }

    public void refreshCurrencyData() {
        if (isFetching) return;
        performFetch();
    }

    private void performFetch() {
        isFetching = true;
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.fetchAndParseRates(new CurrencyRepository.DataCallback() {
            @Override
            public void onDataLoaded(List<CurrencyRate> rates) {
                currencyRates.setValue(rates);
                isLoading.setValue(false);
                isFetching = false;
                updateLastUpdateTime();
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
                isFetching = false;
            }
        });
    }

    private void updateLastUpdateTime() {
        lastUpdateTime.setValue(DateUtils.formatLastUpdateTime());
    }

    public List<CurrencyRate> searchCurrencies(String query) {
        List<CurrencyRate> allRates = currencyRates.getValue();
        if (allRates == null) return new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return allRates;

        String search = query.toLowerCase().trim();
        List<CurrencyRate> filtered = new ArrayList<>();

        for (CurrencyRate rate : allRates) {
            boolean match =
                    safe(rate.getTargetCurrency()).contains(search) ||
                            safe(rate.getTargetCode()).contains(search) ||
                            safe(rate.getTitle()).contains(search);

            if (match) filtered.add(rate);
        }
        return filtered;
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    public void startAutoUpdate() {
        if (autoUpdateEnabled) return;
        autoUpdateEnabled = true;
        autoUpdateHandler.postDelayed(autoUpdateRunnable, AUTO_UPDATE_INTERVAL_MS);
    }

    public void stopAutoUpdate() {
        autoUpdateEnabled = false;
        autoUpdateHandler.removeCallbacks(autoUpdateRunnable);
    }

    public boolean isAutoUpdateEnabled() {
        return autoUpdateEnabled;
    }

    @Override
    protected void onCleared() {
        stopAutoUpdate();
        super.onCleared();
    }
}
