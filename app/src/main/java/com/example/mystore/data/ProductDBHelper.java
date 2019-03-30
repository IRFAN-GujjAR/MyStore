package com.example.mystore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mystore.data.ProductContract.Product;

public class ProductDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store.db";

    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + Product.TABLE_NAME + "("
            + Product._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Product.COLUMN_PRODUCT_PICTURES + " TEXT,"
            + Product.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
            + Product.COLUMN_PRODUCT_QUANTITY + " INTEGER DEFAULT 0,"
            + Product.COLUMN_PRODUCT_UNIT + " TEXT NOT NULL,"
            + Product.COLUMN_PRODUCT_CURRENCY + " TEXT NOT NULL,"
            + Product.COLUMN_PRODUCT_UNIT_PRICE + " REAL NOT NULL,"
            + Product.COLUMN_PRODUCT_TOTAL_PRICE + " REAL NOT NULL,"
            + Product.COLUMN_PRODUCT_AVAILABILITY + " INTEGER,"
            + Product.COLUMN_SUPPLIER_NAME + " TEXT,"
            + Product.COLUMN_SUPPLIER_EMAIL_ADDRESS + " TEXT,"
            + Product.COLUMN_SUPPLIER_CONTACT_NUMBER + " TEXT)";


    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + Product.TABLE_NAME;


    public ProductDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
