package com.example.mystore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mystore.data.ProductContract;

import java.io.File;

public class MyStore extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int CURSOR_LOADER_ID = 1;
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_store);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view);
        setSupportActionBar(toolbar);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyStore.this, ProductDetails.class);
                startActivity(intent);
            }
        });


        productAdapter = new ProductAdapter(this, null);

        View emptyView = (View) findViewById(R.id.empty_view);

        ListView listView = (ListView) findViewById(R.id.list_View);

        listView.setAdapter(productAdapter);
        if (productAdapter.getCount() == 0) {
            listView.setEmptyView(emptyView);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyStore.this, ProductDetails.class);
                intent.setData(Uri.withAppendedPath(ProductContract.Product.CONTENT_URI, String.valueOf(id)));
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_product_menu_item:
                Intent intent = new Intent(MyStore.this, ProductDetails.class);
                startActivity(intent);
                return true;
            case R.id.delete_menu_item:
                if (productAdapter.getCount() != 0) {
                    alertDialogForDeleteAllProducts();
                }else {
                    Toast.makeText(this,"Your store is empty!",Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllProducts() {


        /**
         * Before deleting database table values, we need to delete the image files.
         */
        String[] projection = new String[]{ProductContract.Product.COLUMN_PRODUCT_PICTURES};
        Cursor cursor = getContentResolver().query(
                ProductContract.Product.CONTENT_URI,
                projection,
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            String imageFilePath = cursor.getString(cursor.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_PICTURES));
            File imageFile = new File(imageFilePath);
            imageFile.delete();
        }

        getContentResolver().delete(ProductContract.Product.CONTENT_URI, null, null);
    }

    private void alertDialogForDeleteAllProducts() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alertDialogForDeleteAllProductMessage));
        builder.setPositiveButton(getString(R.string.alertDialogForDeleteProductMenuItemPositiveButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllProducts();
            }
        });
        builder.setNegativeButton(getString(R.string.alertDialogForDeleteProductMenuItemNegativeButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                ProductContract.Product._ID,
                ProductContract.Product.COLUMN_PRODUCT_PICTURES,
                ProductContract.Product.COLUMN_PRODUCT_NAME,
                ProductContract.Product.COLUMN_PRODUCT_QUANTITY,
                ProductContract.Product.COLUMN_PRODUCT_UNIT,
                ProductContract.Product.COLUMN_PRODUCT_CURRENCY,
                ProductContract.Product.COLUMN_PRODUCT_UNIT_PRICE,
        };
        return new CursorLoader(this, ProductContract.Product.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        productAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productAdapter.swapCursor(null);
    }

}
