package com.eli.orange.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.eli.orange.R;
import com.eli.orange.activity.MainActivity;
import com.eli.orange.fragments.HomeFragment.homeFragment;
import com.eli.orange.fragments.orders.Orders;
import com.eli.orange.models.Upload;
import com.eli.orange.utils.Constants;
import com.eli.orange.utils.GlideApp;
import com.eli.orange.activity.viewSelectedItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class availableContentAdapter extends RecyclerView.Adapter<availableContentAdapter.ViewHolder> {

    private Context context;
    private List<Upload> uploads = new ArrayList<>();
    private LayoutInflater mInflater;
    private MainActivity mainActivity;
    private Dialog myDialog;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    public availableContentAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        auth = FirebaseAuth.getInstance();
        mainActivity = new MainActivity();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.available_uploads_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Upload upload = uploads.get(position);

        holder.textViewName.setText(uploads.get(position).getTitle());
        holder.itemPrice.setText(uploads.get(position).getPrice());


        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference userStorage = storage.child(Constants.DATABASE_PATH_UPLOADS).child(uploads.get(position).getUserKey() + "/" + uploads.get(position).getUrl());

        userStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(context)
                        .load(uri.toString())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                        .into(holder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Exception", exception.getMessage() + "PrintTrace: " + exception.getStackTrace());
                // failed
            }
        });
    }


    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public void addItems(List<Upload> uploads) {
        this.uploads = uploads;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        @BindView(R.id.textViewName)
        TextView textViewName;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.textViewprice)
        TextView itemPrice;
        @BindView(R.id.uploadsLocation)
        ImageButton locationButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(this::onLongClick);
            locationButton.setOnClickListener(this::onClick);
            imageView.setOnClickListener(this::onClick);
            itemPrice.setOnClickListener(this::onClick);

        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            switch (v.getId()) {
                case R.id.uploadsLocation:
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    homeFragment fragm = (homeFragment) fm.findFragmentById(R.id.frame);
                    fragm.displayPositionMarker(uploads.get(adapterPosition).getLatitude(), uploads.get(adapterPosition).getLongitude());
                    break;
                case R.id.textViewprice:
                    myDialog = new Dialog(context);
                    ShowPopup(v, uploads.get(adapterPosition));
                    break;
                case R.id.imageView:
                    Intent parcelIntent = new Intent(context, viewSelectedItem.class);
                    ArrayList<Upload> dataList = new ArrayList<Upload>();
                    dataList.add(uploads.get(adapterPosition));
                    parcelIntent.putParcelableArrayListExtra(Constants.CUSTOME_DATA, (ArrayList<? extends Parcelable>) dataList);
                    context.startActivity(parcelIntent);

                    break;
            }

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }


    }

    public void ShowPopup(View v, Upload upload) {
        TextView txtclose, productPrice, desc, title;
        Button btnFollow;
        ImageView imageView;
        EditText  userphone;
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerArray);

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
                GlideApp.with(context)
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
        View toastLayout = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView text = toastLayout.findViewById(R.id.textView);
        text.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        myDialog.cancel();
    }
}