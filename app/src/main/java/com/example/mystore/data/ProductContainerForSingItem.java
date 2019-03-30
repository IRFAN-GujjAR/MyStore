package com.example.mystore.data;

import java.text.DecimalFormat;

public final class ProductContainerForSingItem {

    private static String mProductName;
    private static int mQuantity;
    private static String mUnit;
    private static String mCurrency;
    private static String mCurrencySymbol;
    private static double mUnitPrice;

    private ProductContainerForSingItem() {
    }

    public static void setmProductName(String mProductName) {
        ProductContainerForSingItem.mProductName = mProductName;
    }

    public static void setmQuantity(int mQuantity) {
        ProductContainerForSingItem.mQuantity = mQuantity;
    }

    public static void setmUnit(String mUnit) {
        ProductContainerForSingItem.mUnit = mUnit;
    }

    public static void setmCurrency(String mCurrency) {
        ProductContainerForSingItem.mCurrency = mCurrency;
    }

    public static void setmCurrencySymbol(String mCurrencySymbol) {
        ProductContainerForSingItem.mCurrencySymbol = mCurrencySymbol;
    }

    public static void setmUnitPrice(double mUnitPrice) {
        ProductContainerForSingItem.mUnitPrice = mUnitPrice;
    }

    public static String getmProductName() {
        return mProductName;
    }

    public static int getmQuantity() {
        return mQuantity;
    }

    public static String getmUnit() {
        return mUnit;
    }

    public static String getmCurrency() {
        return mCurrency;
    }

    public static String getmCurrencySymbol() {
        return mCurrencySymbol;
    }

    public static double getmUnitPrice() {
        return mUnitPrice;
    }

    /**
     * Creating a method which will return formatted Unit Price & Total Price with 2 decimal places
     */
    public static String formatUnitAndTotalPrice(double unitOrTotalPrice) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(unitOrTotalPrice);
    }

    /**
     * Creating a method which will return Currency Symbol depending upon the currency code
     */
    public static String getCurrencySymbol(String currencyCode) {
        String currencySymbol = "";
        switch (currencyCode) {
            case "AUD":
                currencySymbol = "$";
                break;
            case "CAD":
                currencySymbol = "$";
                break;
            case "EUR":
                currencySymbol = "€";
                break;
            case "INR":
                currencySymbol = "₹";
                break;
            case "PKR":
                currencySymbol = "Rs";
                break;
            case "USD":
                currencySymbol = "$";
                break;
            case "CNY":
                currencySymbol = "¥";
                break;

        }
        return currencySymbol;
    }
}
