package com.example.electricitybillcalculator;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText editTextUnits;
    RadioGroup radioRebate;
    Button buttonUpdate, buttonBack;
    DataHelper dbHelper;
    long billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerMonth = findViewById(R.id.spinnerMonth);
        editTextUnits = findViewById(R.id.editTextUnits);
        radioRebate = findViewById(R.id.radioRebate);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonBack = findViewById(R.id.buttonBack);

        dbHelper = new DataHelper(this);
        billId = getIntent().getLongExtra("id", -1);

        loadCurrentValues();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBill();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private int getSelectedRebate() {
        int checkedId = radioRebate.getCheckedRadioButtonId();
        RadioButton selected = findViewById(checkedId);
        if (selected == null) {
            return 0;
        }
        return Integer.parseInt(selected.getTag().toString());
    }

    private void selectRebate(int rebate) {
        for (int i = 0; i < radioRebate.getChildCount(); i++) {
            RadioButton rb = (RadioButton) radioRebate.getChildAt(i);
            if (rb.getTag().toString().equals(String.valueOf(rebate))) {
                rb.setChecked(true);
                return;
            }
        }
    }

    private void loadCurrentValues() {
        Cursor c = dbHelper.getBill(billId);
        if (c.moveToFirst()) {
            String month = c.getString(c.getColumnIndexOrThrow(DataHelper.COL_MONTH));
            int units = c.getInt(c.getColumnIndexOrThrow(DataHelper.COL_UNITS));
            int rebate = c.getInt(c.getColumnIndexOrThrow(DataHelper.COL_REBATE));
            c.close();

            String[] months = getResources().getStringArray(R.array.months);
            for (int i = 0; i < months.length; i++) {
                if (months[i].equals(month)) {
                    spinnerMonth.setSelection(i);
                    break;
                }
            }
            editTextUnits.setText(String.valueOf(units));
            selectRebate(rebate);
        } else {
            c.close();
            Toast.makeText(this, "Record not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateBill() {
        String unitsText = editTextUnits.getText().toString().trim();

        if (unitsText.isEmpty()) {
            showUnitsError("Please enter the units used (1 - 1000 kWh)");
            return;
        }

        int units;
        try {
            units = Integer.parseInt(unitsText);
        } catch (NumberFormatException e) {
            showUnitsError("Please enter a whole number only");
            return;
        }

        if (units < 1 || units > 1000) {
            showUnitsError("Units must be between 1 and 1000 kWh");
            return;
        }

        int rebate = getSelectedRebate();
        String month = spinnerMonth.getSelectedItem().toString();

        double total = BillCalculation.calculateTotal(units);
        double finalCost = BillCalculation.calculateFinal(total, rebate);

        int rows = dbHelper.updateBill(billId, month, units, total, rebate, finalCost);
        if (rows > 0) {
            Toast.makeText(this,
                    String.format(Locale.US, "Updated. New final cost: RM %.2f", finalCost),
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed. Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUnitsError(String message) {
        editTextUnits.setError(message);
        editTextUnits.requestFocus();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}