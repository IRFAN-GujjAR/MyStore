package com.example.mystore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {

    //We don't want to accidently create an instance of StoreContract so, we will create constructor with private.
    private ProductContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.mystore";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.mystore/pets/ is a valid path for
     * looking at product data. content://com.example.android.mystore/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PRODUCTS = "products";


    /**
     * Inner class that defines constant values for the products database table.
     * Each entry in the table represents a single product.
     */
    public static final class Product implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * Name of database table for products
         */
        public final static String TABLE_NAME = "products";


        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_PICTURES="pictures";

        public final static String COLUMN_PRODUCT_NAME = "name";

        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        public final static String COLUMN_PRODUCT_UNIT="unit";

        public final static String COLUMN_PRODUCT_UNIT_PRICE="unit_price";

        public final static String COLUMN_PRODUCT_CURRENCY="currency";

        public final static String COLUMN_PRODUCT_TOTAL_PRICE="total_price";

        public final static String COLUMN_PRODUCT_AVAILABILITY = "availability";

        public final static String COLUMN_SUPPLIER_NAME="supplier_name";

        public final static String COLUMN_SUPPLIER_EMAIL_ADDRESS="supplier_email";

        public final static String COLUMN_SUPPLIER_CONTACT_NUMBER="supplier_contact_number";



        /**
         * Possible values for PRODUCT AVAILABILITY
         */
        public final static int PRODUCT_IN_STOCK=1;

        public final static int PRODUCT_OUT_OF_STOCK=0;

    }
}
