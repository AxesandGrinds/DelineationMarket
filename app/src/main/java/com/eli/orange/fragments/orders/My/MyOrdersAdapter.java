package com.eli.orange.fragments.orders.My;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.eli.orange.R;
import com.eli.orange.activity.MainActivity;
import com.eli.orange.fragments.orders.Orders;
import com.eli.orange.utils.Constants;
import com.eli.orange.utils.GlideApp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    private Context context;
    private List<Orders> orders = new ArrayList<>();
    private LayoutInflater mInflater;
    private MainActivity mainActivity;
    private Dialog myDialog;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    public MyOrdersAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_item, parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         Orders order = orders.get(position);

        holder.title.setText("Quantity: "+order.getProductQuantity());
        holder.textViewPhone.setText("Price: "+order.getProductPrice());


        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference userStorage = storage.child(Constants.DATABASE_PATH_UPLOADS).child(order.getSellerIdentity() + "/" + order.getProductImage());

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
        if (!orders.isEmpty())
            return orders.size();
        else
            return 0;

    }

    public void addItems(List<Orders> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.textViewTitle)
        TextView title;
        @BindView(R.id.textViewPrice)
        TextView textViewPhone;
        @BindView(R.id.txtdelete)
        TextView deleteOrder;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            deleteOrder.setOnClickListener(this::onClick);

        }

        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to Delete Order?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            int pos = getAdapterPosition();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_ORDERS);

                            //Create query for database Child reference
                            Query applesQuery = databaseReference
                                    .orderByChild("orderIdentity").equalTo(orders.get(pos).getOrderIdentity());

                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                        appleSnapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(Constants.TAG, "onCancelled", databaseError.toException());
                                }
                            });

                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }


    }

    void displayToast(String message){
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