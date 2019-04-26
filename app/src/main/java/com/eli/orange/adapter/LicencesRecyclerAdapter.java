package com.eli.orange.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.eli.orange.R;
import com.eli.orange.fragments.LicencesFragment;
import com.eli.orange.models.Licence;
import com.eli.orange.models.MapData;
import com.eli.orange.utils.ShowPopup;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.eli.orange.utils.Constants.PRIVACY_POLICY_URL;

public class LicencesRecyclerAdapter extends RecyclerView.Adapter<LicencesRecyclerAdapter.ViewHolder> {
    Context context;
    private final LayoutInflater mInflater;
    private List<Licence> licenceList;
    private static LocationsAdapter.ClickListener clickListener;
    private View.OnLongClickListener longClickListener;
    public LicencesRecyclerAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public LicencesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LicencesRecyclerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.licence_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LicencesRecyclerAdapter.ViewHolder holder, int position) {
        holder.licenceId.setText(position+1+".");
        holder.licenceName.setText(licenceList.get(position).getHint());
        holder.licenceTitle.setText(licenceList.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        if (licenceList != null) {
            return licenceList.size();
        } else

        return 0;
    }
    public void addItems(List<Licence> licences) {
        this.licenceList = licences;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.licenceID)
        TextView licenceId;
        @BindView(R.id.licenceName)
        TextView licenceName;
        @BindView(R.id.licenceTitle)
        TextView licenceTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this::onClick);
            ButterKnife.bind(this,itemView);
        }

        @Override
        public void onClick(View v) {
            ShowPopup  showPopup = new ShowPopup();
           openCustormChromTabs(licenceList.get(this.getAdapterPosition()).getUrl());
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
    public void setOnItemClickListener(LocationsAdapter.ClickListener clickListener) {
        LicencesRecyclerAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }
    public void openCustormChromTabs(String LICENCE_URL){

        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        // and launch the desired Url with CustomTabsIntent.launchUrl()

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(context.getResources().getColor(R.color.primaryColor));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(LICENCE_URL));
    }

}
