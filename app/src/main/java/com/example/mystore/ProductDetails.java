package com.example.mystore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mystore.data.ProductContainerForSingItem;
import com.example.mystore.data.ProductContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.mystore.data.ProductContainerForSingItem.formatUnitAndTotalPrice;
import static com.example.mystore.data.ProductContainerForSingItem.getCurrencySymbol;


public class ProductDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SaleDialogFragment.NoticeDialogListenerSale, OrderDialogFragment.NoticeDialogListenerOrder {


    private final Context context = ProductDetails.this;

    private ImageView mImageView;
    private ImageView mUploadPicture;
    private ImageView mCapturePicture;
    private EditText mProductName;
    private ImageButton mDecreaseQuantityButton;
    private ImageButton mIncreaseQuantityButton;
    private EditText mQuantityEditText;
    private Spinner mQuantityUnitSpinner;
    private Spinner mPriceCurrencySpinner;
    private TextView mTotalPriceCurrencySymbol;
    private EditText mUnitPrice;
    private TextView mTotalPrice;
    private TextView mSaleTextView;
    private TextView mOrderTextView;
    private TextView mProductAvailability;
    private EditText mSupplierName;
    private EditText mSupplierEmailAddress;
    private EditText mSupplierContactNumber;


    private boolean mProductHasChanged = false;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    /**
     * CurrencyCode and QuantityUnit variables are initialized.
     */
    private String mCurrencyCode = "USD";
    private String mQuantityUnit = "kg";

    private int quantity = 0;

    private Uri mCurrentProductUri;

    private static final int EXISTING_LOADER_ID = 1;

    private static final int CAPTURE_PICTURE=1;
    private static final int UPLOAD_PICTURE=2;


    //This Bitmap object will be used to store both Capture and Upload image.
    private Bitmap mCaptureOrUploadBitmap=null;

    //This is the variable which will contain the path of ProductImage File and we will store this address in database instead of files.
    private String mProductImagePath=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        /**
         * Associating Variables with the appropriate ID's
         */
        mImageView = (ImageView) findViewById(R.id.product_details_image_view);
        mUploadPicture = (ImageView) findViewById(R.id.upload_picture_image_view);
        mCapturePicture=(ImageView) findViewById(R.id.capture_picture_image_view);
        mProductName = (EditText) findViewById(R.id.product_name_edit_text_view);
        mDecreaseQuantityButton = (ImageButton) findViewById(R.id.product_quantity_minus_image_button_view);
        mIncreaseQuantityButton = (ImageButton) findViewById(R.id.product_quantity_plus_image_button_view);
        mQuantityEditText = (EditText) findViewById(R.id.product_quantity_edit_text_view);
        mQuantityUnitSpinner = (Spinner) findViewById(R.id.product_quantity_unit_spinner);
        mPriceCurrencySpinner = (Spinner) findViewById(R.id.price_currency_spinner);
        mTotalPriceCurrencySymbol = (TextView) findViewById(R.id.product_details_total_price_currency_symbol_text_view);
        mUnitPrice = (EditText) findViewById(R.id.unit_price_edit_text_view);
        mTotalPrice = (TextView) findViewById(R.id.total_price_text_view);
        mSaleTextView = (TextView) findViewById(R.id.product_details_sale_text_view);
        mOrderTextView = (TextView) findViewById(R.id.product_details_order_text_view);
        mProductAvailability = (TextView) findViewById(R.id.availability_text_view_product_details);
        mSupplierName = (EditText) findViewById(R.id.supplier_name_edit_text_view);
        mSupplierEmailAddress = (EditText) findViewById(R.id.supplier_email_address_edit_text_view);
        mSupplierContactNumber = (EditText) findViewById(R.id.supplier_contact_number_edit_text_view);


