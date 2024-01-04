package com.hindustan.inapppurchase.Activitys;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;
import com.hindustan.inapppurchase.Adapter.Manager;
import com.hindustan.inapppurchase.Adapter.PurchaseAdapter;
import com.hindustan.inapppurchase.Adapter.PurchaseItemClickListener;
import com.hindustan.inapppurchase.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PremiumActivity extends AppCompatActivity implements PurchaseItemClickListener {
    String TAG = "TAG";
    RecyclerView rv_purchaseList;
    BillingClient billingClient;
    PurchasesUpdatedListener purchasesUpdatedListener;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);


        sharedPreferences = getSharedPreferences("premium", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (Purchase purchase : purchases) {
                        verifySubPurchase(purchase);
                    }
                }

            }
        };
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                            for (Purchase purchase : list) {
                                verifySubPurchase(purchase);
                            }
                        }
                    }
                })
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

            }
        });


        rv_purchaseList = findViewById(R.id.rv_purchaseList);
        Log.d(TAG, "onCreate: "+Manager.getInstance().getProduct().get(0).getName());
        rv_purchaseList.setLayoutManager(new LinearLayoutManager(this));
        rv_purchaseList.setAdapter(new PurchaseAdapter(this, Manager.getInstance().getProduct(), this));


    }

    @Override
    public void onPurchaseItemClicked(int position, List<BillingFlowParams.ProductDetailsParams> productDetailsList) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsList)
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(PremiumActivity.this, billingFlowParams);

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
            Log.d(TAG, "onPurchaseItemClicked: ");
        }else{
            Log.d(TAG, "onPurchaseItemClicked: "+billingResult.getDebugMessage());
        }
    }

    private void verifySubPurchase(Purchase purchase) {
        Log.d(TAG, "verifySubPurchase: ");

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // User prefs to set premium
                    Toast.makeText(getApplicationContext(), "Subscription activated, Enjoy!", Toast.LENGTH_SHORT).show();

                    editor.putBoolean("premium", true);
                    editor.apply();

                }
            }
        });

        Log.d(TAG, "Purchase Token: " + purchase.getPurchaseToken());
        Log.d(TAG, "Purchase Time: " + purchase.getPurchaseTime());
        Log.d(TAG, "Purchase OrderID: " + purchase.getOrderId());
    }
}