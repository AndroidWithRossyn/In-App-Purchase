package com.hindustan.inapppurchase.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ProductDetails;
import com.hindustan.inapppurchase.R;

import java.util.ArrayList;
import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {
    List<ProductDetails> skus;
    List<BillingFlowParams.ProductDetailsParams> params = new ArrayList<>();

    private PurchaseItemClickListener itemClickListener;

    Context context;

    public PurchaseAdapter(Context applicationContext, List<ProductDetails> skuList, PurchaseItemClickListener clickListener) {
        this.skus = skuList;
        this.itemClickListener = clickListener;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public PurchaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ProductDetails productDetails = skus.get(position);

        holder.tv_title.setText(productDetails.getName());

        if (productDetails.getSubscriptionOfferDetails() != null) {
            String formattedPrice = productDetails.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
            holder.tv_price.setText(formattedPrice);
        } else {

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetails productDetails1 = skus.get(position);
                params.clear();
                params.add(BillingFlowParams
                        .ProductDetailsParams
                        .newBuilder()
                        .setProductDetails(productDetails1)
                        .setOfferToken(productDetails1.getSubscriptionOfferDetails().get(0).getOfferToken())
                        .build());
                if (params != null && itemClickListener != null) {
                    itemClickListener.onPurchaseItemClicked(position, params);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return skus.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_price, tv_offer, tv_offer_per;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_price = itemView.findViewById(R.id.tv_price);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_offer = itemView.findViewById(R.id.tv_offer);
            tv_offer_per = itemView.findViewById(R.id.tv_offer_percentage);

        }
    }
}
