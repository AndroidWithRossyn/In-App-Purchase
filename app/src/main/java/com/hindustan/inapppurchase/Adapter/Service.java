package com.hindustan.inapppurchase.Adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.hindustan.inapppurchase.Activitys.PremiumActivity;

import java.util.Arrays;
import java.util.List;

public class Service extends android.app.Service {
    BillingClient billingClient;
    PurchasesUpdatedListener purchasesUpdatedListener;

    String TAG = "TAG";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
        establishConnection();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void establishConnection() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to Google Play
                // by calling the startConnection() method.
                establishConnection();
            }
        });

    }

    private void showProducts() {

        List<QueryProductDetailsParams.Product> productList = Arrays.asList(
                // Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("product_sku_1")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                // Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("product_sku_2")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                // Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("product_sku_3")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();


        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<ProductDetails> prodDetailsList) {
                // Process the result
                if (prodDetailsList != null) {

                    Log.d(TAG, prodDetailsList.size() + " number of products");
                    Log.d(TAG, "onProductDetailsResponse: " + prodDetailsList.get(0).getName());
                    Manager.getInstance().addProduct(prodDetailsList);
                }
            }
        });


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
