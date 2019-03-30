package com.example.mystore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.CursorAdapter;
import android.widget.Toast;

import com.example.mystore.data.ProductContract.Product;
import com.example.mystore.data.ProductContract;

public class ProductProvider extends ContentProvider {

    private ProductDBHelper mProductDBHelper;

    public static final int PRODUCTS = 100;
    public static final int PRODUCTS_ID = 101;

    private static final UriMatcher mUriMathcer = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMathcer.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        mUriMathcer.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    @Override
    public boolean onCreate() {
        mProductDBHelper = new ProductDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase sqLiteDatabase = mProductDBHelper.getReadableDatabase();

        Cursor cursor;

        final int match = mUriMathcer.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = sqLiteDatabase.query(Product.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                selection = Product._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(Product.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot Query Unkown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = mUriMathcer.match(uri);
        switch (match) {
            case PRODUCTS:
                return Product.CONTENT_LIST_TYPE;

            case PRODUCTS_ID:
                return Product.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase sqLiteDatabase = mProductDBHelper.getWritableDatabase();

        final int match = mUriMathcer.match(uri);

        switch (match) {
            case PRODUCTS:
                long id = sqLiteDatabase.insert(Product.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase sqLiteDatabase = mProductDBHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = mUriMathcer.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = sqLiteDatabase.delete(Product.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                selection = Product._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = sqLiteDatabase.delete(Product.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase sqLiteDatabase = mProductDBHelper.getWritableDatabase();

        int rowsUpdated;

        final int match = mUriMathcer.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsUpdated = sqLiteDatabase.update(Product.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                selection = Product._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = sqLiteDatabase.update(Product.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
