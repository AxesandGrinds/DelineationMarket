package com.eli.orange.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.eli.orange.R;
import com.eli.orange.fragments.HomeFragment.homeFragment;
import com.eli.orange.models.Upload;
import com.eli.orange.utils.Constants;
import com.eli.orange.utils.GlideApp;
import com.eli.orange.viewSelectedItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class availableContentAdapter extends RecyclerView.Adapter<availableContentAdapter.ViewHolder> {

    private Context context;
    private List<Upload> uploads;
    private LayoutInflater mInflater;

    private FirebaseAuth auth;

    public availableContentAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.available_uploads_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Upload upload = uploads.get(position);

        holder.textViewName.setText(uploads.get(position).getTitle());
        holder.itemPrice.setText("Buy "+uploads.get(position).getPrice());


        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference userStorage = storage.child(Constants.DATABASE_PATH_UPLOADS).child(auth.getCurrentUser().getUid() + "/" + uploads.get(position).getUrl());

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
        Chip itemPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(this::onLongClick);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
            homeFragment fragm = (homeFragment)fm.findFragmentById(R.id.frame);
            fragm.displayPositionMarker(uploads.get(adapterPosition).getLatitude(),uploads.get(adapterPosition).getLongitude());
           /* int adapterPosition = getAdapterPosition();
            Intent parcelIntent = new Intent(context, viewSelectedItem.class);
            ArrayList<Upload> dataList = new ArrayList<Upload>();
            dataList.add(uploads.get(adapterPosition));
            parcelIntent.putParcelableArrayListExtra(Constants.CUSTOME_DATA, (ArrayList<? extends Parcelable>) dataList);

            Log.d("ARRAY _LIST",""+dataList.get(0).getTitle());
            context.startActivity(parcelIntent);*/

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }


    }

}