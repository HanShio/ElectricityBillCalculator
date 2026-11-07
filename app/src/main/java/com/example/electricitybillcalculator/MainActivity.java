package com.example.electricitybillcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Spinner spinnerMonth;
    EditText editTextUnits;
    RadioGroup radioRebate;
    TextView textTotal, textFinal;
    Button buttonCalculate, buttonSave, buttonHistory, buttonReset;
    DataHelper dbHelper;

    double lastTotal = 0;
    double lastFinal = 0;
    int lastUnits = 0;
    int lastRebate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            findViewById(R.id.toolbar).setPadding(0, systemBars.top, 0, 0);
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        editTextUnits = findViewById(R.id.editTextUnits);
        radioRebate = findViewById(R.id.radioRebate);
        textTotal = findViewById(R.id.textTotal);
        textFinal = findViewById(R.id.textFinal);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonSave = findViewById(R.id.buttonSave);
        buttonHistory = findViewById(R.id.buttonHistory);
        buttonReset = findViewById(R.id.buttonReset);
        dbHelper = new DataHelper(this);

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBill();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBill();
            }
        });

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetForm();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Electricity Bill Calculator");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        } else if (id == R.id.menu_help) {
            showInstructions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getSelectedRebate() {
        int checkedId = radioRebate.getCheckedRadioButtonId();
        RadioButton selected = findViewById(checkedId);
        if (selected == null) {
            return 0;
        }
        return Integer.parseInt(selected.getTag().toString());
    }

    private void calculateBill() {
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

        double total = BillCalculation.calculateTotal(units);
        double finalCost = BillCalculation.calculateFinal(total, rebate);

        textTotal.setText(String.format(Locale.US, "RM %.2f", total));
        textFinal.setText(String.format(Locale.US, "RM %.2f", finalCost));

        lastUnits = units;
        lastRebate = rebate;
        lastTotal = total;
        lastFinal = finalCost;

        Toast.makeText(this,
                "Bill calculated with " + rebate + "% rebate",
                Toast.LENGTH_SHORT).show();
    }

    private void saveBill() {
        if (lastUnits == 0) {
            Toast.makeText(this, "Please tap CALCULATE before saving", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean unitsChanged = !editTextUnits.getText().toString().trim()
                .equals(String.valueOf(lastUnits));
        boolean rebateChanged = getSelectedRebate() != lastRebate;
        if (unitsChanged || rebateChanged) {
            Toast.makeText(this, "Inputs changed - tap CALCULATE again before saving",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String month = spinnerMonth.getSelectedItem().toString();
        long id = dbHelper.insertBill(month, lastUnits, lastTotal, lastRebate, lastFinal);

        if (id == -1) {
            Toast.makeText(this, "Failed to save. Please try again", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, month + " saved (" + dbHelper.getCount() + " records)",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showUnitsError(String message) {
        editTextUnits.setError(message);
        editTextUnits.requestFocus();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetForm() {
        spinnerMonth.setSelection(0);
        editTextUnits.setText("");
        editTextUnits.setError(null);
        radioRebate.check(R.id.radioRebate0);
        textTotal.setText("RM 0.00");
        textFinal.setText("RM 0.00");
        lastUnits = 0;
        lastRebate = 0;
        lastTotal = 0;
        lastFinal = 0;
        Toast.makeText(this, "Form cleared", Toast.LENGTH_SHORT).show();
    }

    private void showInstructions() {
        String steps =
                "1. Select the month.\n"
                        + "2. Enter the electricity used in kWh (1 - 1000).\n"
                        + "3. Choose the rebate (0 - 5%).\n"
                        + "4. Tap CALCULATE to see the total charges and the final cost after rebate.\n"
                        + "5. Tap SAVE to store the result, and VIEW HISTORY to see saved bills.\n"
                        + "6. Tap RESET to clear the form and start again.";

        new AlertDialog.Builder(this)
                .setTitle("How to use")
                .setMessage(steps)
                .setPositiveButton("OK", null)
                .show();
    }
}
