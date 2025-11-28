package com.example.gurung_rikesh_s2426621;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {

    private List<CurrencyRate> currencyRates;
    private final OnItemClickListener clickListener;
    private final Context context;

    public interface OnItemClickListener {
        void onItemClick(CurrencyRate rate);
    }

    public CurrencyAdapter(Context context, List<CurrencyRate> rates, OnItemClickListener listener) {
        this.context = context;
        this.currencyRates = rates;
        this.clickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView flagImageView;
        TextView currencyPairTextView, rateTextView;
        View itemContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flagImageView = itemView.findViewById(R.id.flagImageView);
            currencyPairTextView = itemView.findViewById(R.id.currencyPairTextView);
            rateTextView = itemView.findViewById(R.id.rateTextView);
            itemContainer = itemView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.currency_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CurrencyRate rate = currencyRates.get(position);

        holder.currencyPairTextView.setText("GBP → " + rate.getCurrencyCode());
        holder.rateTextView.setText(CurrencyUtils.formatRate(rate.getRate()));
        holder.itemContainer.setBackgroundColor(CurrencyUtils.getColorForRate(context, rate.getRate()));
        CurrencyUtils.setFlagIcon(context, holder.flagImageView, rate.getCurrencyCode());

        holder.itemContainer.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(rate);
        });
    }

    @Override
    public int getItemCount() {
        return currencyRates != null ? currencyRates.size() : 0;
    }

    public void updateData(List<CurrencyRate> newRates) {
        this.currencyRates = newRates;
        notifyDataSetChanged();
    }
}
