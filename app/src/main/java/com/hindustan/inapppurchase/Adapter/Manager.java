package com.hindustan.inapppurchase.Adapter;

import com.android.billingclient.api.ProductDetails;

import java.util.ArrayList;
import java.util.List;

public class Manager {

    private List<ProductDetails> list;
    private static Manager instance;

    private Manager() {
        list = new ArrayList<>();
    }

    public static synchronized Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    public List<ProductDetails> getProduct(){
        return list;
    }

    public void addProduct(List<ProductDetails> productDetails){
        list.addAll(productDetails);
    }

}
