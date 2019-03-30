package com.example.mystore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatDialogFragment;
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
import com.example.mystore.data.ProductContract;

import org.w3c.dom.Text;

import static com.example.mystore.data.ProductContainerForSingItem.formatUnitAndTotalPrice;

public class OrderDialogFragment extends AppCompatDialogFragment {

    ImageButton orderDecreaseImageButton;
    ImageButton orderIncreaseImageButton;
    EditText orderQuantityEditText;
    TextView orderQuantityUnitTextView;
    TextView orderUnitPriceTextView;
    TextView orderTotalPriceTextView;
    TextView orderCurrencyUnitPriceTextView;
    TextView orderCurrencySymbolTotalPriceTextView;

    private int mQuantity = 0;
    private double mTotalPrice = 0;

    public OrderDialogFragment() {
    }

    public interface NoticeDialogListenerOrder {
        void getOrderDetails(int quantity);
    }

    NoticeDialogListenerOrder listenerOrder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listenerOrder = (NoticeDialogListenerOrder) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_layout_for_order_dialog, null);

        builder.setView(view)
                .setPositiveButton(getString(R.string.positive_button_for_order_dialog_fragment), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerOrder.getOrderDetails(mQuantity);
                    }
                })
                .setNegativeButton(getString(R.string.negative_button_for_order_dialog_fragment), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        orderDecreaseImageButton = (ImageButton) view.findViewById(R.id.order_decrease_quantity_image_button_view);
        orderIncreaseImageButton = (ImageButton) view.findViewById(R.id.order_increase_quantity_image_button_view);
        orderQuantityEditText = (EditText) view.findViewById(R.id.order_quantity_edit_text_view);
        orderQuantityUnitTextView = (TextView) view.findViewById(R.id.order_quantity_unit_text_view);
        orderUnitPriceTextView = (TextView) view.findViewById(R.id.order_unit_price_text_view);
        orderTotalPriceTextView = (TextView) view.findViewById(R.id.order_total_price_text_view);
        orderCurrencyUnitPriceTextView = (TextView) view.findViewById(R.id.order_currency_unit_price_text_view);
        orderCurrencySymbolTotalPriceTextView = (TextView) view.findViewById(R.id.order_currency_symbol_total_price_text_view);

        orderQuantityUnitTextView.setText(ProductContainerForSingItem.getmUnit());
        orderCurrencyUnitPriceTextView.setText(ProductContainerForSingItem.getmCurrency());
        orderCurrencySymbolTotalPriceTextView.setText(ProductContainerForSingItem.getmCurrencySymbol());
        orderUnitPriceTextView.setText(Double.toString(ProductContainerForSingItem.getmUnitPrice()));
        orderTotalPriceTextView.setText(Double.toString(mTotalPrice));


        orderQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!s.toString().isEmpty()){
                    if(Integer.parseInt(s.toString().trim())>0){
                        mQuantity=Integer.parseInt(s.toString().trim());
                        mTotalPrice= ProductContainerForSingItem.getmUnitPrice()*mQuantity;
                        orderTotalPriceTextView.setText(Double.toString(mTotalPrice));
                    }else{
                        mQuantity=0;
                        mTotalPrice=0;
                        orderTotalPriceTextView.setText(Double.toString(mTotalPrice));
                    }
                }else {
                    mQuantity=0;
                    mTotalPrice=0;
                    orderTotalPriceTextView.setText(Double.toString(mTotalPrice));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        orderDecreaseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantityInput = orderQuantityEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(quantityInput)) {
                    mQuantity = Integer.parseInt(quantityInput);

                    if (mQuantity > 0) {
                        mQuantity--;
                        mTotalPrice = mQuantity * ProductContainerForSingItem.getmUnitPrice();
                        mTotalPrice = Double.parseDouble(formatUnitAndTotalPrice(mTotalPrice));
                        orderQuantityEditText.setText(Integer.toString(mQuantity));
                        orderTotalPriceTextView.setText(Double.toString(mTotalPrice));
                    }
                }

            }
        });

        orderIncreaseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityInput = orderQuantityEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(quantityInput)) {
                    mQuantity = Integer.parseInt(quantityInput);
                    mQuantity++;
                    mTotalPrice = mQuantity * ProductContainerForSingItem.getmUnitPrice();
                    mTotalPrice = Double.parseDouble(formatUnitAndTotalPrice(mTotalPrice));
                    orderQuantityEditText.setText(Integer.toString(mQuantity));
                    orderTotalPriceTextView.setText(Double.toString(mTotalPrice));
                } else {
                    mQuantity = 0;
                    mQuantity++;
                    mTotalPrice = mQuantity * ProductContainerForSingItem.getmUnitPrice();
                    mTotalPrice = Double.parseDouble(formatUnitAndTotalPrice(mTotalPrice));
                    orderQuantityEditText.setText(Integer.toString(mQuantity));
                    orderTotalPriceTextView.setText(Double.toString(mTotalPrice));
                }
            }
        });


        return builder.create();
    }
}
