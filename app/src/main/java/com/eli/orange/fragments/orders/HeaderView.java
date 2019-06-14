package com.eli.orange.fragments.orders;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.eli.orange.R;
import com.eli.orange.utils.Constants;
import com.eli.orange.utils.GlideApp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.Collapse;
import com.mindorks.placeholderview.annotations.expand.Expand;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.SingleTop;

@Parent
@SingleTop
@Layout(R.layout.header_layout)
public class HeaderView {
    private static String TAG = "HeaderView";
    @View(R.id.header_text)
    TextView headerText;
    @View(R.id.header_image)
    ImageView headerImage;
    private Context mContext;
    private String mHeaderText, mSeller, mImage;
    private FirebaseAuth auth;
    public HeaderView(Context context,String headerText) {
        this.mContext = context;
        this.mHeaderText = headerText;
        auth = FirebaseAuth.getInstance();
    }
    @Resolve
    private void onResolve(){
        Log.d(TAG, "onResolve");
        headerText.setText("Order No:"+mHeaderText.substring(0,mHeaderText.length()-4));

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference userStorage = storage.child(Constants.DATABASE_PATH_UPLOADS).child(auth.getCurrentUser().getUid() + "/" + mHeaderText);

        userStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(mContext)
                        .load(uri.toString())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                        .into(headerImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Exception", exception.getMessage() + "PrintTrace: " + exception.getStackTrace());
                // failed
            }
        });



    }
    @Expand
    private void onExpand(){
        Log.d(TAG, "onExpand");
    }
    @Collapse
    private void onCollapse(){
        Log.d(TAG, "onCollapse");
    }
}