        /**
         * setting onTouchClickListener on all interactive views
         */
        mUploadPicture.setOnTouchListener(onTouchListener);
        mCapturePicture.setOnTouchListener(onTouchListener);
        mProductName.setOnTouchListener(onTouchListener);
        mDecreaseQuantityButton.setOnTouchListener(onTouchListener);
        mIncreaseQuantityButton.setOnTouchListener(onTouchListener);
        mQuantityEditText.setOnTouchListener(onTouchListener);
        mQuantityUnitSpinner.setOnTouchListener(onTouchListener);
        mPriceCurrencySpinner.setOnTouchListener(onTouchListener);
        mProductName.setOnTouchListener(onTouchListener);
        mSaleTextView.setOnTouchListener(onTouchListener);
        mOrderTextView.setOnTouchListener(onTouchListener);
        mSupplierName.setOnTouchListener(onTouchListener);
        mSupplierEmailAddress.setOnTouchListener(onTouchListener);
        mSupplierContactNumber.setOnTouchListener(onTouchListener);

        setupIncreaseAndDecreaseButtons();
        setupQuantityUnitsSpinner();
        setupPriceCurrencySpinner();


        mUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


        mCapturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        /**
         * Adding TextChangedListener to UnitPriceEditTextView, so whenever the unit price changes, total price will also be changed.
         */
        mUnitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {

                    String unitPriceString = s.toString().trim();

                    char firstCharacter = s.charAt(0);
                    if (firstCharacter == '.') {
                        unitPriceString = "0" + unitPriceString;
                    }

                    totalPriceChanger(unitPriceString, "UnitPrice");
                } else {
                    mTotalPrice.setText("0.00");
                }
            }
        });

        /**
         * Adding TextChangedListener to QuantityEditTextView, so whenever the quanity increase from 0, Availability Text View Changes to In_Stock
         */
        mQuantityEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    if (Integer.parseInt(s.toString()) > 0) {
                        mProductAvailability.setText(getString(R.string.product_in_stock));
                    } else if (Integer.parseInt(s.toString()) == 0) {
                        mProductAvailability.setText(getString(R.string.product_out_of_stock));
                    }
                    totalPriceChanger(s.toString().trim(), "Quantity");

                    /**
                     *  Setting  value of quantity to {@ProductContainerForSingleItem} class to use in {@SaleDialogFragment}
                     */
                    ProductContainerForSingItem.setmQuantity(Integer.parseInt(s.toString()));
                } else {
                    mTotalPrice.setText("0.00");
                    mProductAvailability.setText(getString(R.string.product_out_of_stock));
                }

            }
        });


        mSaleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saleTextViewClick();
            }
        });

        mOrderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderTextViewClick();
            }
        });


        mCurrentProductUri = getIntent().getData();
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.adding_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editing_product));
            getLoaderManager().initLoader(EXISTING_LOADER_ID, null, this);
        }


        /**
         * Initially setting the {@mTotalPriceCurrencySymbol} value
         */
        mTotalPriceCurrencySymbol.setText(getCurrencySymbol(mCurrencyCode));


        /**
         * Setting values for Quantity & Unit Of {@ProductContainerForSingleItem} class,
         * so we can use these value later in {@SaleDialogFragment}
         */
        ProductContainerForSingItem.setmQuantity(quantity);
        ProductContainerForSingItem.setmUnit(mQuantityUnit);

    }


    /**
     * Setting up the Quantity Increase & Decrease Buttons functionality
     */

    private void setupIncreaseAndDecreaseButtons() {
        mDecreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityInput = mQuantityEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(quantityInput)) {
                    quantity = Integer.parseInt(quantityInput);
                    if (quantity > 0) {
                        quantity--;
                        if (quantity == 0) {
                            mProductAvailability.setText(getString(R.string.product_out_of_stock));
                        }
                    }
                    mQuantityEditText.setText(Integer.toString(quantity));
                }
            }
        });
        mIncreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityInput = mQuantityEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(quantityInput)) {
                    quantity = Integer.parseInt(quantityInput);
                    quantity++;
                    mQuantityEditText.setText(Integer.toString(quantity));
                } else {
                    quantity = 0;
                    quantity++;
                    mQuantityEditText.setText(Integer.toString(quantity));
                }
                mProductAvailability.setText(getString(R.string.product_in_stock));
            }
        });
    }

    /**
     * Setting up Total price Changer Method which changes the total price with respect to Quantity Value
     */
    private void totalPriceChanger(String s, String editText) {
        if (!s.isEmpty() && !mUnitPrice.getText().toString().isEmpty()) {
            double totalPrice;
            switch (editText) {
                case "Quantity":
                    /**
                     * Setting value to {@mUnitPrice} variable of {@ProductContainerForSingleItem} class,
                     * so {@SaleDialogFragment} unit price changes also changes
                     */
                    double unitPrice1 = Double.parseDouble(s);
                    unitPrice1 = Double.parseDouble(formatUnitAndTotalPrice(unitPrice1));
                    ProductContainerForSingItem.setmUnitPrice(unitPrice1);

                    totalPrice = Double.parseDouble(s) * Double.parseDouble(mUnitPrice.getText().toString().trim());
                    totalPrice = Double.parseDouble(formatUnitAndTotalPrice(totalPrice));
                    break;
                case "UnitPrice":
                    /**
                     * Setting value to {@mUnitPrice} variable of {@ProductContainerForSingleItem} class,
                     * so {@SaleDialogFragment} unit price changes also changes
                     */
                    double unitPrice2 = Double.parseDouble(s);
                    unitPrice2 = Double.parseDouble(formatUnitAndTotalPrice(unitPrice2));
                    ProductContainerForSingItem.setmUnitPrice(unitPrice2);

                    totalPrice = Double.parseDouble(s) * Double.parseDouble(mQuantityEditText.getText().toString().trim());
                    totalPrice = Double.parseDouble(formatUnitAndTotalPrice(totalPrice));
                    break;
                default:
                    throw new IllegalArgumentException("Error while calculating total price!");

            }

            mTotalPrice.setText(Double.toString(totalPrice));
        }
    }


    /**
     * Setting up the spinner for Quantity Units
     */
    private void setupQuantityUnitsSpinner() {

        ArrayAdapter quantityUnitsAdapter = ArrayAdapter.createFromResource(
                this, R.array.quanity_units_array, android.R.layout.simple_spinner_item);

        quantityUnitsAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mQuantityUnitSpinner.setAdapter(quantityUnitsAdapter);
        mQuantityUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.kilogram))) {
                        mQuantityUnit = getString(R.string.kilogram);
                    } else if (selection.equals(getString(R.string.gram))) {
                        mQuantityUnit = getString(R.string.gram);
                    } else if (selection.equals(getString(R.string.pounds))) {
                        mQuantityUnit = getString(R.string.pounds);
                    } else if (selection.equals(getString(R.string.ounce))) {
                        mQuantityUnit = getString(R.string.ounce);
                    } else if (selection.equals(getString(R.string.litre))) {
                        mQuantityUnit = getString(R.string.litre);
                    } else if (selection.equals(getString(R.string.millilitre))) {
                        mQuantityUnit = getString(R.string.millilitre);
                    } else {
                        mQuantityUnit = getString(R.string.kilogram);
                    }
                }

                /**
                 * Setting the Quantity Unit Value to {@ProductContainerForSingleItem} to use in {@SaleDialogFragment}
                 */
                ProductContainerForSingItem.setmUnit(mQuantityUnit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mQuantityUnit = getString(R.string.kilogram);
            }
        });
    }

    /**
     * Setting up the spinner for Currency
     */
    private void setupPriceCurrencySpinner() {

        ArrayAdapter currencyAdapter = ArrayAdapter.createFromResource(
                this, R.array.currency_codes_array, android.R.layout.simple_spinner_item);

        currencyAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mPriceCurrencySpinner.setAdapter(currencyAdapter);
        mPriceCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.austrailian_dollar))) {
                        mCurrencyCode = getString(R.string.austrailian_dollar);
                        ProductContainerForSingItem.setmCurrencySymbol(getCurrencySymbol(mCurrencyCode));
                    } else if (selection.equals(getString(R.string.canadian_dollar))) {
                        mCurrencyCode = getString(R.string.canadian_dollar);
                        ProductContainerForSingItem.setmCurrencySymbol(getCurrencySymbol(mCurrencyCode));
                    } else if (selection.equals(getString(R.string.euro))) {
                        mCurrencyCode = getString(R.string.euro);
                        ProductContainerForSingItem.setmCurrencySymbol(getCurrencySymbol(mCurrencyCode));
                    } else if (selection.equals(getString(R.string.indian_rupees))) {
                        mCurrencyCode = getString(R.string.indian_rupees);
                        ProductContainerForSingItem.setmCurrencySymbol(getCurrencySymbol(mCurrencyCode));
                    } else if (selection.equals(getString(R.string.pakistan_rupees))) {
                        mCurrencyCode = getString(R.string.pakistan_rupees);
                        ProductContainerForSingItem.setmCurrencySymbol(getCurrencySymbol(mCurrencyCode));
                    } else if (selection.equals(getString(R.string.us_dollar))) {
                        mCurrencyCode = getString(R.string.us_dollar);
                        ProductContainerForSingItem.setmCurrencySymbol(getCurrencySymbol(mCurrencyCode));
                    } else if (selection.equals(getString(R.string.yuan_renminbi))) {
                        mCurrencyCode = getString(R.string.yuan_renminbi);
                        ProductContainerForSingItem.setmCurrencySymbol(getCurrencySymbol(mCurrencyCode));
                    } else {
                        mCurrencyCode = getString(R.string.us_dollar);
                        ProductContainerForSingItem.setmCurrencySymbol(getCurrencySymbol(mCurrencyCode));
                    }
                }
                mTotalPriceCurrencySymbol.setText(ProductContainerForSingItem.getmCurrencySymbol());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCurrencyCode = getString(R.string.us_dollar);
                ProductContainerForSingItem.setmCurrencySymbol(getCurrencySymbol(mCurrencyCode));
                mTotalPriceCurrencySymbol.setText(ProductContainerForSingItem.getmCurrencySymbol());
            }
        });
    }


    /**
     * Setting up the functionality of adding pic from storage
     */

    //Uploading image from storage
    private void uploadImage() {

        Intent intent;
        if (Build.VERSION.SDK_INT > 19) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), UPLOAD_PICTURE);

    }

    /**
     *Setting up the functionality of capturing pic
     */
    private void captureImage(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.mystore.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_PICTURE);
            }
        }
    }

    //This is the the string containing the path of captured image from camera
    private String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //After selection, onActivityResult will set the selected image into the image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK&&requestCode==CAPTURE_PICTURE) {
            File file = new File(mCurrentPhotoPath);
            Bitmap bitmap=null;
            try {
                bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), Uri.fromFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mImageView.setImageBitmap(bitmap);
            mCaptureOrUploadBitmap=bitmap;

        }

        if (resultCode == RESULT_OK&&requestCode==UPLOAD_PICTURE) {

            Uri targetURI = data.getData();
            Bitmap bitmap=null;

            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetURI));
                mImageView.setImageBitmap(bitmap);
                mCaptureOrUploadBitmap=bitmap;

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            }

        }

    }

    /**
     * Creating a method which will create an image file from {@mCaptureOrUploadBitmap} value.
     */
    private void createProductImageFile(){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("Pictures", Context.MODE_PRIVATE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File myPath = new File(directory, imageFileName + ".jpg");

        mProductImagePath = myPath.toString();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            mCaptureOrUploadBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Setting up the SaleImage View Clicking Functionality
     */
    private void saleTextViewClick() {
        double unitPrice = 0;

        if (!mUnitPrice.getText().toString().isEmpty()) {
            unitPrice = Double.parseDouble(mUnitPrice.getText().toString().trim());
        }

        if (!mProductName.getText().toString().isEmpty()) {
            ProductContainerForSingItem.setmProductName(mProductName.getText().toString().trim());
        }

        if (mProductName.getText().toString().isEmpty()) {
            Toast.makeText(context, "Enter Product Name ?", Toast.LENGTH_SHORT).show();
        } else if (mQuantityEditText.getText().toString().isEmpty() || Integer.parseInt(mQuantityEditText.getText().toString().trim()) == 0) {
            Toast.makeText(context, mProductName.getText().toString().trim() + " is out of stock!", Toast.LENGTH_SHORT).show();
        } else if (mUnitPrice.getText().toString().isEmpty()) {
            Toast.makeText(context, "Enter the Unit Price of " + ProductContainerForSingItem.getmProductName() + " ?", Toast.LENGTH_SHORT).show();
        } else if (unitPrice == 0) {
            Toast.makeText(context, "Set the Unit Price greater than 0 ?", Toast.LENGTH_SHORT).show();
        } else {

            ProductContainerForSingItem.setmProductName(mProductName.getText().toString().trim());
            ProductContainerForSingItem.setmUnitPrice(unitPrice);
            ProductContainerForSingItem.setmCurrency(mCurrencyCode);

            SaleDialogFragment saleDialogFragment = new SaleDialogFragment();
            saleDialogFragment.show(getSupportFragmentManager(), "Sale");
        }
    }

    /**
     * Setting up the OrderImage View Clicking Functionality
     */
    private void orderTextViewClick() {

        double unitPrice = 0;

        if (!mUnitPrice.getText().toString().isEmpty()) {
            unitPrice = Double.parseDouble(mUnitPrice.getText().toString().trim());
        }

        if (!mProductName.getText().toString().isEmpty()) {
            ProductContainerForSingItem.setmProductName(mProductName.getText().toString().trim());
        }

        if (mProductName.getText().toString().isEmpty()) {
            Toast.makeText(context, "Enter Product Name ?", Toast.LENGTH_SHORT).show();
        } else if (mUnitPrice.getText().toString().isEmpty()) {
            Toast.makeText(context, "Enter the Unit Price of " + ProductContainerForSingItem.getmProductName() + " ?", Toast.LENGTH_SHORT).show();
        } else if (unitPrice == 0) {
            Toast.makeText(context, "Set the Unit Price greater than 0 ?", Toast.LENGTH_SHORT).show();
        } else if (mSupplierName.getText().toString().isEmpty()) {
            Toast.makeText(context, "Enter Supplier Name?", Toast.LENGTH_SHORT).show();
        } else if (mSupplierEmailAddress.getText().toString().isEmpty()) {
            Toast.makeText(context, "Enter Supplier Email Address?", Toast.LENGTH_SHORT).show();
        } else {

            ProductContainerForSingItem.setmProductName(mProductName.getText().toString().trim());
            ProductContainerForSingItem.setmUnitPrice(unitPrice);
            ProductContainerForSingItem.setmCurrency(mCurrencyCode);

            OrderDialogFragment orderDialogFragment = new OrderDialogFragment();
            orderDialogFragment.show(getSupportFragmentManager(), "Order");
        }
    }

    /**
     * Creating insert method to add new product
     */
    private void insertProduct() {

        createProductImageFile();

        String productImagePath = mProductImagePath;
        String productName = mProductName.getText().toString().trim();

        int productQuantity;
        if (mQuantityEditText.getText().toString().isEmpty()) {
            productQuantity = 0;
        } else {
            productQuantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        }

        String productQuantityUnit = mQuantityUnit;

        double productUnitPrice;
        if (mUnitPrice.getText().toString().isEmpty()) {
            productUnitPrice = 0.00;
        } else {
            productUnitPrice = Double.parseDouble(mUnitPrice.getText().toString().trim());
            productUnitPrice = Double.parseDouble(formatUnitAndTotalPrice(productUnitPrice));
        }

        double productTotalPrice = Double.parseDouble(mTotalPrice.getText().toString().trim());
        productTotalPrice = Double.parseDouble(formatUnitAndTotalPrice(productTotalPrice));

        String productPriceCurrency = mCurrencyCode;
        String productAvailabilityString = mProductAvailability.getText().toString().trim();


        int productAvailabity = ProductContract.Product.PRODUCT_OUT_OF_STOCK;
        if (productAvailabilityString == getString(R.string.product_in_stock)) {
            productAvailabity = ProductContract.Product.PRODUCT_IN_STOCK;
        } else if (productAvailabilityString == getString(R.string.product_out_of_stock)) {
            productAvailabity = ProductContract.Product.PRODUCT_OUT_OF_STOCK;
        }

        String supplierName = null;
        String supplierEmail = null;
        String supplierContactNumber = null;

        if (!mSupplierName.getText().toString().isEmpty()) {
            supplierName = mSupplierName.getText().toString().trim();
        }
        if (!mSupplierEmailAddress.getText().toString().isEmpty()) {
            supplierEmail = mSupplierEmailAddress.getText().toString().trim();
        }
        if (!mSupplierContactNumber.getText().toString().isEmpty()) {
            supplierContactNumber = mSupplierContactNumber.getText().toString().trim();
        }


        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_PICTURES, productImagePath);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_NAME, productName);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_QUANTITY, productQuantity);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_UNIT, productQuantityUnit);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_UNIT_PRICE, productUnitPrice);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_TOTAL_PRICE, productTotalPrice);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_CURRENCY, productPriceCurrency);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_AVAILABILITY, productAvailabity);
        contentValues.put(ProductContract.Product.COLUMN_SUPPLIER_NAME, supplierName);
        contentValues.put(ProductContract.Product.COLUMN_SUPPLIER_EMAIL_ADDRESS, supplierEmail);
        contentValues.put(ProductContract.Product.COLUMN_SUPPLIER_CONTACT_NUMBER, supplierContactNumber);

        getContentResolver().insert(ProductContract.Product.CONTENT_URI, contentValues);
    }

    /**
     * Creating update method to update existing product details
     */
    private void updateProductDetails() {

        final String selection = ProductContract.Product._ID + "=?";
        long id = ContentUris.parseId(mCurrentProductUri);
        String[] selectionArgs = new String[]{String.valueOf(id)};

        createProductImageFile();

        String productImagePath = mProductImagePath;
        String productName = mProductName.getText().toString().trim();

        int productQuantity;
        if (mQuantityEditText.getText().toString().isEmpty()) {
            productQuantity = 0;
        } else {
            productQuantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        }

        String productQuantityUnit = mQuantityUnit;

        double productUnitPrice;
        if (mUnitPrice.getText().toString().isEmpty()) {
            productUnitPrice = 0.00;
        } else {
            productUnitPrice = Double.parseDouble(mUnitPrice.getText().toString().trim());
            productUnitPrice = Double.parseDouble(formatUnitAndTotalPrice(productUnitPrice));
        }

        double productTotalPrice = Double.parseDouble(mTotalPrice.getText().toString().trim());
        productTotalPrice = Double.parseDouble(formatUnitAndTotalPrice(productTotalPrice));

        String productPriceCurrency = mCurrencyCode;
        String productAvailabilityString = mProductAvailability.getText().toString().trim();
        int productAvailability = ProductContract.Product.PRODUCT_OUT_OF_STOCK;
        if (productAvailabilityString == getString(R.string.product_in_stock)) {
            productAvailability = ProductContract.Product.PRODUCT_IN_STOCK;
        } else if (productAvailabilityString == getString(R.string.product_out_of_stock)) {
            productAvailability = ProductContract.Product.PRODUCT_OUT_OF_STOCK;
        }

        String supplierName = null;
        String supplierEmail = null;
        String supplierContactNumber = null;

        if (!mSupplierName.getText().toString().isEmpty()) {
            supplierName = mSupplierName.getText().toString().trim();
        }
        if (!mSupplierEmailAddress.getText().toString().isEmpty()) {
            supplierEmail = mSupplierEmailAddress.getText().toString().trim();
        }
        if (!mSupplierContactNumber.getText().toString().isEmpty()) {
            supplierContactNumber = mSupplierContactNumber.getText().toString().trim();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_PICTURES, productImagePath);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_NAME, productName);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_QUANTITY, productQuantity);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_UNIT, productQuantityUnit);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_UNIT_PRICE, productUnitPrice);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_TOTAL_PRICE, productTotalPrice);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_CURRENCY, productPriceCurrency);
        contentValues.put(ProductContract.Product.COLUMN_PRODUCT_AVAILABILITY, productAvailability);
        contentValues.put(ProductContract.Product.COLUMN_SUPPLIER_NAME, supplierName);
        contentValues.put(ProductContract.Product.COLUMN_SUPPLIER_EMAIL_ADDRESS, supplierEmail);
        contentValues.put(ProductContract.Product.COLUMN_SUPPLIER_CONTACT_NUMBER, supplierContactNumber);

        getContentResolver().update(mCurrentProductUri, contentValues, selection, selectionArgs);
    }

    /**
     * Creating a method for deleting a product
     */
    private void deleteProduct() {
        String selection = ProductContract.Product._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentProductUri))};

        /**
         * First we need to delete the image file
         */
        String[] projection=new String[]{ProductContract.Product.COLUMN_PRODUCT_PICTURES};
        Cursor cursor=getContentResolver().query(mCurrentProductUri,projection,selection,selectionArgs,null);
        cursor.moveToNext();
        String imageFilePath=cursor.getString(cursor.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_PICTURES));
        File imageFile=new File(imageFilePath);
        imageFile.delete();

        getContentResolver().delete(mCurrentProductUri, selection, selectionArgs);
    }


    /**
     * Creating a method for validating all the fields
     */
    private boolean validateUserInputs() {

        boolean validator = true;

        if (mProductName.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Valid Product Name!", Toast.LENGTH_SHORT).show();
            validator = false;
        } else if (mCaptureOrUploadBitmap == null) {
            Toast.makeText(this, "Upload Product Image!", Toast.LENGTH_SHORT).show();
            validator = false;
        }


        if (validator) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * Creating an alert Dialogs
     */

    //AlertDialog for back Button
    private void alertDialogForBackButton() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.alertDialogForBackButtonMessage));
        builder.setPositiveButton(getString(R.string.alertDialogForBackButtonPositiveButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NavUtils.navigateUpFromSameTask(ProductDetails.this);
            }
        });
        builder.setNegativeButton(getString(R.string.alertDialogForBackButtonNegativeButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //AlertDialog for delete product menu item
    private void alertDialogForDeleteProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.alertDialogForDeleteProductMenuItemMessage));
        builder.setPositiveButton(getString(R.string.alertDialogForDeleteProductMenuItemPositiveButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
                finish();
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
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        } else {
            alertDialogForBackButton();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        if (getIntent().getData() == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_product_menu_item);
            menuItem.setVisible(false);
            MenuItem menuItem1 = menu.findItem(R.id.order_product_menu_item);
            menuItem1.setVisible(false);
        }
        return true;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductDetails.this);
                    return true;
                } else {
                    alertDialogForBackButton();
                    return true;
                }

            case R.id.save_product_menu_item:
                if (mCurrentProductUri != null) {

                    if (validateUserInputs()) {
                        updateProductDetails();
                        finish();
                    }

                } else {

                    if (validateUserInputs()) {
                        insertProduct();
                        finish();
                    }

                }

                return true;

            case R.id.order_product_menu_item:
                orderTextViewClick();
                break;

            case R.id.delete_product_menu_item:
                alertDialogForDeleteProduct();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]{
                ProductContract.Product._ID,
                ProductContract.Product.COLUMN_PRODUCT_PICTURES,
                ProductContract.Product.COLUMN_PRODUCT_NAME,
                ProductContract.Product.COLUMN_PRODUCT_QUANTITY,
                ProductContract.Product.COLUMN_PRODUCT_UNIT,
                ProductContract.Product.COLUMN_PRODUCT_UNIT_PRICE,
                ProductContract.Product.COLUMN_PRODUCT_TOTAL_PRICE,
                ProductContract.Product.COLUMN_PRODUCT_CURRENCY,
                ProductContract.Product.COLUMN_PRODUCT_AVAILABILITY,
                ProductContract.Product.COLUMN_SUPPLIER_NAME,
                ProductContract.Product.COLUMN_SUPPLIER_EMAIL_ADDRESS,
                ProductContract.Product.COLUMN_SUPPLIER_CONTACT_NUMBER
        };


        String selection = ProductContract.Product._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentProductUri))};
        return new CursorLoader(this, mCurrentProductUri, projection, selection, selectionArgs, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        while (data.moveToNext()) {

            int _ID = data.getInt(data.getColumnIndex(ProductContract.Product._ID));
            String productName = data.getString(data.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_NAME));
            String productPicture = data.getString(data.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_PICTURES));
            int productQuantityNumber = data.getInt(data.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_QUANTITY));
            String productQuantityUnit = data.getString(data.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_UNIT));
            double productUnitPrice = data.getDouble(data.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_UNIT_PRICE));
            double productTotalPrice = data.getDouble(data.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_TOTAL_PRICE));
            String productCurrency = data.getString(data.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_CURRENCY));
            int productAvailability = data.getInt(data.getColumnIndex(ProductContract.Product.COLUMN_PRODUCT_AVAILABILITY));
            String supplierName = data.getString(data.getColumnIndex(ProductContract.Product.COLUMN_SUPPLIER_NAME));
            String supplierEmailAddress = data.getString(data.getColumnIndex(ProductContract.Product.COLUMN_SUPPLIER_EMAIL_ADDRESS));
            String supplierContactNumber = data.getString(data.getColumnIndex(ProductContract.Product.COLUMN_SUPPLIER_CONTACT_NUMBER));


            mProductName.setText(productName);

            Bitmap bitmap=BitmapFactory.decodeFile(productPicture);
            mCaptureOrUploadBitmap=bitmap;
            mImageView.setImageBitmap(bitmap);

            mProductImagePath = productPicture;
            mQuantityEditText.setText(Integer.toString(productQuantityNumber));
            mUnitPrice.setText(Double.toString(productUnitPrice));
            mTotalPrice.setText(Double.toString(productTotalPrice));
            mSupplierName.setText(supplierName);
            mSupplierEmailAddress.setText(supplierEmailAddress);
            mSupplierContactNumber.setText(supplierContactNumber);

            if (productAvailability == ProductContract.Product.PRODUCT_IN_STOCK) {
                mProductAvailability.setText(getString(R.string.product_in_stock));
            } else if (productAvailability == ProductContract.Product.PRODUCT_OUT_OF_STOCK) {
                mProductAvailability.setText(getString(R.string.product_out_of_stock));
            }

            switch (productQuantityUnit) {
                case "kg":
                    mQuantityUnitSpinner.setSelection(0);
                    break;
                case "g":
                    mQuantityUnitSpinner.setSelection(1);
                    break;
                case "lbs":
                    mQuantityUnitSpinner.setSelection(2);
                    break;
                case "oz":
                    mQuantityUnitSpinner.setSelection(3);
                    break;
                case "L":
                    mQuantityUnitSpinner.setSelection(4);
                    break;
                case "mL":
                    mQuantityUnitSpinner.setSelection(5);
                    break;
            }

            switch (productCurrency) {
                case "USD":
                    mPriceCurrencySpinner.setSelection(0);
                    break;
                case "AUD":
                    mPriceCurrencySpinner.setSelection(1);
                    break;
                case "CAD":
                    mPriceCurrencySpinner.setSelection(2);
                    break;
                case "EUR":
                    mPriceCurrencySpinner.setSelection(3);
                    break;
                case "INR":
                    mPriceCurrencySpinner.setSelection(4);
                    break;
                case "PKR":
                    mPriceCurrencySpinner.setSelection(5);
                    break;
                case "CNY":
                    mPriceCurrencySpinner.setSelection(6);
                    break;
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    @Override
    public void getQuantity(int quantity) {
        if (quantity != 0) {
            int number = Integer.parseInt(mQuantityEditText.getText().toString().trim()) - quantity;
            mQuantityEditText.setText(Integer.toString(number));
            Toast.makeText(this, quantity + " " + mQuantityUnit + " of " + mProductName.getText().toString().trim() + " sold!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void getOrderDetails(int quantity) {
        if (quantity != 0) {

            String totalPrice = Double.toString(Double.parseDouble(mUnitPrice.getText().toString().trim()) * quantity);

            String email = mSupplierEmailAddress.getText().toString().trim();
            String subject = "Order of " + mProductName.getText().toString().trim() + "!";
            String body = " Order Details:\n\n   Quantity : " + Integer.toString(quantity) + " " + mQuantityUnit +
                    "\n" + "   Unit Price : " + mUnitPrice.getText().toString().trim() + " " + ProductContainerForSingItem.getmCurrencySymbol() +
                    "\n" + "   \tTotal Price : " + totalPrice + " " + ProductContainerForSingItem.getmCurrencySymbol();

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);

            startActivity(emailIntent);
        }
    }
}
