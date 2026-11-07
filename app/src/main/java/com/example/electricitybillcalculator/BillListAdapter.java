package com.example.electricitybillcalculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class BillListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] months;
    private final String[] finalCosts;

    public BillListAdapter(Context context, String[] months, String[] finalCosts) {
        super(context, R.layout.custom_list_layout, months);
        this.context = context;
        this.months = months;
        this.finalCosts = finalCosts;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.custom_list_layout, parent, false);
        }

        TextView textMonth = view.findViewById(R.id.textMonth);
        TextView textFinalCost = view.findViewById(R.id.textFinalCost);

        textMonth.setText(months[position]);
        textFinalCost.setText(finalCosts[position]);

        return view;
    }
}
