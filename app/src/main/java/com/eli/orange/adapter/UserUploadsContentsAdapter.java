package com.eli.orange.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.eli.orange.R;
import com.eli.orange.models.Upload;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserUploadsContentsAdapter extends RecyclerView.Adapter<UserUploadsContentsAdapter.ViewHolder> {

    private Context context;
    private List<Upload> uploads;
    private LayoutInflater mInflater;

    private FirebaseAuth auth;

    public UserUploadsContentsAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_uploads_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Upload upload = uploads.get(position);

        holder.textViewName.setText(uploads.get(position).getTitle());
        holder.itemDescrription.setText(uploads.get(position).getDescription());
        holder.itemPrice.setText( uploads.get(position).getPrice());


        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference userStorage = storage.child(Constants.DATABASE_PATH_UPLOADS).child(auth.getCurrentUser().getUid() + "/" + uploads.get(position).getUrl());

        userStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(context).load(uri.toString()).into(holder.imageView);
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
        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }


    }



}