package com.example.electricitybillcalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "electricitybill.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE = "bills";
    public static final String COL_ID = "id";
    public static final String COL_MONTH = "month";
    public static final String COL_UNITS = "units";
    public static final String COL_TOTAL = "total";
    public static final String COL_REBATE = "rebate";
    public static final String COL_FINAL = "final_cost";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MONTH + " TEXT NOT NULL, "
                + COL_UNITS + " INTEGER NOT NULL, "
                + COL_TOTAL + " REAL NOT NULL, "
                + COL_REBATE + " INTEGER NOT NULL, "
                + COL_FINAL + " REAL NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // CREATE
    public long insertBill(String month, int units, double total, int rebate, double finalCost) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNITS, units);
        values.put(COL_TOTAL, total);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL, finalCost);
        return db.insert(TABLE, null, values);
    }

    // READ ALL
    public Cursor getAllBills() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE + " ORDER BY " + COL_ID + " DESC", null);
    }

    // READ ONE
    public Cursor getBill(long id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE + " WHERE " + COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // UPDATE
    public int updateBill(long id, String month, int units, double total, int rebate, double finalCost) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNITS, units);
        values.put(COL_TOTAL, total);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL, finalCost);
        return db.update(TABLE, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // DELETE
    public int deleteBill(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE, null);
        int count = 0;
        if (c.moveToFirst()) {
            count = c.getInt(0);
        }
        c.close();
        return count;
    }
}