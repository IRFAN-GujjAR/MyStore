package com.example.mystore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mystore.data.ProductContract;
import com.example.mystore.data.ProductContainerForListView;

public class ProductAdapter extends CursorAdapter{

    public ProductAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_design, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        ImageView imageView = (ImageView) view.findViewById(R.id.product_image_view);
        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name_text_view);
        TextView productQuantityNumberTextView = (TextView) view.findViewById(R.id.product_quantity_number_text_view);
        TextView productQuantityUnitTextView = (TextView) view.findViewById(R.id.product_quantity_unit_text_view);
        TextView productUnitPriceTextView = (TextView) view.findViewById(R.id.product_unit_price_number_text_view);
        TextView productUnitPriceCurrencyTextView=(TextView) view.findViewById(R.id.product_currency_code_text_view);
        TextView productAvailabilityTextView=(TextView) view.findViewById(R.id.product_availability_text_view);
        final ImageView saleImageView = (ImageView) view.findViewById(R.id.product_sale_image_view);


        int idColumnIndex=cursor.getColumnIndex(ProductContract.Product._ID);
        int imageColumnIndex = cursor.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_PICTURES);
        int productNameColumnIndex = cursor.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_NAME);
        int productQuantityNumberColumnIndex = cursor.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_QUANTITY);
        int productQuantityUnitColumnIndex = cursor.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_UNIT);
        int productUnitPriceCurrencyColumnIndex = cursor.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_CURRENCY);
        int productUnitPriceColumnIndex = cursor.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_UNIT_PRICE);


        int _ID=cursor.getInt(idColumnIndex);
        String image = cursor.getString(imageColumnIndex);
        final String productName = cursor.getString(productNameColumnIndex);
        int productQuanityNumber = cursor.getInt(productQuantityNumberColumnIndex);
        final String productQuantityUnit = cursor.getString(productQuantityUnitColumnIndex);
        String productUnitPriceCurrency=cursor.getString(productUnitPriceCurrencyColumnIndex);
        double productUnitPrice=cursor.getDouble(productUnitPriceColumnIndex);


        Bitmap bitmap= BitmapFactory.decodeFile(image);
        imageView.setImageBitmap(bitmap);

        productNameTextView.setText(productName);
        productQuantityNumberTextView.setText(Integer.toString(productQuanityNumber));
        productQuantityUnitTextView.setText(productQuantityUnit);
        productUnitPriceCurrencyTextView.setText(productUnitPriceCurrency);
        productUnitPriceTextView.setText(Double.toString(productUnitPrice));

        ProductContainerForListView productContainerForListView =new ProductContainerForListView(_ID,productQuanityNumber);
        saleImageView.setTag(productContainerForListView);

        saleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProductContainerForListView object=(ProductContainerForListView) v.getTag();
                String selection= ProductContract.Product._ID+"=?";
                String[] selectionArgs=new String[]{String.valueOf(object.get_ID())};
                Uri updateUri=Uri.withAppendedPath(ProductContract.Product.CONTENT_URI,String.valueOf(object.get_ID()));
                if(object.getmQuantity()>0){
                    ContentValues contentValues=new ContentValues();
                    contentValues.put(ProductContract.Product.COLUMN_PRODUCT_QUANTITY,object.getmQuantity()-1);
                    int count=context.getContentResolver().update(updateUri,contentValues,selection,selectionArgs);
                    if(count>0){
                        Toast.makeText(context,"1 "+productQuantityUnit+" of "+productName+" sold!",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context,productName+" is out of stock!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(productQuanityNumber>0){
            productAvailabilityTextView.setText("In Stock");
        }else {
            productAvailabilityTextView.setText("Out Of Stock");
        }

    }
}
