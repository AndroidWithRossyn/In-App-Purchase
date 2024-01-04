package com.hindustan.inapppurchase.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.hindustan.inapppurchase.Adapter.Service;
import com.hindustan.inapppurchase.R;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    BillingClient billingClient;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPreferences = getSharedPreferences("premium",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        checkSubscription();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        },3000);

        Intent intent = new Intent(this, Service.class);
        startService(intent);


    }

    private void checkSubscription() {
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {

                            for (Purchase purchase : purchases) {
                                verifySubPurchase(purchase);
                            }

                        }
                    }
                })
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Log.d("TAG", "onBillingServiceDisconnected: ");
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, new PurchasesResponseListener() {
                        @Override
                        public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                Log.d("testOffer", list.size() + " size");
                                if (list.size() > 0) {
                                    editor.putBoolean("premium",true);
                                    editor.apply();
                                    int i = 0;
                                    for (Purchase purchase : list) {
                                        // Here you can manage each product, if you have multiple subscriptions
                                        Log.d("testOffer", purchase.getOriginalJson()); // Get to see the order information
                                        Log.d("testOffer", " index " + i);
                                        i++;
                                    }
                                } else {
                                    editor.putBoolean("premium",false);
                                    editor.apply();
                                }

                            }

                        }
                    });
                }
            }
        });
    }

    private void verifySubPurchase(Purchase purchase) {

        Log.d("TAG", "verifySubPurchase: ");

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // User prefs to set premium
                    Toast.makeText(SplashActivity.this, "Subscription activated, Enjoy!", Toast.LENGTH_SHORT).show();

                    editor.putBoolean("premium", true);
                    editor.apply();

                }
            }
        });

        Log.d("TAG", "Purchase Token: " + purchase.getPurchaseToken());
        Log.d("TAG", "Purchase Time: " + purchase.getPurchaseTime());
        Log.d("TAG", "Purchase OrderID: " + purchase.getOrderId());
    }
}