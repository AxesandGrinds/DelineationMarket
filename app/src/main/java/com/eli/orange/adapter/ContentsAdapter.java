package com.eli.orange.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.eli.orange.R;
import com.eli.orange.models.Upload;
import com.eli.orange.utils.Constants;
import com.eli.orange.utils.GlideApp;
import com.eli.orange.viewSelectedItem;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentsAdapter extends RecyclerView.Adapter<ContentsAdapter.ViewHolder> {

    private Context context;
    private List<Upload> uploads;
    private LayoutInflater mInflater;

    private FirebaseAuth auth;

    public ContentsAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.uploads_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Upload upload = uploads.get(position);

        holder.textViewName.setText(uploads.get(position).getTitle());
        holder.itemDescrription.setText(uploads.get(position).getDescription());
        holder.itemPrice.setText("Price: "+ uploads.get(position).getPrice());


        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference userStorage = storage.child(Constants.DATABASE_PATH_UPLOADS).child(auth.getCurrentUser().getUid() + "/" + uploads.get(position).getUrl());

        userStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(context)
                        .load(uri.toString())
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
            View.OnLongClickListener, View.OnCreateContextMenuListener {

        @BindView(R.id.textViewName)
        TextView textViewName;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.textViewDescription)
        TextView itemDescrription;
        @BindView(R.id.textViewprice)
        TextView itemPrice;
        @BindView(R.id.uploadDelete)
        TextView deleteButton;

        public ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(this::onLongClick);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {

           /* int adapterPosition = getAdapterPosition();
            Intent parcelIntent = new Intent(context, viewSelectedItem.class);
            ArrayList<Upload> dataList = new ArrayList<Upload>();
            dataList.add(uploads.get(adapterPosition));

            parcelIntent.putParcelableArrayListExtra(Constants.CUSTOME_DATA, (ArrayList<? extends Parcelable>) dataList);

            //Log.d("ARRAY _LIST",""+dataList.get(0).getTitle());
            context.startActivity(parcelIntent);*/
        }

        @Override
        public boolean onLongClick(View v) {
            v.setOnCreateContextMenuListener(this::onCreateContextMenu);
            return false;
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int adapterPosition = getAdapterPosition();
            menu.add(0, v.getId(), 0, "Update").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(context,"Update",Toast.LENGTH_LONG).show();
                    return true;
                }
            });//groupId, itemId, order, title
            menu.add(1, v.getId(), 0, "Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to Delete this?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int pos = getAdapterPosition();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS).child(auth.getCurrentUser().getUid());
                                    StorageReference photoReference =FirebaseStorage.getInstance().getReference().child(Constants.DATABASE_PATH_UPLOADS).child(auth.getCurrentUser().getUid() + "/" + uploads.get(pos).getUrl());

                                    //Create query for database Child reference
                                    Query applesQuery = databaseReference.orderByChild("url").equalTo(uploads.get(pos).getUrl());

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

                                    //Delete file from firebase database reference
                                    photoReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // File deleted successfully
                                            Log.d(Constants.TAG, "onSuccess: deleted file");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Uh-oh, an error occurred!
                                            Log.d(Constants.TAG, "onFailure: did not delete file");
                                        }
                                    });

                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    return true;
                }
            });

        }

    }



}