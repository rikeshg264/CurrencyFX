package com.example.gurung_rikesh_s2426621;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CurrencyListFragment extends Fragment {

    public interface CurrencyListListener {
        void onCurrencySelected(CurrencyRate selectedRate);
    }

    private CurrencyListListener listener;
    private CurrencyViewModel viewModel;

    private RecyclerView recyclerView;
    private CurrencyAdapter adapter;
    private EditText searchEditText;
    private ProgressBar loadingSpinner;
    private TextView statusTextView;
    private TextView currencyCountTextView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CurrencyListListener) {
            listener = (CurrencyListListener) context;
        } else {
            throw new ClassCastException(context + " must implement CurrencyListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // IMPORTANT: Must inflate your LIST layout, not item_currency.xml
        View view = inflater.inflate(R.layout.item_currency, container, false);

        recyclerView = view.findViewById(R.id.currencyRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        statusTextView = view.findViewById(R.id.statusTextView);
        currencyCountTextView = view.findViewById(R.id.currencyCountTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CurrencyAdapter(getContext(), new ArrayList<>(), rate -> {
            if (listener != null) listener.onCurrencySelected(rate);
        });

        recyclerView.setAdapter(adapter);

        setupSearchListener();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);
        setupObservers();
    }

    private void setupObservers() {

        viewModel.getCurrencyRates().observe(getViewLifecycleOwner(), rates -> {
            if (rates == null || !isAdded()) return;

            adapter.updateData(rates);
            updateCurrencyCount(rates.size());

            statusTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (!isAdded()) return;

            loadingSpinner.setVisibility(loading ? View.VISIBLE : View.GONE);

            if (loading) {
                statusTextView.setText("Loading currency data...");
                statusTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (!isAdded() || error == null) return;

            statusTextView.setText("Error: " + error);
            statusTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCurrencies(s.toString());
            }
        });
    }

    private void filterCurrencies(String query) {
        List<CurrencyRate> filtered = viewModel.searchCurrencies(query);
        if (filtered != null) {
            adapter.updateData(filtered);
            updateCurrencyCount(filtered.size());
        }
    }

    private void updateCurrencyCount(int count) {
        currencyCountTextView.setText(
                count == 1 ? "1 currency listed" : count + " currencies listed"
        );
    }
}
