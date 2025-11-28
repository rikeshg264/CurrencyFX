package com.example.gurung_rikesh_s2426621;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity
        implements CurrencyListFragment.CurrencyListListener {

    private static final String TAG = "FXMate";
    private CurrencyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);

        Log.d(TAG, "Initiating currency data fetch on startup...");
        viewModel.fetchCurrencyData();

        if (savedInstanceState == null) {
            loadCurrencyListFragment();
            Log.d(TAG, "Starting automatic periodic updates...");
            viewModel.startAutoUpdate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!viewModel.isAutoUpdateEnabled()) {
            Log.d(TAG, "Resuming auto-update on activity resume");
            viewModel.startAutoUpdate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewModel.isAutoUpdateEnabled()) {
            Log.d(TAG, "Pausing auto-update on activity pause");
            viewModel.stopAutoUpdate();
        }
    }

    private void loadCurrencyListFragment() {
        Log.d(TAG, "Loading CurrencyListFragment...");

        CurrencyListFragment listFragment = new CurrencyListFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.fragmentContainer, listFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCurrencySelected(CurrencyRate selectedRate) {
        Log.d(TAG, "Currency selected: " + selectedRate.getTargetCode());

        CurrencyDetailFragment detailFragment = CurrencyDetailFragment.newInstance(selectedRate);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.fragmentContainer, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
