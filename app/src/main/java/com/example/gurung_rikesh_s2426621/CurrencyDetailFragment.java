package com.example.gurung_rikesh_s2426621;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class CurrencyDetailFragment extends Fragment {

    private static final String ARG_CURRENCY_RATE = "currency_rate";

    private ImageButton backButton, swapButton;
    private ImageView currencyFlagImageView;
    private TextView currencyNameTextView, exchangeRateTextView, timestampTextView;
    private EditText topAmountEditText, bottomAmountEditText;
    private TextView topCurrencyCodeTextView, bottomCurrencyCodeTextView;

    private CurrencyRate currencyRate;
    private CurrencyDetailViewModel viewModel;
    private boolean isUpdating = false;

    public static CurrencyDetailFragment newInstance(CurrencyRate rate) {
        CurrencyDetailFragment fragment = new CurrencyDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CURRENCY_RATE, rate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currencyRate = (CurrencyRate) getArguments().getSerializable(ARG_CURRENCY_RATE);
        }
        viewModel = new ViewModelProvider(this).get(CurrencyDetailViewModel.class);
        viewModel.setCurrencyRate(currencyRate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.currency_detail, container, false);

        backButton = view.findViewById(R.id.backButton);
        swapButton = view.findViewById(R.id.swapButton);
        currencyFlagImageView = view.findViewById(R.id.currencyFlagImageView);
        currencyNameTextView = view.findViewById(R.id.currencyNameTextView);
        exchangeRateTextView = view.findViewById(R.id.exchangeRateTextView);
        timestampTextView = view.findViewById(R.id.timestampTextView);
        topAmountEditText = view.findViewById(R.id.topAmountEditText);
        bottomAmountEditText = view.findViewById(R.id.bottomAmountEditText);
        topCurrencyCodeTextView = view.findViewById(R.id.topCurrencyCodeTextView);
        bottomCurrencyCodeTextView = view.findViewById(R.id.bottomCurrencyCodeTextView);

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        setupDisplay();
        setupTextWatchers();

        swapButton.setOnClickListener(v -> swapCurrencies());

        return view;
    }

    private void setupDisplay() {
        if (currencyRate == null) return;

        updateNameDisplay();
        updateRateDisplay();

        CurrencyUtils.setFlagIcon(getContext(), currencyFlagImageView, currencyRate.getCurrencyCode());
        timestampTextView.setText(DateUtils.formatDetailTimestamp());
        updateCurrencyLabels();
    }

    private void updateRateDisplay() {
        double rate = currencyRate.getRate();
        if (!viewModel.isSwapped()) {
            exchangeRateTextView.setText("1£ = " + CurrencyUtils.formatRateDetailed(rate));
        } else {
            double reversed = 1 / rate;
            exchangeRateTextView.setText(
                    "1 " + currencyRate.getCurrencyCode() + " = " +
                            CurrencyUtils.formatRateDetailed(reversed) + "£"
            );
        }
    }

    private void updateNameDisplay() {
        if (!viewModel.isSwapped()) {
            currencyNameTextView.setText("GBP / " +
                    currencyRate.getCurrencyName() + " (" + currencyRate.getCurrencyCode() + ")");
        } else {
            currencyNameTextView.setText(
                    currencyRate.getCurrencyCode() + " (" + currencyRate.getCurrencyName() + ") / GBP"
            );
        }
    }

    private void updateCurrencyLabels() {
        topCurrencyCodeTextView.setText(viewModel.getTopCurrencyCode());
        bottomCurrencyCodeTextView.setText(viewModel.getBottomCurrencyCode());
    }

    private void setupTextWatchers() {

        topAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdating) convertTopToBottom();
            }
        });

        bottomAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdating) convertBottomToTop();
            }
        });
    }

    private void convertTopToBottom() {
        String text = topAmountEditText.getText().toString();
        String result = viewModel.convertTopToBottom(text);

        isUpdating = true;
        bottomAmountEditText.setText(result);
        isUpdating = false;
    }

    private void convertBottomToTop() {
        String text = bottomAmountEditText.getText().toString();
        String result = viewModel.convertBottomToTop(text);

        isUpdating = true;
        topAmountEditText.setText(result);
        isUpdating = false;
    }

    private void swapCurrencies() {
        viewModel.swapCurrencies();
        updateCurrencyLabels();

        String top = topAmountEditText.getText().toString();
        String bottom = bottomAmountEditText.getText().toString();

        isUpdating = true;
        topAmountEditText.setText(bottom);
        bottomAmountEditText.setText(top);
        isUpdating = false;

        updateNameDisplay();
        updateRateDisplay();
    }
}
