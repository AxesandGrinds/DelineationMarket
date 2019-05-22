package com.eli.orange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.eli.orange.models.Upload;
import com.eli.orange.utils.Constants;
import com.eli.orange.utils.GlideApp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.eli.orange.utils.Constants.CUSTOME_DATA;

public class viewSelectedItem extends AppCompatActivity {
    @BindView(R.id.textBack)
    TextView backIcon;
    @BindView(R.id.viewImageView)
    ImageView productImage;
    @BindView(R.id.viewOrderButton)
    Button orderButton;
    @BindView(R.id.viewProductTitle)
    TextView productName;
    @BindView(R.id.viewProductDescription)
    TextView productDescriptions;
    @BindView(R.id.viewProductPrice)
    Chip productPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_selected_item);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();

        ArrayList<Upload> testing = getIntent().getParcelableArrayListExtra(CUSTOME_DATA);

        productName.setText(testing.get(0).getTitle());
        productDescriptions.setText(testing.get(0).getDescription());
        productPrice.setText(testing.get(0).getPrice());
        StorageReference userStorage = FirebaseStorage.getInstance().getReference().child(Constants.DATABASE_PATH_UPLOADS).child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + testing.get(0).getUrl());

        userStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(getApplicationContext())
                        .load(uri.toString())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                        .into(productImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Exception", exception.getMessage() + "PrintTrace: " + exception.getStackTrace());
                // failed
            }
        });
    }
    @OnClick(R.id.viewOrderButton)
    void close(){
        this.finish();
    }
}
