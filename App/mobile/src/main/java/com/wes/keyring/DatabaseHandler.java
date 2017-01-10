package com.wes.keyring;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data.db";
    public static final String TABLE_DATA = "data";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CARD_NAME = "cardName";
    public static final String COLUMN_CARD_HOLDER = "cardHolder";
    public static final String COLUMN_BARCODE_FORMAT = "barcodeFormat";
    public static final String COLUMN_SERIAL_NUMBER = "serialNumber";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory,
                           int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " +
                TABLE_DATA + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_CARD_NAME
                + " TEXT," + COLUMN_CARD_HOLDER
                + " TEXT," + COLUMN_BARCODE_FORMAT
                + " TEXT," + COLUMN_SERIAL_NUMBER + " TEXT" + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        onCreate(db);
    }

    public void addCard (Card card) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_CARD_NAME, card.get_cardName());
        contentValues.put(COLUMN_CARD_HOLDER, card.get_cardHolder());
        contentValues.put(COLUMN_BARCODE_FORMAT, card.get_barcodeFormat());
        contentValues.put(COLUMN_SERIAL_NUMBER, card.get_serialNumber());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_DATA, null, contentValues);
        db.close();
    }

    public Card findCard (String cardName) {
        String query = "Select * FROM " + TABLE_DATA + " WHERE " + COLUMN_CARD_NAME + " =  \""
                + cardName + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Card card = new Card();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            card.set_id(Integer.parseInt(cursor.getString(0)));
            card.set_cardName(cursor.getString(1));
            card.set_cardHolder(cursor.getString(2));
            card.set_barcodeFormat(cursor.getString(3));
            card.set_serialNumber(cursor.getString(4));
            cursor.close();
        } else {
            card = null;
        }
        db.close();
        return card;
    }

    public boolean deleteProduct(String serialNumber) {
        boolean result = false;

        String query = "Select * FROM " + TABLE_DATA + " WHERE " + COLUMN_SERIAL_NUMBER + " =  \""
                + serialNumber + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Card card = new Card();

        if (cursor.moveToFirst()) {
            card.set_id(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_DATA, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(card.get_id()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_DATA, null);
        return res;
    }
}
