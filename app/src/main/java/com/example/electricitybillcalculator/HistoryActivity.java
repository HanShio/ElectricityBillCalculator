package com.example.electricitybillcalculator;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    ListView listViewBills;
    TextView textEmpty;
    Button buttonBack;
    DataHelper dbHelper;

    long[] ids = new long[0];
    String[] months = new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewBills = findViewById(R.id.listViewBills);
        textEmpty = findViewById(R.id.textEmpty);
        buttonBack = findViewById(R.id.buttonBack);
        dbHelper = new DataHelper(this);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listViewBills.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
                intent.putExtra("id", ids[position]);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    private void loadList() {
        Cursor cursor = dbHelper.getAllBills();
        int count = cursor.getCount();

        ids = new long[count];
        months = new String[count];
        String[] finalCosts = new String[count];

        int i = 0;
        while (cursor.moveToNext()) {
            ids[i] = cursor.getLong(cursor.getColumnIndexOrThrow(DataHelper.COL_ID));
            months[i] = cursor.getString(cursor.getColumnIndexOrThrow(DataHelper.COL_MONTH));
            double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COL_FINAL));
            finalCosts[i] = String.format(Locale.US, "RM %.2f", finalCost);
            i++;
        }
        cursor.close();

        listViewBills.setAdapter(new BillListAdapter(this, months, finalCosts));
        textEmpty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
    }
}