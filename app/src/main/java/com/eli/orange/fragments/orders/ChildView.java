package com.eli.orange.fragments.orders;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.eli.orange.R;
import com.eli.orange.activity.MainActivity;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.child_layout)
public class ChildView {
    private static String TAG ="ChildView";
    @View(R.id.child_name)
    TextView textViewChild;
    @View(R.id.child_price)
    TextView textViewPrice;
    @View(R.id.child_update)
    TextView updates;
    @View(R.id.card_view)
    CardView cardView;
    @View(R.id.child_call)
    TextView callBuyyer;
    private Context mContext;
    private Orders orders;
    private String imageUrl;
    public ChildView(Context mContext, Orders orders) {
        this.mContext = mContext;
        this.orders = orders;
    }
    @Resolve
    private void onResolve(){
        textViewChild.setText("Product: "+orders.getProductTitle());
        textViewPrice.setText("Price:   "+orders.getProductPrice());
              updates.setText("Phone No:"+orders.getUserPhoneNumber());

        callBuyyer.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + orders.getUserPhoneNumber()));// Initiates the Intent
                mContext.startActivity(intent);

            }
        });
    }

}