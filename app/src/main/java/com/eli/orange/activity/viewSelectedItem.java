package com.eli.orange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.eli.orange.R;
import com.eli.orange.activity.MainActivity;
import com.eli.orange.adapter.availableContentAdapter;
import com.eli.orange.fragments.orders.Orders;
import com.eli.orange.models.Upload;
import com.eli.orange.utils.Constants;
import com.eli.orange.utils.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.eli.orange.utils.Constants.CUSTOME_DATA;

public class viewSelectedItem extends AppCompatActivity {
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
    private  ArrayList<Upload> testing;
     Dialog myDialog;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_selected_item);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();




        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

       testing = getIntent().getParcelableArrayListExtra(CUSTOME_DATA);

       if (!testing.isEmpty()) {
           try {
               configureToolBar(testing.get(0).getTitle());
               productName.setText(testing.get(0).getTitle());
               productDescriptions.setText(testing.get(0).getDescription());
               productPrice.setText(testing.get(0).getPrice());
               StorageReference userStorage = FirebaseStorage.getInstance().getReference().child(Constants.DATABASE_PATH_UPLOADS).child(testing.get(0).getUserKey() + "/" + testing.get(0).getUrl());

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
           }catch (Exception e){
               throw e;
           }

       }else
           displayToast("No Data Available !!");
       orderButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ShowPopup(v,testing.get(0));
           }
       });

    }

    public void ShowPopup(View v, Upload upload) {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to Buy this?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       displayToast("Please You can't place order here now");

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void sendOrder(String quantity,String userPhone, String userId, String productId, String image, String productTitle, String productPrice) {

        if (userId.equals(auth.getCurrentUser().getUid())) {

            displayToast("Order Failed! \n Please You cant place order on your own products...");

        } else {
            String key = databaseReference.push().getKey();

            Orders orders = new Orders(key, quantity, userPhone, userId, auth.getUid(), productId, image,productTitle, productPrice);


            databaseReference.child(Constants.DATABASE_PATH_ORDERS).child(key).setValue(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    displayToast("Order placed Succesfully...");

                }
            });
        }


    }

    void displayToast(String message) {
        View toastLayout = LayoutInflater.from(this).inflate(R.layout.toast_layout, null);
        TextView text = toastLayout.findViewById(R.id.textView);
        text.setText(message);

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public void configureToolBar(String toolBarTitle){
        Toolbar toolbar = findViewById(R.id.my_toolbar_new_product);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(toolBarTitle);
        }
    }
}
