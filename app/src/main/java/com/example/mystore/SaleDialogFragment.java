package com.example.mystore;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mystore.data.ProductContainerForSingItem;


import static com.example.mystore.data.ProductContainerForSingItem.formatUnitAndTotalPrice;

public class SaleDialogFragment extends AppCompatDialogFragment {

    private ImageButton saleQuanityDecreaseImageButton;
    private ImageButton saleQuanityIncreaseImageButton;
    private EditText saleQuantityEditText;
    private TextView saleQuantityUnitTextView;
    private TextView saleCurrencyUnitPriceTextView;
    private TextView saleCurrencySymboltotalPriceTextView;
    private TextView unitPriceTextView;
    private TextView totalPriceTextView;

    private int mQuantity = 0;
    private double mTotalPrice = 0;

    public SaleDialogFragment() {
    }

    public interface NoticeDialogListenerSale {
        void getQuantity(int quantity);
    }

    NoticeDialogListenerSale listener;

   @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeDialogListenerSale) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.custom_layout_for_sale_dialog, null);

        builder.setView(view)
                .setPositiveButton(getString(R.string.positive_button_for_sale_dialog_fragment), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.getQuantity(mQuantity);
                    }
                })
                .setNegativeButton(getString(R.string.negative_button_for_sale_dialog_fragment), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        saleQuanityDecreaseImageButton = (ImageButton) view.findViewById(R.id.sale_decrease_quantity_image_button_view);
        saleQuanityIncreaseImageButton = (ImageButton) view.findViewById(R.id.sale_increase_quantity_image_button_view);
        saleQuantityEditText = (EditText) view.findViewById(R.id.sale_quantity_edit_text_view);
        saleQuantityUnitTextView = (TextView) view.findViewById(R.id.sale_quantity_unit_text_view);
        saleCurrencyUnitPriceTextView = (TextView) view.findViewById(R.id.sale_currency_unit_price_text_view);
        saleCurrencySymboltotalPriceTextView = (TextView) view.findViewById(R.id.sale_currency_symbol_total_price_text_view);
        unitPriceTextView = (TextView) view.findViewById(R.id.sale_unit_price_text_view);
        totalPriceTextView = (TextView) view.findViewById(R.id.sale_total_price_text_view);


        saleQuantityUnitTextView.setText(ProductContainerForSingItem.getmUnit());
        saleCurrencyUnitPriceTextView.setText(ProductContainerForSingItem.getmCurrency());
        saleCurrencySymboltotalPriceTextView.setText(ProductContainerForSingItem.getmCurrencySymbol());
        unitPriceTextView.setText(Double.toString(ProductContainerForSingItem.getmUnitPrice()));
        totalPriceTextView.setText(Double.toString(mTotalPrice));


        saleQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    if (s.toString().length() <= ProductContainerForSingItem.getmQuantity()) {

                        mTotalPrice = ProductContainerForSingItem.getmUnitPrice() * Double.parseDouble(s.toString().trim());

                        if (Integer.parseInt(s.toString().trim()) > ProductContainerForSingItem.getmQuantity()) {

                            String string = s.toString().trim().substring(0, s.toString().trim().length() - 1);
                            saleQuantityEditText.setText(string);
                            Toast.makeText(getContext(), "You cannot add more quantity than " +
                                    Integer.toString(ProductContainerForSingItem.getmQuantity()) + " " +
                                    ProductContainerForSingItem.getmUnit() + "!", Toast.LENGTH_SHORT).show();

                            mTotalPrice = Integer.parseInt(string) * ProductContainerForSingItem.getmUnitPrice();
                            mTotalPrice = Double.parseDouble(formatUnitAndTotalPrice(mTotalPrice));

                        } else {
                            mQuantity = Integer.parseInt(s.toString().trim());
                        }

                        totalPriceTextView.setText(Double.toString(mTotalPrice));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        saleQuanityIncreaseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityInput = saleQuantityEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(quantityInput)) {
                    mQuantity = Integer.parseInt(quantityInput);
                    if (mQuantity < ProductContainerForSingItem.getmQuantity()) {
                        mQuantity++;
                        mTotalPrice = mQuantity * ProductContainerForSingItem.getmUnitPrice();
                        mTotalPrice = Double.parseDouble(formatUnitAndTotalPrice(mTotalPrice));
                        saleQuantityEditText.setText(Integer.toString(mQuantity));
                        totalPriceTextView.setText(Double.toString(mTotalPrice));
                    } else {
                        Toast.makeText(getContext(), ProductContainerForSingItem.getmProductName() + " stock limit reached!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mQuantity = 0;
                    mQuantity++;
                    mTotalPrice = mQuantity * ProductContainerForSingItem.getmUnitPrice();
                    mTotalPrice = Double.parseDouble(formatUnitAndTotalPrice(mTotalPrice));
                    saleQuantityEditText.setText(Integer.toString(mQuantity));
                    totalPriceTextView.setText(Double.toString(mTotalPrice));
                }
            }
        });
        saleQuanityDecreaseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityInput = saleQuantityEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(quantityInput)) {
                    mQuantity = Integer.parseInt(quantityInput);

                    if (mQuantity > 0) {
                        mQuantity--;
                        mTotalPrice = mQuantity * ProductContainerForSingItem.getmUnitPrice();
                        mTotalPrice = Double.parseDouble(formatUnitAndTotalPrice(mTotalPrice));
                        saleQuantityEditText.setText(Integer.toString(mQuantity));
                        totalPriceTextView.setText(Double.toString(mTotalPrice));
                    }
                }
            }
        });


        return builder.create();
    }


}
