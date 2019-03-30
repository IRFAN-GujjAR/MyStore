package com.example.mystore.data;

public class ProductContainerForListView {

    private int _ID;
    private int mQuantity;

    public ProductContainerForListView(int _ID, int mQuantity) {
        this._ID = _ID;
        this.mQuantity = mQuantity;
    }


    public int get_ID() {
        return _ID;
    }

    public int getmQuantity() {
        return mQuantity;
    }

}
