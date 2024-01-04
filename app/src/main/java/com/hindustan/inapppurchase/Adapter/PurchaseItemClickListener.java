package com.hindustan.inapppurchase.Adapter;

import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.SkuDetails;

import java.util.List;

public interface PurchaseItemClickListener {
    void onPurchaseItemClicked(int position, List<BillingFlowParams.ProductDetailsParams> productDetailsList);
}
