package com.eli.orange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

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
    private  ArrayList<Upload> testing;
    private Dialog myDialog;

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
    @OnClick(R.id.textBack)
    void close(){
        this.finish();
    }

    @OnClick(R.id.viewOrderButton)
    void PlaceOrder(){
        }
    public void ShowPopup(View v, Upload upload) {
        TextView txtclose, productPrice, desc, title;
        Button btnFollow;
        ImageView imageView;
        EditText userphone;
        Spinner quantitySpinner;
        myDialog.setContentView(R.layout.custom_popup);

        userphone = (EditText)myDialog.findViewById(R.id.popupUserPhone);
        quantitySpinner = (Spinner)myDialog.findViewById(R.id.quantitySpinner);
        txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
        productPrice = (TextView) myDialog.findViewById(R.id.popupProductPrice);
        imageView = myDialog.findViewById(R.id.popupProductImage);
        desc = myDialog.findViewById(R.id.popupProductDesc);
        title = myDialog.findViewById(R.id.popupProductTitle);

        title.setText(upload.getTitle());

        List<String> spinnerArray =  new ArrayList<String>();
        for (int i=1; i<=10;i++){

            spinnerArray.add(""+i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        quantitySpinner.setAdapter(adapter);





        desc.setText(upload.getDescription());
        productPrice.setText(upload.getPrice());
        txtclose.setText("X");
        btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selected = quantitySpinner.getSelectedItem().toString();
                if (selected.isEmpty()) {
                    selected = " "+1;
                    btnFollow.setEnabled(false);
                }
                String userMObile = userphone.getText().toString();
                if(userMObile.isEmpty()){
                    userphone.setError("Mobile Required !!!");
                    btnFollow.setEnabled(false);
                }else


                    sendOrder(selected,userMObile ,upload.getUserKey(), upload.getProductKey(), upload.getUrl(),upload.getTitle(),upload.getPrice());
            }
        });
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference userStorage = storage.child(Constants.DATABASE_PATH_UPLOADS).child(upload.getUserKey() + "/" + upload.getUrl());

        userStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(getApplicationContext())
                        .load(uri.toString())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Exception", exception.getMessage() + "PrintTrace: " + exception.getStackTrace());
                // failed
            }
        });


        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
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
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        myDialog.cancel();
    }
}
