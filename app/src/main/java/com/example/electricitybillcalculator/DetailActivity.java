package com.example.electricitybillcalculator;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    TextView textMonth, textUnits, textTotal, textRebate, textFinal;
    Button buttonEdit, buttonDelete, buttonBack;
    DataHelper dbHelper;
    long billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textMonth = findViewById(R.id.textMonth);
        textUnits = findViewById(R.id.textUnits);
        textTotal = findViewById(R.id.textTotal);
        textRebate = findViewById(R.id.textRebate);
        textFinal = findViewById(R.id.textFinal);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonBack = findViewById(R.id.buttonBack);

        dbHelper = new DataHelper(this);
        billId = getIntent().getLongExtra("id", -1);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, EditActivity.class);
                intent.putExtra("id", billId);
                startActivity(intent);
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBill();
    }

    private void loadBill() {
        Cursor c = dbHelper.getBill(billId);
        if (c.moveToFirst()) {
            textMonth.setText(c.getString(c.getColumnIndexOrThrow(DataHelper.COL_MONTH)));
            textUnits.setText(c.getInt(c.getColumnIndexOrThrow(DataHelper.COL_UNITS)) + " kWh");
            textTotal.setText(String.format(Locale.US, "RM %.2f",
                    c.getDouble(c.getColumnIndexOrThrow(DataHelper.COL_TOTAL))));
            textRebate.setText(c.getInt(c.getColumnIndexOrThrow(DataHelper.COL_REBATE)) + "%");
            textFinal.setText(String.format(Locale.US, "RM %.2f",
                    c.getDouble(c.getColumnIndexOrThrow(DataHelper.COL_FINAL))));
            c.close();
        } else {
            c.close();
            Toast.makeText(this, "Record not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete record")
                .setMessage("Are you sure you want to delete this record? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteBill(billId);
                    Toast.makeText(DetailActivity.this, "Record deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}